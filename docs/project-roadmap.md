# Project Roadmap

## Current Status

**Version:** 0.2.0
**Phase:** Backend API with RBAC + Workspace Settings Hub
**Status:** Active Development
**Last Updated:** February 14, 2026

## Roadmap Overview

The project is progressing through distinct development phases, from frontend prototype to production-ready full-stack application with real-time collaboration features.

```
Phase 0: Frontend Prototype  ████████████████████ 100% (COMPLETE)
Phase 1: Backend + Settings ████████████████░░░░  80% (IN PROGRESS)
Phase 2: Authentication     ░░░░░░░░░░░░░░░░░░░░   0% (PLANNED)
Phase 3: Real-Time Features ░░░░░░░░░░░░░░░░░░░░   0% (PLANNED)
Phase 4: Advanced Features  ░░░░░░░░░░░░░░░░░░░░   0% (FUTURE)
Phase 5: Production Launch  ░░░░░░░░░░░░░░░░░░░░   0% (FUTURE)
```

---

## Phase 0: Frontend Prototype ✅ COMPLETE

**Duration:** Completed
**Objective:** Build functional frontend SPA with dummy data to validate UX and features

### Completed Deliverables ✅

**Core Application Structure:**
- [x] React 19 + Vite 7 project setup
- [x] Redux Toolkit state management
- [x] React Router v7 routing configuration
- [x] Tailwind CSS 4 styling system
- [x] ESLint 9 configuration

**UI Components (18 components):**
- [x] Layout system (Sidebar, Navbar, Layout)
- [x] Navigation (WorkspaceDropdown, theme toggle)
- [x] Dashboard components (StatsGrid, ProjectOverview, RecentActivity, TasksSummary)
- [x] Project components (ProjectCard, ProjectTasks, ProjectCalendar, ProjectAnalytics, ProjectSettings)
- [x] Team components (Team page, member management)
- [x] Task components (TaskDetails, CreateTaskDialog, task list)
- [x] Dialog components (CreateProjectDialog, InviteMemberDialog, AddProjectMember)

**Pages (6 pages):**
- [x] Dashboard - workspace overview with stats and recent activity
- [x] Projects - project listing with grid view
- [x] ProjectDetails - tabbed project view (overview/tasks/calendar/analytics/settings)
- [x] Team - team member management
- [x] TaskDetails - detailed task view
- [x] Layout - application shell

**Features:**
- [x] Multiple workspace support with switcher
- [x] Project management (CRUD with dummy data)
- [x] Task management (CRUD with dummy data)
- [x] Task filtering (status, priority, assignee, type)
- [x] Task search functionality
- [x] Calendar view for task due dates
- [x] Project analytics (charts with Recharts)
- [x] Dark mode with persistent theme
- [x] Responsive design (mobile/tablet/desktop)
- [x] Toast notifications (react-hot-toast)

**State Management:**
- [x] Redux Toolkit slices (workspace, theme)
- [x] Workspace CRUD operations
- [x] Project CRUD operations
- [x] Task CRUD operations
- [x] Member management
- [x] Theme persistence (localStorage)

**Data Model:**
- [x] Dummy data structure (459 LOC in assets.js)
- [x] Prisma schema reference (future backend blueprint)
- [x] Data relationships defined (User, Workspace, Project, Task, Comment)

**Build & Development:**
- [x] Vite dev server configuration
- [x] Production build optimization
- [x] ESLint rules configured
- [x] Code organization and file structure

### Success Metrics ✅

- [x] 3,695 LOC implemented
- [x] 30+ component/page files
- [x] Zero console errors in development
- [x] Fast HMR updates (< 100ms)
- [x] Responsive UI across breakpoints
- [x] Dark mode fully functional

### Key Learnings

**What Worked Well:**
- Redux Toolkit simplified state management
- Tailwind CSS enabled rapid UI development
- Vite provided excellent DX with fast builds
- Component-based architecture scales well
- Dummy data allowed focus on UX without backend dependencies

**Challenges Encountered:**
- Some components exceeded 200 LOC (candidates for refactoring)
- No TypeScript increases risk of runtime errors
- Lack of testing means manual QA required
- State structure may need normalization for backend integration

**Recommendations for Next Phase:**
- Consider TypeScript migration for type safety
- Add testing infrastructure (Vitest + React Testing Library)
- Refactor large components (ProjectTasks, Team, ProjectCalendar)
- Implement code splitting for better performance

