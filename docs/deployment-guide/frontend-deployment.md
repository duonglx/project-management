    {
      "source": "/(.*)",
      "destination": "/index.html"
    }
  ]
}
```

**Environment Variables on Vercel:**
1. Go to Project Settings → Environment Variables
2. Add variables (e.g., `VITE_API_URL`)
3. Select environments (Production, Preview, Development)
4. Redeploy to apply changes

**Vercel Features:**
- Global CDN (Edge Network)
- Automatic HTTPS/SSL
- Custom domains
- Preview deployments for PRs
- Analytics (free tier)
- Web Vitals monitoring

**Deployment URL:**
```
https://project-management-xyz.vercel.app
```

#### Deployment to Netlify (Alternative)

Similar to Vercel, with drag-and-drop deployment option.

**Steps:**
1. Build locally: `npm run build`
2. Go to netlify.com
3. Drag `dist/` folder to deploy
4. Or connect GitHub repository for automatic deployments

**Build Settings:**
```
Build Command: npm run build
Publish Directory: dist
```

**Netlify Configuration (_redirects file in public/):**
```
/* /index.html 200
```

This ensures SPA routing works (all routes serve index.html).

#### Deployment to GitHub Pages

Free hosting for static sites.

**Steps:**

1. Install gh-pages package:
```bash
npm install -D gh-pages
```

2. Add scripts to package.json:
```json
{
  "scripts": {
    "predeploy": "npm run build",
    "deploy": "gh-pages -d dist"
  }
}
```

3. Set base in vite.config.js:
```javascript
export default defineConfig({
  base: '/project-management/', // Replace with your repo name
  // ... other config
});
```

4. Deploy:
```bash
npm run deploy
```

**URL:**
```
https://yourusername.github.io/project-management/
```

**Limitations:**
- GitHub Pages URL format (not custom domain by default)
- Manual deployment (unless using GitHub Actions)
- No server-side features

### Static Hosting Providers Comparison

| Provider | Free Tier | Custom Domain | HTTPS | CDN | Auto Deploy |
|----------|-----------|---------------|-------|-----|-------------|
| **Vercel** | Yes (generous) | Yes | Yes | Yes | Yes (GitHub) |
| **Netlify** | Yes | Yes | Yes | Yes | Yes (GitHub) |
| **GitHub Pages** | Yes | Yes (custom setup) | Yes | No | Manual/Actions |
| **Cloudflare Pages** | Yes | Yes | Yes | Yes | Yes (GitHub) |
| **AWS S3 + CloudFront** | Pay-as-you-go | Yes | Yes | Yes | Manual/CI |

**Recommendation:** **Vercel** for best DX and zero-config deployment.

---

## Backend Deployment (Future - Phase 1)

### Backend Infrastructure (Planned)

**Recommended Stack:**
- **API Server:** Railway, Render, or Heroku
- **Database:** Managed PostgreSQL (Railway, Render, or Supabase)
- **Cache:** Redis (Upstash or Redis Cloud)
- **File Storage:** AWS S3 or Cloudinary (for attachments)

### Railway Deployment (Recommended)

Railway provides seamless Node.js + PostgreSQL deployment.

**Setup:**

1. Install Railway CLI:
```bash
npm install -g @railway/cli
```

2. Login:
```bash
railway login
```

3. Initialize project:
```bash
railway init
```

4. Add PostgreSQL:
```bash
railway add postgresql
```

5. Deploy:
```bash
railway up
```

**Configuration (railway.json):**
```json
{
  "build": {
    "builder": "NIXPACKS"
  },
  "deploy": {
    "startCommand": "npm run start",
    "healthcheckPath": "/api/health",
    "restartPolicyType": "ON_FAILURE"
  }
}
```

**Environment Variables (Railway Dashboard):**
- `DATABASE_URL` (auto-provided)
- `JWT_SECRET`
- `NODE_ENV=production`
- `PORT` (auto-assigned)

**Database Migrations:**
```bash
railway run npx prisma migrate deploy
```

### Render Deployment (Alternative)

**Setup:**
1. Create account at render.com
2. New → Web Service
3. Connect GitHub repository
4. Configure:
   - Build Command: `npm install && npx prisma generate && npm run build`
   - Start Command: `npm run start`
   - Environment: Node
5. Add PostgreSQL database (New → PostgreSQL)
6. Link database to web service (environment variable)

### Environment Variables (Backend)

**Production .env (never commit):**
```bash
# Database
DATABASE_URL=postgresql://user:password@host:5432/dbname

# JWT
JWT_SECRET=your-super-secret-256-bit-key
JWT_EXPIRES_IN=15m
REFRESH_TOKEN_EXPIRES_IN=7d

# Server
NODE_ENV=production
PORT=3000

# CORS
FRONTEND_URL=https://your-app.vercel.app

# OAuth (if using)
GOOGLE_CLIENT_ID=...
GOOGLE_CLIENT_SECRET=...
GITHUB_CLIENT_ID=...
GITHUB_CLIENT_SECRET=...

# Email (if using)
SMTP_HOST=smtp.sendgrid.net
SMTP_PORT=587
SMTP_USER=apikey
SMTP_PASS=...

# Redis (if using)
REDIS_URL=redis://...
```

### Database Hosting

**Managed PostgreSQL Options:**

**Railway PostgreSQL:**
- Included with Railway app
- Automatic backups
- Connection pooling
- $5/month base (generous free tier)

**Render PostgreSQL:**
- Free tier available (limited)
- Automatic backups on paid tiers
- Connection pooling

**Supabase:**
- PostgreSQL + additional features (auth, realtime)
- Free tier: 500MB database, 2GB bandwidth
- Built-in connection pooling

**AWS RDS:**
- Enterprise-grade
- More expensive
- Manual configuration

**Database Migrations (Prisma):**
```bash
# Development
npx prisma migrate dev --name init

# Production
npx prisma migrate deploy
```

### Full-Stack Deployment Architecture

```
Frontend (Vercel)
    ↓ HTTPS
Backend API (Railway/Render)
    ↓
PostgreSQL Database (Managed)
    ↓
Redis Cache (Upstash)
```

**Communication:**
- Frontend calls API via HTTPS
- API validates JWT tokens
- API queries database via Prisma
- Cache layer reduces DB load

---

## CI/CD Pipeline (Future)

### GitHub Actions Workflow

Create `.github/workflows/deploy.yml`:

```yaml
name: Deploy

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

jobs:
  lint:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-node@v3
        with:
          node-version: '18'
      - run: npm ci
