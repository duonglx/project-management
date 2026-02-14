# Codebase Summary

## Overview

Full-stack project management platform with Spring Boot backend (Java 17+, PostgreSQL) and React 19 frontend. Implements JWT-based authentication with httpOnly cookies and 3-level RBAC permission system (workspace > project > task). Built with Vite 7 and Tailwind CSS 4.

**Frontend LOC:** ~4,000+ LOC (src directory with auth/RBAC)
**Backend:** Spring Boot microservices with Spring Security, Prisma ORM
**Database:** PostgreSQL with migrations
**Architecture:** REST API backend with component-based React SPA, Redux state management

## Directory Structure

```
project-management/
├── src/                        # Frontend React application (4000+ LOC)
│   ├── App.jsx                 # Route definitions with protected routes
│   ├── main.jsx                # App entry point
│   ├── index.css               # Global styles
│   ├── app/                    # Redux store configuration
│   │   └── store.js            # Store setup with auth + workspace slices
│   ├── assets/                 # Static assets and data
│   │   ├── assets.js           # Mock data
│   │   ├── schema.prisma       # Prisma schema reference
│   │   └── images/             # SVG/PNG assets
│   ├── components/             # Reusable UI components (25+ files, ~2800 LOC)
│   │   ├── ProtectedRoute.jsx  # JWT authentication wrapper
│   │   ├── PermissionGate.jsx  # Permission-based component gating
│   │   ├── settings-layout.jsx # Settings sidebar layout
│   │   ├── settings/           # Settings-specific components
│   │   │   └── color-preset-picker.jsx  # Color selection component
│   │   ├── role-tab-bar.jsx    # Settings navigation tabs
│   │   ├── role-permission-panel.jsx   # Role permissions display
│   │   ├── task/custom-field-renderer.jsx  # Type-specific field inputs
│   │   └── ...                 # Other UI components
│   ├── features/               # Redux slices for state management
│   │   ├── auth-slice.js       # Auth state (login, logout, permissions)
│   │   ├── workspaceSlice.js   # Workspace/project/task state
│   │   └── themeSlice.js       # Theme state
│   ├── hooks/                  # Custom React hooks
│   │   ├── use-permission.js   # Permission checking utilities
│   │   └── use-auth.js         # Auth utilities
│   ├── services/               # API client services
│   │   ├── auth-api.js         # Auth endpoints
│   │   └── api-client.js       # HTTP client with JWT handling
│   ├── pages/                  # Route pages (10+ files)
│   │   ├── LoginPage.jsx       # Login form page
│   │   ├── Dashboard.jsx       # Main dashboard
│   │   ├── settings/           # Workspace settings pages
│   │   │   ├── general-settings-page.jsx
│   │   │   ├── labels-settings-page.jsx
│   │   │   ├── statuses-settings-page.jsx
│   │   │   ├── members-settings-page.jsx
│   │   │   ├── custom-fields-settings-page.jsx
│   │   │   └── danger-zone-page.jsx
│   │   └── ...                 # Other pages
│   └── utils/                  # Utility functions
├── backend/                    # Spring Boot Java backend
│   ├── src/main/java/com/shbvn/jms/
│   │   ├── controller/         # REST API endpoints
│   │   │   ├── AuthController.java       # /api/auth/* endpoints
│   │   │   ├── WorkspaceController.java  # Workspace CRUD
│   │   │   ├── ProjectController.java    # Project CRUD
│   │   │   ├── TaskController.java       # Task CRUD
│   │   │   ├── LabelController.java      # Workspace labels CRUD
│   │   │   ├── TaskStatusController.java # Custom task statuses CRUD + reorder
│   │   │   ├── CustomFieldController.java    # Custom fields CRUD + reorder
│   │   │   ├── RolePermissionController.java  # Permission management
│   │   │   └── AdminController.java      # Admin endpoints
│   │   ├── service/            # Business logic
│   │   │   ├── AuthService.java          # JWT token generation & validation
│   │   │   ├── PermissionService.java    # RBAC permission checks
│   │   │   ├── WorkspaceService.java     # Workspace operations
│   │   │   ├── ProjectService.java       # Project operations
│   │   │   ├── TaskService.java          # Task operations
│   │   │   ├── LabelService.java         # Label management
│   │   │   ├── TaskStatusService.java    # Status management
│   │   │   ├── CustomFieldService.java   # Custom fields management
│   │   │   └── UserService.java          # User management
│   │   ├── security/           # Spring Security implementation
│   │   │   ├── JwtService.java           # JWT token creation & parsing
│   │   │   ├── JwtAuthenticationFilter.java  # JWT filter for requests
│   │   │   ├── CustomUserDetailsService.java # User loading from DB
│   │   │   ├── CustomUserDetails.java    # User details with ID
│   │   │   └── PermissionEvaluator.java  # Custom @PreAuthorize evaluator
│   │   ├── config/             # Spring configuration
│   │   │   ├── SecurityConfig.java       # Security filter chain setup
│   │   │   └── CorsConfig.java           # CORS configuration
│   │   ├── model/              # JPA entities
│   │   │   ├── User.java
│   │   │   ├── Workspace.java
│   │   │   ├── WorkspaceMember.java
│   │   │   ├── Project.java
│   │   │   ├── ProjectMember.java
│   │   │   ├── Task.java
│   │   │   ├── TaskStatus.java           # Custom task statuses
│   │   │   ├── Label.java                # Workspace labels
│   │   │   ├── TaskLabel.java            # Task-Label junction
│   │   │   ├── CustomFieldDefinition.java # Custom field definitions
│   │   │   ├── CustomFieldValue.java     # Task custom field values
│   │   │   ├── Permission.java           # Permission entity
│   │   │   ├── RolePermission.java       # Role-to-Permission mapping
│   │   │   ├── RefreshToken.java         # Refresh token storage
│   │   │   ├── Comment.java              # Task comments
│   │   │   └── enums/CustomFieldType.java # Custom field type enum
│   │   ├── repository/         # Database access layer
│   │   │   ├── UserRepository.java
│   │   │   ├── WorkspaceMemberRepository.java
│   │   │   ├── ProjectRepository.java
│   │   │   ├── TaskRepository.java
│   │   │   ├── LabelRepository.java      # Label queries
│   │   │   ├── TaskStatusRepository.java # Status queries
│   │   │   ├── CustomFieldDefinitionRepository.java  # Field definition queries
│   │   │   ├── CustomFieldValueRepository.java       # Field value queries
│   │   │   ├── PermissionRepository.java
│   │   │   ├── RolePermissionRepository.java
│   │   │   ├── RefreshTokenRepository.java
│   │   │   └── CommentRepository.java
│   │   ├── dto/                # Data transfer objects
│   │   │   ├── request/        # Request DTOs
│   │   │   │   ├── LoginRequest.java
│   │   │   │   ├── UpdateRolePermissionsRequest.java
│   │   │   │   ├── CreateCustomFieldRequest.java
│   │   │   │   ├── UpdateCustomFieldRequest.java
│   │   │   │   ├── ReorderCustomFieldsRequest.java
│   │   │   │   ├── UpdateCustomFieldValuesRequest.java
│   │   │   │   ├── AddMemberRequest.java
│   │   │   │   └── UpdateMemberRoleRequest.java
│   │   │   └── response/       # Response DTOs
│   │   │       ├── AuthResponse.java     # Login response with tokens
│   │   │       ├── AuthMeResponse.java   # Current user + permissions
│   │   │       ├── RolePermissionResponse.java
│   │   │       ├── CustomFieldDefinitionResponse.java
│   │   │       ├── CustomFieldValueResponse.java
│   │   │       ├── WorkspaceMemberResponse.java
│   │   │       └── ProjectMemberResponse.java
│   │   └── mapper/             # MapStruct entity-DTO mappers
│   │       ├── WorkspaceMemberMapper.java
│   │       └── ProjectMemberMapper.java
│   ├── src/main/resources/
│   │   ├── application.yml     # Server config, JWT secrets
│   │   └── db/migration/       # Flyway/Liquibase migrations
│   ├── pom.xml                 # Maven dependencies
│   └── target/                 # Build output
├── docs/                       # Project documentation
├── plans/                      # Development plans
├── index.html                  # SPA entry HTML
├── vite.config.js              # Vite configuration
├── eslint.config.js            # ESLint 9 flat config
├── package.json                # Frontend dependencies
└── tsconfig.json               # TypeScript config (if applicable)
```

