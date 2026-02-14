-- Add status_id column to tasks
ALTER TABLE tasks ADD COLUMN status_id VARCHAR(36);

-- Map existing string values to status IDs via workspace context
UPDATE tasks t SET status_id = ts.id
FROM task_statuses ts
JOIN projects p ON p.workspace_id = ts.workspace_id
WHERE p.id = t.project_id
  AND ts.slug = CASE t.status
    WHEN 'TODO' THEN 'todo'
    WHEN 'IN_PROGRESS' THEN 'in-progress'
    WHEN 'DONE' THEN 'done'
  END;

-- Ensure all tasks got mapped (safety check)
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM tasks WHERE status_id IS NULL) THEN
        RAISE EXCEPTION 'Some tasks could not be mapped to status_id — aborting migration';
    END IF;
END $$;

-- Make status_id NOT NULL and add FK
ALTER TABLE tasks ALTER COLUMN status_id SET NOT NULL;
ALTER TABLE tasks ADD CONSTRAINT fk_tasks_status
    FOREIGN KEY (status_id) REFERENCES task_statuses(id) ON DELETE RESTRICT;
CREATE INDEX idx_tasks_status_id ON tasks(status_id);

-- Drop old status column
ALTER TABLE tasks DROP COLUMN status;
