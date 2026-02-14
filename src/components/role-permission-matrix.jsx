import { CheckIcon, MinusIcon, ShieldOffIcon } from 'lucide-react';

const ROLE_COLORS = {
  ADMIN: 'bg-purple-100 dark:bg-purple-500/15 text-purple-600 dark:text-purple-400',
  MEMBER: 'bg-blue-100 dark:bg-blue-500/15 text-blue-600 dark:text-blue-400',
  PROJECT_LEAD: 'bg-emerald-100 dark:bg-emerald-500/15 text-emerald-600 dark:text-emerald-400',
  CONTRIBUTOR: 'bg-amber-100 dark:bg-amber-500/15 text-amber-600 dark:text-amber-400',
  VIEWER: 'bg-gray-100 dark:bg-zinc-700/50 text-gray-600 dark:text-zinc-400',
};

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

/** Format "workspace:manage_settings" → "manage settings" */
function formatPermName(perm) {
  const colonIdx = perm.indexOf(':');
  const name = colonIdx > -1 ? perm.slice(colonIdx + 1) : perm;
  return name.replace(/_/g, ' ');
}

function formatRoleName(role) {
  return role.replace(/_/g, ' ');
}

export default function RolePermissionMatrix({ allPermissions, allRoles, matrix, onToggle }) {
  const grouped = groupByPrefix(allPermissions);

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

  return (
    <div className="overflow-x-auto border border-gray-200 dark:border-zinc-800 rounded-lg" role="region" aria-label="Permission matrix">
      <table className="min-w-full text-sm" role="grid">
        <thead className="bg-gray-50 dark:bg-zinc-900/50 sticky top-0">
          <tr>
            <th scope="col" className="px-4 py-3 text-left text-xs font-medium text-gray-500 dark:text-zinc-400 uppercase tracking-wide w-56">
              Permission
            </th>
            {allRoles.map((role) => (
              <th scope="col" key={role} className="px-3 py-3 text-center">
                <span className={`inline-block px-2.5 py-1 text-[10px] font-semibold rounded-md uppercase tracking-wider ${ROLE_COLORS[role] || ROLE_COLORS.VIEWER}`}>
                  {formatRoleName(role)}
                </span>
              </th>
            ))}
          </tr>
        </thead>
        <tbody className="divide-y divide-gray-100 dark:divide-zinc-800/60">
          {grouped.map(([group, perms]) => (
            <GroupRows
              key={group}
              group={group}
              perms={perms}
              allRoles={allRoles}
              matrix={matrix}
              onToggle={onToggle}
            />
          ))}
        </tbody>
      </table>
    </div>
  );
}

/** Renders a group header row + permission rows for one category */
function GroupRows({ group, perms, allRoles, matrix, onToggle }) {
  return (
    <>
      {/* Group header */}
      <tr className="bg-gray-50/60 dark:bg-zinc-800/30">
        <td
          colSpan={allRoles.length + 1}
          className="px-4 py-2 text-[11px] font-semibold uppercase tracking-wider text-gray-400 dark:text-zinc-500"
        >
          {group}
        </td>
      </tr>
      {/* Permission rows */}
      {perms.map((perm) => (
        <tr key={perm} className="hover:bg-gray-50 dark:hover:bg-zinc-800/40 transition-colors">
          <td className="px-4 py-2.5 text-gray-700 dark:text-zinc-300 text-xs capitalize">
            {formatPermName(perm)}
          </td>
          {allRoles.map((role) => {
            const checked = matrix[role]?.has(perm) || false;
            return (
              <td key={role} className="px-3 py-2.5 text-center">
                <button
                  type="button"
                  onClick={() => onToggle(role, perm)}
                  className={`inline-flex items-center justify-center size-6 rounded transition-all cursor-pointer focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-blue-500 focus-visible:ring-offset-1 dark:focus-visible:ring-offset-zinc-900 ${
                    checked
                      ? 'bg-blue-600 dark:bg-blue-500 text-white shadow-sm'
                      : 'bg-gray-100 dark:bg-zinc-700 text-gray-400 dark:text-zinc-400 hover:bg-gray-200 dark:hover:bg-zinc-600'
                  }`}
                  aria-label={`${checked ? 'Revoke' : 'Grant'} ${perm} for ${role}`}
                >
                  {checked ? <CheckIcon className="size-3.5" /> : <MinusIcon className="size-3" />}
                </button>
              </td>
            );
          })}
        </tr>
      ))}
    </>
  );
}
