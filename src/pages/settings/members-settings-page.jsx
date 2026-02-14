import { useState, useMemo } from 'react';
import { useParams } from 'react-router-dom';
import { PlusIcon, SearchIcon, XIcon, Loader2Icon, Trash2Icon } from 'lucide-react';
import {
    useGetWorkspaceMembersQuery, useGetWorkspaceQuery, useGetUsersQuery,
    useAddWorkspaceMemberMutation, useUpdateMemberRoleMutation, useRemoveWorkspaceMemberMutation,
} from '../../features/api-slice';
import toast from 'react-hot-toast';

const ROLE_BADGES = {
    OWNER: 'bg-purple-100 text-purple-700 dark:bg-purple-900/50 dark:text-purple-300',
    ADMIN: 'bg-blue-100 text-blue-700 dark:bg-blue-900/50 dark:text-blue-300',
    MEMBER: 'bg-gray-100 text-gray-700 dark:bg-zinc-700 dark:text-zinc-300',
};

const ROLE_FILTERS = ['All', 'OWNER', 'ADMIN', 'MEMBER'];

export default function MembersSettingsPage() {
    const { workspaceId } = useParams();
    const { data: workspace } = useGetWorkspaceQuery(workspaceId);
    const { data: membersData, isLoading } = useGetWorkspaceMembersQuery(workspaceId);
    const { data: allUsers = [] } = useGetUsersQuery();
    const [addMember] = useAddWorkspaceMemberMutation();
    const [updateRole] = useUpdateMemberRoleMutation();
    const [removeMember] = useRemoveWorkspaceMemberMutation();

    const [search, setSearch] = useState('');
    const [roleFilter, setRoleFilter] = useState('All');
    const [showInvite, setShowInvite] = useState(false);
    const [inviteUserId, setInviteUserId] = useState('');
    const [inviteRole, setInviteRole] = useState('MEMBER');
    const [confirmRemove, setConfirmRemove] = useState(null);

    const members = useMemo(() => {
        const list = Array.isArray(membersData) ? membersData : membersData?.content || [];
        return list.filter(m => {
            const matchSearch = !search ||
                m.user?.name?.toLowerCase().includes(search.toLowerCase()) ||
                m.user?.email?.toLowerCase().includes(search.toLowerCase());
            const matchRole = roleFilter === 'All' || m.role === roleFilter;
            return matchSearch && matchRole;
        });
    }, [membersData, search, roleFilter]);

    const memberUserIds = useMemo(() => {
        const list = Array.isArray(membersData) ? membersData : membersData?.content || [];
        return new Set(list.map(m => m.user?.id));
    }, [membersData]);

    const availableUsers = allUsers.filter(u => !memberUserIds.has(u.id));

    const handleInvite = async () => {
        if (!inviteUserId) return;
        try {
            await addMember({ workspaceId, userId: inviteUserId, role: inviteRole }).unwrap();
            toast.success('Member added');
            setShowInvite(false);
            setInviteUserId('');
            setInviteRole('MEMBER');
        } catch (err) {
            toast.error(err?.data?.message || 'Failed to add member');
        }
    };

    const handleRoleChange = async (userId, newRole) => {
        try {
            await updateRole({ workspaceId, userId, role: newRole }).unwrap();
            toast.success('Role updated');
        } catch (err) {
            toast.error(err?.data?.message || 'Failed to update role');
        }
    };

    const handleRemove = async (userId) => {
        try {
            await removeMember({ workspaceId, userId }).unwrap();
            toast.success('Member removed');
            setConfirmRemove(null);
        } catch (err) {
            toast.error(err?.data?.message || 'Failed to remove member');
        }
    };

    if (isLoading) {
        return <div className="flex items-center gap-2 text-gray-500 dark:text-zinc-400"><Loader2Icon className="size-4 animate-spin" /> Loading...</div>;
    }

    const totalMembers = Array.isArray(membersData) ? membersData.length : membersData?.content?.length || 0;
    const inputClass = 'w-full rounded border border-gray-300 dark:border-zinc-700 bg-white dark:bg-zinc-900 px-3 py-2 text-sm text-gray-900 dark:text-zinc-200 focus:outline-none focus:ring-2 focus:ring-blue-500';

    return (
        <div className="max-w-2xl">
            {/* Header */}
            <div className="flex items-center justify-between mb-6">
                <div className="flex items-center gap-2">
                    <h1 className="text-xl font-semibold text-gray-900 dark:text-zinc-100">Members</h1>
                    <span className="text-xs px-2 py-0.5 rounded-full bg-gray-100 dark:bg-zinc-800 text-gray-600 dark:text-zinc-400">{totalMembers}</span>
                </div>
                <button onClick={() => setShowInvite(!showInvite)}
                    className="flex items-center gap-1.5 px-3 py-1.5 rounded bg-blue-600 hover:bg-blue-700 text-white text-sm transition">
                    <PlusIcon size={14} /> Add Member
                </button>
            </div>

            {/* Invite form */}
            {showInvite && (
                <div className="mb-6 p-4 rounded-lg border border-gray-200 dark:border-zinc-800 bg-gray-50 dark:bg-zinc-900/50 space-y-3">
                    <div className="flex items-center justify-between">
                        <h3 className="text-sm font-medium text-gray-900 dark:text-zinc-100">Add Member</h3>
                        <button type="button" onClick={() => setShowInvite(false)}><XIcon size={16} className="text-gray-400" /></button>
                    </div>
                    <select value={inviteUserId} onChange={(e) => setInviteUserId(e.target.value)} className={inputClass}>
                        <option value="">Select a user...</option>
                        {availableUsers.map(u => (
                            <option key={u.id} value={u.id}>{u.name} ({u.email})</option>
                        ))}
                    </select>
                    <select value={inviteRole} onChange={(e) => setInviteRole(e.target.value)} className={inputClass}>
                        <option value="MEMBER">Member</option>
                        <option value="ADMIN">Admin</option>
                    </select>
                    <button onClick={handleInvite} disabled={!inviteUserId}
                        className="px-4 py-1.5 rounded bg-blue-600 hover:bg-blue-700 text-white text-sm disabled:opacity-40 transition">
                        Add
                    </button>
                </div>
            )}

            {/* Search + Role Filter */}
            <div className="flex items-center gap-3 mb-4">
                <div className="relative flex-1">
                    <SearchIcon size={14} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
                    <input value={search} onChange={(e) => setSearch(e.target.value)}
                        placeholder="Search by name or email..."
                        className="w-full pl-9 pr-3 py-2 rounded border border-gray-300 dark:border-zinc-700 bg-white dark:bg-zinc-900 text-sm text-gray-900 dark:text-zinc-200 focus:outline-none focus:ring-2 focus:ring-blue-500" />
                </div>
                <div className="flex gap-1">
                    {ROLE_FILTERS.map(f => (
                        <button key={f} onClick={() => setRoleFilter(f)}
                            className={`px-2.5 py-1 rounded text-xs font-medium transition ${roleFilter === f
                                ? 'bg-blue-600 text-white'
                                : 'bg-gray-100 dark:bg-zinc-800 text-gray-600 dark:text-zinc-400 hover:bg-gray-200 dark:hover:bg-zinc-700'
                            }`}>
                            {f === 'All' ? 'All' : f.charAt(0) + f.slice(1).toLowerCase()}
                        </button>
                    ))}
                </div>
            </div>

            {/* Member List */}
            <div className="space-y-1">
                {members.length === 0 ? (
                    <p className="text-sm text-gray-500 dark:text-zinc-400 text-center py-8">No members found.</p>
                ) : (
                    members.map(m => {
                        const isOwner = m.role === 'OWNER';
                        return (
                            <div key={m.id} className="flex items-center gap-3 px-3 py-2.5 rounded-lg hover:bg-gray-50 dark:hover:bg-zinc-800/50 group transition">
                                {/* Avatar */}
                                <div className="w-8 h-8 rounded-full bg-blue-100 dark:bg-blue-900/50 flex items-center justify-center text-xs font-medium text-blue-700 dark:text-blue-300 shrink-0">
                                    {m.user?.name?.charAt(0)?.toUpperCase() || '?'}
                                </div>
                                {/* Info */}
                                <div className="flex-1 min-w-0">
                                    <p className="text-sm font-medium text-gray-900 dark:text-zinc-100 truncate">
                                        {m.user?.name}
                                        {m.user?.id === workspace?.ownerId && <span className="ml-1 text-xs text-gray-400">(owner)</span>}
                                    </p>
                                    <p className="text-xs text-gray-500 dark:text-zinc-400 truncate">{m.user?.email}</p>
                                </div>
                                {/* Role */}
                                {isOwner ? (
                                    <span className={`text-xs px-2 py-0.5 rounded font-medium ${ROLE_BADGES.OWNER}`}>Owner</span>
                                ) : (
                                    <select value={m.role} onChange={(e) => handleRoleChange(m.user?.id, e.target.value)}
                                        className="text-xs rounded border border-gray-300 dark:border-zinc-700 bg-white dark:bg-zinc-900 px-2 py-1 text-gray-700 dark:text-zinc-300">
                                        <option value="ADMIN">Admin</option>
                                        <option value="MEMBER">Member</option>
                                    </select>
                                )}
                                {/* Remove */}
                                {!isOwner && (
                                    <button onClick={() => setConfirmRemove(m)}
                                        className="p-1.5 rounded hover:bg-red-100 dark:hover:bg-red-900/30 opacity-0 group-hover:opacity-100 transition">
                                        <Trash2Icon size={14} className="text-red-500" />
                                    </button>
                                )}
                            </div>
                        );
                    })
                )}
            </div>

            {/* Remove Confirmation Modal */}
            {confirmRemove && (
                <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm">
                    <div className="bg-white dark:bg-zinc-900 border border-gray-200 dark:border-zinc-700 rounded-lg p-6 max-w-md w-full mx-4 shadow-xl">
                        <h3 className="text-lg font-semibold text-gray-900 dark:text-zinc-100 mb-2">Remove Member</h3>
                        <p className="text-sm text-gray-600 dark:text-zinc-400 mb-4">
                            Are you sure you want to remove <strong>{confirmRemove.user?.name}</strong> from this workspace?
                        </p>
                        <div className="flex justify-end gap-3">
                            <button onClick={() => setConfirmRemove(null)}
                                className="px-4 py-2 rounded border border-gray-300 dark:border-zinc-700 text-sm hover:bg-gray-50 dark:hover:bg-zinc-800 transition">Cancel</button>
                            <button onClick={() => handleRemove(confirmRemove.user?.id)}
                                className="px-4 py-2 rounded bg-red-600 hover:bg-red-700 text-white text-sm transition">Remove</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}
