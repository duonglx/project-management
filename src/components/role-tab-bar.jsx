const ROLE_COLORS = {
  ADMIN: {
    active: 'bg-purple-600 text-white',
    inactive: 'bg-purple-100 dark:bg-purple-500/15 text-purple-600 dark:text-purple-400 hover:bg-purple-200 dark:hover:bg-purple-500/25',
  },
  MEMBER: {
    active: 'bg-blue-600 text-white',
    inactive: 'bg-blue-100 dark:bg-blue-500/15 text-blue-600 dark:text-blue-400 hover:bg-blue-200 dark:hover:bg-blue-500/25',
  },
  PROJECT_LEAD: {
    active: 'bg-emerald-600 text-white',
    inactive: 'bg-emerald-100 dark:bg-emerald-500/15 text-emerald-600 dark:text-emerald-400 hover:bg-emerald-200 dark:hover:bg-emerald-500/25',
  },
  CONTRIBUTOR: {
    active: 'bg-amber-600 text-white',
    inactive: 'bg-amber-100 dark:bg-amber-500/15 text-amber-600 dark:text-amber-400 hover:bg-amber-200 dark:hover:bg-amber-500/25',
  },
  VIEWER: {
    active: 'bg-zinc-600 text-white',
    inactive: 'bg-gray-100 dark:bg-zinc-700/50 text-gray-600 dark:text-zinc-400 hover:bg-gray-200 dark:hover:bg-zinc-600',
  },
};

function formatRoleName(role) {
  return role.replace(/_/g, ' ');
}

/**
 * Horizontal scrollable tab bar for selecting roles.
 * Shows color-coded badges with dirty state dot indicator.
 */
export default function RoleTabBar({ roles, activeRole, onSelect, dirtyRoles }) {
  return (
    <div className="flex gap-2 overflow-x-auto no-scrollbar pb-1" role="tablist">
      {roles.map((role) => {
        const isActive = role === activeRole;
        const isDirty = dirtyRoles?.has(role);
        const colors = ROLE_COLORS[role] || ROLE_COLORS.VIEWER;

        return (
          <button
            key={role}
            role="tab"
            aria-selected={isActive}
            onClick={() => onSelect(role)}
            className={`relative px-4 py-2 text-xs font-semibold uppercase tracking-wider rounded-lg transition-all cursor-pointer whitespace-nowrap ${
              isActive ? colors.active : colors.inactive
            }`}
          >
            {formatRoleName(role)}
            {isDirty && (
              <span className="absolute -top-1 -right-1 size-2.5 bg-orange-500 rounded-full border-2 border-white dark:border-zinc-900" />
            )}
          </button>
        );
      })}
    </div>
  );
}

export { ROLE_COLORS };
