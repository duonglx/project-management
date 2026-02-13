# Deployment Guide

## Overview

Deployment and development setup guide for the Project Management Platform.

**Current:** Frontend SPA (Static Site)
**Future:** Full-stack (Frontend + Backend API + Database)

## Documentation Structure

- **[Development Setup](./development-setup.md)** - Local environment, dependencies, dev server
- **[Frontend Deployment](./frontend-deployment.md)** - Vercel, Netlify, GitHub Pages deployment
- **[Backend Deployment](./backend-deployment.md)** - Railway, Render deployment (future)
- **[CI/CD & Monitoring](./cicd-monitoring.md)** - GitHub Actions, performance, security

## Quick Start

### Development

```bash
# Clone and install
git clone <repo-url>
cd project-management
npm install

# Start dev server
npm run dev  # http://localhost:5173

# Build for production
npm run build
npm run preview
```

### Deployment (Current)

**Vercel (Recommended):**
1. Push code to GitHub
2. Import repository on Vercel
3. Auto-detected Vite config
4. Deploy

## Prerequisites

- Node.js 18.0.0+
- npm 9.0.0+
- Git

## Resources

- [Vite Guide](https://vite.dev/guide/)
- [Vercel Docs](https://vercel.com/docs)
- [Railway Docs](https://docs.railway.app/)