---

## Phase 1: Backend Foundation + Workspace Settings 🔧 IN PROGRESS

**Duration:** Ongoing (Started: January 2026)
**Status:** Active Development (80% Complete)
**Priority:** High
**Start Date:** January 2026
**Target Completion:** February 28, 2026

### Objectives

1. Build REST API with Spring Boot backend (Node.js changed to Java/Spring)
2. Integrate PostgreSQL database with JPA/Hibernate ORM
3. Implement API endpoints for all CRUD operations
4. Workspace Settings Hub with centralized configuration
5. Custom Task Statuses and Labels (workspace-level)
6. Role-Based Access Control (RBAC) system
7. JWT Authentication with refresh token rotation
8. Establish development and deployment workflows

### Completed Deliverables ✅

**Backend Infrastructure:**
- [x] Spring Boot server setup (Java 17+, Spring Boot 3.x)
- [x] PostgreSQL database provisioning (with migrations)
- [x] JPA/Hibernate ORM configuration
- [x] Environment configuration (.env management)
- [x] Error handling and logging middleware
- [x] JWT-based authentication system

**Database:**
- [x] Core schema (users, workspaces, projects, tasks, comments)
- [x] Authentication schema (refresh_tokens, permissions, role_permissions)
- [x] Workspace settings schema (labels, task_statuses)
- [x] Database migrations (Flyway)
- [x] Seed data for testing
- [x] Database indexes for performance

**API Endpoints:**
- [x] Workspace endpoints (GET, POST, PUT, DELETE)
- [x] Project endpoints (GET, POST, PUT, DELETE)
- [x] Task endpoints (GET, POST, PUT, DELETE)
- [x] User endpoints (GET, PUT)
- [x] Member management endpoints
- [x] Label CRUD endpoints
- [x] Task Status CRUD + Reorder endpoints
- [x] Comment endpoints (basic)

### Remaining Deliverables

**Frontend Integration:**
- [ ] Full API integration for existing pages
- [ ] Loading states and error handling
- [ ] Optimistic UI updates
- [ ] API client service layer

**Frontend Integration:**
- [ ] Create API client service (axios/fetch wrapper)
- [ ] Replace dummy data with API calls
- [ ] Implement loading states for async operations
- [ ] Add error handling and retry logic
- [ ] Implement optimistic UI updates

**Development Workflow:**
- [ ] Set up development database (local PostgreSQL)
- [ ] Create API development environment
- [ ] Configure CORS for frontend-backend communication
- [ ] Set up API testing (Jest/Vitest + Supertest)
- [ ] Create database backup/restore scripts

**Deployment:**
- [ ] Backend deployment (Railway, Render, or Heroku)
- [ ] Database hosting (managed PostgreSQL)
- [ ] Environment variable management
- [ ] CI/CD pipeline for backend
- [ ] Health check endpoints

### API Design Decisions

**REST API Structure:**
```
/api/workspaces
  GET     /                     # List user's workspaces
  POST    /                     # Create workspace
  GET     /:id                  # Get workspace
  PUT     /:id                  # Update workspace
  DELETE  /:id                  # Delete workspace
  GET     /:id/members          # List members
  POST    /:id/members          # Add member
  DELETE  /:id/members/:userId  # Remove member

/api/projects
  GET     /                     # List projects (filtered by workspace)
  POST    /                     # Create project
  GET     /:id                  # Get project
  PUT     /:id                  # Update project
  DELETE  /:id                  # Delete project
  GET     /:id/tasks            # List tasks in project
  POST    /:id/members          # Add project member

/api/tasks
  POST    /                     # Create task
  GET     /:id                  # Get task
  PUT     /:id                  # Update task
  DELETE  /:id                  # Delete task
  GET     /:id/comments         # List comments (future)

/api/users
  GET     /me                   # Current user (requires auth)
  PUT     /me                   # Update profile
```

**Alternative: GraphQL API (Under Consideration)**
- Single endpoint with flexible queries
- Reduced over-fetching/under-fetching
- Real-time subscriptions built-in
- Steeper learning curve
- Decision pending based on team expertise

### Success Criteria

- [ ] All frontend features work with API data
- [ ] API response time < 200ms (p95)
- [ ] Database queries optimized (no N+1 queries)
- [ ] API test coverage > 70%
- [ ] API documentation complete and accurate
- [ ] No data loss during CRUD operations
- [ ] Error messages are user-friendly

