  tracesSampleRate: 1.0,
  replaysSessionSampleRate: 0.1,
  replaysOnErrorSampleRate: 1.0,
});
```

**Error Boundary:**
```javascript
<Sentry.ErrorBoundary fallback={<ErrorFallback />}>
  <App />
</Sentry.ErrorBoundary>
```

### Web Analytics

**Vercel Analytics (Built-in):**
- No setup required on Vercel
- Core Web Vitals tracking
- Automatic performance monitoring

**Google Analytics (Optional):**
```bash
npm install react-ga4
```

```javascript
import ReactGA from 'react-ga4';

ReactGA.initialize('G-XXXXXXXXXX');
ReactGA.send('pageview');
```

### Performance Monitoring

**Core Web Vitals:**
- LCP (Largest Contentful Paint): < 2.5s
- FID (First Input Delay): < 100ms
- CLS (Cumulative Layout Shift): < 0.1

**Monitor with:**
- Vercel Analytics
- Lighthouse CI
- Web Vitals library

---

## Security Best Practices

### Frontend Security

**Content Security Policy (Vercel):**
```json
// vercel.json
{
  "headers": [
    {
      "source": "/(.*)",
      "headers": [
        {
          "key": "Content-Security-Policy",
          "value": "default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline';"
        },
        {
          "key": "X-Frame-Options",
          "value": "DENY"
        },
        {
          "key": "X-Content-Type-Options",
          "value": "nosniff"
        }
      ]
    }
  ]
}
```

**HTTPS Enforcement:**
- Vercel/Netlify enforce HTTPS automatically
- Redirect HTTP → HTTPS

**Dependency Security:**
```bash
# Check for vulnerabilities
npm audit

# Fix automatically (if possible)
npm audit fix

# Update dependencies
npm update
```

### Backend Security (Future)

**Environment Variables:**
- Never commit secrets to Git
- Use environment variable management (Vercel, Railway)
- Rotate secrets regularly

**Rate Limiting:**
```javascript
// Express rate limiting
import rateLimit from 'express-rate-limit';

const limiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 100, // limit each IP to 100 requests per windowMs
});

app.use('/api/', limiter);
```

**CORS Configuration:**
```javascript
import cors from 'cors';

app.use(cors({
  origin: process.env.FRONTEND_URL,
  credentials: true,
}));
```

---

## Troubleshooting

### Common Deployment Issues

**Issue: Build fails on Vercel**
- Check build logs for errors
- Verify Node version matches local (18+)
- Ensure all dependencies in package.json
- Check for missing environment variables

**Issue: SPA routing doesn't work (404 on refresh)**
- Add rewrite rule (see Vercel/Netlify config above)
- Ensure all routes serve index.html

**Issue: Environment variables not working**
- Verify `VITE_` prefix for Vite variables
- Redeploy after adding variables
- Check variable spelling

**Issue: Large bundle size**
- Analyze bundle with Vite analyzer
- Implement code splitting
- Remove unused dependencies
- Optimize images

**Issue: Slow page load**
- Check Lighthouse score
- Optimize images
- Enable CDN caching
- Implement lazy loading

### Getting Help

**Resources:**
- [Vite Documentation](https://vitejs.dev/)
- [Vercel Documentation](https://vercel.com/docs)
- [GitHub Issues](https://github.com/GreatStackDev/project-management/issues)
- [GitHub Discussions](https://github.com/GreatStackDev/project-management/discussions)

**Community:**
- Open an issue for bugs
- Start a discussion for questions
- Check existing issues before creating new ones

---

## Summary

**Current Deployment:**
- Frontend SPA on Vercel (recommended)
- Zero-config deployment
- Automatic HTTPS and CDN
- Preview deployments for PRs

**Future Deployment:**
- Backend API on Railway/Render
- PostgreSQL database (managed)
- Redis cache (Upstash)
- CI/CD pipeline with GitHub Actions

**Next Steps:**
1. Set up Vercel deployment for frontend
2. Configure automatic deployments
3. Add monitoring (Sentry, Analytics)
4. Prepare for backend integration (Phase 1)