## File Descriptions

### Entry Points

**`index.html`** (root)
- SPA entry point
- Mounts React app to `<div id="root"></div>`
- Includes Vite script injection

**`src/main.jsx`** (13 LOC)
- Application bootstrap
- Wraps app with `BrowserRouter` and Redux `Provider`
- Renders root `App` component
- Imports global styles

**`src/App.jsx`** (27 LOC)
- Route configuration using React Router v7
- Defines application routes:
  - `/` → Dashboard
  - `/team` → Team
  - `/projects` → Projects
  - `/projectsDetail` → ProjectDetails
  - `/taskDetails` → TaskDetails
- All routes wrapped in `Layout` component

**`src/index.css`** (67 LOC)
- Global Tailwind directives (`@import "tailwindcss"`)
- Custom CSS variables for theme colors
- Base styles and utility classes
- Dark mode overrides

### Redux State Management

**`src/app/store.js`** (9 LOC)
- Configures Redux store with `configureStore`
- Registers reducers:
  - `workspace`: workspaceSlice
  - `theme`: themeSlice
- Exports typed hooks (future TypeScript preparation)

**`src/features/workspaceSlice.js`** (109 LOC)
- **Initial State:**
  - `workspaces`: Array of workspace objects (from dummy data)
  - `currentWorkspace`: Currently selected workspace object

