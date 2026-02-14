import { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { AlertTriangleIcon, Loader2Icon } from 'lucide-react';
import {
    useGetWorkspaceQuery, useGetWorkspaceMembersQuery,
    useTransferOwnershipMutation, useDeleteWorkspaceMutation
} from '../../features/api-slice';
import toast from 'react-hot-toast';

export default function DangerZonePage() {
    const { workspaceId } = useParams();
    const navigate = useNavigate();
    const { data: workspace, isLoading } = useGetWorkspaceQuery(workspaceId);
    const { data: members = [] } = useGetWorkspaceMembersQuery(workspaceId);
    const [transferOwnership] = useTransferOwnershipMutation();
    const [deleteWorkspace] = useDeleteWorkspaceMutation();

    const [newOwnerId, setNewOwnerId] = useState('');
    const [confirmName, setConfirmName] = useState('');
    const [showTransferModal, setShowTransferModal] = useState(false);
    const [showDeleteModal, setShowDeleteModal] = useState(false);

    // Filter to admin members excluding current owner
    const adminMembers = (Array.isArray(members) ? members : members?.content || [])
        .filter(m => m.role === 'ADMIN' && m.user?.id !== workspace?.ownerId);

    const selectedAdmin = adminMembers.find(m => m.user?.id === newOwnerId);

    const handleTransfer = async () => {
        try {
            await transferOwnership({ workspaceId, newOwnerId }).unwrap();
            toast.success('Ownership transferred');
            setShowTransferModal(false);
            setNewOwnerId('');
        } catch (err) {
            toast.error(err?.data?.message || 'Failed to transfer ownership');
        }
    };

    const handleDelete = async () => {
        try {
            await deleteWorkspace(workspaceId).unwrap();
            toast.success('Workspace deleted');
            navigate('/');
        } catch (err) {
            toast.error(err?.data?.message || 'Failed to delete workspace');
        }
    };

    if (isLoading) {
        return <div className="flex items-center gap-2 text-gray-500 dark:text-zinc-400"><Loader2Icon className="size-4 animate-spin" /> Loading...</div>;
    }

    const nameMatches = confirmName === workspace?.name;

    return (
        <div className="max-w-2xl space-y-6">
            <h1 className="text-xl font-semibold text-gray-900 dark:text-zinc-100 flex items-center gap-2">
                <AlertTriangleIcon size={20} className="text-red-500" /> Danger Zone
            </h1>

            {/* Transfer Ownership */}
            <div className="p-5 rounded-lg border-2 border-red-200 dark:border-red-900/50">
                <h2 className="text-base font-medium text-red-600 dark:text-red-400 mb-2">Transfer Ownership</h2>
                <p className="text-sm text-gray-600 dark:text-zinc-400 mb-4">
                    Transfer this workspace to another admin. You will be demoted to admin role.
                </p>
                {adminMembers.length === 0 ? (
                    <p className="text-sm text-gray-500 dark:text-zinc-500">No other admins available to transfer to.</p>
                ) : (
                    <div className="flex items-center gap-3">
                        <select value={newOwnerId} onChange={(e) => setNewOwnerId(e.target.value)}
                            className="flex-1 rounded border border-gray-300 dark:border-zinc-700 bg-white dark:bg-zinc-900 px-3 py-2 text-sm text-gray-900 dark:text-zinc-200">
                            <option value="">Select an admin...</option>
                            {adminMembers.map(m => (
                                <option key={m.user.id} value={m.user.id}>{m.user.name} ({m.user.email})</option>
                            ))}
                        </select>
                        <button onClick={() => setShowTransferModal(true)} disabled={!newOwnerId}
                            className="px-4 py-2 rounded border border-red-300 dark:border-red-700 text-red-600 dark:text-red-400 text-sm hover:bg-red-50 dark:hover:bg-red-900/20 disabled:opacity-40 transition">
                            Transfer
                        </button>
                    </div>
                )}
            </div>

            {/* Delete Workspace */}
            <div className="p-5 rounded-lg border-2 border-red-200 dark:border-red-900/50">
                <h2 className="text-base font-medium text-red-600 dark:text-red-400 mb-2">Delete Workspace</h2>
                <p className="text-sm text-gray-600 dark:text-zinc-400 mb-4">
                    This action is <strong>irreversible</strong>. All projects, tasks, and data will be permanently deleted.
                </p>
                <div className="space-y-3">
                    <div>
                        <p className="text-sm text-gray-700 dark:text-zinc-300 mb-1">
                            Type <strong className="text-red-600 dark:text-red-400">{workspace?.name}</strong> to confirm:
                        </p>
                        <input value={confirmName} onChange={(e) => setConfirmName(e.target.value)}
                            placeholder="Workspace name"
                            className="w-full rounded border border-gray-300 dark:border-zinc-700 bg-white dark:bg-zinc-900 px-3 py-2 text-sm text-gray-900 dark:text-zinc-200 focus:outline-none focus:ring-2 focus:ring-red-500" />
                    </div>
                    <button onClick={() => setShowDeleteModal(true)} disabled={!nameMatches}
                        className="px-4 py-2 rounded bg-red-600 hover:bg-red-700 text-white text-sm disabled:opacity-40 transition">
                        Delete Workspace
                    </button>
                </div>
            </div>

            {/* Transfer Confirmation Modal */}
            {showTransferModal && (
                <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm">
                    <div className="bg-white dark:bg-zinc-900 border border-gray-200 dark:border-zinc-700 rounded-lg p-6 max-w-md w-full mx-4 shadow-xl">
                        <h3 className="text-lg font-semibold text-gray-900 dark:text-zinc-100 mb-2">Confirm Transfer</h3>
                        <p className="text-sm text-gray-600 dark:text-zinc-400 mb-4">
                            Are you sure you want to transfer ownership to <strong>{selectedAdmin?.user?.name}</strong>? You will lose owner privileges.
                        </p>
                        <div className="flex justify-end gap-3">
                            <button onClick={() => setShowTransferModal(false)}
                                className="px-4 py-2 rounded border border-gray-300 dark:border-zinc-700 text-sm hover:bg-gray-50 dark:hover:bg-zinc-800 transition">Cancel</button>
                            <button onClick={handleTransfer}
                                className="px-4 py-2 rounded bg-red-600 hover:bg-red-700 text-white text-sm transition">Transfer</button>
                        </div>
                    </div>
                </div>
            )}

            {/* Delete Confirmation Modal */}
            {showDeleteModal && (
                <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm">
                    <div className="bg-white dark:bg-zinc-900 border border-red-200 dark:border-red-800 rounded-lg p-6 max-w-md w-full mx-4 shadow-xl">
                        <h3 className="text-lg font-semibold text-red-600 dark:text-red-400 mb-2">Delete Workspace</h3>
                        <p className="text-sm text-gray-600 dark:text-zinc-400 mb-4">
                            This will permanently delete <strong>{workspace?.name}</strong> and all its data. This cannot be undone.
                        </p>
                        <div className="flex justify-end gap-3">
                            <button onClick={() => setShowDeleteModal(false)}
                                className="px-4 py-2 rounded border border-gray-300 dark:border-zinc-700 text-sm hover:bg-gray-50 dark:hover:bg-zinc-800 transition">Cancel</button>
                            <button onClick={handleDelete}
                                className="px-4 py-2 rounded bg-red-600 hover:bg-red-700 text-white text-sm transition">Delete Forever</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}
