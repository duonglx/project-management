# Code Quality & Standards

## ESLint Configuration

### ESLint 9 Flat Config

**eslint.config.js:**
```javascript
import js from '@eslint/js';
import globals from 'globals';
import react from 'eslint-plugin-react';
import reactHooks from 'eslint-plugin-react-hooks';
import reactRefresh from 'eslint-plugin-react-refresh';

export default [
  { ignores: ['dist'] },
  {
    files: ['**/*.{js,jsx}'],
    languageOptions: {
      ecmaVersion: 2020,
      globals: globals.browser,
      parserOptions: {
        ecmaVersion: 'latest',
        ecmaFeatures: { jsx: true },
        sourceType: 'module',
      },
    },
    settings: { react: { version: '19.1' } },
    plugins: {
      react,
      'react-hooks': reactHooks,
      'react-refresh': reactRefresh,
    },
    rules: {
      ...js.configs.recommended.rules,
      ...react.configs.recommended.rules,
      ...react.configs['jsx-runtime'].rules,
      ...reactHooks.configs.recommended.rules,
      'react/jsx-no-target-blank': 'off',
      'react-refresh/only-export-components': [
        'warn',
        { allowConstantExport: true },
      ],
    },
  },
];
```

### Linting Rules

**1. Run Linting:**
```bash
npm run lint
```

**2. Key Rules Enforced:**
- React Hooks rules (dependencies, order)
- React best practices (keys, prop-types)
- JavaScript best practices (no-unused-vars, etc.)
- React Refresh rules (HMR compatibility)

**3. Ignored Patterns:**
- `dist/` directory (build output)
- `node_modules/` (automatically ignored)

### Code Formatting

**Currently no Prettier configured. Recommended setup:**

**Install Prettier:**
```bash
npm install -D prettier eslint-config-prettier
```

**.prettierrc (recommended):**
```json
{
  "semi": true,
  "singleQuote": true,
  "tabWidth": 2,
  "trailingComma": "es5",
  "printWidth": 80,
  "arrowParens": "avoid"
}
```

**Format command:**
```bash
npx prettier --write "src/**/*.{js,jsx,css,md}"
```

## Testing Standards (Future)

### Recommended Testing Stack

**Unit/Component Testing:**
- **Framework:** Vitest (Vite-native)
- **Library:** React Testing Library
- **Coverage:** v8 (built into Vitest)

**E2E Testing:**
- **Framework:** Playwright or Cypress

**Setup Example:**
```bash
npm install -D vitest @testing-library/react @testing-library/jest-dom
```

**vitest.config.js:**
```javascript
import { defineConfig } from 'vitest/config';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  test: {
    environment: 'jsdom',
    globals: true,
    setupFiles: './src/test/setup.js',
  },
});
```

### Testing Patterns (Future)

**Component Test Example:**
```javascript
import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import ProjectCard from './ProjectCard';

describe('ProjectCard', () => {
  it('renders project name', () => {
    render(<ProjectCard project={{ name: 'Test Project' }} />);
    expect(screen.getByText('Test Project')).toBeInTheDocument();
  });
});
```

**Redux Test Example:**
```javascript
import { describe, it, expect } from 'vitest';
import workspaceReducer, { addProject } from './workspaceSlice';

describe('workspaceSlice', () => {
  it('adds project to workspace', () => {
    const initialState = { workspaces: [] };
    const project = { id: '1', name: 'New Project' };
    const newState = workspaceReducer(initialState, addProject(project));
    expect(newState.workspaces).toContainEqual(project);
  });
});
```

## Git Standards

### Commit Message Format

**Conventional Commits:**
```
<type>(<scope>): <subject>

<body>

<footer>
```

**Types:**
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, no logic change)
- `refactor`: Code refactoring
- `perf`: Performance improvements
- `test`: Adding or updating tests
- `chore`: Build/tooling changes

**Examples:**
```
feat(tasks): add task filtering by status

fix(calendar): correct date calculation for task due dates

docs: update README with new setup instructions

refactor(components): extract common badge component
```

### Branch Naming

**Pattern:** `<type>/<short-description>`

**Examples:**
- `feat/task-comments`
- `fix/calendar-navigation`
- `refactor/redux-slices`
- `docs/api-documentation`

### Pull Request Guidelines

1. Clear title and description
2. Link to related issues
3. Include screenshots for UI changes
4. Ensure CI passes (linting, tests)
5. Request reviews from maintainers

## Security Best Practices

See [Security Standards](./security-standards.md) for detailed authentication, authorization, and data protection patterns.

### Quick Reference

**Frontend:**
- JWT stored in httpOnly cookies (automatic by API client)
- usePermission hook for checking access
- ProtectedRoute for authentication
- PermissionGate component for granular access

**Backend:**
- Spring Security with JWT filter
- @PreAuthorize for method-level security
- PermissionService for RBAC checks
- Password hashing via BCrypt (12 rounds)

**Dependencies:**
- Regular `npm audit` checks
- Update dependencies promptly
- Review dependency licenses

