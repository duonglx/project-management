-- V3: Auth + RBAC schema changes
-- Adds username, password, system_role to users
-- Adds role to project_members
-- Creates permissions, role_permissions, refresh_tokens tables
-- Seeds default permissions and role-permission mappings

-- 1. Add auth columns to users
ALTER TABLE users ADD COLUMN username VARCHAR(50);
ALTER TABLE users ADD COLUMN password VARCHAR(255);
ALTER TABLE users ADD COLUMN system_role VARCHAR(20) DEFAULT 'USER' NOT NULL;

-- 2. Backfill existing seed users with username + password
-- password123 bcrypt hash (strength 12)
UPDATE users SET username = 'alex_smith', password = '$2b$12$co16LaPIP5OnAX9nX7XsoulWfa7GYpIxvnSI5dsXkgzq8nbxpX1WO' WHERE id = 'user_1';
UPDATE users SET username = 'john_warrel', password = '$2b$12$co16LaPIP5OnAX9nX7XsoulWfa7GYpIxvnSI5dsXkgzq8nbxpX1WO' WHERE id = 'user_2';
UPDATE users SET username = 'oliver_watts', password = '$2b$12$co16LaPIP5OnAX9nX7XsoulWfa7GYpIxvnSI5dsXkgzq8nbxpX1WO' WHERE id = 'user_3';

-- 3. Insert admin user (admin123 bcrypt hash)
INSERT INTO users (id, name, email, image, username, password, system_role, created_at, updated_at)
VALUES ('user_admin', 'System Admin', 'admin@system.local', '', 'admin', '$2b$12$Ht.XBHVjV.Ex3uJxRjChrOTOBxuzNM5E3.yJCe34KY/OpVBGu23Py', 'ADMIN_WORKSPACE', NOW(), NOW());

-- 4. Make username + password NOT NULL after backfill
ALTER TABLE users ALTER COLUMN username SET NOT NULL;
ALTER TABLE users ALTER COLUMN password SET NOT NULL;
ALTER TABLE users ADD CONSTRAINT users_username_unique UNIQUE (username);

-- 5. Add role to project_members
ALTER TABLE project_members ADD COLUMN role VARCHAR(20) DEFAULT 'CONTRIBUTOR' NOT NULL;

-- 6. Create refresh_tokens table
CREATE TABLE refresh_tokens (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);

-- 7. Create permissions table
CREATE TABLE permissions (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    scope VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

-- 8. Create role_permissions table
CREATE TABLE role_permissions (
    id VARCHAR(36) PRIMARY KEY,
    role VARCHAR(30) NOT NULL,
    permission_id VARCHAR(36) NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    workspace_id VARCHAR(36) REFERENCES workspaces(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(role, permission_id, workspace_id)
);
CREATE INDEX idx_role_permissions_role ON role_permissions(role);
CREATE INDEX idx_role_permissions_workspace ON role_permissions(workspace_id);

-- 9. Seed default permissions (14 total)
INSERT INTO permissions (id, name, description, scope) VALUES
('perm_01', 'workspace:manage_members', 'Add/remove workspace members', 'WORKSPACE'),
('perm_02', 'workspace:manage_settings', 'Update workspace settings', 'WORKSPACE'),
('perm_03', 'workspace:create_project', 'Create projects in workspace', 'WORKSPACE'),
('perm_04', 'workspace:delete_project', 'Delete projects in workspace', 'WORKSPACE'),
('perm_05', 'project:update', 'Update project details', 'PROJECT'),
('perm_06', 'project:delete', 'Delete a project', 'PROJECT'),
('perm_07', 'project:manage_members', 'Add/remove project members', 'PROJECT'),
('perm_08', 'task:create', 'Create tasks', 'TASK'),
('perm_09', 'task:update', 'Update task details', 'TASK'),
('perm_10', 'task:delete', 'Delete tasks', 'TASK'),
('perm_11', 'task:assign', 'Assign tasks to members', 'TASK'),
('perm_12', 'task:update_status', 'Change task status', 'TASK'),
('perm_13', 'comment:create', 'Create comments on tasks', 'TASK'),
('perm_14', 'comment:delete', 'Delete comments', 'TASK');

-- 10. Seed default role-permission mappings (workspace_id = NULL = system defaults)
-- OWNER gets all workspace permissions
INSERT INTO role_permissions (id, role, permission_id, workspace_id) VALUES
('rp_01', 'OWNER', 'perm_01', NULL),
('rp_02', 'OWNER', 'perm_02', NULL),
('rp_03', 'OWNER', 'perm_03', NULL),
('rp_04', 'OWNER', 'perm_04', NULL);

-- ADMIN gets manage_members + create/delete project
INSERT INTO role_permissions (id, role, permission_id, workspace_id) VALUES
('rp_05', 'ADMIN', 'perm_01', NULL),
('rp_06', 'ADMIN', 'perm_03', NULL),
('rp_07', 'ADMIN', 'perm_04', NULL);

-- MEMBER gets create_project only
INSERT INTO role_permissions (id, role, permission_id, workspace_id) VALUES
('rp_08', 'MEMBER', 'perm_03', NULL);

-- PROJECT_LEAD gets all project + task permissions
INSERT INTO role_permissions (id, role, permission_id, workspace_id) VALUES
('rp_09', 'PROJECT_LEAD', 'perm_05', NULL),
('rp_10', 'PROJECT_LEAD', 'perm_06', NULL),
('rp_11', 'PROJECT_LEAD', 'perm_07', NULL),
('rp_12', 'PROJECT_LEAD', 'perm_08', NULL),
('rp_13', 'PROJECT_LEAD', 'perm_09', NULL),
('rp_14', 'PROJECT_LEAD', 'perm_10', NULL),
('rp_15', 'PROJECT_LEAD', 'perm_11', NULL),
('rp_16', 'PROJECT_LEAD', 'perm_12', NULL),
('rp_17', 'PROJECT_LEAD', 'perm_13', NULL),
('rp_18', 'PROJECT_LEAD', 'perm_14', NULL);

-- CONTRIBUTOR gets project:update + task CRUD + comments
INSERT INTO role_permissions (id, role, permission_id, workspace_id) VALUES
('rp_19', 'CONTRIBUTOR', 'perm_05', NULL),
('rp_20', 'CONTRIBUTOR', 'perm_08', NULL),
('rp_21', 'CONTRIBUTOR', 'perm_09', NULL),
('rp_22', 'CONTRIBUTOR', 'perm_11', NULL),
('rp_23', 'CONTRIBUTOR', 'perm_12', NULL),
('rp_24', 'CONTRIBUTOR', 'perm_13', NULL);

-- VIEWER gets task:update_status + comment:create only
INSERT INTO role_permissions (id, role, permission_id, workspace_id) VALUES
('rp_25', 'VIEWER', 'perm_12', NULL),
('rp_26', 'VIEWER', 'perm_13', NULL);

-- 11. Seed workspace memberships for admin user (OWNER of all workspaces)
INSERT INTO workspace_members (id, user_id, workspace_id, message, role) VALUES
('wm_admin_org1', 'user_admin', 'org_1', '', 'OWNER'),
('wm_admin_org2', 'user_admin', 'org_2', '', 'OWNER');

-- Update existing seed users to MEMBER role (they were all ADMIN, keep as MEMBER for RBAC)
UPDATE workspace_members SET role = 'MEMBER' WHERE user_id IN ('user_1', 'user_2') AND workspace_id IN ('org_1', 'org_2');
UPDATE workspace_members SET role = 'ADMIN' WHERE user_id = 'user_3' AND workspace_id IN ('org_1', 'org_2');
