# Codebase Summary

## Overview

Frontend-only SPA project management platform built with React 19, Vite 7, and Tailwind CSS 4. All data currently handled via Redux Toolkit with dummy data. No backend integration yet.

**Total Lines of Code:** ~3,695 LOC (src directory)
**Files:** 30+ component/page files
**Architecture:** Component-based SPA with Redux state management

## Directory Structure

```
project-management/
├── src/                        # Application source code (3695 LOC)
│   ├── App.jsx                 # Route definitions (27 LOC)
│   ├── main.jsx                # App entry point (13 LOC)
│   ├── index.css               # Global styles (67 LOC)
│   ├── app/                    # Redux configuration
│   │   └── store.js            # Store setup (9 LOC)
│   ├── assets/                 # Static assets and dummy data
│   │   ├── assets.js           # Mock data (459 LOC)
│   │   ├── schema.prisma       # DB schema reference (146 LOC)
│   │   └── images/             # SVG/PNG assets
│   ├── components/             # Reusable components (18 files, ~2400 LOC)
│   ├── features/               # Redux slices
│   │   ├── workspaceSlice.js   # Workspace state (109 LOC)
│   │   └── themeSlice.js       # Theme state (32 LOC)
│   └── pages/                  # Route pages (6 files)
├── public/                     # Static public assets
├── docs/                       # Project documentation
├── .claude/                    # Claude Code configuration
├── plans/                      # Development plans and reports
├── index.html                  # SPA entry HTML
├── vite.config.js              # Vite configuration
├── eslint.config.js            # ESLint 9 flat config
├── package.json                # Dependencies and scripts
└── tailwind.config.js          # Tailwind CSS configuration (if exists)
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
