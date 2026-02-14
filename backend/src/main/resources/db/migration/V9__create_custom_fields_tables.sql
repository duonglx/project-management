CREATE TABLE custom_field_definitions (
    id VARCHAR(36) PRIMARY KEY,
    workspace_id VARCHAR(36) NOT NULL,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL,
    options JSONB,
    is_required BOOLEAN NOT NULL DEFAULT false,
    position INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_cfd_workspace FOREIGN KEY (workspace_id)
        REFERENCES workspaces(id) ON DELETE CASCADE,
    CONSTRAINT uq_cfd_workspace_name UNIQUE (workspace_id, name)
);
CREATE INDEX idx_cfd_workspace_id ON custom_field_definitions(workspace_id);

CREATE TABLE custom_field_values (
    id VARCHAR(36) PRIMARY KEY,
    task_id VARCHAR(36) NOT NULL,
    field_id VARCHAR(36) NOT NULL,
    value TEXT,
    CONSTRAINT fk_cfv_task FOREIGN KEY (task_id)
        REFERENCES tasks(id) ON DELETE CASCADE,
    CONSTRAINT fk_cfv_field FOREIGN KEY (field_id)
        REFERENCES custom_field_definitions(id) ON DELETE CASCADE,
    CONSTRAINT uq_cfv_task_field UNIQUE (task_id, field_id)
);
CREATE INDEX idx_cfv_task_id ON custom_field_values(task_id);
CREATE INDEX idx_cfv_field_id ON custom_field_values(field_id);
