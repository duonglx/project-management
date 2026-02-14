# Backend Architecture

## Spring Boot Backend with JWT & RBAC

This document describes the implemented backend architecture and authentication/authorization system.

**Current Status:** Spring Boot backend with JWT authentication and RBAC permission system (Phase 1 complete)
**Technology:** Java 17+, Spring Boot 3.x, Spring Security, PostgreSQL, Prisma ORM


## JWT-Based Authentication (Implemented)

### Authentication Flow

```
1. User Login
   ├─► Client POSTs credentials to /api/auth/login
   ├─► Server validates credentials via CustomUserDetailsService
   ├─► Server generates JWT pair (access + refresh tokens)
   │   ├─ Access token: 15 min expiry, HS512 signed
   │   └─ Refresh token: 7 day expiry, stored in RefreshToken table
   ├─► Server sets httpOnly cookies (secure, sameSite=Strict)
   └─► Client receives AuthResponse with user data

2. Authenticated Request
   ├─► JwtAuthenticationFilter intercepts request
   ├─► Extracts JWT from Authorization header or httpOnly cookie
   ├─► JwtService validates signature, expiration, and type
   ├─► CustomUserDetailsService loads user from database
   ├─► SecurityContext populated with CustomUserDetails + authorities
   ├─► @PreAuthorize(@perm.hasPermission(...)) evaluated
   └─► Response sent with user context available

3. Token Refresh
   ├─► Access token expires or about to expire
   ├─► Client calls POST /api/auth/refresh with refresh token
   ├─► AuthService validates refresh token from RefreshToken table
   ├─► New access token generated + old one invalidated
   ├─► New refresh token issued (rotation)
   └─► Updated cookies sent to client

4. Logout
   ├─► Client calls POST /api/auth/logout
   ├─► AuthService deletes RefreshToken from database
   ├─► httpOnly cookies cleared
   ├─► Client-side auth state cleared
   └─► User redirected to /login

5. Permission-Protected Endpoint
   ├─► Controller method annotated with @PreAuthorize
   ├─► Example: @PreAuthorize("@perm.hasPermission(#userId, #workspaceId, 'workspace:edit')")
   ├─► PermissionEvaluator.hasPermission() called with userId, workspaceId, permission
   ├─► PermissionService queries RolePermission + WorkspaceMember tables
   ├─► Cache lookup (Redis) for role→permissions mapping
   ├─► Returns true/false, framework denies access if false
   └─► Response 403 Forbidden if permission denied
```

### Token Structure

**Access Token Claims:**
```json
{
  "sub": "user@example.com",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "type": "access",
  "iat": 1644856800,
  "exp": 1644860400
}
```

**Refresh Token Claims:**
```json
{
  "sub": "user@example.com",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "type": "refresh",
  "iat": 1644856800,
  "exp": 1660502400
}
```

### Security Measures

- **Algorithm:** HS512 (HMAC SHA-512)
- **Secret:** Configured in application.yml, minimum 64 bytes
- **httpOnly Cookies:** Prevents JavaScript access (XSS protection)
- **Secure Flag:** HTTPS only (production)
- **SameSite:** Strict to prevent CSRF
- **Expiration:** Access token short-lived (15 min), refresh token long-lived (7 days)
- **Rotation:** Old refresh tokens invalidated on new token issuance
- **Stateless:** No session storage on server (JWT contains all required info)

## Role-Based Access Control (RBAC) - 3-Level System (Implemented)

### Hierarchy

```
Workspace Level
├─ OWNER (special, highest privilege)
├─ ADMIN (manage workspace, projects, members)
└─ MEMBER (limited access, workspace-level)
    │
    └─ Project Level (members of specific project)
       ├─ LEAD (manage project, assign members)
       └─ MEMBER (create/view tasks)
           │
           └─ Task Level (granular permissions)
              ├─ CREATOR (edit own task)
              └─ ASSIGNEE (edit own task)
```

### Permission Model

**System Permissions** (stored in Permission table):
- Granular permission names: `workspace:create_project`, `workspace:delete_member`, `project:edit`, `task:delete`, etc.
- Defined in database, managed by admins
- Reusable across roles and workspaces

