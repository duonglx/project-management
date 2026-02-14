# Deployment Guide

## Overview

This guide covers development setup, build process, and deployment strategies for the Project Management Platform. Currently focused on frontend-only deployment; backend deployment will be added in Phase 1.

**Current Architecture:** Frontend SPA (Static Site)
**Future Architecture:** Full-stack (Frontend + Backend API + Database)

---

## Development Setup

### Prerequisites

**Required:**
- Node.js 18.0.0 or higher
- npm 9.0.0 or higher (comes with Node.js)
- Git

**Optional:**
- VS Code (recommended IDE)
- React Developer Tools (browser extension)
- Redux DevTools (browser extension)

**System Requirements:**
- macOS, Linux, or Windows
- 2GB free RAM (for development server)
- 500MB free disk space

### Local Development Setup

#### 1. Clone Repository

```bash
git clone https://github.com/GreatStackDev/project-management.git
cd project-management
```

#### 2. Install Dependencies

```bash
npm install
```

This installs all dependencies from `package.json`:
- React 19.1.1
- Vite 7.1.2
- Redux Toolkit 2.8.2
- Tailwind CSS 4.1.12
- And other dependencies (~30 packages)

**Installation Time:** 1-2 minutes (depending on connection)

#### 3. Start Development Server

```bash
npm run dev
```

**Output:**
```
VITE v7.1.2  ready in 234 ms

➜  Local:   http://localhost:5173/
➜  Network: use --host to expose
➜  press h + enter to show help
```

**Development Server Features:**
- Hot Module Replacement (HMR) - instant updates on file save
- Fast refresh for React components
- Error overlay for build/runtime errors
- Source maps for debugging

#### 4. Open Application

Open browser and navigate to:
```
http://localhost:5173
```

**Expected Behavior:**
- Dashboard page loads with dummy data
- Multiple workspaces visible in dropdown
- Projects and tasks displayed
- Theme toggle functional (light/dark mode)

### Development Workflow

**File Changes:**
- Edit files in `src/` directory
- Save changes
- Browser auto-refreshes (HMR)
- No manual refresh needed

**Common Issues:**
- Port 5173 already in use: Kill existing process or change port in `vite.config.js`
- Module not found: Run `npm install` again
- Tailwind classes not working: Restart dev server

### Backend Setup

#### Prerequisites

- Java 21+
- Maven 3.9+
- PostgreSQL 15+ (running on port 5433)

#### 1. Create Database

```bash
PGPASSWORD=postgres psql -h localhost -p 5433 -U postgres -c "CREATE DATABASE project_management;"
```

#### 2. Start Backend

```bash
cd backend
mvn spring-boot:run
```

Backend starts on `http://localhost:8080`. Flyway auto-runs all migrations (schema + seed data).

#### 3. Seed Users

All users are created via Flyway migrations (V2 + V3 + V4). No manual seeding needed.

| Username | Password | System Role | Workspace Roles | Notes |
|---|---|---|---|---|
| `superadmin` | `admin123` | SUPER_ADMIN | - | Platform super admin |
| `admin` | `admin123` | ADMIN_WORKSPACE | OWNER (both workspaces) | Workspace admin |
| `oliver_watts` | `password123` | USER | ADMIN (Corp), ADMIN (Cloud Ops) | Also PROJECT_LEAD on several projects |
| `alex_smith` | `password123` | USER | MEMBER (both) | PROJECT_LEAD on Regression Suite |
| `john_warrel` | `password123` | USER | MEMBER (both) | CONTRIBUTOR / VIEWER varies by project |
| `sarah_connor` | `password123` | USER | ADMIN (Corp), MEMBER (Cloud Ops) | VIEWER on CRM, CONTRIBUTOR on K8s |
| `mike_chen` | `password123` | USER | MEMBER (Corp), ADMIN (Cloud Ops) | PROJECT_LEAD on K8s Migration |
| `lisa_nguyen` | `password123` | USER | MEMBER (both) | CONTRIBUTOR / VIEWER varies |
| `david_park` | `password123` | USER | MEMBER (Corp) | VIEWER / CONTRIBUTOR varies |

