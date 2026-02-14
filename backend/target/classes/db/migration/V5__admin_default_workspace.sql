-- V5: Create default workspace for SUPER_ADMIN user

-- Default admin workspace
INSERT INTO workspaces (id, name, slug, description, settings, owner_id, image_url, created_at, updated_at)
VALUES ('org_admin', 'Admin Workspace', 'admin-workspace', 'Default workspace for system administration', '{}', 'user_super', '', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Superadmin membership in admin workspace
INSERT INTO workspace_members (id, user_id, workspace_id, message, role)
VALUES ('wm_super_admin', 'user_super', 'org_admin', '', 'ADMIN')
ON CONFLICT (user_id, workspace_id) DO NOTHING;

-- Superadmin membership in existing workspaces for oversight
INSERT INTO workspace_members (id, user_id, workspace_id, message, role) VALUES
('wm_super_org1', 'user_super', 'org_1', '', 'ADMIN'),
('wm_super_org2', 'user_super', 'org_2', '', 'ADMIN')
ON CONFLICT (user_id, workspace_id) DO NOTHING;