**Role-Permission Mapping** (RolePermission table):
- Maps roles to permissions with optional workspace override
- **Global defaults** (workspaceId=NULL): Apply to all workspaces
- **Workspace overrides** (workspaceId=ABC123): Workspace-specific customization
- Examples:
  - OWNER: No entry (always bypassed)
  - ADMIN: Set of workspace management permissions (global default)
  - MEMBER: Limited permissions (can be customized per workspace)

**Workspace Roles** (Enum):
```java
public enum WorkspaceRole {
  OWNER,      // Workspace creator, full control
  ADMIN,      // Manage projects, members (no workspace deletion)
  MEMBER      // View workspace, create tasks in projects
}
```

**Project Roles** (Enum):
```java
public enum ProjectRole {
  LEAD,       // Manage project, edit settings
  MEMBER      // Create/view tasks
}
```

### Database Schema

```sql
CREATE TABLE permission (
  id UUID PRIMARY KEY,
  name VARCHAR(100) UNIQUE NOT NULL,   -- e.g., "workspace:create_project"
  description TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE role_permission (
  id UUID PRIMARY KEY,
  role VARCHAR(50) NOT NULL,           -- OWNER, ADMIN, MEMBER, LEAD
  permission_id UUID NOT NULL,
  workspace_id UUID,                   -- NULL = global default, non-NULL = override
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (permission_id) REFERENCES permission(id),
  FOREIGN KEY (workspace_id) REFERENCES workspace(id),
  UNIQUE(role, permission_id, workspace_id)
);

CREATE TABLE workspace_member (
  id UUID PRIMARY KEY,
  user_id UUID NOT NULL,
  workspace_id UUID NOT NULL,
  role VARCHAR(50) NOT NULL,           -- OWNER, ADMIN, MEMBER
  joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES "user"(id),
  FOREIGN KEY (workspace_id) REFERENCES workspace(id),
  UNIQUE(user_id, workspace_id)
);

CREATE TABLE project_member (
  id UUID PRIMARY KEY,
  user_id UUID NOT NULL,
  project_id UUID NOT NULL,
  role VARCHAR(50) NOT NULL,           -- LEAD, MEMBER
  joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES "user"(id),
  FOREIGN KEY (project_id) REFERENCES project(id),
  UNIQUE(user_id, project_id)
);
```

### Permission Evaluation Flow

**PermissionService Logic:**

```java
// 1. Check workspace membership + role
WorkspaceMember member = workspaceMemberRepository
  .findByUserIdAndWorkspaceId(userId, workspaceId);

// 2. OWNER bypasses everything
if (member.getRole() == WorkspaceRole.OWNER) return true;

// 3. Get permissions for role + workspace
Set<String> perms = getPermissionsForRole(role, workspaceId);
// - Checks workspace-specific overrides first
// - Falls back to global defaults
// - Results cached in Redis

// 4. Check if permission in set
return perms.contains(permissionName);
```

**Spring Security Integration:**

```java
// Example controller method with @PreAuthorize
@DeleteMapping("/workspaces/{wid}/projects/{pid}")
@PreAuthorize("@perm.hasProjectPermission(#userId, #wid, #pid, 'project:delete')")
public ResponseEntity<Void> deleteProject(
    @PathVariable String wid,
    @PathVariable String pid,
    @AuthenticationPrincipal CustomUserDetails user) {
  // userId extracted from CustomUserDetails
  // Permission evaluated by PermissionEvaluator.hasPermission()
  // Returns 403 if denied
  return ResponseEntity.noContent().build();
}
```

### API Endpoints for Permission Management

