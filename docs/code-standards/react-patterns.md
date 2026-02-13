# React Patterns & Best Practices

## Component Standards

### Component Structure

**Functional Components with Hooks:**
```javascript
import { useState, useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { SomeIcon } from 'lucide-react';

export default function ComponentName({ prop1, prop2, onAction }) {
  // 1. Hooks
  const [localState, setLocalState] = useState(initialValue);
  const reduxState = useSelector(state => state.feature.data);
  const dispatch = useDispatch();

  // 2. Effects
  useEffect(() => {
    // Side effects
  }, [dependencies]);

  // 3. Event Handlers
  const handleClick = () => {
    // Handler logic
  };

  // 4. Derived State / Computations
  const derivedValue = computeSomething(localState);

  // 5. Early Returns (if needed)
  if (!data) return <div>Loading...</div>;

  // 6. Render
  return (
    <div className="container">
      {/* JSX content */}
    </div>
  );
}
```

### Component Patterns

**1. Export Pattern:**
- **Default export** for single component per file (current pattern)
- Named exports for multiple related utilities

**2. Props Handling:**
- Destructure props in function signature
- Provide default values for optional props
- Use prop spreading sparingly (explicit props preferred)

**3. State Management:**
- Use `useState` for local component state
- Use Redux for shared/global state
- Lift state up when needed by multiple components

**4. Event Handlers:**
- Prefix with `handle` (e.g., `handleClick`, `handleSubmit`)
- Define inline for simple cases, separate function for complex logic
- Pass callbacks to child components (e.g., `onClose`, `onCreate`)

**5. Conditional Rendering:**
- Early returns for loading/error states
- Ternary operators for simple conditionals
- `&&` operator for conditional display
- Extract complex conditionals to variables

## Hooks Usage

### useState

```javascript
// Simple state
const [isOpen, setIsOpen] = useState(false);

// Object state
const [form, setForm] = useState({ name: '', email: '' });

// Array state
const [items, setItems] = useState([]);

// Lazy initialization for expensive computations
const [data, setData] = useState(() => computeInitialValue());
```

### useEffect

```javascript
// Run once on mount
useEffect(() => {
  fetchData();
}, []);

// Run when dependency changes
useEffect(() => {
  updateData(someValue);
}, [someValue]);

// Cleanup function
useEffect(() => {
  const timer = setTimeout(() => {}, 1000);
  return () => clearTimeout(timer);
}, []);
```

### useSelector (Redux)

```javascript
// Select slice of state
const workspace = useSelector(state => state.workspace.currentWorkspace);

// Select derived state
const projectCount = useSelector(state =>
  state.workspace.currentWorkspace?.projects?.length || 0
);

// Multiple selectors
const projects = useSelector(state => state.workspace.currentWorkspace?.projects);
const tasks = useSelector(state => /* ... */);
```

### useDispatch (Redux)

```javascript
const dispatch = useDispatch();

const handleCreate = () => {
  dispatch(addProject({ id: Date.now(), name: 'New Project' }));
};
```

### useNavigate (React Router)

```javascript
const navigate = useNavigate();

const handleClick = () => {
  navigate('/projects');
  // or
  navigate(-1); // Go back
};
```

### useParams (React Router)

```javascript
const { projectId, taskId } = useParams();
const project = projects.find(p => p.id === projectId);
```

## Performance Optimization

### Avoid Unnecessary Re-renders

```javascript
// Use React.memo for pure components (future)
const MemoizedComponent = React.memo(Component);

// Use useMemo for expensive computations
const filteredTasks = useMemo(() => {
  return tasks.filter(task => task.status === 'TODO');
}, [tasks]);

// Use useCallback for stable function references
const handleClick = useCallback(() => {
  doSomething(value);
}, [value]);
```

### Code Splitting

```javascript
// Lazy load routes (future)
const Dashboard = lazy(() => import('./pages/Dashboard'));

<Suspense fallback={<Loading />}>
  <Dashboard />
</Suspense>
```

### List Rendering

```javascript
// Always use keys for lists
{items.map(item => (
  <Item key={item.id} data={item} />
))}

// Avoid index as key (unless list is static)
```

## Error Handling

### Component Error Boundaries (Future)

```javascript
// Wrap components with error boundaries
<ErrorBoundary>
  <ComponentThatMightError />
</ErrorBoundary>
```

### Async Error Handling

```javascript
// Use try-catch for async operations (future API calls)
try {
  const data = await fetchData();
  setData(data);
} catch (error) {
  setError(error.message);
  toast.error('Failed to load data');
}
```

### Conditional Rendering

```javascript
// Handle loading and error states
if (loading) return <LoadingSpinner />;
if (error) return <ErrorMessage message={error} />;
if (!data) return <EmptyState />;

return <DataDisplay data={data} />;
```

## Authentication & Authorization Patterns

### Protected Routes with JWT