- **Reducers:**
  - `setCurrentWorkspace(id)`: Switch active workspace
  - `addWorkspace(workspace)`: Create new workspace
  - `updateWorkspace({id, updates})`: Update workspace properties
  - `deleteWorkspace(id)`: Remove workspace
  - `addProject({workspaceId, project})`: Create project in workspace
  - `updateProject({projectId, updates})`: Update project
  - `deleteProject(projectId)`: Remove project
  - `addTask({projectId, task})`: Create task in project
  - `updateTask({taskId, updates})`: Update task
  - `deleteTask(taskId)`: Remove task
  - `addMemberToProject({projectId, member})`: Add team member
  - `removeMemberFromProject({projectId, memberId})`: Remove member

**`src/features/themeSlice.js`** (32 LOC)
- **Initial State:**
  - `mode`: 'light' or 'dark' (loaded from localStorage)

- **Reducers:**
  - `toggleTheme()`: Switch between light/dark modes
  - Persists preference to localStorage
  - Updates document root class for Tailwind dark mode

### Assets & Data

**`src/assets/assets.js`** (459 LOC)
- **Dummy Data Exports:**
  - `workspaces`: Array of 2 sample workspaces
  - `projects`: Array of 6 projects with full metadata
  - `tasks`: Array of 30+ tasks across projects
  - `members`: Array of 8 team members with profiles

- **Data Structure:**
  - Workspaces: id, name, slug, description, settings, ownerId
  - Projects: id, name, description, priority, status, dates, team_lead, workspaceId, progress, team (members array)
  - Tasks: id, title, description, status, type, priority, assigneeId, due_date, projectId
  - Members: id, name, email, image, role

**`src/assets/schema.prisma`** (146 LOC)
- Prisma schema defining future backend data model
- **Models:** User, Workspace, WorkspaceMember, Project, ProjectMember, Task, Comment
- **Enums:** WorkspaceRole, TaskStatus, TaskType, ProjectStatus, Priority
- Defines relationships and constraints
- Reference for future API development

**`src/assets/images/`**
- SVG and PNG assets
- Logo, icons, illustrations
- Empty state graphics

### Pages (6 files)

**`src/pages/Layout.jsx`** (38 LOC)
- Root layout component wrapping all pages
- Structure: `Sidebar + (Navbar + main content area)`
- Uses `Outlet` for nested routes
- Responsive layout with mobile considerations
- Applies dark mode classes from theme state

**`src/pages/Dashboard.jsx`** (44 LOC)
- Landing page showing workspace overview
- Components used:
  - `StatsGrid`: Key metrics (projects, tasks, completion rate)
  - `ProjectOverview`: Recent projects cards
  - `RecentActivity`: Activity feed
  - `TasksSummary`: Task status breakdown
- Responsive grid layout

**`src/pages/Projects.jsx`** (111 LOC)
- Projects listing page for current workspace
- Features:
  - Header with workspace name and "Create Project" button
  - Project grid with `ProjectCard` components
  - Empty state when no projects
- Displays project count
- Opens `CreateProjectDialog` modal

**`src/pages/ProjectDetails.jsx`** (139 LOC)
- Project detail page with tabbed interface
- Tabs: Overview, Tasks, Calendar, Analytics, Settings
- State management:
  - Active tab tracking
  - Project data from Redux (matched by URL param)
- Tab components:
  - Overview: `ProjectOverview` (project cards view)
  - Tasks: `ProjectTasks` (task list with filters)
  - Calendar: `ProjectCalendar` (calendar view)
  - Analytics: `ProjectAnalytics` (charts)
  - Settings: `ProjectSettings` (project configuration)
- Breadcrumb navigation
- Responsive tab switching

**`src/pages/Team.jsx`** (207 LOC)
- Team members management page
- Features:
  - Members count header
  - "Invite Member" button (opens `InviteMemberDialog`)
  - Member grid display
  - Member cards with:
    - Avatar, name, email
    - Role badge
    - Projects count
    - "View Profile" button (placeholder)
- Responsive grid (1-3 columns based on screen size)
- Empty state handling

**`src/pages/TaskDetails.jsx`** (180 LOC)
- Task detail view with full task information
- Sections:
  - Header: Task title, status badge, type badge
  - Assignee info with avatar
  - Due date display
  - Priority indicator
  - Full description
  - Project breadcrumb link
  - Action buttons: Edit, Delete, Back to Project
- Uses Lucide icons throughout
- Responsive layout
- Integrates with Redux for task data

### Components (18 files, ~2400 LOC)

#### Navigation Components

**`src/components/Sidebar.jsx`** (57 LOC)
- Main application sidebar
- Navigation items:
  - Dashboard (Home icon)
  - Projects (Folder icon)
  - Team (Users icon)
- Highlights active route
- Dark mode styling support
- Responsive (collapsible on mobile - future)

**`src/components/Navbar.jsx`** (53 LOC)
- Top navigation bar
- Components:
  - Left: Menu button (mobile), Workspace name
  - Right: Theme toggle, User avatar dropdown
- `WorkspaceDropdown` integration
- Theme toggle button with Sun/Moon icons
- Responsive padding and layout

**`src/components/WorkspaceDropdown.jsx`** (89 LOC)
- Workspace switcher dropdown
- Features:
  - Current workspace display
  - Workspace list with click to switch
  - Active workspace indicator (checkmark)
  - Close on selection
