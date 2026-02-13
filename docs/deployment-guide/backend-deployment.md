      - run: npm run lint

  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-node@v3
        with:
          node-version: '18'
      - run: npm ci
      - run: npm run test # (when tests added)

  build:
    runs-on: ubuntu-latest
    needs: [lint, test]
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-node@v3
        with:
          node-version: '18'
      - run: npm ci
      - run: npm run build
      - uses: actions/upload-artifact@v3
        with:
          name: dist
          path: dist/

  deploy:
    runs-on: ubuntu-latest
    needs: build
    if: github.ref == 'refs/heads/main'
    steps:
      - uses: actions/download-artifact@v3
        with:
          name: dist
      - name: Deploy to Vercel
        uses: amondnet/vercel-action@v25
        with:
          vercel-token: ${{ secrets.VERCEL_TOKEN }}
          vercel-org-id: ${{ secrets.VERCEL_ORG_ID }}
          vercel-project-id: ${{ secrets.VERCEL_PROJECT_ID }}
          vercel-args: '--prod'
```

**Required Secrets (GitHub Settings):**
- `VERCEL_TOKEN`
- `VERCEL_ORG_ID`
- `VERCEL_PROJECT_ID`

**Pipeline Flow:**
1. Code pushed to `main` or PR created
2. Linting runs (ESLint)
3. Tests run (when added)
4. Build created
5. Deploy to Vercel (only on `main` push)

### Deployment Checklist

**Before Every Deployment:**
- [ ] All tests pass locally
- [ ] Linting passes (`npm run lint`)
- [ ] Production build succeeds (`npm run build`)
- [ ] Preview build tested (`npm run preview`)
- [ ] Environment variables configured
- [ ] Database migrations ready (future)

**Production Deployment:**
- [ ] Merge PR to `main` (triggers auto-deploy)
- [ ] Monitor deployment logs
- [ ] Verify deployment success
- [ ] Smoke test production site
- [ ] Check error monitoring (Sentry, etc.)
- [ ] Monitor performance metrics

**Rollback Plan:**
- Vercel: Revert to previous deployment (one click)
- Manual: Deploy previous Git commit
- Emergency: Disable problematic feature via feature flag

---

## Performance Optimization

### Build Optimization

**Code Splitting:**
```javascript
// Future: Lazy load routes
import { lazy, Suspense } from 'react';

const Dashboard = lazy(() => import('./pages/Dashboard'));
const Projects = lazy(() => import('./pages/Projects'));

<Suspense fallback={<Loading />}>
  <Routes>
    <Route path="/" element={<Dashboard />} />
    <Route path="/projects" element={<Projects />} />
  </Routes>
</Suspense>
```

**Vite Configuration:**
```javascript
// vite.config.js
export default defineConfig({
  build: {
    rollupOptions: {
      output: {
        manualChunks: {
          vendor: ['react', 'react-dom', 'react-router-dom'],
          redux: ['@reduxjs/toolkit', 'react-redux'],
          charts: ['recharts'],
        },
      },
    },
    chunkSizeWarningLimit: 600, // KB
  },
});
```

### Asset Optimization

**Images:**
- Use WebP format (smaller than PNG/JPG)
- Compress images (TinyPNG, ImageOptim)
- Lazy load images below fold
- Use `srcset` for responsive images

**Fonts:**
- Subset fonts (include only used characters)
- Use `font-display: swap`
- Host fonts locally (avoid external requests)

### Runtime Optimization

**Memoization:**
```javascript
import { memo, useMemo, useCallback } from 'react';

// Memoize expensive components
const ProjectCard = memo(({ project }) => {
  // Component logic
});

// Memoize expensive computations
const filteredTasks = useMemo(() => {
  return tasks.filter(task => task.status === filter);
}, [tasks, filter]);

// Memoize callbacks
const handleClick = useCallback(() => {
  doSomething(value);
}, [value]);
```

**Virtualization (for long lists):**
```bash
npm install react-window
```

```javascript
import { FixedSizeList } from 'react-window';

<FixedSizeList
  height={600}
  itemCount={1000}
  itemSize={50}
  width="100%"
>
  {({ index, style }) => (
    <div style={style}>
      Item {index}
    </div>
  )}
</FixedSizeList>
```

---

## Monitoring & Analytics

### Error Tracking (Recommended: Sentry)

**Setup:**
```bash
npm install @sentry/react
```

**Configuration:**
```javascript
// src/main.jsx
import * as Sentry from '@sentry/react';

Sentry.init({
  dsn: import.meta.env.VITE_SENTRY_DSN,
  environment: import.meta.env.MODE,
  integrations: [
    new Sentry.BrowserTracing(),
    new Sentry.Replay(),
  ],
