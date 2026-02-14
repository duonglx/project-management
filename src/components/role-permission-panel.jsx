import { useState } from 'react';
import { ChevronDownIcon, ChevronRightIcon, ShieldOffIcon } from 'lucide-react';

/** Group flat permission list by prefix (e.g. "workspace", "project", "task") */
function groupByPrefix(permissions) {
  const groups = {};
  for (const p of permissions) {
    const colonIdx = p.indexOf(':');
    const prefix = colonIdx > -1 ? p.slice(0, colonIdx) : 'other';
    if (!groups[prefix]) groups[prefix] = [];
    groups[prefix].push(p);
  }
  return Object.entries(groups).sort(([a], [b]) => a.localeCompare(b));
}

/** Format "workspace:manage_settings" → "Manage settings" */
function formatPermName(perm) {
  const colonIdx = perm.indexOf(':');
  const name = colonIdx > -1 ? perm.slice(colonIdx + 1) : perm;
  const formatted = name.replace(/_/g, ' ');
  return formatted.charAt(0).toUpperCase() + formatted.slice(1);
}

/** Human-readable descriptions for common permissions */
const PERM_DESCRIPTIONS = {
  'workspace:manage_settings': 'Change workspace name, avatar, and general settings',
  'workspace:invite_members': 'Send invitations to new workspace members',
  'workspace:remove_members': 'Remove existing members from workspace',
  'workspace:delete': 'Permanently delete this workspace',
  'project:create': 'Create new projects in this workspace',
  'project:edit': 'Edit project details like name and description',
  'project:delete': 'Permanently delete projects',
  'task:create': 'Create new tasks within projects',
  'task:edit': 'Edit task details, assignees, and status',
  'task:delete': 'Permanently delete tasks',
  'task:assign': 'Assign or reassign tasks to members',
  'comment:create': 'Add comments to tasks',
  'comment:delete': 'Delete comments on tasks',
};

/**
 * Categorized permission list for a single role.
 * Each category is collapsible, shows "X/Y granted" count, uses toggle switches.
 * Supports search filtering from parent.
 */
export default function RolePermissionPanel({ allPermissions, grantedPerms, onToggle, searchQuery }) {
  const [collapsed, setCollapsed] = useState({});

  // Filter permissions by search query
  const filtered = searchQuery
    ? allPermissions.filter((p) => p.toLowerCase().includes(searchQuery.toLowerCase()))
    : allPermissions;

  const grouped = groupByPrefix(filtered);

  if (allPermissions.length === 0) {
    return (
      <div className="text-center py-16 border border-gray-200 dark:border-zinc-800 rounded-lg">
        <div className="w-20 h-20 mx-auto mb-4 bg-gray-200 dark:bg-zinc-800 rounded-full flex items-center justify-center">
          <ShieldOffIcon className="size-8 text-gray-400 dark:text-zinc-500" />
        </div>
        <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-1">No permissions found</h3>
        <p className="text-sm text-gray-500 dark:text-zinc-400">This workspace has no configurable permissions yet.</p>
      </div>
    );
  }

  if (filtered.length === 0) {
    return (
      <div className="text-center py-12 text-gray-500 dark:text-zinc-400 text-sm">
        No permissions match &ldquo;{searchQuery}&rdquo;
      </div>
    );
  }

  const toggleCollapse = (group) => {
    setCollapsed((prev) => ({ ...prev, [group]: !prev[group] }));
  };

  return (
    <div className="space-y-2">
      {grouped.map(([group, perms]) => {
        const grantedCount = perms.filter((p) => grantedPerms.has(p)).length;
        const isCollapsed = collapsed[group];

        return (
          <div key={group} className="border border-gray-200 dark:border-zinc-800 rounded-lg overflow-hidden">
            {/* Category header */}
            <button
              onClick={() => toggleCollapse(group)}
              className="w-full flex items-center justify-between px-4 py-3 bg-gray-50 dark:bg-zinc-800/40 hover:bg-gray-100 dark:hover:bg-zinc-800/60 transition-colors cursor-pointer"
            >
              <div className="flex items-center gap-2">
                {isCollapsed ? (
                  <ChevronRightIcon className="size-4 text-gray-400 dark:text-zinc-500" />
                ) : (
                  <ChevronDownIcon className="size-4 text-gray-400 dark:text-zinc-500" />
                )}
                <span className="text-sm font-semibold uppercase tracking-wide text-gray-700 dark:text-zinc-300">
                  {group}
                </span>
              </div>
              <span className="text-xs text-gray-500 dark:text-zinc-400 font-medium">
                {grantedCount}/{perms.length} granted
              </span>
            </button>

            {/* Permission list */}
            {!isCollapsed && (
              <div className="divide-y divide-gray-100 dark:divide-zinc-800/60">
                {perms.map((perm) => {
                  const checked = grantedPerms.has(perm);
                  const description = PERM_DESCRIPTIONS[perm];

                  return (
                    <div
                      key={perm}
                      className="flex items-center justify-between px-4 py-3 hover:bg-gray-50 dark:hover:bg-zinc-800/30 transition-colors"
                    >
                      <div className="flex-1 min-w-0 mr-4">
                        <p className="text-sm text-gray-800 dark:text-zinc-200">{formatPermName(perm)}</p>
                        {description && (
                          <p className="text-xs text-gray-400 dark:text-zinc-500 mt-0.5">{description}</p>
                        )}
                      </div>
                      {/* Toggle switch */}
                      <button
                        type="button"
                        role="switch"
                        aria-checked={checked}
                        aria-label={`${checked ? 'Revoke' : 'Grant'} ${formatPermName(perm)}`}
                        onClick={() => onToggle(perm)}
                        className={`relative inline-flex h-6 w-11 shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-blue-500 focus-visible:ring-offset-2 dark:focus-visible:ring-offset-zinc-900 ${
                          checked ? 'bg-blue-600' : 'bg-gray-200 dark:bg-zinc-700'
                        }`}
                      >
                        <span
                          className={`pointer-events-none inline-block size-5 transform rounded-full bg-white shadow-sm ring-0 transition-transform ${
                            checked ? 'translate-x-5' : 'translate-x-0'
                          }`}
                        />
                      </button>
                    </div>
                  );
                })}
              </div>
            )}
          </div>
        );
      })}
    </div>
  );
}

export { groupByPrefix, formatPermName };
