# Redux Toolkit Conventions

## Slice Structure

```javascript
import { createSlice } from '@reduxjs/toolkit';

const featureSlice = createSlice({
  name: 'feature',
  initialState: {
    data: [],
    loading: false,
    error: null,
  },
  reducers: {
    // Sync actions
    actionName(state, action) {
      // Immer-powered immutable updates
      state.data.push(action.payload);
    },
    resetState(state) {
      state.data = [];
      state.error = null;
    },
  },
  // Future: extraReducers for async thunks
});

export const { actionName, resetState } = featureSlice.actions;
export default featureSlice.reducer;
```

## Redux Conventions

### 1. Slice Naming

- **File:** `featureSlice.js` (camelCase + "Slice" suffix)
- **Slice name:** `'feature'` (lowercase, matches store key)
- **Reducer export:** default export

### 2. Action Naming

- **Verb-based:** `addProject`, `updateTask`, `deleteWorkspace`
- **Present tense, action-oriented**
- **Auto-generated** by `createSlice`

### 3. State Shape

- **Normalized when possible** (flat, indexed by ID)
- **Avoid deep nesting**
- **Keep related data together**

### 4. Selectors

- Define reusable selectors in slice file (future)
- Use `useSelector` in components
- Derive state in selectors, not components

### 5. Immutability

- Redux Toolkit uses Immer (write "mutable" code, auto-immutable)
- Direct mutations allowed in reducers: `state.value = newValue`
- Don't return new state, mutate draft state

## Store Configuration

```javascript
// src/app/store.js
import { configureStore } from '@reduxjs/toolkit';
import workspaceReducer from '../features/workspaceSlice';
import themeReducer from '../features/themeSlice';

export const store = configureStore({
  reducer: {
    workspace: workspaceReducer,
    theme: themeReducer,
    // Add more slices here
  },
});
```

## Example: Workspace Slice

**Current Implementation (src/features/workspaceSlice.js):**

```javascript
import { createSlice } from '@reduxjs/toolkit';
import { workspaces } from '../assets/assets';

const workspaceSlice = createSlice({
  name: 'workspace',
  initialState: {
    workspaces: workspaces,
    currentWorkspace: workspaces[0],
  },
  reducers: {
    setCurrentWorkspace(state, action) {
      state.currentWorkspace = state.workspaces.find(
        ws => ws.id === action.payload
      );
    },
    addWorkspace(state, action) {
      state.workspaces.push(action.payload);
    },
    updateWorkspace(state, action) {
      const { id, updates } = action.payload;
      const workspace = state.workspaces.find(ws => ws.id === id);
      if (workspace) {
        Object.assign(workspace, updates);
      }
    },
    deleteWorkspace(state, action) {
      state.workspaces = state.workspaces.filter(
        ws => ws.id !== action.payload
      );
    },
    addProject(state, action) {
      const { workspaceId, project } = action.payload;
      const workspace = state.workspaces.find(ws => ws.id === workspaceId);
      if (workspace) {
        workspace.projects.push(project);
      }
    },
    updateProject(state, action) {
      const { projectId, updates } = action.payload;
      // Find and update project
    },
    deleteProject(state, action) {
      // Remove project from workspace
    },
    addTask(state, action) {
      const { projectId, task } = action.payload;
      // Add task to project
    },
    updateTask(state, action) {
      const { taskId, updates } = action.payload;
      // Update task
    },
    deleteTask(state, action) {
      // Remove task
    },
  },
});

export const {
  setCurrentWorkspace,
  addWorkspace,
  updateWorkspace,
  deleteWorkspace,
  addProject,
  updateProject,
  deleteProject,
  addTask,
  updateTask,
  deleteTask,
} = workspaceSlice.actions;

export default workspaceSlice.reducer;
```

## Best Practices

### 1. Keep Slices Focused

Each slice should manage a specific domain of state:
- `workspaceSlice` - workspace and project data
- `themeSlice` - UI theme preferences
- `authSlice` (future) - authentication state
- `uiSlice` (future) - UI state (modals, sidebars, etc.)

### 2. Avoid Nested State

**Bad:**
```javascript
{
  workspace: {
    data: {
      current: {
        projects: {
          items: []
        }
      }
    }
  }
}
```

**Good:**
```javascript
{
  workspace: {
    workspaces: [],
    currentWorkspace: {},
  }
}
```

### 3. Use Immer's Draft State

```javascript
// Good - Immer handles immutability
reducers: {
  addItem(state, action) {
    state.items.push(action.payload);
  },
  updateItem(state, action) {
    const item = state.items.find(i => i.id === action.payload.id);
    if (item) {
      item.name = action.payload.name;
    }
  }
}
```

### 4. Selector Patterns (Future)

```javascript
// Define in slice file
export const selectCurrentWorkspace = state => state.workspace.currentWorkspace;
export const selectProjects = state => state.workspace.currentWorkspace?.projects || [];
export const selectProjectById = (state, projectId) =>
  state.workspace.currentWorkspace?.projects.find(p => p.id === projectId);

// Use in components
const workspace = useSelector(selectCurrentWorkspace);
const projects = useSelector(selectProjects);
const project = useSelector(state => selectProjectById(state, projectId));
```

### 5. Async Operations (Future with RTK Query)

```javascript
import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';

export const fetchProjects = createAsyncThunk(
  'workspace/fetchProjects',
  async (workspaceId) => {
    const response = await api.get(`/workspaces/${workspaceId}/projects`);
    return response.data;
  }
);

const workspaceSlice = createSlice({
  name: 'workspace',
  initialState: {
    projects: [],
    loading: false,
    error: null,
  },
  reducers: {
    // sync reducers
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchProjects.pending, (state) => {
        state.loading = true;
      })
      .addCase(fetchProjects.fulfilled, (state, action) => {
        state.loading = false;
        state.projects = action.payload;
      })
      .addCase(fetchProjects.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      });
  },
});
```

## Common Patterns

### Toggle Boolean State

```javascript
toggleSidebar(state) {
  state.sidebarOpen = !state.sidebarOpen;
}
```

### Update Object Property

```javascript
updateSettings(state, action) {
  state.settings = {
    ...state.settings,
    ...action.payload,
  };
}
```

### Filter Array

```javascript
removeItem(state, action) {
  state.items = state.items.filter(item => item.id !== action.payload);
}
```

### Find and Update

```javascript
updateItem(state, action) {
  const { id, updates } = action.payload;
  const item = state.items.find(i => i.id === id);
  if (item) {
    Object.assign(item, updates);
  }
}
```

### Reset State

```javascript
resetState(state) {
  return initialState; // Return new state object
}
```

## Testing Redux (Future)

```javascript
import { describe, it, expect } from 'vitest';
import workspaceReducer, { addProject } from './workspaceSlice';

describe('workspaceSlice', () => {
  it('adds project to workspace', () => {
    const initialState = {
      workspaces: [{ id: 'ws-1', projects: [] }],
      currentWorkspace: { id: 'ws-1', projects: [] },
    };

    const project = { id: 'proj-1', name: 'New Project' };
    const action = addProject({ workspaceId: 'ws-1', project });

    const newState = workspaceReducer(initialState, action);

    expect(newState.workspaces[0].projects).toContainEqual(project);
  });
});
```

## Resources

- [Redux Toolkit Docs](https://redux-toolkit.js.org/)
- [Redux Style Guide](https://redux.js.org/style-guide/)
- [RTK Query](https://redux-toolkit.js.org/rtk-query/overview) (future API integration)