- Uses Redux `setCurrentWorkspace` action
- Click-outside handling to close dropdown
- Responsive positioning

#### Dashboard Components

**`src/components/StatsGrid.jsx`** (110 LOC)
- Dashboard statistics cards grid
- Metrics calculated from Redux state:
  - Total projects count
  - Total tasks count
  - Completed tasks count
  - Completion rate percentage
- Card structure:
  - Icon (Folder, CheckSquare, Users, TrendingUp)
  - Label and value
  - Color-coded backgrounds
- Responsive grid (1-4 columns)

**`src/components/ProjectOverview.jsx`** (108 LOC)
- Recent projects grid for dashboard
- Shows first 6 projects from current workspace
- Project cards display:
  - Project name and description
  - Status badge (color-coded)
  - Priority badge
  - Progress bar with percentage
  - Team avatars (first 3 members)
  - Dates (start - end)
- "View All Projects" link
- Responsive grid layout

**`src/components/RecentActivity.jsx`** (97 LOC)
- Activity feed component
- Mock activities:
  - Task created/updated
  - Member added
  - Project status changed
- Activity item structure:
  - User avatar
  - Activity description
  - Timestamp (formatted with date-fns)
- Scrollable list (max-height)
- Empty state message

**`src/components/TasksSummary.jsx`** (92 LOC)
- Task status summary widget
- Calculates task counts by status:
  - TODO (tasks pending)
  - IN_PROGRESS (active tasks)
  - DONE (completed tasks)
- Visual design:
  - Status icon with color coding
  - Count and label
  - Percentage of total
- Responsive grid layout

#### Project Components

**`src/components/ProjectCard.jsx`** (50 LOC)
- Individual project card component
- Displays:
  - Project name and description (truncated)
  - Status and priority badges
  - Progress bar
  - Team member avatars (up to 3, with +N overflow)
  - Click to navigate to project details
- Hover effects
- Responsive card sizing

**`src/components/ProjectTasks.jsx`** (283 LOC)
- Task list with filtering and search
- Features:
  - Search input (filters by title)
  - Filter dropdowns:
    - Status (All, TODO, IN_PROGRESS, DONE)
    - Priority (All, Low, Medium, High)
    - Assignee (All, or specific member)
    - Type (All, Task, Bug, Feature, Improvement, Other)
  - "Create Task" button
  - Task table/list:
    - Checkbox (future bulk actions)
    - Task title (clickable to details)
    - Status badge
    - Priority badge
    - Assignee avatar and name
    - Due date
    - Type badge
    - Actions (Edit, Delete icons)
- Filtering logic applied to task array
- Responsive table (horizontal scroll on mobile)
- Empty state when no tasks

**`src/components/ProjectCalendar.jsx`** (189 LOC)
- Calendar view for project tasks
- Features:
  - Month navigation (prev/next)
  - Current month/year header
  - Calendar grid (7x6)
  - Day cells with:
    - Day number
    - Task indicators (colored dots)
    - Task count badge
  - Tasks due on selected day displayed below calendar
  - Task list: title, status, priority, assignee
- Uses date-fns for date manipulation
- Responsive calendar grid
- Current day highlighting

**`src/components/ProjectAnalytics.jsx`** (180 LOC)
- Project analytics dashboard with charts
- Charts (using Recharts):
  - **Tasks by Status** (PieChart)
    - TODO, IN_PROGRESS, DONE distribution
    - Color-coded segments
  - **Tasks by Type** (BarChart)
    - Task, Bug, Feature, Improvement, Other counts
    - Vertical bars with labels
  - **Tasks by Priority** (BarChart)
    - Low, Medium, High distribution
    - Horizontal bars
  - **Task Completion Timeline** (AreaChart)
    - Tasks completed over time (mock data)
    - Gradient area fill
- Responsive charts (adjust height/width for mobile)
- Grid layout (2x2 on desktop, stacked on mobile)

**`src/components/ProjectSettings.jsx`** (130 LOC)
- Project settings management form
- Editable fields:
  - Project name
  - Description
  - Status (dropdown)
  - Priority (dropdown)
  - Start date
  - End date
  - Team lead (dropdown from project members)
  - Progress (slider 0-100%)
- Action buttons:
  - Save Changes
  - Delete Project
  - Cancel
- Form state management (controlled inputs)
- Dispatches Redux `updateProject` action
- Delete confirmation (future modal)
- Responsive form layout

#### Team Components

**`src/components/AddProjectMember.jsx`** (78 LOC)
- Add member to project dialog
- Features:
  - Member selection dropdown (workspace members not in project)
  - Add button
  - Success toast notification
- Uses Redux:
  - `addMemberToProject` action
  - Reads workspace members and project members
- Closes dialog after adding
- Empty state when all members added

**`src/components/InviteMemberDialog.jsx`** (73 LOC)
- Invite new member to workspace modal
- Form fields:
  - Email input
  - Role selection (Admin/Member radio buttons)
- Validation:
  - Email required
  - Valid email format
- Actions:
  - Send Invite button
  - Cancel button
