# Frontend Architecture

## Application Structure

```
React Application
├── Routing Layer (React Router)
│   ├── Route Configuration (App.jsx)
│   └── Navigation Management
│
├── UI Layer (Components & Pages)
│   ├── Layout Components (Sidebar, Navbar)
│   ├── Page Components (Dashboard, Projects, Team, etc.)
│   ├── Feature Components (ProjectCard, TaskList, etc.)
│   └── Dialog Components (Modals, Forms)
│
├── State Management Layer (Redux Toolkit)
│   ├── Global State (Store)
│   ├── Feature Slices (workspace, theme)
│   ├── Actions & Reducers
│   └── Selectors
│
├── Data Layer
│   ├── Dummy Data (assets.js)
│   └── Future: API Client (axios/fetch)
│
└── Utilities
    ├── Date Formatting (date-fns)
    ├── Notifications (react-hot-toast)
    └── Charts (recharts)
```

## Frontend Architecture

### Application Structure

```
React Application
├── Routing Layer (React Router)
│   ├── Route Configuration (App.jsx)
│   └── Navigation Management
│
├── UI Layer (Components & Pages)
│   ├── Layout Components (Sidebar, Navbar)
│   ├── Page Components (Dashboard, Projects, Team, etc.)
│   ├── Feature Components (ProjectCard, TaskList, etc.)
│   └── Dialog Components (Modals, Forms)
│
├── State Management Layer (Redux Toolkit)
│   ├── Global State (Store)
│   ├── Feature Slices (workspace, theme)
│   ├── Actions & Reducers
│   └── Selectors
│
├── Data Layer
│   ├── Dummy Data (assets.js)
│   └── Future: API Client (axios/fetch)
│
└── Utilities
    ├── Date Formatting (date-fns)
    ├── Notifications (react-hot-toast)
    └── Charts (recharts)
```

### Component Architecture

#### Component Hierarchy

```
App (Route Definitions)
└── Layout (Application Shell)
    ├── Sidebar (Navigation)
    │   └── Navigation Items (Dashboard, Projects, Team)
    │
    ├── Navbar (Top Bar)
    │   ├── WorkspaceDropdown (Workspace Switcher)
    │   ├── Theme Toggle (Light/Dark Mode)
    │   └── User Menu (Future)
    │
    └── Outlet (Page Content Area)
        │
        ├── Dashboard Page
        │   ├── StatsGrid (Project/Task Metrics)
        │   ├── ProjectOverview (Recent Projects)
        │   │   └── ProjectCard × N
        │   ├── RecentActivity (Activity Feed)
        │   └── TasksSummary (Task Status Breakdown)
        │
        ├── Projects Page
        │   ├── ProjectCard × N (Project Grid)
        │   └── CreateProjectDialog (Modal)
        │
        ├── ProjectDetails Page
        │   ├── Tab Navigation (Overview/Tasks/Calendar/Analytics/Settings)
        │   ├── ProjectOverview (Tab Content)
        │   ├── ProjectTasks (Tab Content)
        │   │   ├── Task Filters & Search
        │   │   ├── Task Table/List
        │   │   └── CreateTaskDialog
        │   ├── ProjectCalendar (Tab Content)
        │   ├── ProjectAnalytics (Tab Content)
        │   │   └── Charts (Recharts)
        │   └── ProjectSettings (Tab Content)
        │
        ├── Team Page
        │   ├── Member Grid
        │   ├── InviteMemberDialog
        │   └── AddProjectMember
        │
        └── TaskDetails Page
            ├── Task Information Display
            └── Action Buttons (Edit/Delete)
```

#### Component Communication Patterns

**1. Parent-Child (Props Down):**
```javascript
// Parent passes data and callbacks to child
<ProjectCard
  project={projectData}
  onClick={handleProjectClick}
/>
```

**2. Child-Parent (Callbacks Up):**
```javascript
// Child invokes parent callback
<CreateProjectDialog
  isOpen={isOpen}
  onClose={() => setIsOpen(false)}
  onCreate={(project) => handleCreateProject(project)}
/>
```

**3. Sibling Communication (Redux):**
```javascript
// Sibling A dispatches action
dispatch(updateTask({ taskId, updates }));

// Sibling B subscribes to state
const tasks = useSelector(state => state.workspace.tasks);
```

**4. Global State Access:**
```javascript
// Any component can access global state
const currentWorkspace = useSelector(state => state.workspace.currentWorkspace);
const theme = useSelector(state => state.theme.mode);
```

### State Management Architecture

#### Redux Store Structure