**App.jsx - Route Setup:**
```jsx
import { Routes, Route } from 'react-router-dom';
import ProtectedRoute from './components/ProtectedRoute';
import LoginPage from './pages/LoginPage';
import Layout from './pages/Layout';

function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route element={<ProtectedRoute />}>
        <Route path="/" element={<Layout />}>
          <Route index element={<Dashboard />} />
          <Route path="projects" element={<Projects />} />
          <Route path="w/:workspaceId/*" element={<WorkspaceLayout />} />
          <Route path="team" element={<Team />} />
        </Route>
      </Route>
    </Routes>
  );
}
```

**ProtectedRoute.jsx - Authentication Check:**
```jsx
import { useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { Navigate, Outlet } from 'react-router-dom';
import { fetchCurrentUser, selectIsAuthenticated, selectAuthStatus }
  from '../features/auth-slice';
import { Loader2Icon } from 'lucide-react';

export default function ProtectedRoute() {
  const dispatch = useDispatch();
  const isAuthenticated = useSelector(selectIsAuthenticated);
  const status = useSelector(selectAuthStatus);

  useEffect(() => {
    if (!isAuthenticated && status !== 'loading') {
      dispatch(fetchCurrentUser());  // Verify JWT session
    }
  }, [isAuthenticated, status, dispatch]);

  if (status === 'loading' || (status === 'idle' && !isAuthenticated)) {
    return (
      <div className="flex items-center justify-center h-screen bg-white dark:bg-zinc-950">
        <Loader2Icon className="size-7 text-blue-500 animate-spin" />
      </div>
    );
  }

  if (!isAuthenticated && status === 'failed') {
    return <Navigate to="/login" replace />;
  }

  return <Outlet />;
}
```

### Permission Checking

**usePermission Hook:**
```javascript
import { useMemo } from 'react';
import { useSelector } from 'react-redux';
import { selectPermissions } from '../features/auth-slice';

/**
 * Hook for checking user permissions in component logic
 * Returns permission set and helper methods
 */
export function usePermission() {
  const permissions = useSelector(selectPermissions);

  return useMemo(() => ({
    permissions,  // Array of permission strings
    has: (perm) => permissions.includes(perm),
    hasAny: (perms) => perms.some((p) => permissions.includes(p)),
    hasAll: (perms) => perms.every((p) => permissions.includes(p)),
  }), [permissions]);
}
```

**Usage in Components:**
```jsx
function ProjectActions({ projectId }) {
  const { has, hasAny } = usePermission();

  return (
    <div className="flex gap-2">
      {has('project:edit') && (
        <button onClick={() => editProject(projectId)}>Edit</button>
      )}
      {has('project:delete') && (
        <button onClick={() => deleteProject(projectId)}>Delete</button>
      )}
      {hasAny(['workspace:admin', 'project:settings']) && (
        <button onClick={() => openSettings()}>Settings</button>
      )}
    </div>
  );
}
```

**PermissionGate Component (future):**
```jsx
/**
 * Conditional rendering based on user permissions
 * Hides content if permission not granted
 */
export function PermissionGate({ permission, permissions, fallback, children }) {
  const { has, hasAll } = usePermission();

  // Check single permission
  if (permission && !has(permission)) {
    return fallback || null;
  }

  // Check multiple permissions (all required)
  if (permissions && !hasAll(permissions)) {
    return fallback || null;
  }

  return children;
}

// Usage:
<PermissionGate permission="workspace:delete">
  <DeleteWorkspaceButton />
</PermissionGate>

<PermissionGate permissions={['workspace:admin', 'audit:view']}>
  <AuditDashboard />
</PermissionGate>
```

### Auth State Management

**auth-slice.js - Redux Auth State:**
```javascript
import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { loginApi, fetchMeApi, logoutApi } from '../services/auth-api';

export const login = createAsyncThunk(
  'auth/login',
  async ({ username, password }) => {
    const data = await loginApi(username, password);
    return data;  // { user: {...}, permissions: [...] }
  }
);

export const fetchCurrentUser = createAsyncThunk(
  'auth/fetchCurrentUser',
  async (workspaceId) => {
    const data = await fetchMeApi(workspaceId);
    return data;  // { user: {...}, permissions: [...] }
  }
);

export const logout = createAsyncThunk(
  'auth/logout',
  async () => {
    await logoutApi();
  }
);

const initialState = {
  user: null,
  permissions: [],
  status: 'idle',  // idle | loading | succeeded | failed
  error: null,
  isAuthenticated: false,
  activeWorkspaceId: localStorage.getItem('lastWorkspaceId') || null,
};

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    clearAuth: (state) => {
      state.user = null;
      state.permissions = [];
      state.isAuthenticated = false;
      state.error = null;
    },
    setActiveWorkspaceId: (state, action) => {
      state.activeWorkspaceId = action.payload;
      localStorage.setItem('lastWorkspaceId', action.payload);
    },
  },
  extraReducers: (builder) => {
    builder
      // Login flow
      .addCase(login.pending, (state) => {
        state.status = 'loading';
        state.error = null;
      })
      .addCase(login.fulfilled, (state, action) => {
        state.status = 'succeeded';
        state.user = action.payload.user;
        state.isAuthenticated = true;
      })
      .addCase(login.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.error.message;
      })
      // Fetch current user (verify session)
      .addCase(fetchCurrentUser.fulfilled, (state, action) => {
        state.user = action.payload.user;
        state.permissions = action.payload.permissions || [];
        state.isAuthenticated = true;
        state.status = 'succeeded';
      })
      .addCase(fetchCurrentUser.rejected, (state) => {
        state.user = null;
        state.permissions = [];
        state.isAuthenticated = false;
        state.status = 'failed';
      })
      // Logout
      .addCase(logout.fulfilled, (state) => {
        state.user = null;
        state.permissions = [];
        state.isAuthenticated = false;
        state.status = 'idle';
      });
  },
});

export const { clearAuth, setActiveWorkspaceId } = authSlice.actions;

// Selectors
export const selectCurrentUser = (state) => state.auth.user;
export const selectIsAuthenticated = (state) => state.auth.isAuthenticated;
export const selectPermissions = (state) => state.auth.permissions;
export const selectAuthStatus = (state) => state.auth.status;

export default authSlice.reducer;
```