- Mock invite functionality (no API)
- Toast notification on success
- Responsive modal design

**`src/components/MyTasksSidebar.jsx`** (91 LOC)
- User's tasks sidebar (right side)
- Features:
  - Tasks grouped by status:
    - TODO
    - IN_PROGRESS
    - DONE
  - Status section headers with count badges
  - Task items:
    - Checkbox (toggle status)
    - Task title
    - Project name (small text)
    - Due date (if exists)
  - Scrollable task list
- Filters tasks assigned to current user (hardcoded user ID for demo)
- Checkbox click updates task status via Redux
- Collapsible sections
- Responsive width

#### Dialog Components

**`src/components/CreateProjectDialog.jsx`** (157 LOC)
- Create new project modal
- Form fields:
  - Project name (required)
  - Description (textarea)
  - Status (dropdown)
  - Priority (dropdown)
  - Start date
  - End date
- Validation:
  - Name required (shows error)
- Actions:
  - Create Project button
  - Cancel button
- Dispatches Redux `addProject` action
- Generates unique ID (timestamp-based)
- Toast notification on success
- Resets form after creation
- Modal overlay with backdrop blur

**`src/components/CreateTaskDialog.jsx`** (120 LOC)
- Create new task modal
- Form fields:
  - Task title (required)
  - Description (textarea)
  - Type (dropdown)
  - Priority (dropdown)
  - Status (dropdown)
  - Assignee (dropdown from project members)
  - Due date
- Validation:
  - Title required
- Actions:
  - Create Task button
  - Cancel button
- Dispatches Redux `addTask` action
- Toast notification on success
- Resets form after creation
- Modal styling consistent with project dialog

**`src/components/ProjectsSidebar.jsx`** (76 LOC)
- Projects list sidebar component
- Features:
  - "My Projects" header
  - Project list from current workspace
  - Each project:
    - Folder icon
    - Project name
    - Click to navigate to project details
  - Active project highlighting
  - "+ New Project" button at bottom
- Scrollable project list
- Empty state when no projects
- Responsive width

## JWT Authentication System

### Overview
JWT-based authentication with httpOnly cookie storage for enhanced security. Access tokens (15 min expiry) + refresh tokens (7 day expiry) with rotation mechanism.

### Backend Components

**JwtService.java** - Token generation and validation
- `generateAccessToken(userDetails)`: Creates short-lived access token with userId claim
- `generateRefreshToken(userDetails)`: Creates long-lived refresh token
- `extractUsername(token)`: Parses username from token claims
- `extractUserId(token)`: Extracts userId from token
- `isTokenValid(token, userDetails)`: Validates signature and expiration
- Token signing with HS512 algorithm (HMAC secret from config)

**JwtAuthenticationFilter.java** - Request interceptor
- Extracts JWT from Authorization header or httpOnly cookies
- Validates token and loads user from CustomUserDetailsService
- Sets SecurityContext for @PreAuthorize evaluation
- Passes unauthenticated requests to next filter

**CustomUserDetailsService.java** - User loader
- Loads User entity from database by username
- Converts to Spring SecurityContext-compatible CustomUserDetails
- Handles user not found exceptions

**SecurityConfig.java** - Spring Security setup
- CORS configuration (localhost:5173 allowed)
- CSRF disabled for stateless API
- Session creation disabled (JWT stateless)
- JwtAuthenticationFilter registered before UsernamePasswordAuthenticationFilter
- @PreAuthorize enabled for method-level security
- Public endpoints: /api/auth/login, /api/auth/refresh, /api/health

**AuthService.java** - Authentication business logic
- `login(request, response)`: Validates credentials, generates tokens, sets httpOnly cookies
- `refresh(request, response)`: Validates refresh token, issues new access token
- `logout(request, response)`: Clears refresh token from database and cookies
- Token rotation strategy: invalidate old refresh tokens

### Frontend Components

**auth-slice.js** - Redux auth state
- `login`: Async thunk dispatching loginApi
- `fetchCurrentUser`: Loads user + permissions from /api/auth/me
- `logout`: Calls logoutApi
- State: user, permissions, isAuthenticated, status, error, activeWorkspaceId

**ProtectedRoute.jsx** - Route wrapper
- Checks isAuthenticated status on mount
- Dispatches fetchCurrentUser if needed
- Shows loading spinner while verifying auth
- Redirects to /login if authentication fails

**useAuth.js** - Auth hook (future)
- Returns user, isAuthenticated, loading state
- Provides login/logout/refresh functions

**LoginPage.jsx** - Login form
- Email/password inputs
- Submits to /api/auth/login via auth-api service
- Stores JWT in httpOnly cookies automatically
- Redirects to workspace dashboard on success

**auth-api.js** - API service layer
- `loginApi(username, password)`: POST /api/auth/login
- `fetchMeApi(workspaceId)`: GET /api/auth/me?workspaceId={id}
- `logoutApi()`: POST /api/auth/logout
- Auto-includes JWT from httpOnly cookies in all requests

### API Endpoints

