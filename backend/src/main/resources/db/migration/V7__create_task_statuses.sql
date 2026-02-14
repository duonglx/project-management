-- Create task_statuses table for dynamic workspace-level statuses
CREATE TABLE task_statuses (
    id VARCHAR(36) PRIMARY KEY,
    workspace_id VARCHAR(36) NOT NULL,
    name VARCHAR(50) NOT NULL,
    slug VARCHAR(50) NOT NULL,
    color VARCHAR(7) NOT NULL DEFAULT '#3b82f6',
    category VARCHAR(20) NOT NULL,
    position INTEGER NOT NULL DEFAULT 0,
    is_default BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_task_statuses_workspace FOREIGN KEY (workspace_id)
        REFERENCES workspaces(id) ON DELETE CASCADE,
    CONSTRAINT uq_task_statuses_workspace_slug UNIQUE (workspace_id, slug)
);
CREATE INDEX idx_task_statuses_workspace_id ON task_statuses(workspace_id);

-- Seed default statuses for every existing workspace
INSERT INTO task_statuses (id, workspace_id, name, slug, color, category, position, is_default, created_at, updated_at)
SELECT gen_random_uuid()::text, w.id, 'Backlog', 'backlog', '#6b7280', 'NOT_STARTED', 0, false, NOW(), NOW()
FROM workspaces w;

INSERT INTO task_statuses (id, workspace_id, name, slug, color, category, position, is_default, created_at, updated_at)
SELECT gen_random_uuid()::text, w.id, 'Todo', 'todo', '#3b82f6', 'NOT_STARTED', 1, true, NOW(), NOW()
FROM workspaces w;

INSERT INTO task_statuses (id, workspace_id, name, slug, color, category, position, is_default, created_at, updated_at)
SELECT gen_random_uuid()::text, w.id, 'In Progress', 'in-progress', '#f59e0b', 'ACTIVE', 2, false, NOW(), NOW()
FROM workspaces w;

INSERT INTO task_statuses (id, workspace_id, name, slug, color, category, position, is_default, created_at, updated_at)
SELECT gen_random_uuid()::text, w.id, 'In Review', 'in-review', '#8b5cf6', 'ACTIVE', 3, false, NOW(), NOW()
FROM workspaces w;

INSERT INTO task_statuses (id, workspace_id, name, slug, color, category, position, is_default, created_at, updated_at)
SELECT gen_random_uuid()::text, w.id, 'Done', 'done', '#22c55e', 'DONE', 4, false, NOW(), NOW()
FROM workspaces w;

INSERT INTO task_statuses (id, workspace_id, name, slug, color, category, position, is_default, created_at, updated_at)
SELECT gen_random_uuid()::text, w.id, 'Cancelled', 'cancelled', '#ef4444', 'CLOSED', 5, false, NOW(), NOW()
FROM workspaces w;