```
GET /api/workspaces/{workspaceId}/role-permissions
  Description: List all roles and their permissions in workspace
  Response: { roles: [{ role: "ADMIN", permissions: ["workspace:create_project", ...] }] }
  Requires: workspace:admin permission

PUT /api/admin/workspaces/{workspaceId}/role-permissions
  Description: Update workspace-specific role permissions
  Request: { role: "MEMBER", permissionNames: ["workspace:view", ...] }
  Response: { message: "Role permissions updated" }
  Requires: workspace:admin permission
  Effect: Invalidates Redis cache, new rules apply immediately

GET /api/admin/permissions
  Description: List all available permissions
  Response: [{ id: "...", name: "workspace:create_project", description: "..." }]
  Requires: System admin role

GET /api/auth/me?workspaceId={id}
  Description: Get current user + permissions in workspace
  Response: { user: {...}, permissions: ["workspace:view", "project:create", ...] }
  Requires: Authenticated

POST /api/workspaces/{wId}/labels
  Description: Create a workspace label
  Request: { name: "Bug", color: "#ef4444", description: "..." }
  Response: { id: "...", name: "Bug", color: "#ef4444", ... }
  Requires: workspace:admin permission
  Max: 50 labels per workspace

GET /api/workspaces/{wId}/labels
  Description: List all workspace labels
  Response: [{ id: "...", name: "Bug", color: "#ef4444", ... }]
  Requires: workspace:view permission

PUT /api/workspaces/{wId}/labels/{lId}
  Description: Update a label
  Request: { name: "Critical Bug", color: "#ff0000" }
  Response: { id: "...", name: "Critical Bug", color: "#ff0000", ... }
  Requires: workspace:admin permission

DELETE /api/workspaces/{wId}/labels/{lId}
  Description: Delete a label (removes from all tasks)
  Requires: workspace:admin permission

POST /api/workspaces/{wId}/task-statuses
  Description: Create custom task status
  Request: { name: "In Review", category: "ACTIVE", color: "#8b5cf6" }
  Response: { id: "...", name: "In Review", slug: "in-review", ... }
  Requires: workspace:admin permission

GET /api/workspaces/{wId}/task-statuses
  Description: List workspace statuses
  Response: [{ id: "...", name: "Todo", slug: "todo", category: "NOT_STARTED", ... }]
  Requires: workspace:view permission

PUT /api/workspaces/{wId}/task-statuses/{sId}
  Description: Update status
  Request: { name: "Code Review", color: "#8b5cf6" }
  Response: { id: "...", name: "Code Review", ... }
  Requires: workspace:admin permission

DELETE /api/workspaces/{wId}/task-statuses/{sId}
  Description: Delete status (reassigns tasks to default)
  Requires: workspace:admin permission

PUT /api/workspaces/{wId}/task-statuses/reorder
  Description: Reorder task statuses
  Request: { statusIds: ["id1", "id2", "id3"] }
  Requires: workspace:admin permission

PUT /api/workspaces/{wId}/members/{userId}
  Description: Update workspace member role
  Request: { role: "ADMIN" }
  Response: { id: "...", userId: "...", role: "ADMIN", joinedAt: "..." }
  Requires: workspace:manage_members permission
  Restrictions: OWNER role cannot be changed, OWNER cannot be removed

POST /api/workspaces/{wId}/custom-fields
  Description: Create custom field definition
  Request: { name: "Priority", type: "DROPDOWN", options: ["Low", "High"], isRequired: true }
  Response: { id: "...", name: "Priority", type: "DROPDOWN", options: [...], isRequired: true, position: 0 }
  Requires: workspace:manage_settings permission
  Max: 20 custom fields per workspace
  Field Types: TEXT, NUMBER, DROPDOWN, DATE, CHECKBOX, URL

GET /api/workspaces/{wId}/custom-fields
  Description: List custom field definitions for workspace
  Response: [{ id: "...", name: "Priority", type: "DROPDOWN", ... }]
  Requires: workspace:view permission

PUT /api/workspaces/{wId}/custom-fields/{fieldId}
  Description: Update custom field definition
  Request: { name: "Priority Level", type: "DROPDOWN", options: [...], isRequired: true }
  Response: { id: "...", name: "Priority Level", ... }
  Requires: workspace:manage_settings permission

DELETE /api/workspaces/{wId}/custom-fields/{fieldId}
  Description: Delete custom field (cascades to all task values)
  Requires: workspace:manage_settings permission

PUT /api/workspaces/{wId}/custom-fields/reorder
  Description: Reorder custom field definitions
  Request: { fieldIds: ["id1", "id2", "id3"] }
  Requires: workspace:manage_settings permission

GET /api/tasks/{taskId}/custom-fields
  Description: Get custom field values for a task
  Response: [{ fieldId: "...", fieldName: "Priority", fieldType: "DROPDOWN", value: "High" }]
  Requires: Authenticated + workspace:view permission

PUT /api/tasks/{taskId}/custom-fields
  Description: Batch upsert custom field values for task
  Request: { fields: [{ fieldId: "...", value: "High" }, ...] }
  Response: [{ fieldId: "...", fieldName: "Priority", fieldType: "DROPDOWN", value: "High" }]
  Requires: Authenticated + task:edit permission
```