```
POST /api/auth/login
  Request: { username, password }
  Response: { user, accessToken, refreshToken }

GET /api/auth/me?workspaceId={id}
  Response: { user, permissions }

POST /api/auth/refresh
  Response: { accessToken, refreshToken }

POST /api/auth/logout
  Response: {}
```

## RBAC Permission System (3-Level)

### Architecture

**Level 1: Workspace Permissions**
- OWNER: Full control (manages workspace, projects, members)
- ADMIN: Manage workspace (projects, members, but can't delete workspace)
- MEMBER: Limited (view, create tasks in assigned projects)

**Level 2: Project Permissions**
- LEAD: Manage project (edit, delete, assign members)
- MEMBER: Create/edit own tasks, view project

**Level 3: Task Permissions**
- ASSIGNEE: Edit own task
- CREATOR: Edit own task
- PROJECT_MEMBER: View task

### Backend Components

**Permission.java** - Permission entity
- Fields: id, name (e.g., "workspace:create_project"), description
- Persisted in database, managed by admins

**RolePermission.java** - Role-to-Permission mapping
- Fields: id, role (enum), workspaceId (nullable for global defaults), permission_id
- workspaceId=null = system default (all workspaces)
- workspaceId=ABC = workspace-specific override

**PermissionService.java** - Core permission logic
- `getPermissionsForRole(role, workspaceId)`: Returns Set<String> of permission names
  - Checks workspace overrides first, falls back to system defaults
  - Cacheable for performance
- `hasPermission(userId, workspaceId, permissionName)`: Boolean check
  - OWNER bypasses all checks
  - Returns false if user not member
- `hasProjectPermission(userId, workspaceId, projectId, permissionName)`: Checks project role
  - Workspace ADMIN has implicit project permissions
  - Workspace OWNER bypasses all checks
- `getUserPermissions(userId, workspaceId)`: Returns List<String> of all permissions for user
- `isMember(userId, workspaceId)`: Quick membership check
- `updateRolePermissions(workspaceId, role, permissionNames)`: Admin-only update with cache invalidation

**PermissionEvaluator.java** - Custom evaluator for @PreAuthorize
- Implements Spring's PermissionEvaluator interface
- Called by `@PreAuthorize("@perm.hasPermission(...)")` expressions
- Integrates PermissionService into method-level security

**AdminService.java** - Admin operations
- `updateRolePermissions()`: Update workspace-specific role permissions
- Cache invalidation on permission changes

### API Endpoints

```
GET /api/workspaces/{workspaceId}/role-permissions
  Response: { roles: [{ role, permissions: [...] }] }

PUT /api/admin/workspaces/{workspaceId}/role-permissions
  Request: { role, permissionNames: [...] }
  Response: { message: "Updated" }

GET /api/admin/permissions
  Response: [{ id, name, description }, ...]
```

### Frontend Components

**usePermission.js** - Permission hook
- `permissions`: Array of permission strings from auth state
- `has(perm)`: Boolean check for single permission
- `hasAny(perms)`: Boolean check for any permission in array
- `hasAll(perms)`: Boolean check for all permissions in array
- Memoized for performance

**PermissionGate.jsx** - Conditional rendering (future)
- Wraps content requiring specific permission
- Props: permission/permissions, fallback component
- Example: `<PermissionGate permission="workspace:edit_project">Edit button</PermissionGate>`

**RoleManagement.jsx** - Admin role config UI (future)
- Lists roles (OWNER, ADMIN, MEMBER)
- Shows permissions for each role
- Allows updating workspace-specific overrides
- Calls PUT /api/admin/workspaces/{id}/role-permissions

**AdminUsersPage.jsx** - User management (future)
- Lists workspace members by role
- Change member roles
- Remove members
- Calls API endpoints for management

### Permission Caching

- Redis cache key: `rolePermissions:{role}:{workspaceId}`
- TTL: 5 minutes (configurable)
- Cache invalidated on permission updates
- Fallback to database if cache miss

## Component Hierarchy

```
App
└── Layout
    ├── Sidebar
    ├── Navbar
    │   ├── WorkspaceDropdown
    │   └── ThemeToggle
    └── Outlet (page content)
        ├── Dashboard
        │   ├── StatsGrid
        │   ├── ProjectOverview
        │   │   └── ProjectCard (multiple)
        │   ├── RecentActivity
        │   └── TasksSummary
        ├── Projects
        │   ├── ProjectCard (multiple)
        │   └── CreateProjectDialog
        ├── ProjectDetails
        │   ├── ProjectOverview
        │   ├── ProjectTasks
        │   │   └── CreateTaskDialog
        │   ├── ProjectCalendar
        │   ├── ProjectAnalytics
        │   └── ProjectSettings
        ├── Team
        │   ├── InviteMemberDialog
        │   └── AddProjectMember
        └── TaskDetails
```

## Data Flow

### State Management Flow

1. **Application Bootstrap:**
   - `main.jsx` initializes Redux store
   - `themeSlice` loads theme from localStorage
   - `workspaceSlice` loads dummy data from `assets.js`
   - App renders with initial state

2. **Workspace Operations:**
   - User selects workspace via `WorkspaceDropdown`
   - Dispatches `setCurrentWorkspace(id)`
   - All components reading `currentWorkspace` re-render
   - Projects/tasks filtered by workspace

3. **CRUD Operations:**
   - User action (create/update/delete) triggers event
   - Component dispatches Redux action with payload
   - Reducer updates state immutably
   - Connected components re-render with new data

4. **Navigation:**
   - React Router handles route changes
   - `Outlet` component renders matched route
   - Route components read relevant Redux state
   - URL params used to filter data (projectId, taskId)

### Component Communication

- **Parent → Child:** Props passing (project data, callbacks)
- **Child → Parent:** Event callbacks (onClose, onCreate)
- **Sibling → Sibling:** Redux state (shared via selectors)
- **Global State:** Redux Toolkit slices (workspace, theme)

## Key Dependencies

### Production Dependencies
- **react** (19.1.1): UI library
- **react-dom** (19.1.1): DOM rendering
- **react-router-dom** (7.8.1): Routing
- **@reduxjs/toolkit** (2.8.2): State management
- **react-redux** (9.2.0): React-Redux bindings
- **tailwindcss** (4.1.12): Styling framework
- **@tailwindcss/vite** (4.1.12): Vite plugin for Tailwind
- **lucide-react** (0.540.0): Icon library
- **recharts** (3.1.2): Chart library
- **date-fns** (4.1.0): Date utilities
- **react-hot-toast** (2.6.0): Toast notifications

### Development Dependencies
- **vite** (7.1.2): Build tool and dev server
- **@vitejs/plugin-react** (5.0.0): Vite React plugin
- **eslint** (9.33.0): Linting
- **@eslint/js** (9.33.0): ESLint core rules
- **eslint-plugin-react-hooks** (5.2.0): React Hooks linting
- **eslint-plugin-react-refresh** (0.4.20): React Refresh linting
- **globals** (16.3.0): Global identifiers

## Build Configuration

### Vite Config
- Uses `@vitejs/plugin-react` for React support
- Uses `@tailwindcss/vite` for Tailwind CSS
- Dev server on port 5173
- Build output to `dist/`

### ESLint Config (Flat Config)
- ESLint 9 flat config format
- React Hooks rules enforced
- React Refresh rules for HMR
- Browser globals enabled
- Ignores `dist/` directory

## Code Statistics

### Lines of Code by Category
- **Pages:** ~719 LOC (6 files)
- **Components:** ~2,400 LOC (18 files)
- **Redux:** ~150 LOC (3 files)
- **Assets/Data:** ~605 LOC (2 files)
- **Styles:** ~67 LOC (1 file)
- **Entry/Config:** ~49 LOC (3 files)
- **Total:** ~3,695 LOC

### File Size Distribution
- **Large files (150+ LOC):**
  - ProjectTasks.jsx (283)
  - Team.jsx (207)
  - ProjectCalendar.jsx (189)
  - TaskDetails.jsx (180)
  - ProjectAnalytics.jsx (180)
  - CreateProjectDialog.jsx (157)

- **Medium files (50-150 LOC):**
  - ProjectDetails.jsx (139)
  - ProjectSettings.jsx (130)
  - CreateTaskDialog.jsx (120)
  - Projects.jsx (111)
  - StatsGrid.jsx (110)
  - workspaceSlice.js (109)
  - ProjectOverview.jsx (108)

- **Small files (<50 LOC):**
  - Most utility components and config files

### Complexity Analysis
- **High complexity:** ProjectTasks (filtering, search, CRUD)
- **Medium complexity:** Project/Task detail pages, Analytics
- **Low complexity:** Simple display components, cards, sidebars

## Patterns & Conventions

### Component Patterns
- Functional components with Hooks
- `useState` for local component state
- `useSelector` for Redux state access
- `useDispatch` for Redux actions
- `useNavigate` for programmatic navigation
- `useParams` for route parameters

### State Management Patterns
- Redux Toolkit slices with `createSlice`
- Immer-powered immutable updates
- Selector functions for derived state
- Action creators auto-generated
- Thunks not used (no async operations yet)

### Styling Patterns
- Tailwind utility classes
- Dark mode via `dark:` variants
- Responsive breakpoints (sm, md, lg, xl)
- Custom color palette (blue primary)
- Consistent spacing scale

### File Naming
- **Components:** PascalCase (`ProjectCard.jsx`)
- **Pages:** PascalCase (`Dashboard.jsx`)
- **Redux:** camelCase (`workspaceSlice.js`)
- **Assets:** camelCase (`assets.js`)
- **Config:** kebab-case (`vite.config.js`)

## Phase 6: Enhanced Members Management (NEW)

### Members Settings Page

**File:** `src/pages/settings/members-settings-page.jsx`

Components:
- Member list with search and role filter
- Invite flow with email input
- Role change dropdown
- Member removal with confirmation
- Owner protection (cannot remove/change owner)

Backend Endpoints:
- `GET /api/workspaces/{wId}/members` - List members
- `POST /api/workspaces/{wId}/members` - Add member
- `PUT /api/workspaces/{wId}/members/{userId}` - Update role
- `DELETE /api/workspaces/{wId}/members/{userId}` - Remove member

Security Features:
- RBAC enforcement on all endpoints
- Owner role blocked in addMember
- OWNER cannot be removed
- workspace:manage_members permission required

### Backend Services

**WorkspaceService.java - Member Management:**
- addMember(workspaceId, email, role)
- updateMemberRole(workspaceId, userId, newRole)
- removeMember(workspaceId, userId)
- Includes owner protection logic
- Transaction-based for data consistency

---

## Phase 7: Custom Fields (NEW)

### Custom Fields Settings Page

**File:** `src/pages/settings/custom-fields-settings-page.jsx`

Features:
- Field definition CRUD (create, read, update, delete)
- Drag-to-reorder functionality
- Type-specific form (options for DROPDOWN)
- Required field toggle
- Visual type badges (6 colors)
- Max 20 fields per workspace limit

Field Types with Badges:
- TEXT: Gray badge
- NUMBER: Blue badge
- DROPDOWN: Purple badge with options editor
- DATE: Green badge
- CHECKBOX: Amber badge
- URL: Teal badge with link preview

### Custom Field Renderer Component

**File:** `src/components/task/custom-field-renderer.jsx`

Type-specific input rendering:
- TEXT: Text input (max 500 chars)
- NUMBER: Number input
- DROPDOWN: Select from predefined options
- DATE: Date picker input
- CHECKBOX: Toggle checkbox with label
- URL: URL input with external link button

Props:
- `field`: CustomFieldDefinition object
- `value`: Current field value
- `onChange`: Callback for value changes
- `disabled`: Read-only mode

### Backend Services

**CustomFieldService.java:**
- getDefinitions(workspaceId)
- createDefinition(workspaceId, definition)
- updateDefinition(workspaceId, fieldId, updates)
- deleteDefinition(workspaceId, fieldId)
- reorderDefinitions(workspaceId, request)
- getValuesForTask(taskId)
- updateValuesForTask(taskId, request)

**Validation Rules:**
- Max 20 definitions per workspace
- Field name uniqueness per workspace
- Type-specific validation (DROPDOWN options, URL format, etc.)
- Required field enforcement
- Task-field relationship uniqueness

### Data Model

**CustomFieldDefinition Entity:**
- id: UUID
- workspaceId: FK to Workspace
- name: String (max 100, unique per workspace)
- type: CustomFieldType enum (6 types)
- options: JSON array (for DROPDOWN)
- isRequired: Boolean
- position: Integer (for ordering)
- createdAt: DateTime
- updatedAt: DateTime

**CustomFieldValue Entity:**
- id: UUID
- taskId: FK to Task
- fieldId: FK to CustomFieldDefinition
- value: String/Text
- Constraints: Unique (taskId, fieldId)

**CustomFieldType Enum:**
```java
TEXT, NUMBER, DROPDOWN, DATE, CHECKBOX, URL
```

### API Endpoints

**Field Definitions:**
```
POST /api/workspaces/{wId}/custom-fields
GET /api/workspaces/{wId}/custom-fields
PUT /api/workspaces/{wId}/custom-fields/{fieldId}
DELETE /api/workspaces/{wId}/custom-fields/{fieldId}
PUT /api/workspaces/{wId}/custom-fields/reorder
```

**Field Values:**
```
GET /api/tasks/{taskId}/custom-fields
PUT /api/tasks/{taskId}/custom-fields
```

**Security:**
- workspace:manage_settings for definition CRUD
- isAuthenticated() for value endpoints
- task:edit permission for value updates

### Database Schema (Flyway V9)

**custom_field_definitions:**
- UUID primary key
- FK to workspaces (CASCADE delete)
- JSON column for options
- Indexes on workspace_id
- Unique constraint (workspace_id, name)

**custom_field_values:**
- UUID primary key
- FK to tasks (CASCADE delete)
- FK to custom_field_definitions (CASCADE delete)
- Indexes on task_id and field_id
- Unique constraint (task_id, field_id)

---

## Future Refactoring Opportunities

### Modularization Candidates
- **ProjectTasks.jsx (283 LOC):** Extract filter controls, task table
- **Team.jsx (207 LOC):** Extract member card grid, member card component
- **ProjectCalendar.jsx (189 LOC):** Extract calendar grid, day cell, task list

### Code Reuse Opportunities
- Common modal wrapper component
- Shared form input components
- Reusable badge component (status, priority, type)
- Common dropdown/select component
- Shared empty state component

### Performance Optimizations
- Memoize expensive computations (filtering, sorting)
- Use `React.memo` for pure components
- Implement virtualization for long lists
- Lazy load route components with `React.lazy`
- Code splitting for analytics charts

### Type Safety
- Consider TypeScript migration
- PropTypes for component props (currently none)
- TypeScript definitions for Redux state

## Testing Gaps

Currently no test files present. Recommended coverage:
- Unit tests for Redux reducers
- Component tests with React Testing Library
- Integration tests for critical flows
- E2E tests with Playwright/Cypress
- Visual regression tests for UI components