```javascript
{
  workspace: {
    workspaces: [
      {
        id: 'ws-1',
        name: 'Workspace Name',
        slug: 'workspace-slug',
        description: 'Description',
        settings: {},
        ownerId: 'user-1',
        projects: [
          {
            id: 'proj-1',
            name: 'Project Name',
            description: 'Description',
            priority: 'HIGH',
            status: 'IN_PROGRESS',
            start_date: '2024-01-01',
            end_date: '2024-12-31',
            team_lead: 'user-1',
            workspaceId: 'ws-1',
            progress: 45,
            team: [
              { id: 'user-1', name: 'User Name', email: 'user@example.com', ... }
            ],
            tasks: [
              {
                id: 'task-1',
                title: 'Task Title',
                description: 'Description',
                status: 'TODO',
                type: 'TASK',
                priority: 'MEDIUM',
                assigneeId: 'user-1',
                due_date: '2024-06-30',
                projectId: 'proj-1'
              }
            ]
          }
        ]
      }
    ],
    currentWorkspace: { ... } // Reference to selected workspace
  },

  theme: {
    mode: 'light' // or 'dark'
  }
}
```

#### State Update Flow

```
User Action (Click, Form Submit)
        │
        ▼
Event Handler in Component
        │
        ▼
Dispatch Redux Action
        │
        ▼
Redux Reducer (via createSlice)
        │
        ├─► Immer creates draft state
        ├─► Reducer mutates draft (looks mutable)
        └─► Immer produces new immutable state
        │
        ▼
Redux Store Updated
        │
        ▼
Connected Components Re-render (useSelector)
        │
        ▼
UI Updates with New Data
```

#### Redux Slice Organization

**workspaceSlice.js (109 LOC):**
- **State:** workspaces array, currentWorkspace
- **Reducers:**
  - Workspace CRUD: `setCurrentWorkspace`, `addWorkspace`, `updateWorkspace`, `deleteWorkspace`
  - Project CRUD: `addProject`, `updateProject`, `deleteProject`
  - Task CRUD: `addTask`, `updateTask`, `deleteTask`
  - Member management: `addMemberToProject`, `removeMemberFromProject`

**themeSlice.js (32 LOC):**
- **State:** mode ('light' or 'dark')
- **Reducers:** `toggleTheme`
- **Side Effects:** Updates localStorage, modifies document root class

### Routing Architecture

#### Route Configuration

```javascript
// App.jsx - Route Definitions
<Routes>
  <Route path="/" element={<Layout />}>
    <Route index element={<Dashboard />} />
    <Route path="projects" element={<Projects />} />
    <Route path="projectsDetail" element={<ProjectDetails />} />
    <Route path="team" element={<Team />} />
    <Route path="taskDetails" element={<TaskDetails />} />
  </Route>
</Routes>
```

#### Navigation Flow

```
URL Change (User clicks link or navigates)
        │
        ▼
React Router Matches Route
        │
        ▼
Layout Component Renders (Persistent Shell)
        │
        ├─► Sidebar (Always visible)
        ├─► Navbar (Always visible)
        └─► Outlet (Matched route component)
        │
        ▼
Route Component Mounts
        │
        ├─► Reads Redux state (workspaces, projects, tasks)
        ├─► Reads URL params (if any)
        └─► Renders UI
```

#### Route Parameters & Query Strings (Future)

**Current:** No URL params used (data passed via state)
**Future:** Use URL params for deep linking

```javascript
// Planned route structure
<Route path="workspace/:workspaceId" />
<Route path="workspace/:workspaceId/project/:projectId" />
<Route path="workspace/:workspaceId/project/:projectId/task/:taskId" />

// Query string for filters
/projects?status=active&priority=high
```

### Data Flow Architecture

#### Current Data Flow (Dummy Data)

```
1. Application Bootstrap
   ├─► main.jsx initializes app
   ├─► Redux store created
   └─► workspaceSlice loads dummy data from assets.js

2. Component Mount
   ├─► Component reads state via useSelector
   ├─► Renders UI with dummy data
   └─► No API calls

3. User Interaction (e.g., Create Task)
   ├─► User fills form and submits
   ├─► Component dispatches addTask action
   ├─► Redux reducer updates state (in-memory)
   ├─► Components re-render with new state
   └─► Toast notification shows success

4. Page Refresh
   └─► All data resets to initial dummy data (no persistence)
```

#### Future Data Flow (API Integration)

```
1. Application Bootstrap
   ├─► main.jsx initializes app
   ├─► Check for auth token (localStorage/cookie)
   ├─► If authenticated: Fetch user data
   └─► Fetch workspaces for current user

2. Component Mount
   ├─► Component reads state via useSelector
   ├─► If data missing: Dispatch async thunk (API call)
   ├─► Loading state shown while fetching
   ├─► Data returned and stored in Redux