### Risks & Mitigation

**Risk:** Database schema changes require complex migrations
**Mitigation:** Careful schema design upfront, use Prisma migrations, test migrations on staging DB

**Risk:** API performance issues with large datasets
**Mitigation:** Implement pagination early, add database indexes, use query analysis tools

**Risk:** Frontend-backend integration bugs
**Mitigation:** Maintain API contract documentation, implement comprehensive error handling

**Risk:** Deployment complexity for full-stack app
**Mitigation:** Use managed services (Railway/Render), document deployment process, automate with CI/CD

### Dependencies

**Technical:**
- PostgreSQL 14+ database instance
- Node.js 18+ runtime
- Prisma 5+ compatible environment

**Team:**
- Backend developer familiar with Node.js/Express
- Database design expertise
- DevOps/deployment knowledge

---

## Phase 2: Authentication & Authorization 🔒

**Duration:** 3-4 weeks (Estimated)
**Status:** Planned
**Priority:** High
**Dependencies:** Phase 1 complete
**Target Start:** After Phase 1
**Target Completion:** TBD

### Objectives

1. Implement secure user authentication
2. Add user registration and login flows
3. Implement role-based access control (RBAC)
4. Secure all API endpoints
5. Add OAuth providers (Google, GitHub)

### Planned Deliverables

**Authentication System:**
- [ ] User registration endpoint with validation
- [ ] Email verification flow
- [ ] Login endpoint with JWT generation
- [ ] Password hashing (bcrypt, rounds: 12)
- [ ] JWT token management (access + refresh tokens)
- [ ] Logout endpoint with token invalidation
- [ ] Password reset flow (forgot password)

**Authorization (RBAC):**
- [ ] Workspace role enforcement (Admin/Member)
- [ ] API middleware for role checks
- [ ] Workspace-level data isolation
- [ ] Project member permission checks
- [ ] Endpoint-level authorization guards

**Frontend Integration:**
- [ ] Login page UI
- [ ] Registration page UI
- [ ] Protected route wrapper component
- [ ] Auth context/provider
- [ ] Token storage (httpOnly cookies or localStorage)
- [ ] Automatic token refresh
- [ ] Redirect unauthenticated users

**OAuth Integration:**
- [ ] Google OAuth provider
- [ ] GitHub OAuth provider
- [ ] OAuth callback handlers
- [ ] Account linking (email matching)

**Security Features:**
- [ ] Rate limiting on auth endpoints (prevent brute force)
- [ ] CSRF protection
- [ ] Password strength validation
- [ ] Account lockout after failed attempts
- [ ] Session management (invalidate old sessions)

**User Profile:**
- [ ] User profile page UI
- [ ] Update profile endpoint
- [ ] Avatar upload (future, or use Gravatar)
- [ ] Change password functionality
- [ ] Delete account (with confirmation)

### API Endpoints (New)

```
POST   /api/auth/register
POST   /api/auth/login
POST   /api/auth/logout
POST   /api/auth/refresh
POST   /api/auth/forgot-password
POST   /api/auth/reset-password
POST   /api/auth/verify-email
GET    /api/auth/oauth/google
GET    /api/auth/oauth/google/callback
GET    /api/auth/oauth/github
GET    /api/auth/oauth/github/callback
```

### Success Criteria

- [ ] Users can register and login successfully
- [ ] JWT tokens expire correctly (15 min access, 7 day refresh)
- [ ] Passwords stored securely (bcrypt hashed)
- [ ] Unauthorized API requests return 401/403
- [ ] Role-based permissions enforced correctly
- [ ] OAuth login works for Google and GitHub
- [ ] Email verification prevents unverified access
- [ ] Password reset flow secure (time-limited tokens)

### Security Checklist

- [ ] Passwords never logged or exposed
- [ ] JWTs signed with secure secret (256-bit+)
- [ ] Refresh tokens rotated on use
- [ ] Rate limiting prevents brute force attacks
- [ ] SQL injection prevented (Prisma parameterized queries)
- [ ] XSS protection (React auto-escaping + CSP headers)
- [ ] CSRF tokens on state-changing requests
- [ ] HTTPS enforced in production
- [ ] Sensitive data encrypted at rest

---

## Phase 3: Real-Time Collaboration 🔴 LIVE

