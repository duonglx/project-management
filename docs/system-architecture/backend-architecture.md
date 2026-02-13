# Backend Architecture (Future)

## Future Backend Architecture

This document outlines the planned backend architecture for Phase 1+ of the project.

**Current Status:** Frontend-only prototype
**Target:** Full-stack application with REST/GraphQL API


#### JWT-Based Authentication

```
1. User Login
   ├─► Client POSTs credentials to /api/auth/login
   ├─► Server validates credentials
   ├─► Server generates JWT (access + refresh tokens)
   └─► Client stores tokens (httpOnly cookies or localStorage)

2. Authenticated Request
   ├─► Client includes JWT in Authorization header
   ├─► Server validates JWT signature and expiration
   ├─► Server extracts user ID from token
   ├─► Server processes request with user context
   └─► Server responds with data

3. Token Refresh
   ├─► Access token expires (short-lived, 15 min)
   ├─► Client uses refresh token to get new access token
   └─► Server issues new access token

4. Logout
   ├─► Client calls /api/auth/logout
   ├─► Server invalidates refresh token (blacklist)
   └─► Client deletes stored tokens
```

#### Role-Based Access Control (RBAC)

**Workspace Roles:**
- **ADMIN:** Full control (manage workspace, projects, members)
- **MEMBER:** Limited access (view, create tasks, update own tasks)

**Permission Matrix:**

| Action | Workspace Admin | Workspace Member |
|--------|----------------|------------------|
| Create Project | ✓ | ✗ |
| Edit Project | ✓ | Project Member Only |
| Delete Project | ✓ | ✗ |
| Add Workspace Member | ✓ | ✗ |
| Create Task | ✓ | ✓ (in assigned projects) |
| Edit Task | ✓ | ✓ (assignee or project member) |
| Delete Task | ✓ | ✗ |
| View Analytics | ✓ | ✓ (assigned projects) |

**Implementation:**
```javascript
// Middleware example
async function requireWorkspaceAdmin(req, res, next) {
  const { workspaceId } = req.params;
  const userId = req.user.id;

  const member = await db.workspaceMember.findFirst({
    where: { workspaceId, userId, role: 'ADMIN' }
  });

  if (!member) {
    return res.status(403).json({ error: 'Forbidden' });
  }

  next();
}
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
- comments

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
CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_tasks_due_date ON tasks(due_date);

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
