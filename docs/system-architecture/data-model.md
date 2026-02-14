# Data Model & Architecture

## Data Model (Prisma Schema)

The codebase includes a Prisma schema (`src/assets/schema.prisma`) defining the future backend data structure.

   └─► Renders UI with fetched data

3. User Interaction (e.g., Create Task)
   ├─► User fills form and submits
   ├─► Optimistic update (update UI immediately)
   ├─► Dispatch async thunk to POST /api/tasks
   ├─► API responds with created task
   ├─► Update Redux with server response
   └─► Toast notification shows success/error

4. Real-time Updates (WebSocket)
   ├─► WebSocket connection established on login
   ├─► Server pushes updates (task created, status changed)
   ├─► Client updates Redux state
   └─► UI updates automatically

5. Page Refresh
   ├─► Auth token persists (localStorage/cookie)
   ├─► User data re-fetched from API
   └─► State restored from server
```

## Data Model Architecture

### Current Data Model (Dummy Data)

Defined in `src/assets/assets.js` and `src/assets/schema.prisma` (reference)

#### Entity Relationships

```
User
  │
  ├─► WorkspaceMember ──► Workspace
  │                          │
  │                          ├─► Project
  │                          │     │
  │                          │     ├─► Task
  │                          │     │     │
  │                          │     │     ├─► TaskStatus
  │                          │     │     ├─► TaskLabel ──► Label
  │                          │     │     └─► Comment
  │                          │     │
  │                          │     └─► ProjectMember ──► User
  │                          │
  │                          ├─► Label
  │                          │     └─► TaskLabel ──► Task
  │                          │
  │                          ├─► TaskStatus
  │                          │     └─► Task
  │                          │
  │                          └─► WorkspaceMember (other users)
  │
  └─► Task (assignee)
```

#### Detailed Data Model (Prisma Schema)

**User:**
- id (String, UUID)
- name (String)
- email (String, unique)
- password (String, hashed)
- image (String, optional)
- createdAt (DateTime)
- updatedAt (DateTime)
- Relations: workspaces (via WorkspaceMember), projects (via ProjectMember), tasks (assigned), comments

**Workspace:**
- id (String, UUID)
- name (String)
- slug (String, unique)
- description (String, optional)
- settings (JSON, optional)
- ownerId (String, references User)
- createdAt (DateTime)
- updatedAt (DateTime)
- Relations: owner (User), members (via WorkspaceMember), projects

**WorkspaceMember:**
- id (String, UUID)
- userId (String, references User)
- workspaceId (String, references Workspace)
- role (WorkspaceRole: ADMIN | MEMBER)
- joinedAt (DateTime)
- Relations: user, workspace

**Project:**
- id (String, UUID)
- name (String)
- description (String, optional)
- priority (Priority: LOW | MEDIUM | HIGH)
- status (ProjectStatus: NOT_STARTED | IN_PROGRESS | COMPLETED | ON_HOLD)
- start_date (DateTime, optional)
- end_date (DateTime, optional)
- team_lead (String, references User, optional)
- workspaceId (String, references Workspace)
- progress (Int, 0-100)
- createdAt (DateTime)
- updatedAt (DateTime)
- Relations: workspace, teamLead (User), members (via ProjectMember), tasks

**ProjectMember:**
- id (String, UUID)
- userId (String, references User)
- projectId (String, references Project)
- joinedAt (DateTime)
- Relations: user, project

**Task:**
- id (String, UUID)
- title (String)
- description (String, optional)
- statusId (String, references TaskStatus)
- type (TaskType: TASK | BUG | FEATURE | IMPROVEMENT | OTHER)
- priority (Priority: LOW | MEDIUM | HIGH)
- assigneeId (String, references User, optional)
- due_date (DateTime, optional)
- projectId (String, references Project)
- createdAt (DateTime)
- updatedAt (DateTime)
- Relations: project, assignee (User), status (TaskStatus), labels (Label), comments

**TaskStatus:** (Workspace-level custom statuses)
- id (String, UUID)
- workspaceId (String, references Workspace)
- name (String)
- slug (String)
- color (String, hex color)
- category (StatusCategory: NOT_STARTED | ACTIVE | DONE | CLOSED)
- position (Integer, for ordering)
- isDefault (Boolean)
- createdAt (DateTime)
- updatedAt (DateTime)
- Relations: workspace, tasks

**Label:** (Workspace-level labels for tasks)
- id (String, UUID)
- workspaceId (String, references Workspace)
- name (String)
- color (String, hex color)
- description (String, optional)
- createdAt (DateTime)
- updatedAt (DateTime)
- Relations: workspace, tasks (via TaskLabel)

**TaskLabel:** (Junction table for task-label relationships)
- taskId (String, references Task)
- labelId (String, references Label)
- Relations: task, label

**Comment:**
- id (String, UUID)
- content (String)
- userId (String, references User)
- taskId (String, references Task)
- createdAt (DateTime)
- updatedAt (DateTime)
- Relations: user, task

**CustomFieldDefinition:** (Workspace-level custom field types)
- id (String, UUID)
- workspaceId (String, references Workspace)
- name (String, max 100, unique per workspace)
- type (CustomFieldType: TEXT | NUMBER | DROPDOWN | DATE | CHECKBOX | URL)
- options (JSON array, for DROPDOWN type)
- isRequired (Boolean)
- position (Integer, for ordering)
- createdAt (DateTime)
- updatedAt (DateTime)
- Relations: workspace, customFieldValues
- Constraints: Max 20 per workspace

**CustomFieldValue:** (Task-specific custom field values)
- id (String, UUID)
- taskId (String, references Task)
- fieldId (String, references CustomFieldDefinition)
- value (String/Text)
- Relations: task, customFieldDefinition
- Constraints: Unique (taskId, fieldId)

### Data Normalization Strategy (Future)

**Current:** Nested data structure (projects contain tasks)
**Future:** Normalized Redux state with relational lookups

```javascript
// Normalized State Structure (Future)
{
  entities: {
    workspaces: { 'ws-1': { id: 'ws-1', name: '...', projectIds: ['p1', 'p2'] } },
    projects: { 'p1': { id: 'p1', name: '...', taskIds: ['t1', 't2'] } },
    tasks: { 't1': { id: 't1', title: '...', assigneeId: 'u1' } },
    users: { 'u1': { id: 'u1', name: '...', email: '...' } }
  },
  currentWorkspaceId: 'ws-1',
  currentProjectId: 'p1',
  currentUserId: 'u1'
}
```

**Benefits:**
- Avoid data duplication
- Easy updates (single source of truth)
- Efficient lookups
- Better performance with large datasets

## Future Backend Architecture

### API Design (Planned)

#### RESTful API Endpoints

**Authentication:**
```
POST   /api/auth/register       # Register new user
POST   /api/auth/login          # Login user
POST   /api/auth/logout         # Logout user
POST   /api/auth/refresh        # Refresh JWT token
POST   /api/auth/forgot-password
POST   /api/auth/reset-password
```

**Workspaces:**
```
GET    /api/workspaces          # List user's workspaces
POST   /api/workspaces          # Create workspace
GET    /api/workspaces/:id      # Get workspace details
PUT    /api/workspaces/:id      # Update workspace
DELETE /api/workspaces/:id      # Delete workspace
```

**Workspace Members:**
```
GET    /api/workspaces/:id/members        # List members
POST   /api/workspaces/:id/members        # Add member
DELETE /api/workspaces/:id/members/:userId # Remove member
PUT    /api/workspaces/:id/members/:userId # Update member role
```

**Projects:**
```
GET    /api/workspaces/:wsId/projects     # List projects
POST   /api/workspaces/:wsId/projects     # Create project
GET    /api/projects/:id                  # Get project details
PUT    /api/projects/:id                  # Update project
DELETE /api/projects/:id                  # Delete project
```

**Tasks:**
```
GET    /api/projects/:projId/tasks        # List tasks
POST   /api/projects/:projId/tasks        # Create task
GET    /api/tasks/:id                     # Get task details
PUT    /api/tasks/:id                     # Update task
DELETE /api/tasks/:id                     # Delete task
```

**Comments:**
```
GET    /api/tasks/:taskId/comments        # List comments
POST   /api/tasks/:taskId/comments        # Add comment
PUT    /api/comments/:id                  # Update comment
DELETE /api/comments/:id                  # Delete comment
```

**Labels:**
```
GET    /api/workspaces/:wsId/labels       # List workspace labels
POST   /api/workspaces/:wsId/labels       # Create label
PUT    /api/workspaces/:wsId/labels/:id   # Update label
DELETE /api/workspaces/:wsId/labels/:id   # Delete label
```

**Task Statuses:**
```
GET    /api/workspaces/:wsId/task-statuses        # List workspace statuses
POST   /api/workspaces/:wsId/task-statuses        # Create status
PUT    /api/workspaces/:wsId/task-statuses/:id    # Update status
DELETE /api/workspaces/:wsId/task-statuses/:id    # Delete status
PUT    /api/workspaces/:wsId/task-statuses/reorder # Reorder statuses
```

**Users:**
```
GET    /api/users/me                      # Current user profile
PUT    /api/users/me                      # Update profile
GET    /api/users/:id                     # Get user (team member)
```

#### GraphQL Schema (Alternative)

```graphql
type Query {
  me: User!
  workspace(id: ID!): Workspace
  workspaces: [Workspace!]!
  project(id: ID!): Project
  task(id: ID!): Task
}