### Caching Strategy

**Redis Cache for Permissions:**
- Key: `rolePermissions:{role}:{workspaceId}`
- TTL: 5 minutes (configurable)
- Invalidation: On permission update via @CacheEvict(allEntries=true)
- Fallback: Database query on cache miss

**Example Cache Flow:**
```
1. Request to check "project:edit" permission
2. Cache lookup: rolePermissions:ADMIN:550e8400-e29b
3. Cache hit → Return cached permission set
4. Cache miss → Query database for RolePermission entries
5. Store in cache with 5min TTL
6. Return permissions to PermissionService
7. Permission check evaluates against cached set
```

### Permission Matrix (Default)

| Permission | OWNER | ADMIN | MEMBER | PROJECT_LEAD | PROJECT_MEMBER |
|-----------|-------|-------|--------|--------------|-----------------|
| workspace:create_project | ✓ | ✓ | ✗ | N/A | N/A |
| workspace:edit | ✓ | ✓ | ✗ | N/A | N/A |
| workspace:delete | ✓ | ✗ | ✗ | N/A | N/A |
| workspace:add_member | ✓ | ✓ | ✗ | N/A | N/A |
| workspace:remove_member | ✓ | ✓ | ✗ | N/A | N/A |
| project:view | ✓ | ✓ | ✓ | ✓ | ✓ |
| project:edit | ✓ | ✓ | ✗ | ✓ | ✗ |
| project:delete | ✓ | ✓ | ✗ | ✗ | ✗ |
| task:create | ✓ | ✓ | ✓ | ✓ | ✓ |
| task:edit | ✓ | ✓ | Own Only | ✓ | Own Only |
| task:delete | ✓ | ✓ | ✗ | ✓ | ✗ |

### Frontend Permission Usage

**usePermission Hook:**
```javascript
const { permissions, has, hasAny, hasAll } = usePermission();

// Single permission
if (has('workspace:create_project')) {
  // Show create project button
}

// Any of multiple
if (hasAny(['project:delete', 'admin:manage'])) {
  // Show delete button
}

// All permissions
if (hasAll(['workspace:admin', 'audit:view'])) {
  // Show audit reports
}
```

**PermissionGate Component (future):**
```jsx
<PermissionGate permission="project:edit">
  <EditButton />
</PermissionGate>

// Falls back to null or custom fallback component if permission denied
```

### Database Architecture

#### PostgreSQL Schema

**Database:** PostgreSQL 14+ (with UUID support)
**ORM:** Prisma 5+

**Tables:**
- users
- workspaces
- workspace_members
- projects
- project_members
- tasks
- task_statuses
- labels
- task_labels
- comments
- permissions
- role_permissions
- refresh_tokens

**Indexes:**
```sql
-- Users
CREATE INDEX idx_users_email ON users(email);

-- Workspaces
CREATE INDEX idx_workspaces_slug ON workspaces(slug);
CREATE INDEX idx_workspaces_owner_id ON workspaces(owner_id);

-- Projects
CREATE INDEX idx_projects_workspace_id ON projects(workspace_id);
CREATE INDEX idx_projects_status ON projects(status);

-- Tasks
CREATE INDEX idx_tasks_project_id ON tasks(project_id);
CREATE INDEX idx_tasks_assignee_id ON tasks(assignee_id);
CREATE INDEX idx_tasks_status_id ON tasks(status_id);
CREATE INDEX idx_tasks_due_date ON tasks(due_date);

-- Task Statuses
CREATE INDEX idx_task_statuses_workspace_id ON task_statuses(workspace_id);

-- Labels
CREATE INDEX idx_labels_workspace_id ON labels(workspace_id);

-- Task Labels
CREATE INDEX idx_task_labels_task_id ON task_labels(task_id);
CREATE INDEX idx_task_labels_label_id ON task_labels(label_id);

-- Comments
CREATE INDEX idx_comments_task_id ON comments(task_id);
CREATE INDEX idx_comments_user_id ON comments(user_id);
```

