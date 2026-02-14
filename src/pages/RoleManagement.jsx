import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { ShieldIcon, Loader2Icon, KeyRoundIcon, UsersIcon, SaveIcon } from 'lucide-react';
import { useGetRolePermissionsQuery, useUpdateRolePermissionsMutation } from '../features/api-slice';
import RolePermissionMatrix from '../components/role-permission-matrix';
import toast from 'react-hot-toast';

const WORKSPACE_ROLES = ['ADMIN', 'MEMBER'];
const PROJECT_ROLES = ['PROJECT_LEAD', 'CONTRIBUTOR', 'VIEWER'];
const ALL_ROLES = [...WORKSPACE_ROLES, ...PROJECT_ROLES];

export default function RoleManagement() {
  const { workspaceId } = useParams();
  const { data: rolePermissions, isLoading } = useGetRolePermissionsQuery(workspaceId);
  const [updateRolePermissions] = useUpdateRolePermissionsMutation();

  const [matrix, setMatrix] = useState({});
  const [allPermissions, setAllPermissions] = useState([]);
  const [saving, setSaving] = useState(null);

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
  }, [rolePermissions]);

  const togglePermission = (role, perm) => {
    setMatrix((prev) => {
      const s = new Set(prev[role] || []);
      s.has(perm) ? s.delete(perm) : s.add(perm);
      return { ...prev, [role]: s };
    });
  };

  const saveRole = async (role) => {
    setSaving(role);
    try {
      await updateRolePermissions({
        workspaceId,
        role,
        permissionNames: [...(matrix[role] || [])],
      }).unwrap();
      toast.success(`${role.replace(/_/g, ' ')} permissions updated`);
    } catch (err) {
      toast.error(err?.data?.message || 'Failed to update permissions');
    } finally {
      setSaving(null);
    }
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <Loader2Icon className="size-6 text-blue-500 animate-spin" />
      </div>
    );
  }

  return (
    <div className="max-w-6xl mx-auto space-y-6">
      {/* Header - consistent with Dashboard/Team */}
      <div>
        <h1 className="text-xl sm:text-2xl font-semibold text-gray-900 dark:text-white mb-1 flex items-center gap-2">
          <ShieldIcon className="size-5" /> Role & Permission Management
        </h1>
        <p className="text-gray-500 dark:text-zinc-400 text-sm">
          Configure which permissions each role has in this workspace.
        </p>
      </div>

      {/* Stats cards */}
      <div className="flex flex-wrap gap-4">
        <div className="max-sm:w-full dark:bg-gradient-to-br dark:from-zinc-800/70 dark:to-zinc-900/50 border border-gray-300 dark:border-zinc-800 rounded-lg p-6">
          <div className="flex items-center justify-between gap-8 md:gap-22">
            <div>
              <p className="text-sm text-gray-500 dark:text-zinc-400">Total Roles</p>
              <p className="text-xl font-bold text-gray-900 dark:text-white">{ALL_ROLES.length}</p>
            </div>
            <div className="p-3 rounded-xl bg-purple-100 dark:bg-purple-500/10">
              <UsersIcon className="size-4 text-purple-500 dark:text-purple-200" />
            </div>
          </div>
        </div>
        <div className="max-sm:w-full dark:bg-gradient-to-br dark:from-zinc-800/70 dark:to-zinc-900/50 border border-gray-300 dark:border-zinc-800 rounded-lg p-6">
          <div className="flex items-center justify-between gap-8 md:gap-22">
            <div>
              <p className="text-sm text-gray-500 dark:text-zinc-400">Permissions</p>
              <p className="text-xl font-bold text-gray-900 dark:text-white">{allPermissions.length}</p>
            </div>
            <div className="p-3 rounded-xl bg-blue-100 dark:bg-blue-500/10">
              <KeyRoundIcon className="size-4 text-blue-500 dark:text-blue-200" />
            </div>
          </div>
        </div>
      </div>

      {/* Permission matrix table */}
      <RolePermissionMatrix
        allPermissions={allPermissions}
        allRoles={ALL_ROLES}
        matrix={matrix}
        onToggle={togglePermission}
      />

      {/* Save buttons per role */}
      <div className="flex flex-wrap gap-3">
        {ALL_ROLES.map((role) => (
          <button
            key={role}
            onClick={() => saveRole(role)}
            disabled={saving === role}
            className="px-4 py-2 text-sm rounded bg-gradient-to-br from-blue-500 to-blue-600 hover:opacity-90 text-white disabled:opacity-50 flex items-center gap-2 transition cursor-pointer"
          >
            {saving === role ? (
              <Loader2Icon className="size-3.5 animate-spin" />
            ) : (
              <SaveIcon className="size-3.5" />
            )}
            Save {role.replace(/_/g, ' ')}
          </button>
        ))}
      </div>
    </div>
  );
}
