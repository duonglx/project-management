-- V4: Comprehensive seed data for all role combinations
-- Covers: SystemRole (SUPER_ADMIN, ADMIN_WORKSPACE, USER)
--         WorkspaceRole (OWNER, ADMIN, MEMBER)
--         ProjectRole (PROJECT_LEAD, CONTRIBUTOR, VIEWER)

-- ============================================================
-- 1. SUPER_ADMIN user (password: super123)
-- ============================================================
-- bcrypt hash for "super123"
INSERT INTO users (id, name, email, image, username, password, system_role, created_at, updated_at)
VALUES ('user_super', 'Super Admin', 'superadmin@system.local', '', 'superadmin',
        '$2b$12$Ht.XBHVjV.Ex3uJxRjChrOTOBxuzNM5E3.yJCe34KY/OpVBGu23Py', 'SUPER_ADMIN', NOW(), NOW());

-- ============================================================
-- 1b. Default workspace for SUPER_ADMIN
-- ============================================================
INSERT INTO workspaces (id, name, slug, description, settings, owner_id, image_url, created_at, updated_at)
VALUES ('org_admin', 'Admin Workspace', 'admin-workspace', 'Default workspace for system administration', '{}', 'user_super', '', NOW(), NOW());

INSERT INTO workspace_members (id, user_id, workspace_id, message, role)
VALUES ('wm_super_admin', 'user_super', 'org_admin', '', 'ADMIN');

-- Also add superadmin to existing workspaces for full oversight
INSERT INTO workspace_members (id, user_id, workspace_id, message, role) VALUES
('wm_super_org1', 'user_super', 'org_1', '', 'ADMIN'),
('wm_super_org2', 'user_super', 'org_2', '', 'ADMIN');

-- ============================================================
-- 2. Additional regular users (password: password123 for all)
-- ============================================================
INSERT INTO users (id, name, email, image, username, password, system_role, created_at, updated_at) VALUES
('user_4', 'Sarah Connor', 'sarah.connor@example.com', '', 'sarah_connor',
 '$2b$12$co16LaPIP5OnAX9nX7XsoulWfa7GYpIxvnSI5dsXkgzq8nbxpX1WO', 'USER', NOW(), NOW()),
('user_5', 'Mike Chen', 'mike.chen@example.com', '', 'mike_chen',
 '$2b$12$co16LaPIP5OnAX9nX7XsoulWfa7GYpIxvnSI5dsXkgzq8nbxpX1WO', 'USER', NOW(), NOW()),
('user_6', 'Lisa Nguyen', 'lisa.nguyen@example.com', '', 'lisa_nguyen',
 '$2b$12$co16LaPIP5OnAX9nX7XsoulWfa7GYpIxvnSI5dsXkgzq8nbxpX1WO', 'USER', NOW(), NOW()),
('user_7', 'David Park', 'david.park@example.com', '', 'david_park',
 '$2b$12$co16LaPIP5OnAX9nX7XsoulWfa7GYpIxvnSI5dsXkgzq8nbxpX1WO', 'USER', NOW(), NOW());

-- ============================================================
-- 3. Workspace memberships (diverse roles)
-- ============================================================
-- org_1: Sarah=ADMIN, Mike=MEMBER, Lisa=MEMBER, David=MEMBER
INSERT INTO workspace_members (id, user_id, workspace_id, message, role) VALUES
('wm_sarah_org1', 'user_4', 'org_1', '', 'ADMIN'),
('wm_mike_org1',  'user_5', 'org_1', '', 'MEMBER'),
('wm_lisa_org1',  'user_6', 'org_1', '', 'MEMBER'),
('wm_david_org1', 'user_7', 'org_1', '', 'MEMBER');

-- org_2: Mike=ADMIN, Sarah=MEMBER, Lisa=MEMBER
INSERT INTO workspace_members (id, user_id, workspace_id, message, role) VALUES
('wm_mike_org2',  'user_5', 'org_2', '', 'ADMIN'),
('wm_sarah_org2', 'user_4', 'org_2', '', 'MEMBER'),
('wm_lisa_org2',  'user_6', 'org_2', '', 'MEMBER');