**Duration:** 4-6 weeks (Estimated)
**Status:** Future
**Priority:** Medium
**Dependencies:** Phases 1 & 2 complete
**Target Start:** TBD
**Target Completion:** TBD

### Objectives

1. Add real-time task updates across clients
2. Implement live notifications
3. Show online presence indicators
4. Enable collaborative task editing

### Planned Deliverables

**WebSocket Server:**
- [ ] Socket.io server integration
- [ ] WebSocket authentication (JWT)
- [ ] Room-based messaging (workspace, project)
- [ ] Event broadcasting logic
- [ ] Connection management (reconnection, heartbeat)

**Real-Time Events:**
- [ ] Task created/updated/deleted
- [ ] Project updated
- [ ] Member added/removed
- [ ] Comment posted
- [ ] User online/offline status

**Frontend Integration:**
- [ ] WebSocket client connection
- [ ] Redux integration for real-time updates
- [ ] Optimistic UI updates with server confirmation
- [ ] Event handlers for all real-time events
- [ ] Connection status indicator

**Notifications:**
- [ ] In-app notification system
- [ ] Notification center/dropdown
- [ ] Mark as read functionality
- [ ] Notification preferences
- [ ] Email notifications (optional)

**Presence System:**
- [ ] User online status tracking
- [ ] "Currently viewing" indicators
- [ ] Active user list in workspace/project
- [ ] Last seen timestamps

**Collaborative Editing (Stretch Goal):**
- [ ] Operational Transform (OT) or CRDT for conflict resolution
- [ ] Live cursor positions
- [ ] Multi-user editing indicators

### Success Criteria

- [ ] Task updates appear instantly for all users (<1s latency)
- [ ] WebSocket connection stable (auto-reconnect)
- [ ] Online status accurate
- [ ] No data conflicts during concurrent edits
- [ ] Notifications delivered reliably
- [ ] WebSocket server handles 1000+ concurrent connections

### Performance Targets

- Real-time update latency: < 500ms
- WebSocket reconnection: < 2s
- Server memory usage: < 512MB per 1000 connections
- Message throughput: > 10,000 messages/second

---

## Phase 4: Advanced Features 🚀

**Duration:** 8-12 weeks (Estimated)
**Status:** Future
**Priority:** Low
**Dependencies:** Phases 1-3 complete
**Target Start:** TBD

### Planned Features

**File Attachments:**
- [ ] File upload to tasks/comments
- [ ] S3 or Cloudinary integration
- [ ] File preview (images, PDFs)
- [ ] File size limits and validation
- [ ] Drag-and-drop upload

**Task Comments:**
- [ ] Comment thread on tasks
- [ ] Rich text editor (Markdown or WYSIWYG)
- [ ] @ mentions (notify users)
- [ ] Edit/delete comments
- [ ] Comment reactions (emoji)

**Advanced Filtering & Search:**
- [ ] Full-text search (ElasticSearch or PostgreSQL FTS)
- [ ] Saved filters/views
- [ ] Advanced query builder UI
- [ ] Search across workspaces
- [ ] Search history

**Data Export/Import:**
- [ ] Export projects to CSV/JSON
- [ ] Import tasks from CSV
- [ ] Bulk task operations
- [ ] Template projects

**Integrations:**
- [ ] GitHub issue sync
- [ ] Slack notifications
- [ ] Google Calendar integration
- [ ] Zapier/Make.com webhooks
- [ ] API webhooks for custom integrations

**Custom Fields:**
- [ ] User-defined task fields
- [ ] Field types (text, number, date, select)
- [ ] Field validation rules
- [ ] Custom field search/filter

**Gantt Chart:**
- [ ] Timeline view for projects
- [ ] Drag-to-reschedule tasks
- [ ] Dependency visualization
- [ ] Critical path highlighting

**Time Tracking:**
- [ ] Log time on tasks
- [ ] Time estimates vs actuals
- [ ] Time reports
- [ ] Timesheet view

**Recurring Tasks:**
- [ ] Schedule repeating tasks
- [ ] Recurrence rules (daily, weekly, monthly)
- [ ] Auto-create future instances
- [ ] Skip/modify instances

**Task Dependencies:**
- [ ] Define task dependencies (blocked by, blocks)
- [ ] Dependency graph visualization
- [ ] Auto-update status based on dependencies

**Subtasks:**
- [ ] Nested task hierarchy
- [ ] Subtask progress rollup
- [ ] Subtask completion tracking