## Routing Standards

### Route Configuration

**src/App.jsx:**
```javascript
import { Routes, Route } from 'react-router-dom';

function App() {
  return (
    <Routes>
      <Route path="/" element={<Layout />}>
        <Route index element={<Dashboard />} />
        <Route path="projects" element={<Projects />} />
        <Route path="projectsDetail" element={<ProjectDetails />} />
        <Route path="team" element={<Team />} />
        <Route path="taskDetails" element={<TaskDetails />} />
      </Route>
    </Routes>
  );
}
```

### Nested Workspace URLs

**Nested Workspace URL Pattern:**
```
/w/:workspaceId/
├── dashboard
├── projects
├── projects/:projectId
│   ├── overview
│   ├── tasks
│   └── settings
├── team
└── settings
```

**Implementation:**
```jsx
// App.jsx
<Route path="/w/:workspaceId/*" element={<WorkspaceLayout />} />

// WorkspaceLayout.jsx
import { useParams, Routes, Route, Outlet } from 'react-router-dom';

export default function WorkspaceLayout() {
  const { workspaceId } = useParams();

  return (
    <Layout>
      <Routes>
        <Route index element={<WorkspaceDashboard />} />
        <Route path="projects" element={<Projects />} />
        <Route path="projects/:projectId/*" element={<ProjectLayout />} />
        <Route path="team" element={<Team />} />
        <Route path="settings" element={<WorkspaceSettings />} />
      </Routes>
    </Layout>
  );
}

// ProjectLayout.jsx
export default function ProjectLayout() {
  const { workspaceId, projectId } = useParams();

  return (
    <Routes>
      <Route index element={<ProjectOverview />} />
      <Route path="tasks" element={<ProjectTasks />} />
      <Route path="tasks/:taskId" element={<TaskDetails />} />
      <Route path="settings" element={<ProjectSettings />} />
    </Routes>
  );
}
```

**Benefits:**
- Workspace ID always available via useParams
- Clean URL structure matching backend API paths
- Easier permission checks (workspace context available)
- Better for analytics and user tracking

### Navigation Patterns

**1. Declarative Navigation:**
```jsx
import { Link } from 'react-router-dom';

<Link to="/projects">Go to Projects</Link>
<Link to={`/project/${id}`}>View Project</Link>
```

**2. Programmatic Navigation:**
```javascript
import { useNavigate } from 'react-router-dom';

const navigate = useNavigate();
navigate('/dashboard');
navigate(-1); // Go back
```

**3. URL Parameters:**
```javascript
// Define route with param
<Route path="project/:projectId" element={<ProjectDetails />} />

// Access in component
const { projectId } = useParams();
```

**4. Query Parameters:**
```javascript
// Set query params
navigate('/projects?filter=active&sort=name');

// Read query params
const [searchParams] = useSearchParams();
const filter = searchParams.get('filter');
```

## Performance Standards

### Build Performance

**Target Metrics:**
- Development server start: < 1 second
- HMR update: < 100ms
- Production build: < 30 seconds
- Bundle size (gzipped): < 500KB

### Runtime Performance

**Target Metrics (Lighthouse):**
- Performance: > 90
- Accessibility: > 90
- Best Practices: > 90
- SEO: > 90

**Core Web Vitals:**
- LCP (Largest Contentful Paint): < 2.5s
- FID (First Input Delay): < 100ms
- CLS (Cumulative Layout Shift): < 0.1

### Optimization Techniques

1. **Code Splitting:** Lazy load routes and heavy components
2. **Asset Optimization:** Compress images, use WebP format
3. **Tree Shaking:** Vite automatically removes unused code
4. **Memoization:** Use React.memo, useMemo, useCallback
5. **Virtualization:** For long lists (future implementation)

## Component Documentation

**Add brief descriptions to complex components:**
```javascript
/**
 * ProjectTasks component displays a filterable, searchable task list
 * with CRUD operations. Supports filtering by status, priority, assignee, and type.
 */
export default function ProjectTasks({ projectId }) {
  // Implementation
}
```