## Accessibility Standards

### WCAG 2.1 AA Compliance (Target)

**1. Keyboard Navigation:**
- All interactive elements keyboard accessible
- Visible focus indicators
- Logical tab order

**2. Color Contrast:**
- Text contrast ratio ≥ 4.5:1 (normal text)
- Text contrast ratio ≥ 3:1 (large text)
- Test with tools (e.g., axe DevTools)

**3. Semantic HTML:**
- Use proper heading hierarchy (h1 → h6)
- Use semantic elements (nav, main, section, article)
- Use buttons for actions, links for navigation

**4. ARIA Labels:**
```jsx
<button aria-label="Close dialog" onClick={onClose}>
  <X size={20} />
</button>

<input
  type="search"
  aria-label="Search tasks"
  placeholder="Search..."
/>
```

**5. Form Accessibility:**
- Associate labels with inputs
- Provide error messages
- Use fieldsets for grouped inputs

**6. Images:**
- Provide alt text for images
- Use empty alt for decorative images

## Documentation Standards

### Code Comments

**1. When to Comment:**
- Complex algorithms or logic
- Non-obvious business rules
- Workarounds or hacks (with explanation)
- TODOs for future improvements

**2. When NOT to Comment:**
- Obvious code (let code be self-documenting)
- Redundant information
- Commented-out code (delete instead)

**3. Comment Style:**
```javascript
// Single-line comment for brief explanations

/**
 * Multi-line comment for function documentation
 * @param {string} projectId - The project identifier
 * @returns {Object} The project object or null
 */
function getProject(projectId) {
  // Implementation
}

// TODO: Add pagination for large task lists
// FIXME: Calendar view not handling DST correctly
// NOTE: This assumes projects array is always sorted by name
```

## Deployment Standards

### Build for Production

```bash
npm run build
```

**Checklist:**
- Remove console.logs (or use environment-based logging)
- Set production environment variables
- Test production build locally (`npm run preview`)
- Verify bundle size (check `dist/` folder)
- Run lighthouse audit

### Deployment Platforms

**Recommended (Frontend):**
- **Vercel:** Zero-config deployment for Vite/React
- **Netlify:** Alternative with similar features
- **GitHub Pages:** For static hosting

**Future (Backend):**
- **Railway:** Node.js + PostgreSQL hosting
- **Render:** Alternative full-stack hosting
- **AWS/GCP/Azure:** Enterprise-scale deployment

### Environment-Specific Configuration

**Development:**
```
VITE_API_URL=http://localhost:3000/api
VITE_ENV=development
```

**Production:**
```
VITE_API_URL=https://api.example.com
VITE_ENV=production
```

## Maintenance & Updates

### Dependency Updates

**Regular Updates:**
```bash
# Check for outdated packages
npm outdated

# Update all dependencies (carefully)
npm update

# Update specific package
npm update react react-dom
```

**Major Version Updates:**
- Read changelogs and migration guides
- Test thoroughly before deploying
- Update one major dependency at a time

### Security Audits

```bash
# Run security audit
npm audit

# Fix vulnerabilities automatically (if safe)
npm audit fix

# Force fix (may introduce breaking changes)
npm audit fix --force
```

### Code Reviews

**Review Checklist:**
- [ ] Code follows established patterns
- [ ] No console.logs or debug code
- [ ] Proper error handling
- [ ] Accessible UI components
- [ ] Responsive design
- [ ] Performance considerations
- [ ] Tests added/updated (future)
- [ ] Documentation updated (if needed)

## Troubleshooting

### Common Issues

**1. Vite Build Errors:**
- Clear cache: `rm -rf node_modules/.vite`
- Reinstall: `rm -rf node_modules package-lock.json && npm install`

**2. ESLint Errors:**
- Disable specific rules if needed (add comment)
- Fix automatically: `npm run lint -- --fix`

**3. Tailwind Classes Not Working:**
- Check Tailwind plugin is in `vite.config.js`
- Verify `@import "tailwindcss"` in `index.css`
- Restart dev server

**4. Redux State Not Updating:**
- Check reducer is properly exported and imported
- Verify action is dispatched correctly
- Use Redux DevTools to inspect state

**5. Dark Mode Not Working:**
- Verify `dark` class is on `<html>` element
- Check dark mode classes are applied correctly
- Test theme toggle in Redux DevTools

## Tools & Resources

### Tools
- [ESLint](https://eslint.org/)
- [Prettier](https://prettier.io/)
- [Redux DevTools](https://github.com/reduxjs/redux-devtools)
- [React DevTools](https://react.dev/learn/react-developer-tools)
- [axe DevTools](https://www.deque.com/axe/devtools/) (Accessibility)

### Community
- [GitHub Discussions](https://github.com/GreatStackDev/project-management/discussions)
- [GitHub Issues](https://github.com/GreatStackDev/project-management/issues)
- See CONTRIBUTING.md and CODE_OF_CONDUCT.md