**Workspace Templates:**
- [ ] Predefined workspace types (Software Dev, Marketing, etc.)
- [ ] Template projects with tasks
- [ ] Clone workspace with structure

**Mobile App (PWA or React Native):**
- [ ] Progressive Web App (PWA) with offline support
- [ ] Push notifications
- [ ] OR: React Native mobile apps (iOS/Android)

### Prioritization Criteria

Features will be prioritized based on:
- User demand (GitHub issues, discussions, surveys)
- Development effort vs value
- Technical dependencies
- Competitive analysis

---

## Phase 5: Production Launch & Scale 📈

**Duration:** 2-3 weeks (Estimated)
**Status:** Future
**Priority:** High
**Dependencies:** Phases 1-3 complete (Phase 4 optional)
**Target Start:** TBD

### Objectives

1. Deploy to production infrastructure
2. Implement monitoring and observability
3. Set up CI/CD pipeline
4. Establish backup and disaster recovery
5. Performance optimization and load testing

### Planned Deliverables

**Production Infrastructure:**
- [ ] Frontend hosting (Vercel production tier)
- [ ] Backend hosting (auto-scaling, load balanced)
- [ ] Database hosting (managed PostgreSQL with backups)
- [ ] Redis cache (managed instance)
- [ ] CDN configuration (Cloudflare or similar)
- [ ] DNS configuration
- [ ] SSL certificates (auto-renewal)

**CI/CD Pipeline:**
- [ ] GitHub Actions workflows
- [ ] Automated testing (unit, integration, E2E)
- [ ] Automated linting and code quality checks
- [ ] Automated builds
- [ ] Automated deployments (staging + production)
- [ ] Rollback strategy
- [ ] Database migration automation

**Monitoring & Observability:**
- [ ] Application monitoring (Datadog, New Relic, or Sentry)
- [ ] Error tracking (Sentry)
- [ ] Log aggregation (Datadog, LogRocket)
- [ ] Performance monitoring (Core Web Vitals)
- [ ] Uptime monitoring (Pingdom, UptimeRobot)
- [ ] API metrics dashboard
- [ ] Database performance metrics

**Alerts:**
- [ ] Error rate threshold alerts
- [ ] API downtime alerts
- [ ] Database connection alerts
- [ ] High CPU/memory alerts
- [ ] Security incident alerts

**Backup & Recovery:**
- [ ] Automated database backups (daily)
- [ ] Point-in-time recovery capability
- [ ] Backup restoration testing
- [ ] Disaster recovery plan documented

**Performance Optimization:**
- [ ] Database query optimization
- [ ] API response caching
- [ ] Frontend bundle optimization
- [ ] Image optimization (WebP, lazy loading)
- [ ] CDN caching strategy
- [ ] Database indexing review

**Load Testing:**
- [ ] API load testing (k6, Artillery)
- [ ] Database stress testing
- [ ] WebSocket connection testing
- [ ] Identify bottlenecks
- [ ] Optimize based on results

**Security Hardening:**
- [ ] Security audit (external if possible)
- [ ] Penetration testing
- [ ] Dependency vulnerability scanning
- [ ] Security headers configured
- [ ] DDoS protection (Cloudflare)
- [ ] Rate limiting tuning

**Documentation:**
- [ ] Public API documentation
- [ ] User documentation/help center
- [ ] Deployment runbook
- [ ] Incident response plan
- [ ] Contributing guidelines updated

**Legal & Compliance:**
- [ ] Terms of Service
- [ ] Privacy Policy
- [ ] GDPR compliance (if applicable)
- [ ] Data retention policy
- [ ] Cookie policy

### Success Criteria

- [ ] 99.9% uptime SLA achieved
- [ ] Core Web Vitals all green (LCP < 2.5s, FID < 100ms, CLS < 0.1)
- [ ] API p95 response time < 300ms
- [ ] Zero critical security vulnerabilities
- [ ] Automated deployments with < 5 min downtime
- [ ] Database backups restore successfully
- [ ] Load testing shows capacity for 10,000 concurrent users

### Launch Checklist

**Pre-Launch:**
- [ ] All Phase 1-2 features tested and stable
- [ ] Security audit complete
- [ ] Performance optimization complete
- [ ] Monitoring and alerts configured
- [ ] Backup strategy tested
- [ ] Legal documents in place

