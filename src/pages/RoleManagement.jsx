import { useState, useEffect, useMemo, useCallback } from 'react';
import { useParams } from 'react-router-dom';
import { ShieldIcon, Loader2Icon, SaveIcon, RotateCcwIcon, SearchIcon } from 'lucide-react';
import { useGetRolePermissionsQuery, useUpdateRolePermissionsMutation } from '../features/api-slice';
import RoleTabBar from '../components/role-tab-bar';
import RolePermissionPanel from '../components/role-permission-panel';
import toast from 'react-hot-toast';

const WORKSPACE_ROLES = ['ADMIN', 'MEMBER'];
const PROJECT_ROLES = ['PROJECT_LEAD', 'CONTRIBUTOR', 'VIEWER'];
const ALL_ROLES = [...WORKSPACE_ROLES, ...PROJECT_ROLES];

export default function RoleManagement() {
  const { workspaceId } = useParams();
  const { data: rolePermissions, isLoading } = useGetRolePermissionsQuery(workspaceId);
  const [updateRolePermissions] = useUpdateRolePermissionsMutation();

  const [activeRole, setActiveRole] = useState(ALL_ROLES[0]);
  const [searchQuery, setSearchQuery] = useState('');
  const [saving, setSaving] = useState(false);

  // Server state (source of truth for dirty comparison)
  const [serverMatrix, setServerMatrix] = useState({});
  // Local editable state
  const [matrix, setMatrix] = useState({});
  const [allPermissions, setAllPermissions] = useState([]);

  useEffect(() => {
    if (!rolePermissions) return;
    const perms = new Set();
    const m = {};
    for (const rp of rolePermissions) {
      if (!rp.permission) continue;
      if (!m[rp.role]) m[rp.role] = new Set();
      m[rp.role].add(rp.permission.name);
      perms.add(rp.permission.name);
    }
    setAllPermissions([...perms].sort());
    setMatrix(m);
    setServerMatrix(m);
  }, [rolePermissions]);

  // Compute which roles have unsaved changes
  const dirtyRoles = useMemo(() => {
    const dirty = new Set();
    for (const role of ALL_ROLES) {
      const local = matrix[role] || new Set();
      const server = serverMatrix[role] || new Set();
      if (local.size !== server.size || [...local].some((p) => !server.has(p))) {
        dirty.add(role);
      }
    }
    return dirty;
  }, [matrix, serverMatrix]);

  const unsavedCount = dirtyRoles.size;
  const isActiveRoleDirty = dirtyRoles.has(activeRole);

  const togglePermission = useCallback((perm) => {
    setMatrix((prev) => {
      const s = new Set(prev[activeRole] || []);
      s.has(perm) ? s.delete(perm) : s.add(perm);
      return { ...prev, [activeRole]: s };
    });
  }, [activeRole]);

  const saveRole = async () => {
    setSaving(true);
    try {
      const savedPerms = [...(matrix[activeRole] || [])];
      await updateRolePermissions({
        workspaceId,
        role: activeRole,
        permissionNames: savedPerms,
      }).unwrap();
      // Sync server state for this role so dirty indicator clears
      setServerMatrix((prev) => ({
        ...prev,
        [activeRole]: new Set(savedPerms),
      }));
      toast.success(`${activeRole.replace(/_/g, ' ')} permissions updated`);
    } catch (err) {
      toast.error(err?.data?.message || 'Failed to update permissions');
    } finally {
      setSaving(false);
    }
  };

  const resetRole = () => {
    setMatrix((prev) => ({
      ...prev,
      [activeRole]: new Set(serverMatrix[activeRole] || []),
    }));
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <Loader2Icon className="size-6 text-blue-500 animate-spin" />
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto space-y-5">
      {/* Header */}
      <div>
        <h1 className="text-xl sm:text-2xl font-semibold text-gray-900 dark:text-white mb-1 flex items-center gap-2">
          <ShieldIcon className="size-5" /> Role & Permission Management
        </h1>
        <p className="text-gray-500 dark:text-zinc-400 text-sm">
          Select a role and configure its permissions.
        </p>
      </div>

      {/* Role tabs */}
      <RoleTabBar
        roles={ALL_ROLES}
        activeRole={activeRole}
        onSelect={setActiveRole}
        dirtyRoles={dirtyRoles}
      />

      {/* Search + action bar */}
      <div className="flex flex-col sm:flex-row gap-3 items-start sm:items-center justify-between">
        <div className="relative w-full sm:w-72">
          <SearchIcon className="absolute left-3 top-1/2 -translate-y-1/2 size-4 text-gray-400 dark:text-zinc-500" />
          <input
            type="text"
            placeholder="Search permissions..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full pl-9 pr-3 py-2 text-sm bg-white dark:bg-zinc-800 border border-gray-200 dark:border-zinc-700 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-gray-900 dark:text-zinc-100 placeholder:text-gray-400 dark:placeholder:text-zinc-500"
          />
        </div>

        <div className="flex items-center gap-3">
          {unsavedCount > 0 && (
            <span className="text-xs text-orange-500 font-medium">
              {unsavedCount} unsaved {unsavedCount === 1 ? 'change' : 'changes'}
            </span>
          )}
          <button
            onClick={resetRole}
            disabled={!isActiveRoleDirty}
            className="px-3 py-2 text-sm rounded-lg border border-gray-200 dark:border-zinc-700 text-gray-600 dark:text-zinc-400 hover:bg-gray-50 dark:hover:bg-zinc-800 disabled:opacity-40 disabled:cursor-not-allowed flex items-center gap-2 transition cursor-pointer"
          >
            <RotateCcwIcon className="size-3.5" />
            Reset
          </button>
          <button
            onClick={saveRole}
            disabled={!isActiveRoleDirty || saving}
            className="px-4 py-2 text-sm rounded-lg bg-gradient-to-br from-blue-500 to-blue-600 hover:opacity-90 text-white disabled:opacity-40 disabled:cursor-not-allowed flex items-center gap-2 transition cursor-pointer"
          >
            {saving ? (
              <Loader2Icon className="size-3.5 animate-spin" />
            ) : (
              <SaveIcon className="size-3.5" />
            )}
            Save Changes
          </button>
        </div>
      </div>

      {/* Permission panel for active role */}
      <RolePermissionPanel
        allPermissions={allPermissions}
        grantedPerms={matrix[activeRole] || new Set()}
        onToggle={togglePermission}
        searchQuery={searchQuery}
      />
    </div>
  );
}
