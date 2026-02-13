import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { ShieldIcon, Loader2Icon } from 'lucide-react';
import { useGetRolePermissionsQuery, useUpdateRolePermissionsMutation } from '../features/api-slice';
import toast from 'react-hot-toast';

const WORKSPACE_ROLES = ['ADMIN', 'MEMBER'];
const PROJECT_ROLES = ['PROJECT_LEAD', 'CONTRIBUTOR', 'VIEWER'];
const ALL_ROLES = [...WORKSPACE_ROLES, ...PROJECT_ROLES];

export default function RoleManagement() {
  const { workspaceId } = useParams();
  const { data: rolePermissions, isLoading } = useGetRolePermissionsQuery(workspaceId);
  const [updateRolePermissions] = useUpdateRolePermissionsMutation();

  // { [role]: Set<permissionName> }
  const [matrix, setMatrix] = useState({});
  const [allPermissions, setAllPermissions] = useState([]);
  const [saving, setSaving] = useState(null);

  useEffect(() => {
    if (!rolePermissions) return;
    const perms = new Set();
    const m = {};

    for (const rp of rolePermissions) {
      const role = rp.role;
      if (!m[role]) m[role] = new Set();
      m[role].add(rp.permission.name);
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
      toast.success(`${role} permissions updated`);
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
      <div>
        <h1 className="text-xl font-semibold text-gray-900 dark:text-white flex items-center gap-2">
          <ShieldIcon className="size-5" /> Role & Permission Management
        </h1>
        <p className="text-sm text-gray-500 dark:text-zinc-400 mt-1">
          Configure which permissions each role has in this workspace.
        </p>
      </div>

      <div className="overflow-x-auto border border-gray-200 dark:border-zinc-800 rounded-lg">
        <table className="min-w-full text-sm">
          <thead className="bg-gray-50 dark:bg-zinc-900/50">
            <tr>
              <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 dark:text-zinc-400 uppercase">Permission</th>
              {ALL_ROLES.map((role) => (
                <th key={role} className="px-3 py-3 text-center text-xs font-medium text-gray-500 dark:text-zinc-400 uppercase">
                  {role.replace('_', ' ')}
                </th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-200 dark:divide-zinc-800">
            {allPermissions.map((perm) => (
              <tr key={perm} className="hover:bg-gray-50 dark:hover:bg-zinc-800/50">
                <td className="px-4 py-2 text-gray-700 dark:text-zinc-300 text-xs font-mono">{perm}</td>
                {ALL_ROLES.map((role) => (
                  <td key={role} className="px-3 py-2 text-center">
                    <input
                      type="checkbox"
                      checked={matrix[role]?.has(perm) || false}
                      onChange={() => togglePermission(role, perm)}
                      className="size-3.5 accent-blue-600 dark:accent-blue-500"
                    />
                  </td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Save buttons per role */}
      <div className="flex flex-wrap gap-3">
        {ALL_ROLES.map((role) => (
          <button
            key={role}
            onClick={() => saveRole(role)}
            disabled={saving === role}
            className="px-4 py-2 text-sm rounded bg-blue-600 hover:bg-blue-700 text-white disabled:opacity-50 flex items-center gap-2"
          >
            {saving === role && <Loader2Icon className="size-3 animate-spin" />}
            Save {role.replace('_', ' ')}
          </button>
        ))}
      </div>
    </div>
  );
}