**Role Hierarchy:**
- **SystemRole:** SUPER_ADMIN > ADMIN_WORKSPACE > USER
- **WorkspaceRole:** OWNER > ADMIN > MEMBER
- **ProjectRole:** PROJECT_LEAD > CONTRIBUTOR > VIEWER

**Login Example:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

JWT tokens are returned as HttpOnly cookies (`jwt` and `jwt_refresh`).

### Environment Variables

Create `.env` file in project root:
```bash
# API Configuration
VITE_API_URL=http://localhost:8080/api

# App Configuration
VITE_APP_NAME=Project Management
VITE_APP_VERSION=0.0.0
```

**Important:**
- Vite only exposes variables prefixed with `VITE_`
- Never commit `.env` to version control (add to `.gitignore`)
- Use `.env.example` for documenting required variables

### Running Linting

```bash
npm run lint
```

**Checks:**
- ESLint rules (React, React Hooks, JavaScript best practices)
- Unused variables
- Missing dependencies in useEffect
- React Refresh violations

**Auto-fix Minor Issues:**
```bash
npm run lint -- --fix
```

**Expected Output (if no errors):**
```
✔ No ESLint errors found
```

---

## Build Process

### Production Build

#### 1. Create Production Build

```bash
npm run build
```

**Build Process:**
1. Vite analyzes dependencies
2. Tree-shakes unused code
3. Minifies JavaScript/CSS
4. Generates hashed filenames (cache busting)
5. Outputs to `dist/` directory

**Build Output:**
```
dist/
├── assets/
│   ├── index-abc123.js       # Main bundle (minified)
│   ├── index-def456.css      # Styles (minified)
│   ├── vendor-xyz789.js      # Third-party libraries
│   └── [images/fonts]        # Static assets
└── index.html                # Entry HTML
```

**Build Time:** 10-20 seconds (typical)

**Build Metrics (Expected):**
- Total bundle size: < 500KB (gzipped)
- JavaScript bundle: ~300KB (gzipped)
- CSS bundle: ~50KB (gzipped)
- Vendor chunk: ~200KB (gzipped)

#### 2. Analyze Build Size

```bash
npm run build -- --mode=analyze
```

Or use Vite bundle analyzer plugin (future).

**Optimization Tips:**
- Code splitting reduces initial load
- Lazy loading for routes (future)
- Image optimization (future)

### Preview Production Build Locally

```bash
npm run preview
```

**Output:**
```
➜  Local:   http://localhost:4173/
➜  Network: use --host to expose
```

**Purpose:**
- Test production build before deployment
- Verify optimizations work correctly
- Check for build-specific issues

**Differences from Dev:**
- No HMR (manual refresh required)
- Minified code (harder to debug)
- Optimizations applied (code splitting, etc.)

---

## Deployment

### Frontend Deployment (Current)

#### Deployment to Vercel (Recommended)

Vercel provides zero-config deployment for Vite/React apps with free tier for personal projects.

**Prerequisites:**
- GitHub account
- Vercel account (sign up at vercel.com)

**Deployment Steps:**

**Option 1: Vercel CLI**

1. Install Vercel CLI:
```bash
npm install -g vercel
```

2. Login to Vercel:
```bash
vercel login
```

3. Deploy:
```bash
vercel
```

4. Follow prompts:
   - Link to existing project or create new
   - Select framework preset: Vite
   - Confirm build settings

5. Production deployment:
```bash
vercel --prod
```

**Option 2: Vercel GitHub Integration (Recommended)**

1. Push code to GitHub repository
2. Go to vercel.com and click "New Project"
3. Import GitHub repository
4. Vercel auto-detects Vite configuration
5. Click "Deploy"

**Automatic Deployments:**
- Every push to `main` branch → Production deployment
- Every PR → Preview deployment with unique URL
- No manual deployment needed

**Build Configuration (Auto-detected):**
```
Framework Preset: Vite
Build Command: npm run build
Output Directory: dist
Install Command: npm install
Node Version: 18.x
```

**Custom Configuration (vercel.json):**
```json
{
  "buildCommand": "npm run build",
  "outputDirectory": "dist",
  "framework": "vite",
  "rewrites": [
