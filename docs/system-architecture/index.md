# System Architecture

## Overview

Project Management Platform architecture documentation. Currently a frontend-only SPA designed for future backend integration.

**Current Phase:** Frontend Prototype with Dummy Data
**Future Phase:** Full-stack with REST/GraphQL API + PostgreSQL

## Documentation Structure

- **[Frontend Architecture](./frontend-architecture.md)** - React app structure, components, state management, routing
- **[Backend Architecture](./backend-architecture.md)** - Future API design, authentication, database, WebSocket (planned)
- **[Data Model](./data-model.md)** - Prisma schema, entity relationships, normalization strategy
- **[Deployment](./deployment-architecture.md)** - Current/future deployment architecture, scaling considerations

## High-Level Architecture

### Current (Frontend-Only)

```
┌─────────────────────────────────────────────────┐
│                   Browser                       │
│  ┌──────────────────────────────────────────┐   │
│  │        React Application (SPA)           │   │
│  │  React Components ← Redux Store          │   │
│  │         ↓                ↓                │   │
│  │    Dummy Data (assets.js)                │   │
│  └──────────────────────────────────────────┘   │
└─────────────────────────────────────────────────┘
```

### Future (Full-Stack)

```
Frontend (Vercel)
    ↓ HTTPS
Backend API (Railway/Render)
    ↓
PostgreSQL + Redis
```

## Quick Reference

### Current Stack
- **Frontend:** React 19, Vite 7, Redux Toolkit, Tailwind CSS 4
- **State:** Redux with dummy data (in-memory)
- **Routing:** React Router v7
- **Data:** No persistence (resets on reload)

### Future Stack (Planned)
- **Backend:** Node.js + Express/Fastify
- **Database:** PostgreSQL 14+ with Prisma ORM
- **Cache:** Redis
- **Auth:** JWT tokens
- **Real-time:** WebSocket (Socket.io)

## Architecture Principles

1. **Separation of Concerns:** UI, state, data layers distinct
2. **Scalability:** Component and state structure supports growth
3. **Maintainability:** Modular design allows easy updates
4. **Future-Proof:** Architecture accommodates planned backend integration

## Key Patterns

- Component-based UI (functional components + hooks)
- Redux Toolkit for global state
- Immer-powered immutable updates
- Utility-first styling (Tailwind)
- Mobile-first responsive design

## Migration Path (Frontend → Full-Stack)

**Phase 1:** API client layer
**Phase 2:** Authentication integration
**Phase 3:** Replace dummy data with API calls
**Phase 4:** WebSocket for real-time updates
**Phase 5:** Performance optimization

See [Backend Architecture](./backend-architecture.md) for detailed future plans.
