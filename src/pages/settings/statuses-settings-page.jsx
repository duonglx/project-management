import { useState } from 'react';
import { useParams } from 'react-router-dom';
import { PlusIcon, PencilIcon, Trash2Icon, XIcon, ChevronUpIcon, ChevronDownIcon, Loader2Icon } from 'lucide-react';
import {
    useGetTaskStatusesQuery, useCreateTaskStatusMutation, useUpdateTaskStatusMutation,
    useDeleteTaskStatusMutation, useReorderTaskStatusesMutation
} from '../../features/api-slice';
import ColorPresetPicker from '../../components/settings/color-preset-picker';
import toast from 'react-hot-toast';

const CATEGORIES = [
    { value: 'NOT_STARTED', label: 'Not Started', badge: 'bg-gray-100 text-gray-700 dark:bg-zinc-700 dark:text-zinc-300' },
    { value: 'ACTIVE', label: 'Active', badge: 'bg-blue-100 text-blue-700 dark:bg-blue-900/50 dark:text-blue-300' },
    { value: 'DONE', label: 'Done', badge: 'bg-green-100 text-green-700 dark:bg-green-900/50 dark:text-green-300' },
    { value: 'CLOSED', label: 'Closed', badge: 'bg-red-100 text-red-700 dark:bg-red-900/50 dark:text-red-300' },
];

const EMPTY_FORM = { name: '', color: '#3b82f6', category: 'NOT_STARTED', isDefault: false };

