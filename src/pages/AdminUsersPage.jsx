import { useState } from 'react';
import { UserPlusIcon, Trash2Icon, PencilIcon, Loader2Icon } from 'lucide-react';
import { useGetAdminUsersQuery, useCreateAdminUserMutation, useUpdateAdminUserMutation, useDeleteAdminUserMutation } from '../features/api-slice';
import toast from 'react-hot-toast';

const SYSTEM_ROLES = ['USER', 'ADMIN_WORKSPACE', 'SUPER_ADMIN'];

const emptyForm = { name: '', email: '', username: '', password: '', systemRole: 'USER', workspaceIds: [] };

export default function AdminUsersPage() {
  const { data: users = [], isLoading } = useGetAdminUsersQuery();
  const [createUser] = useCreateAdminUserMutation();
  const [updateUser] = useUpdateAdminUserMutation();
  const [deleteUser] = useDeleteAdminUserMutation();

  const [showForm, setShowForm] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [form, setForm] = useState(emptyForm);
  const [submitting, setSubmitting] = useState(false);

  const openCreate = () => {
    setEditingId(null);
    setForm(emptyForm);
    setShowForm(true);
  };

  const openEdit = (user) => {
    setEditingId(user.id);
    setForm({ name: user.name, email: user.email, username: user.username, password: '', systemRole: user.systemRole || 'USER', workspaceIds: [] });
    setShowForm(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      if (editingId) {
        const body = { name: form.name, email: form.email, systemRole: form.systemRole };
        if (form.password) body.password = form.password;
        await updateUser({ id: editingId, ...body }).unwrap();
        toast.success('User updated');
      } else {
        await createUser(form).unwrap();
        toast.success('User created');
      }
      setShowForm(false);
      setForm(emptyForm);
    } catch (err) {
      toast.error(err?.data?.message || err?.message || 'Operation failed');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this user permanently?')) return;
    try {
      await deleteUser(id).unwrap();
      toast.success('User deleted');
    } catch (err) {
      toast.error(err?.data?.message || 'Failed to delete user');
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
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-semibold text-gray-900 dark:text-white">User Management</h1>
          <p className="text-sm text-gray-500 dark:text-zinc-400">Create and manage system users</p>
        </div>
        <button onClick={openCreate} className="flex items-center gap-2 px-4 py-2 text-sm rounded bg-blue-600 hover:bg-blue-700 text-white">
          <UserPlusIcon className="size-4" /> Create User
        </button>
      </div>

      {/* Users Table */}
      <div className="overflow-x-auto border border-gray-200 dark:border-zinc-800 rounded-lg">
        <table className="min-w-full text-sm divide-y divide-gray-200 dark:divide-zinc-800">
          <thead className="bg-gray-50 dark:bg-zinc-900/50">
            <tr>
              <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 dark:text-zinc-400">Name</th>
              <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 dark:text-zinc-400">Username</th>
              <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 dark:text-zinc-400">Email</th>
              <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 dark:text-zinc-400">System Role</th>
              <th className="px-4 py-3 text-right text-xs font-medium text-gray-500 dark:text-zinc-400">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-200 dark:divide-zinc-800">
            {users.map((user) => (
              <tr key={user.id} className="hover:bg-gray-50 dark:hover:bg-zinc-800/50">
                <td className="px-4 py-3 text-gray-900 dark:text-white">{user.name}</td>
                <td className="px-4 py-3 text-gray-600 dark:text-zinc-400">{user.username}</td>
                <td className="px-4 py-3 text-gray-600 dark:text-zinc-400">{user.email}</td>
                <td className="px-4 py-3">
                  <span className={`px-2 py-0.5 text-xs rounded ${user.systemRole === 'SUPER_ADMIN' ? 'bg-red-100 dark:bg-red-900/30 text-red-700 dark:text-red-400' : user.systemRole === 'ADMIN_WORKSPACE' ? 'bg-purple-100 dark:bg-purple-900/30 text-purple-700 dark:text-purple-400' : 'bg-gray-100 dark:bg-zinc-700 text-gray-700 dark:text-zinc-300'}`}>
                    {user.systemRole}
                  </span>
                </td>
                <td className="px-4 py-3 text-right">
                  <div className="flex items-center justify-end gap-2">
                    <button onClick={() => openEdit(user)} className="p-1.5 rounded hover:bg-gray-100 dark:hover:bg-zinc-700 text-gray-500 dark:text-zinc-400">
                      <PencilIcon className="size-3.5" />
                    </button>
                    <button onClick={() => handleDelete(user.id)} className="p-1.5 rounded hover:bg-red-50 dark:hover:bg-red-900/20 text-gray-500 dark:text-zinc-400 hover:text-red-600">
                      <Trash2Icon className="size-3.5" />
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Create/Edit Modal */}
      {showForm && (
        <div className="fixed inset-0 bg-black/20 dark:bg-black/60 backdrop-blur flex items-center justify-center z-50">
          <div className="bg-white dark:bg-zinc-950 border border-zinc-200 dark:border-zinc-800 rounded-xl p-6 w-full max-w-md">
            <h2 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">
              {editingId ? 'Edit User' : 'Create User'}
            </h2>
            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label className="block text-sm mb-1 text-gray-700 dark:text-zinc-300">Name</label>
                <input value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} className="w-full px-3 py-2 rounded border border-gray-300 dark:border-zinc-700 dark:bg-zinc-900 text-sm text-gray-900 dark:text-white" required />
              </div>
              {!editingId && (
                <div>
                  <label className="block text-sm mb-1 text-gray-700 dark:text-zinc-300">Username</label>
                  <input value={form.username} onChange={(e) => setForm({ ...form, username: e.target.value })} className="w-full px-3 py-2 rounded border border-gray-300 dark:border-zinc-700 dark:bg-zinc-900 text-sm text-gray-900 dark:text-white" required />
                </div>
              )}
              <div>
                <label className="block text-sm mb-1 text-gray-700 dark:text-zinc-300">Email</label>
                <input type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} className="w-full px-3 py-2 rounded border border-gray-300 dark:border-zinc-700 dark:bg-zinc-900 text-sm text-gray-900 dark:text-white" required />
              </div>
              <div>
                <label className="block text-sm mb-1 text-gray-700 dark:text-zinc-300">
                  Password {editingId && <span className="text-xs text-gray-400">(leave blank to keep current)</span>}
                </label>
                <input type="password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} className="w-full px-3 py-2 rounded border border-gray-300 dark:border-zinc-700 dark:bg-zinc-900 text-sm text-gray-900 dark:text-white" {...(!editingId ? { required: true } : {})} />
              </div>
              <div>
                <label className="block text-sm mb-1 text-gray-700 dark:text-zinc-300">System Role</label>
                <select value={form.systemRole} onChange={(e) => setForm({ ...form, systemRole: e.target.value })} className="w-full px-3 py-2 rounded border border-gray-300 dark:border-zinc-700 dark:bg-zinc-900 text-sm text-gray-900 dark:text-white">
                  {SYSTEM_ROLES.map((r) => (
                    <option key={r} value={r}>{r}</option>
                  ))}
                </select>
              </div>
              <div className="flex justify-end gap-3 pt-2">
                <button type="button" onClick={() => setShowForm(false)} className="px-4 py-2 text-sm rounded border border-gray-300 dark:border-zinc-700 hover:bg-gray-100 dark:hover:bg-zinc-800">
                  Cancel
                </button>
                <button type="submit" disabled={submitting} className="px-4 py-2 text-sm rounded bg-blue-600 hover:bg-blue-700 text-white disabled:opacity-50 flex items-center gap-2">
                  {submitting && <Loader2Icon className="size-3 animate-spin" />}
                  {editingId ? 'Update' : 'Create'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