**Launch Day:**
- [ ] Deploy to production
- [ ] Monitor error rates and performance
- [ ] Respond to incidents quickly
- [ ] Gather user feedback

**Post-Launch:**
- [ ] Monitor key metrics (signups, retention, errors)
- [ ] Iterate based on user feedback
- [ ] Address bugs and performance issues
- [ ] Plan next feature roadmap

---

## Future Considerations (Post-Launch)

### Enterprise Features
- Single Sign-On (SSO) integration (SAML, LDAP)
- Advanced permissions (custom roles, granular permissions)
- Audit logs (compliance, security)
- White-labeling (custom branding)
- SLA guarantees and dedicated support

### Platform Expansion
- Desktop applications (Electron)
- Browser extensions
- Email integration (task creation via email)
- Calendar integrations (Outlook, Google Calendar)
- Third-party app marketplace

### AI/ML Features
- Smart task assignment (based on member workload/skills)
- Task priority suggestions
- Estimated completion time predictions
- Automated task categorization
- Natural language task creation

### Analytics & Insights
- Team performance metrics
- Burndown charts
- Velocity tracking
- Bottleneck identification
- Predictive analytics (project completion dates)

---

## Version History

### v0.0.0 (Complete - Phase 0)
- Frontend prototype complete
- Dummy data-driven SPA
- All core UI components implemented
- Dark mode support
- Responsive design

### v0.1.0 (Archived)
- Originally planned for backend API integration

### v0.2.0 (Current - Phase 1, 80% Complete)
**Released:** February 14, 2026

**Major Features:**
- Spring Boot backend with Java 17+
- PostgreSQL database with Flyway migrations
- REST API for all CRUD operations
- JWT-based authentication with refresh tokens
- Role-Based Access Control (RBAC) with permission system
- Workspace Settings Hub
- Custom Task Statuses (workspace-level, 4 categories)
- Workspace Labels (up to 50 per workspace)
- Label assignment to tasks
- Status reordering interface
- General Settings (workspace name, description, image, timezone, language)
- Danger Zone (transfer ownership, delete workspace)

**Database Schema:**
- 8 Flyway migrations (V1-V8)
- New tables: task_statuses, labels, task_labels, permission, role_permission, refresh_tokens
- Task status migrated from enum to FK relationship
- Full indexing for performance

**Security:**
- HS512 signed JWT tokens
- httpOnly cookies (XSS protection)
- Token rotation on refresh
- Password hashing (bcrypt)
- Admin-only settings endpoints

**Next:** Frontend API integration, more endpoint implementation

### v0.3.0 (Planned - Phase 2)
- Full frontend API integration
- Comment system
- Advanced task filtering
- OAuth providers (Google, GitHub)

### v0.4.0 (Planned - Phase 3)
- Real-time collaboration
- WebSocket integration
- Live notifications

### v1.0.0 (Planned - Phase 5)
- Production launch
- Full feature set
- Production infrastructure
- Monitoring and observability

---

## How to Contribute

This roadmap is community-driven. You can contribute by:

1. **Suggesting Features:** Open a GitHub Discussion or Issue
2. **Voting on Features:** React to issues with 👍/👎
3. **Implementing Features:** Submit PRs for roadmap items
4. **Testing:** Test beta features and report bugs
5. **Documentation:** Improve docs for existing features

**Roadmap Updates:**
This roadmap is reviewed and updated quarterly. Major changes will be announced via GitHub Discussions.

**Feedback:**
Have feedback on this roadmap? Open a GitHub Discussion or reach out to maintainers.

---

## Key Metrics & KPIs (Post-Launch)

### User Metrics
- Monthly Active Users (MAU)
- Weekly Active Users (WAU)
- User retention rate (7-day, 30-day)
- New user signups per month
- Workspace creation rate

### Product Metrics
- Average tasks per project
- Average projects per workspace
- Task completion rate
- Average time to task completion
- Active workspaces

### Technical Metrics
- API uptime (target: 99.9%)
- Average API response time (target: < 200ms)
- Error rate (target: < 0.1%)
- Page load time (target: < 2s)
- Core Web Vitals scores

### Business Metrics (Future)
- Customer acquisition cost (CAC)
- Monthly recurring revenue (MRR) - if monetized
- Customer lifetime value (LTV)
- Churn rate

---

**Last Updated:** February 13, 2026
**Maintained By:** Project Team
**Review Frequency:** Quarterly
