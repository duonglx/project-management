# Tech Stack & File Organization

## Core Technologies

### Production Dependencies
- **Framework:** React 19.1.1
- **Build Tool:** Vite 7.1.2
- **Language:** JavaScript (ES6+, no TypeScript)
- **State Management:** Redux Toolkit 2.8.2 + React Redux 9.2.0
- **Routing:** React Router DOM 7.8.1
- **Styling:** Tailwind CSS 4.1.12
- **Icons:** Lucide React 0.540.0
- **Charts:** Recharts 3.1.2
- **Date Utilities:** date-fns 4.1.0
- **Notifications:** react-hot-toast 2.6.0

### Development Tools
- **Linting:** ESLint 9.33.0 (flat config)
- **Module System:** ES Modules (type: "module")
- **Package Manager:** npm (recommended)
- **Node Version:** Compatible with Vite 7 (Node 18+)

## File Naming Conventions

### Current State (Existing Files)
The codebase currently uses **PascalCase** for component files:
- Components: `ProjectCard.jsx`, `CreateTaskDialog.jsx`
- Pages: `Dashboard.jsx`, `Team.jsx`, `Layout.jsx`
- Redux: `workspaceSlice.js`, `themeSlice.js` (camelCase)
- Assets: `assets.js` (camelCase)

### Recommended for New Files
While existing files use PascalCase, new **non-component** files should use **kebab-case** for better LLM tool compatibility:

**JavaScript/JSX Files:**
- **Utilities:** `kebab-case.js` (e.g., `date-formatter.js`, `api-client.js`)
- **Hooks:** `kebab-case.js` (e.g., `use-local-storage.js`, `use-debounce.js`)
- **Services:** `kebab-case.js` (e.g., `auth-service.js`, `api-service.js`)
- **Components (existing pattern):** `PascalCase.jsx` (maintain consistency)
- **Redux slices:** `camelCaseSlice.js` (maintain consistency)

**Configuration Files:**
- `kebab-case.js`: `vite.config.js`, `eslint.config.js`

**Documentation:**
- `kebab-case.md`: `code-standards.md`, `project-overview-pdr.md`

### File Organization Rules
1. One component per file (named export or default export)
2. Related components in same directory (e.g., dialogs, cards)
3. Utility functions in separate files (not inline in components)
4. Constants in dedicated files (e.g., `constants.js`)
5. Keep files under 200 LOC (split if larger)

## Directory Structure Standards

### Current Structure
```
src/
├── app/              # Redux store configuration
├── assets/           # Static assets, images, dummy data
├── components/       # Reusable UI components
├── features/         # Redux slices (feature-based organization)
├── pages/            # Route page components
├── main.jsx          # Application entry point
├── App.jsx           # Route definitions
└── index.css         # Global styles
```

### Future Additions (Recommended)
```
src/
├── hooks/            # Custom React hooks
├── utils/            # Utility functions, helpers
├── services/         # API services, external integrations
├── constants/        # Application constants, enums
├── types/            # Type definitions (if TypeScript added)
└── lib/              # Third-party library configurations
```

## Component Categories

**1. Page Components (`src/pages/`):**
- Top-level route components
- Compose multiple smaller components
- Fetch/manage page-level state
- Handle routing logic

**2. Feature Components (`src/components/`):**
- Reusable UI components
- Can be stateful or stateless
- Should be composable
- Examples: `ProjectCard`, `CreateTaskDialog`

**3. Layout Components:**
- Structural components (e.g., `Layout`, `Sidebar`, `Navbar`)
- Provide consistent app structure
- Handle global UI state (theme, navigation)

## Build & Development Standards

### NPM Scripts

```json
{
  "scripts": {
    "dev": "vite",                    // Start dev server
    "build": "vite build",            // Production build
    "preview": "vite preview",        // Preview production build
    "lint": "eslint ."                // Run linting
  }
}
```

### Environment Variables

**Vite Environment Variables:**
- Prefix with `VITE_` to expose to client
- Define in `.env` files (not in repo)
- Access via `import.meta.env.VITE_VARIABLE_NAME`

**Example .env:**
```
VITE_API_URL=http://localhost:3000/api
VITE_APP_NAME=Project Management
```

**Usage:**
```javascript
const apiUrl = import.meta.env.VITE_API_URL;
```

### Build Output

**Production Build:**
```bash
npm run build
```

**Output:** `dist/` directory
- Optimized and minified
- Code splitting
- Asset hashing for cache busting

**Preview Build:**
```bash
npm run preview
```
- Serves production build locally
- Test before deployment

## Data Management Standards

### Dummy Data Structure

**src/assets/assets.js:**
```javascript
export const workspaces = [
  {
    id: 'ws-1',
    name: 'Workspace Name',
    slug: 'workspace-slug',
    description: 'Description',
    settings: {},
    ownerId: 'user-1',
    projects: [/* project objects */],
  }
];

export const projects = [
  {
    id: 'proj-1',
    name: 'Project Name',
    description: 'Description',
    priority: 'HIGH', // LOW, MEDIUM, HIGH
    status: 'IN_PROGRESS', // NOT_STARTED, IN_PROGRESS, COMPLETED, ON_HOLD
    start_date: '2024-01-01',
    end_date: '2024-12-31',
    team_lead: 'user-1',
    workspaceId: 'ws-1',
    progress: 45, // 0-100
    team: [/* member objects */],
    tasks: [/* task objects */],
  }
];

export const tasks = [
  {
    id: 'task-1',
    title: 'Task Title',
    description: 'Description',
    status: 'TODO', // TODO, IN_PROGRESS, DONE
    type: 'TASK', // TASK, BUG, FEATURE, IMPROVEMENT, OTHER
    priority: 'MEDIUM', // LOW, MEDIUM, HIGH
    assigneeId: 'user-1',
    due_date: '2024-06-30',
    projectId: 'proj-1',
  }
];

export const members = [
  {
    id: 'user-1',
    name: 'User Name',
    email: 'user@example.com',
    image: '/path/to/avatar.png',
    role: 'ADMIN', // ADMIN, MEMBER
  }
];
```

### Future API Integration

**Placeholder for API service pattern:**
```javascript
// src/services/api-service.js (future)
const API_BASE_URL = import.meta.env.VITE_API_URL;

export const api = {
  get: async (endpoint) => {
    const response = await fetch(`${API_BASE_URL}${endpoint}`);
    if (!response.ok) throw new Error('API request failed');
    return response.json();
  },

  post: async (endpoint, data) => {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    });
    if (!response.ok) throw new Error('API request failed');
    return response.json();
  },

  // put, delete, etc.
};
```

## Resources

### Documentation Links
- [React Docs](https://react.dev/)
- [Vite Guide](https://vite.dev/guide/)
- [Redux Toolkit Docs](https://redux-toolkit.js.org/)
- [Tailwind CSS Docs](https://tailwindcss.com/docs)
- [React Router Docs](https://reactrouter.com/)
- [Lucide Icons](https://lucide.dev/icons/)
- [Recharts Documentation](https://recharts.org/)
- [date-fns Documentation](https://date-fns.org/)

### Tools
- [ESLint](https://eslint.org/)
- [Prettier](https://prettier.io/)
- [Redux DevTools](https://github.com/reduxjs/redux-devtools)
- [React DevTools](https://react.dev/learn/react-developer-tools)