**Constraints:**
- Foreign keys with CASCADE delete where appropriate
- Unique constraints (email, workspace slug)
- Check constraints (progress 0-100, valid enums)

#### Data Integrity & Transactions

**Transaction Examples:**
```javascript
// Create project with initial members
await prisma.$transaction([
  prisma.project.create({ data: projectData }),
  prisma.projectMember.createMany({ data: memberData })
]);

// Delete workspace (cascade to projects, tasks)
await prisma.workspace.delete({
  where: { id: workspaceId }
});
```

### Caching Strategy (Future)

**Redis Cache Layers:**

**1. Session Cache:**
- User sessions (JWT blacklist)
- Active user data
- TTL: 15 minutes (access token lifetime)

**2. Data Cache:**
- Frequently accessed workspaces
- Project summaries
- User profiles
- TTL: 5 minutes

**3. Query Result Cache:**
- Dashboard statistics
- Analytics data
- TTL: 1 minute

**Cache Invalidation:**
```javascript
// Invalidate on updates
await redis.del(`workspace:${workspaceId}`);
await redis.del(`project:${projectId}`);

// Pattern-based invalidation
await redis.keys(`user:${userId}:*`).then(keys => redis.del(...keys));
```

### Real-Time Architecture (Future)

#### WebSocket Integration

**Technology:** Socket.io

**Events:**
```javascript
// Client → Server
socket.emit('join-workspace', { workspaceId });
socket.emit('join-project', { projectId });
socket.emit('leave-project', { projectId });

// Server → Client
socket.on('task-created', (task) => {});
socket.on('task-updated', (task) => {});
socket.on('task-deleted', (taskId) => {});
socket.on('project-updated', (project) => {});
socket.on('member-added', (member) => {});
```

**Connection Flow:**
```
1. User authenticates (JWT)
2. Client establishes WebSocket connection with token
3. Server validates token and creates socket connection
4. Client joins rooms (workspace, projects)
5. Server broadcasts updates to room members
6. Client updates Redux state on receiving events
```

## Deployment Architecture

### Current Deployment (Frontend-Only)

```
┌──────────────────────────────────────────────────┐
│                   Vercel CDN                     │
│  ┌────────────────────────────────────────────┐  │
│  │         Static Files (SPA Build)           │  │
│  │  ├─ index.html                             │  │
│  │  ├─ assets/                                │  │
│  │  │   ├─ main.[hash].js (minified)         │  │
│  │  │   ├─ vendor.[hash].js                  │  │
│  │  │   └─ styles.[hash].css                 │  │
│  │  └─ images/                                │  │
│  └────────────────────────────────────────────┘  │
│                                                  │
│  Global CDN (Edge Network)                       │
│  HTTPS, Compression, Caching                     │
└──────────────────────────────────────────────────┘
```

### Future Deployment (Full-Stack)

```
┌─────────────────────────────────────────────────────────────┐
│                     Frontend (Vercel)                       │
│  Static SPA + CDN + Edge Functions                          │
│  https://app.example.com                                    │
└────────────────────────┬────────────────────────────────────┘
                         │ HTTPS
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                  Backend API (Railway/Render)               │
│  ┌───────────────────────────────────────────────────────┐  │
│  │              Node.js API Server                       │  │
│  │  ├─ Express/Fastify                                   │  │
│  │  ├─ Prisma ORM                                        │  │
│  │  ├─ WebSocket Server (Socket.io)                     │  │
│  │  └─ Authentication (JWT)                              │  │
│  └───────────────────────────────────────────────────────┘  │
│  https://api.example.com                                    │
└────────────────────────┬────────────────────────────────────┘
                         │
           ┌─────────────┴─────────────┐
           │                           │
           ▼                           ▼
┌──────────────────────┐  ┌──────────────────────┐
│  PostgreSQL Database │  │   Redis Cache        │
│  (Managed Instance)  │  │  (Managed Instance)  │
│  Railway/Render      │  │  Upstash/Redis Cloud │
└──────────────────────┘  └──────────────────────┘
```