export default function StatusesSettingsPage() {
    const { workspaceId } = useParams();
    const { data: statuses = [], isLoading } = useGetTaskStatusesQuery(workspaceId);
    const [createStatus] = useCreateTaskStatusMutation();
    const [updateStatus] = useUpdateTaskStatusMutation();
    const [deleteStatus] = useDeleteTaskStatusMutation();
    const [reorderStatuses] = useReorderTaskStatusesMutation();

    const [showForm, setShowForm] = useState(false);
    const [editingId, setEditingId] = useState(null);
    const [form, setForm] = useState(EMPTY_FORM);

    const resetForm = () => { setForm(EMPTY_FORM); setShowForm(false); setEditingId(null); };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!form.name.trim()) return;
        try {
            if (editingId) {
                await updateStatus({ workspaceId, statusId: editingId, ...form }).unwrap();
                toast.success('Status updated');
            } else {
                await createStatus({ workspaceId, ...form }).unwrap();
                toast.success('Status created');
            }
            resetForm();
        } catch (err) {
            toast.error(err?.data?.message || err?.data?.error || 'Operation failed');
        }
    };

    const handleEdit = (s) => {
        setEditingId(s.id);
        setForm({ name: s.name, color: s.color, category: s.category, isDefault: s.isDefault });
        setShowForm(true);
    };

    const handleDelete = async (statusId) => {
        if (!window.confirm('Delete this status? Tasks using it must be reassigned first.')) return;
        try {
            await deleteStatus({ workspaceId, statusId }).unwrap();
            toast.success('Status deleted');
        } catch (err) {
            toast.error(err?.data?.message || err?.data?.error || 'Cannot delete status');
        }
    };

    const handleMove = async (index, direction) => {
        const swapIdx = index + direction;
        if (swapIdx < 0 || swapIdx >= statuses.length) return;
        const items = statuses.map((s, i) => {
            if (i === index) return { id: s.id, position: swapIdx };
            if (i === swapIdx) return { id: s.id, position: index };
            return { id: s.id, position: i };
        });
        try {
            await reorderStatuses({ workspaceId, items }).unwrap();
        } catch {
            toast.error('Failed to reorder');
        }
    };

    const getCategoryBadge = (cat) => CATEGORIES.find(c => c.value === cat) || CATEGORIES[0];

    if (isLoading) {
        return <div className="flex items-center gap-2 text-gray-500 dark:text-zinc-400"><Loader2Icon className="size-4 animate-spin" /> Loading...</div>;
    }

    const inputClass = 'w-full rounded border border-gray-300 dark:border-zinc-700 bg-white dark:bg-zinc-900 px-3 py-2 text-sm text-gray-900 dark:text-zinc-200 focus:outline-none focus:ring-2 focus:ring-blue-500';

    return (
        <div className="max-w-2xl">
            <div className="flex items-center justify-between mb-6">
                <h1 className="text-xl font-semibold text-gray-900 dark:text-zinc-100">Task Statuses</h1>
                <button onClick={() => { resetForm(); setShowForm(true); }}
                    className="flex items-center gap-1.5 px-3 py-1.5 rounded bg-blue-600 hover:bg-blue-700 text-white text-sm transition">
                    <PlusIcon size={14} /> New Status
                </button>
            </div>

            {/* Create/Edit form */}
            {showForm && (
                <form onSubmit={handleSubmit} className="mb-6 p-4 rounded-lg border border-gray-200 dark:border-zinc-800 bg-gray-50 dark:bg-zinc-900/50 space-y-3">
                    <div className="flex items-center justify-between">
                        <h3 className="text-sm font-medium text-gray-900 dark:text-zinc-100">
                            {editingId ? 'Edit Status' : 'New Status'}
                        </h3>
                        <button type="button" onClick={resetForm}><XIcon size={16} className="text-gray-400" /></button>
                    </div>
                    <input value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })}
                        placeholder="Status name" className={inputClass} required />
                    <div>
                        <p className="text-xs text-gray-500 dark:text-zinc-400 mb-1.5">Category</p>
                        <select value={form.category} onChange={(e) => setForm({ ...form, category: e.target.value })} className={inputClass}>
                            {CATEGORIES.map(c => <option key={c.value} value={c.value}>{c.label}</option>)}
                        </select>
                    </div>
                    <div>
                        <p className="text-xs text-gray-500 dark:text-zinc-400 mb-1.5">Color</p>
                        <ColorPresetPicker value={form.color} onChange={(color) => setForm({ ...form, color })} />
                    </div>
                    <label className="flex items-center gap-2 text-sm text-gray-700 dark:text-zinc-300">
                        <input type="checkbox" checked={form.isDefault} onChange={(e) => setForm({ ...form, isDefault: e.target.checked })}
                            className="accent-blue-600" />
                        Default status for new tasks
                    </label>
                    <button type="submit" className="px-4 py-1.5 rounded bg-blue-600 hover:bg-blue-700 text-white text-sm transition">
                        {editingId ? 'Update' : 'Create'}
                    </button>
                </form>
            )}

            {/* Status list */}
            <div className="space-y-1">
                {statuses.length === 0 ? (
                    <p className="text-sm text-gray-500 dark:text-zinc-400 text-center py-8">No statuses configured.</p>
                ) : (
                    statuses.map((s, index) => {
                        const cat = getCategoryBadge(s.category);
                        return (
                            <div key={s.id} className="flex items-center gap-3 px-3 py-2.5 rounded-lg hover:bg-gray-50 dark:hover:bg-zinc-800/50 group transition">
                                <span className="w-3 h-3 rounded-full shrink-0" style={{ backgroundColor: s.color }} />
                                <div className="flex-1 min-w-0">
                                    <div className="flex items-center gap-2">
                                        <p className="text-sm font-medium text-gray-900 dark:text-zinc-100">{s.name}</p>
                                        {s.isDefault && (
                                            <span className="text-xs px-1.5 py-0.5 rounded bg-blue-100 text-blue-700 dark:bg-blue-900/50 dark:text-blue-300">Default</span>
                                        )}
                                    </div>
                                    <span className={`text-xs px-1.5 py-0.5 rounded ${cat.badge}`}>{cat.label}</span>
                                </div>

                                {/* Reorder + edit + delete */}
                                <div className="flex items-center gap-0.5 opacity-0 group-hover:opacity-100 transition">
                                    <button onClick={() => handleMove(index, -1)} disabled={index === 0}
                                        className="p-1 rounded hover:bg-gray-200 dark:hover:bg-zinc-700 disabled:opacity-30 transition">
                                        <ChevronUpIcon size={14} className="text-gray-500 dark:text-zinc-400" />
                                    </button>
                                    <button onClick={() => handleMove(index, 1)} disabled={index === statuses.length - 1}
                                        className="p-1 rounded hover:bg-gray-200 dark:hover:bg-zinc-700 disabled:opacity-30 transition">
                                        <ChevronDownIcon size={14} className="text-gray-500 dark:text-zinc-400" />
                                    </button>
                                    <button onClick={() => handleEdit(s)} className="p-1.5 rounded hover:bg-gray-200 dark:hover:bg-zinc-700 transition">
                                        <PencilIcon size={14} className="text-gray-500 dark:text-zinc-400" />
                                    </button>
                                    <button onClick={() => handleDelete(s.id)} className="p-1.5 rounded hover:bg-red-100 dark:hover:bg-red-900/30 transition">
                                        <Trash2Icon size={14} className="text-red-500" />
                                    </button>
                                </div>
                            </div>
                        );
                    })
                )}
            </div>
        </div>
    );
}