-- ============================================================
-- 4. Project memberships with diverse project roles
-- ============================================================
-- Update existing project_members to have specific roles
-- LaunchPad CRM: user_3=PROJECT_LEAD, user_1=CONTRIBUTOR, user_2=CONTRIBUTOR
UPDATE project_members SET role = 'PROJECT_LEAD' WHERE user_id = 'user_3' AND project_id = '4d0f6ef3-e798-4d65-a864-00d9f8085c51';
UPDATE project_members SET role = 'CONTRIBUTOR' WHERE user_id = 'user_1' AND project_id = '4d0f6ef3-e798-4d65-a864-00d9f8085c51';
UPDATE project_members SET role = 'CONTRIBUTOR' WHERE user_id = 'user_2' AND project_id = '4d0f6ef3-e798-4d65-a864-00d9f8085c51';

-- Brand Identity: user_3=PROJECT_LEAD, user_1=VIEWER, user_2=CONTRIBUTOR
UPDATE project_members SET role = 'PROJECT_LEAD' WHERE user_id = 'user_3' AND project_id = 'e5f0a667-e883-41c4-8c87-acb6494d6341';
UPDATE project_members SET role = 'VIEWER' WHERE user_id = 'user_1' AND project_id = 'e5f0a667-e883-41c4-8c87-acb6494d6341';
UPDATE project_members SET role = 'CONTRIBUTOR' WHERE user_id = 'user_2' AND project_id = 'e5f0a667-e883-41c4-8c87-acb6494d6341';

-- Kubernetes Migration: user_3=PROJECT_LEAD, user_1=CONTRIBUTOR, user_2=VIEWER
UPDATE project_members SET role = 'PROJECT_LEAD' WHERE user_id = 'user_3' AND project_id = 'c45e93ec-2f68-4f07-af4b-aa84f1bd407c';
UPDATE project_members SET role = 'CONTRIBUTOR' WHERE user_id = 'user_1' AND project_id = 'c45e93ec-2f68-4f07-af4b-aa84f1bd407c';
UPDATE project_members SET role = 'VIEWER' WHERE user_id = 'user_2' AND project_id = 'c45e93ec-2f68-4f07-af4b-aa84f1bd407c';

-- Automated Regression Suite: user_1=PROJECT_LEAD, user_3=CONTRIBUTOR, user_2=CONTRIBUTOR
UPDATE project_members SET role = 'PROJECT_LEAD' WHERE user_id = 'user_1' AND project_id = 'b190343f-a7b1-4a40-b483-ecc59835cba3';
UPDATE project_members SET role = 'CONTRIBUTOR' WHERE user_id = 'user_3' AND project_id = 'b190343f-a7b1-4a40-b483-ecc59835cba3';
UPDATE project_members SET role = 'CONTRIBUTOR' WHERE user_id = 'user_2' AND project_id = 'b190343f-a7b1-4a40-b483-ecc59835cba3';

-- Add new users to projects
-- LaunchPad CRM: Sarah=VIEWER, Mike=CONTRIBUTOR
INSERT INTO project_members (id, user_id, project_id, role) VALUES
('pm_sarah_crm', 'user_4', '4d0f6ef3-e798-4d65-a864-00d9f8085c51', 'VIEWER'),
('pm_mike_crm',  'user_5', '4d0f6ef3-e798-4d65-a864-00d9f8085c51', 'CONTRIBUTOR');

-- Brand Identity: Lisa=CONTRIBUTOR, David=VIEWER
INSERT INTO project_members (id, user_id, project_id, role) VALUES
('pm_lisa_brand',  'user_6', 'e5f0a667-e883-41c4-8c87-acb6494d6341', 'CONTRIBUTOR'),
('pm_david_brand', 'user_7', 'e5f0a667-e883-41c4-8c87-acb6494d6341', 'VIEWER');

-- Kubernetes Migration: Mike=PROJECT_LEAD (co-lead), Sarah=CONTRIBUTOR
INSERT INTO project_members (id, user_id, project_id, role) VALUES
('pm_mike_k8s',  'user_5', 'c45e93ec-2f68-4f07-af4b-aa84f1bd407c', 'PROJECT_LEAD'),
('pm_sarah_k8s', 'user_4', 'c45e93ec-2f68-4f07-af4b-aa84f1bd407c', 'CONTRIBUTOR');

-- Automated Regression: Lisa=VIEWER, David=CONTRIBUTOR
INSERT INTO project_members (id, user_id, project_id, role) VALUES
('pm_lisa_regr',  'user_6', 'b190343f-a7b1-4a40-b483-ecc59835cba3', 'VIEWER'),
('pm_david_regr', 'user_7', 'b190343f-a7b1-4a40-b483-ecc59835cba3', 'CONTRIBUTOR');