### Scalability Considerations

**Frontend:**
- CDN edge caching
- Code splitting (route-based)
- Asset optimization (compression, lazy loading)
- Service Worker (PWA, future)

**Backend:**
- Horizontal scaling (multiple API instances)
- Load balancer (Nginx/cloud provider)
- Database connection pooling (Prisma)
- Background job processing (Bull/BullMQ)

**Database:**
- Read replicas for analytics queries
- Partitioning for large tables (tasks, comments)
- Query optimization with proper indexes

## Security Architecture

### Frontend Security

**Current:**
- React XSS protection (automatic escaping)
- Content Security Policy (CSP headers)
- HTTPS-only (Vercel default)

**Future:**
- JWT token storage (httpOnly cookies preferred)
- CSRF protection
- Input validation before API calls

### Backend Security (Future)

**Authentication:**
- Password hashing (bcrypt, rounds: 12)
- JWT with short expiration (15 min)
- Refresh tokens (httpOnly cookies)
- Rate limiting on auth endpoints

**Authorization:**
- RBAC enforcement on all endpoints
- Workspace-level data isolation
- API key authentication for third-party integrations

**Data Protection:**
- SQL injection prevention (Prisma parameterized queries)
- Input validation (Zod/Joi schemas)
- Output sanitization
- Sensitive data encryption at rest

**Infrastructure:**
- HTTPS/TLS 1.3
- Security headers (HSTS, X-Frame-Options, etc.)
- CORS configuration
- Environment variable protection

## Monitoring & Observability (Future)

**Logging:**
- Structured logs (JSON format)
- Log aggregation (e.g., Datadog, LogRocket)
- Error tracking (Sentry)

**Metrics:**
- API response times
- Database query performance
- Cache hit/miss rates
- Active user counts

**Alerts:**
- Error rate thresholds
- API downtime
- Database connection issues
- High memory/CPU usage

## Performance Optimization

### Frontend Optimization

**Current:**
- Vite optimized build (tree shaking, minification)
- Code splitting (vendor chunk separation)
- Lazy loading images (future)

**Future:**
- Route-based code splitting (React.lazy)
- Component lazy loading
- Virtual scrolling for long lists
- Image optimization (WebP, responsive images)
- Service Worker caching (PWA)

### Backend Optimization (Future)

**Database:**
- Query optimization (proper indexes, EXPLAIN ANALYZE)
- Connection pooling (Prisma built-in)
- Read replicas for heavy queries
- Materialized views for analytics

**Caching:**
- Redis for session data
- Query result caching
- CDN caching for static assets
- HTTP caching headers (ETag, Cache-Control)

**API:**
- GraphQL DataLoader (batch queries, avoid N+1)
- Pagination (cursor-based, limit/offset)
- Compression (gzip/brotli)
- Rate limiting per user/IP

## Migration Path (Frontend to Full-Stack)

### Phase 1: API Client Setup
- Add axios or fetch wrapper
- Create API service layer
- Add environment variables for API URL
- Keep Redux structure, replace dummy data with API calls

### Phase 2: Authentication Integration
- Implement login/register flows
- Store JWT tokens
- Add protected routes
- Redirect unauthenticated users

### Phase 3: API Integration
- Replace Redux CRUD actions with API calls
- Implement optimistic updates
- Add loading and error states
- Handle API errors gracefully

### Phase 4: Real-Time Features
- Add WebSocket connection
- Listen for server events
- Update Redux state on events
- Show real-time notifications

### Phase 5: Optimization
- Implement caching strategies
- Add offline support (PWA)
- Performance monitoring
- Error tracking integration

## Conclusion

The current frontend-only architecture provides a solid foundation for future backend integration. The component structure, state management, and data model are designed with scalability in mind. The Prisma schema serves as a blueprint for the future PostgreSQL database, ensuring a smooth transition to a full-stack application.

Key architectural principles:
- **Separation of Concerns:** UI, state, and data layers are distinct
- **Scalability:** Component and state structure supports growth
- **Maintainability:** Modular design allows easy updates
- **Future-Proof:** Architecture accommodates planned backend integration
