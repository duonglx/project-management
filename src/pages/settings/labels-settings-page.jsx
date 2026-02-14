import { useState, useMemo } from 'react';
import { useParams } from 'react-router-dom';
import { PlusIcon, PencilIcon, Trash2Icon, XIcon, SearchIcon, Loader2Icon } from 'lucide-react';
import { useGetLabelsQuery, useCreateLabelMutation, useUpdateLabelMutation, useDeleteLabelMutation } from '../../features/api-slice';
import ColorPresetPicker from '../../components/settings/color-preset-picker';
import toast from 'react-hot-toast';

const EMPTY_FORM = { name: '', color: '#3b82f6', description: '' };

export default function LabelsSettingsPage() {
    const { workspaceId } = useParams();
    const { data: labels = [], isLoading } = useGetLabelsQuery(workspaceId);
    const [createLabel] = useCreateLabelMutation();
    const [updateLabel] = useUpdateLabelMutation();
    const [deleteLabel] = useDeleteLabelMutation();

    const [search, setSearch] = useState('');
    const [showForm, setShowForm] = useState(false);
    const [editingId, setEditingId] = useState(null);
    const [form, setForm] = useState(EMPTY_FORM);

    const filteredLabels = useMemo(() =>
        labels.filter((l) => l.name.toLowerCase().includes(search.toLowerCase())),
        [labels, search]
    );

    const resetForm = () => { setForm(EMPTY_FORM); setShowForm(false); setEditingId(null); };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!form.name.trim()) return;
        try {
            if (editingId) {
                await updateLabel({ workspaceId, labelId: editingId, ...form }).unwrap();
                toast.success('Label updated');
            } else {
                await createLabel({ workspaceId, ...form }).unwrap();
                toast.success('Label created');
            }
            resetForm();
        } catch (err) {
            toast.error(err?.data?.message || err?.data?.error || 'Operation failed');
        }
    };

    const handleEdit = (label) => {
        setEditingId(label.id);
        setForm({ name: label.name, color: label.color, description: label.description || '' });
        setShowForm(true);
    };

    const handleDelete = async (labelId) => {
        if (!window.confirm('Delete this label? It will be removed from all tasks.')) return;
        try {
            await deleteLabel({ workspaceId, labelId }).unwrap();
            toast.success('Label deleted');
        } catch (err) {
            toast.error(err?.data?.message || 'Failed to delete label');
        }
    };

    if (isLoading) {
        return <div className="flex items-center gap-2 text-gray-500 dark:text-zinc-400"><Loader2Icon className="size-4 animate-spin" /> Loading...</div>;
    }

    const inputClass = 'w-full rounded border border-gray-300 dark:border-zinc-700 bg-white dark:bg-zinc-900 px-3 py-2 text-sm text-gray-900 dark:text-zinc-200 focus:outline-none focus:ring-2 focus:ring-blue-500';

    return (
        <div className="max-w-2xl">
            <div className="flex items-center justify-between mb-6">
                <h1 className="text-xl font-semibold text-gray-900 dark:text-zinc-100">Labels</h1>
                <button onClick={() => { resetForm(); setShowForm(true); }}
                    className="flex items-center gap-1.5 px-3 py-1.5 rounded bg-blue-600 hover:bg-blue-700 text-white text-sm transition">
                    <PlusIcon size={14} /> New Label
                </button>
            </div>

            {/* Create/Edit form */}
            {showForm && (
                <form onSubmit={handleSubmit} className="mb-6 p-4 rounded-lg border border-gray-200 dark:border-zinc-800 bg-gray-50 dark:bg-zinc-900/50 space-y-3">
                    <div className="flex items-center justify-between">
                        <h3 className="text-sm font-medium text-gray-900 dark:text-zinc-100">
                            {editingId ? 'Edit Label' : 'New Label'}
                        </h3>
                        <button type="button" onClick={resetForm}><XIcon size={16} className="text-gray-400" /></button>
                    </div>
                    <input value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })}
                        placeholder="Label name" className={inputClass} required />
                    <input value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })}
                        placeholder="Description (optional)" className={inputClass} />
                    <div>
                        <p className="text-xs text-gray-500 dark:text-zinc-400 mb-1.5">Color</p>
                        <ColorPresetPicker value={form.color} onChange={(color) => setForm({ ...form, color })} />
                    </div>
                    <button type="submit" className="px-4 py-1.5 rounded bg-blue-600 hover:bg-blue-700 text-white text-sm transition">
                        {editingId ? 'Update' : 'Create'}
                    </button>
                </form>
            )}

            {/* Search */}
            {labels.length > 0 && (
                <div className="relative mb-4">
                    <SearchIcon size={14} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
                    <input value={search} onChange={(e) => setSearch(e.target.value)}
                        placeholder="Filter labels..." className={`${inputClass} pl-8`} />
                </div>
            )}

            {/* Label list */}
            <div className="space-y-1">
                {filteredLabels.length === 0 ? (
                    <p className="text-sm text-gray-500 dark:text-zinc-400 text-center py-8">
                        {labels.length === 0 ? 'No labels yet. Create your first one!' : 'No labels match your search.'}
                    </p>
                ) : (
                    filteredLabels.map((label) => (
                        <div key={label.id} className="flex items-center gap-3 px-3 py-2.5 rounded-lg hover:bg-gray-50 dark:hover:bg-zinc-800/50 group transition">
                            <span className="w-4 h-4 rounded-full shrink-0" style={{ backgroundColor: label.color }} />
                            <div className="flex-1 min-w-0">
                                <p className="text-sm font-medium text-gray-900 dark:text-zinc-100 truncate">{label.name}</p>
                                {label.description && (
                                    <p className="text-xs text-gray-500 dark:text-zinc-400 truncate">{label.description}</p>
                                )}
                            </div>
                            <div className="flex items-center gap-1 opacity-0 group-hover:opacity-100 transition">
                                <button onClick={() => handleEdit(label)} className="p-1.5 rounded hover:bg-gray-200 dark:hover:bg-zinc-700 transition">
                                    <PencilIcon size={14} className="text-gray-500 dark:text-zinc-400" />
                                </button>
                                <button onClick={() => handleDelete(label.id)} className="p-1.5 rounded hover:bg-red-100 dark:hover:bg-red-900/30 transition">
                                    <Trash2Icon size={14} className="text-red-500" />
                                </button>
                            </div>
                        </div>
                    ))
                )}
            </div>

            <p className="text-xs text-gray-400 dark:text-zinc-500 mt-4">{labels.length} / 50 labels</p>
        </div>
    );
}