-- ============================================================
-- 5. Additional tasks (assign to new users)
-- ============================================================
INSERT INTO tasks (id, project_id, title, description, status, type, priority, assignee_id, due_date, created_at, updated_at) VALUES
-- LaunchPad CRM
('task_v4_01', '4d0f6ef3-e798-4d65-a864-00d9f8085c51', 'Set Up Analytics Pipeline',
 'Build data pipeline for real-time CRM analytics dashboard.', 'TODO', 'FEATURE', 'HIGH',
 'user_5', '2026-03-15', NOW(), NOW()),
('task_v4_02', '4d0f6ef3-e798-4d65-a864-00d9f8085c51', 'Write API Documentation',
 'Document all REST endpoints for the CRM module.', 'TODO', 'TASK', 'LOW',
 'user_4', '2026-03-20', NOW(), NOW()),

-- Brand Identity
('task_v4_03', 'e5f0a667-e883-41c4-8c87-acb6494d6341', 'Design Color Palette System',
 'Create a comprehensive color palette with primary, secondary, and accent colors.', 'IN_PROGRESS', 'FEATURE', 'HIGH',
 'user_6', '2026-02-28', NOW(), NOW()),
('task_v4_04', 'e5f0a667-e883-41c4-8c87-acb6494d6341', 'Review Brand Guidelines PDF',
 'Final review of the 40-page brand guidelines document.', 'TODO', 'TASK', 'MEDIUM',
 'user_7', '2026-03-05', NOW(), NOW()),

-- Kubernetes Migration
('task_v4_05', 'c45e93ec-2f68-4f07-af4b-aa84f1bd407c', 'Configure Helm Charts',
 'Create Helm charts for all microservices deployment.', 'IN_PROGRESS', 'TASK', 'HIGH',
 'user_5', '2026-02-20', NOW(), NOW()),
('task_v4_06', 'c45e93ec-2f68-4f07-af4b-aa84f1bd407c', 'Set Up Monitoring Stack',
 'Deploy Prometheus + Grafana for cluster monitoring.', 'TODO', 'FEATURE', 'MEDIUM',
 'user_4', '2026-03-01', NOW(), NOW()),

-- Automated Regression Suite
('task_v4_07', 'b190343f-a7b1-4a40-b483-ecc59835cba3', 'Create Test Data Fixtures',
 'Build reusable test data generators for all modules.', 'TODO', 'TASK', 'MEDIUM',
 'user_7', '2026-02-25', NOW(), NOW()),
('task_v4_08', 'b190343f-a7b1-4a40-b483-ecc59835cba3', 'Flaky Test Investigation',
 'Investigate and fix intermittent failures in login flow tests.', 'IN_PROGRESS', 'BUG', 'HIGH',
 'user_6', '2026-02-18', NOW(), NOW());

-- ============================================================
-- 6. Comments on tasks
-- ============================================================
INSERT INTO comments (id, content, user_id, task_id, created_at) VALUES
('cmt_01', 'Dashboard wireframes are ready for review. Check Figma link.', 'user_1', '24ca6d74-7d32-41db-a257-906a90bca8f4', NOW() - INTERVAL '5 days'),
('cmt_02', 'Looking good! Can we add a dark mode toggle?', 'user_3', '24ca6d74-7d32-41db-a257-906a90bca8f4', NOW() - INTERVAL '4 days'),
('cmt_03', 'SendGrid sandbox mode is set up. Waiting for production API key.', 'user_2', '9dbd5f04-5a29-4232-9e8c-a1d8e4c566df', NOW() - INTERVAL '3 days'),
('cmt_04', 'Found root cause: CSV parser not deduplicating on email field.', 'user_1', '0e6798ad-8a1d-4bca-b0cd-8199491dbf03', NOW() - INTERVAL '2 days'),
('cmt_05', 'Helm charts for auth-service and api-gateway are done.', 'user_5', 'task_v4_05', NOW() - INTERVAL '1 day'),
('cmt_06', 'Need to add resource limits before deploying to staging.', 'user_3', 'task_v4_05', NOW()),
('cmt_07', 'Color palette v1 shared in #design channel. Please review.', 'user_6', 'task_v4_03', NOW() - INTERVAL '2 days'),
('cmt_08', 'The flaky test is caused by a race condition in the session cleanup.', 'user_6', 'task_v4_08', NOW() - INTERVAL '1 day');