type Mutation {
  # Auth
  register(email: String!, password: String!, name: String!): AuthPayload!
  login(email: String!, password: String!): AuthPayload!

  # Workspaces
  createWorkspace(input: CreateWorkspaceInput!): Workspace!
  updateWorkspace(id: ID!, input: UpdateWorkspaceInput!): Workspace!
  deleteWorkspace(id: ID!): Boolean!

  # Projects
  createProject(workspaceId: ID!, input: CreateProjectInput!): Project!
  updateProject(id: ID!, input: UpdateProjectInput!): Project!
  deleteProject(id: ID!): Boolean!

  # Tasks
  createTask(projectId: ID!, input: CreateTaskInput!): Task!
  updateTask(id: ID!, input: UpdateTaskInput!): Task!
  deleteTask(id: ID!): Boolean!
}

type Subscription {
  taskUpdated(projectId: ID!): Task!
  projectUpdated(workspaceId: ID!): Project!
}

type User {
  id: ID!
  name: String!
  email: String!
  image: String
  workspaces: [Workspace!]!
  projects: [Project!]!
  tasks: [Task!]!
}

type Workspace {
  id: ID!
  name: String!
  slug: String!
  description: String
  owner: User!
  members: [WorkspaceMember!]!
  projects: [Project!]!
}

type Project {
  id: ID!
  name: String!
  description: String
  status: ProjectStatus!
  priority: Priority!
  progress: Int!
  workspace: Workspace!
  team: [ProjectMember!]!
  tasks: [Task!]!
}

type Task {
  id: ID!
  title: String!
  description: String
  status: TaskStatus!
  type: TaskType!
  priority: Priority!
  assignee: User
  project: Project!
  comments: [Comment!]!
  dueDate: DateTime
}
```

### Authentication & Authorization
