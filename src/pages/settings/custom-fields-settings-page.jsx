import { useState } from 'react';
import { useParams } from 'react-router-dom';
import { PlusIcon, PencilIcon, Trash2Icon, XIcon, ChevronUpIcon, ChevronDownIcon, Loader2Icon } from 'lucide-react';
import {
    useGetCustomFieldsQuery, useCreateCustomFieldMutation, useUpdateCustomFieldMutation,
    useDeleteCustomFieldMutation, useReorderCustomFieldsMutation
} from '../../features/api-slice';
import toast from 'react-hot-toast';

const FIELD_TYPES = [
    { value: 'TEXT', label: 'Text', badge: 'bg-gray-100 text-gray-700 dark:bg-zinc-700 dark:text-zinc-300' },
    { value: 'NUMBER', label: 'Number', badge: 'bg-blue-100 text-blue-700 dark:bg-blue-900/50 dark:text-blue-300' },
    { value: 'DROPDOWN', label: 'Dropdown', badge: 'bg-purple-100 text-purple-700 dark:bg-purple-900/50 dark:text-purple-300' },
    { value: 'DATE', label: 'Date', badge: 'bg-green-100 text-green-700 dark:bg-green-900/50 dark:text-green-300' },
    { value: 'CHECKBOX', label: 'Checkbox', badge: 'bg-amber-100 text-amber-700 dark:bg-amber-900/50 dark:text-amber-300' },
    { value: 'URL', label: 'URL', badge: 'bg-teal-100 text-teal-700 dark:bg-teal-900/50 dark:text-teal-300' },
];

const MAX_FIELDS = 20;
const EMPTY_FORM = { name: '', type: 'TEXT', options: '', isRequired: false };

export default function CustomFieldsSettingsPage() {
    const { workspaceId } = useParams();
    const { data: fields = [], isLoading } = useGetCustomFieldsQuery(workspaceId);
    const [createField] = useCreateCustomFieldMutation();
    const [updateField] = useUpdateCustomFieldMutation();
    const [deleteField] = useDeleteCustomFieldMutation();
    const [reorderFields] = useReorderCustomFieldsMutation();

    const [showForm, setShowForm] = useState(false);
    const [editingId, setEditingId] = useState(null);
    const [form, setForm] = useState(EMPTY_FORM);

    const resetForm = () => { setForm(EMPTY_FORM); setShowForm(false); setEditingId(null); };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!form.name.trim()) return;
        const body = {
            name: form.name,
            type: form.type,
            isRequired: form.isRequired,
            options: form.type === 'DROPDOWN' ? form.options.split(',').map(o => o.trim()).filter(Boolean) : null,
        };
        try {
            if (editingId) {
                await updateField({ workspaceId, fieldId: editingId, ...body }).unwrap();
                toast.success('Field updated');
            } else {
                await createField({ workspaceId, ...body }).unwrap();
                toast.success('Field created');
            }
            resetForm();
        } catch (err) {
            toast.error(err?.data?.message || err?.data?.error || 'Operation failed');
        }
    };

    const handleEdit = (f) => {
        setEditingId(f.id);
        setForm({
            name: f.name,
            type: f.type,
            options: f.options ? f.options.join(', ') : '',
            isRequired: f.isRequired,
        });
        setShowForm(true);
    };

    const handleDelete = async (fieldId) => {
        if (!window.confirm('Delete this field? All task values for this field will be lost.')) return;
        try {
            await deleteField({ workspaceId, fieldId }).unwrap();
            toast.success('Field deleted');
        } catch (err) {
            toast.error(err?.data?.message || err?.data?.error || 'Cannot delete field');
        }
    };

    const handleMove = async (index, direction) => {
        const swapIdx = index + direction;
        if (swapIdx < 0 || swapIdx >= fields.length) return;
        const items = fields.map((f, i) => {
            if (i === index) return { id: f.id, position: swapIdx };
            if (i === swapIdx) return { id: f.id, position: index };
            return { id: f.id, position: i };
        });
        try {
            await reorderFields({ workspaceId, items }).unwrap();
        } catch {
            toast.error('Failed to reorder');
        }
    };

    const getTypeBadge = (type) => FIELD_TYPES.find(t => t.value === type) || FIELD_TYPES[0];

    if (isLoading) {
        return <div className="flex items-center gap-2 text-gray-500 dark:text-zinc-400"><Loader2Icon className="size-4 animate-spin" /> Loading...</div>;
    }

    const inputClass = 'w-full rounded border border-gray-300 dark:border-zinc-700 bg-white dark:bg-zinc-900 px-3 py-2 text-sm text-gray-900 dark:text-zinc-200 focus:outline-none focus:ring-2 focus:ring-blue-500';

    return (
        <div className="max-w-2xl">
            <div className="flex items-center justify-between mb-6">
                <div className="flex items-center gap-2">
                    <h1 className="text-xl font-semibold text-gray-900 dark:text-zinc-100">Custom Fields</h1>
                    <span className="text-xs px-2 py-0.5 rounded-full bg-gray-100 dark:bg-zinc-800 text-gray-600 dark:text-zinc-400">
                        {fields.length} / {MAX_FIELDS}
                    </span>
                </div>
                <button onClick={() => { resetForm(); setShowForm(true); }}
                    disabled={fields.length >= MAX_FIELDS}
                    className="flex items-center gap-1.5 px-3 py-1.5 rounded bg-blue-600 hover:bg-blue-700 text-white text-sm disabled:opacity-40 transition">
                    <PlusIcon size={14} /> New Field
                </button>
            </div>

            {/* Create/Edit form */}
            {showForm && (
                <form onSubmit={handleSubmit} className="mb-6 p-4 rounded-lg border border-gray-200 dark:border-zinc-800 bg-gray-50 dark:bg-zinc-900/50 space-y-3">
                    <div className="flex items-center justify-between">
                        <h3 className="text-sm font-medium text-gray-900 dark:text-zinc-100">
                            {editingId ? 'Edit Field' : 'New Field'}
                        </h3>
                        <button type="button" onClick={resetForm}><XIcon size={16} className="text-gray-400" /></button>
                    </div>
                    <input value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })}
                        placeholder="Field name" className={inputClass} required />
                    <div>
                        <p className="text-xs text-gray-500 dark:text-zinc-400 mb-1.5">Type</p>
                        <select value={form.type} onChange={(e) => setForm({ ...form, type: e.target.value })} className={inputClass}>
                            {FIELD_TYPES.map(t => <option key={t.value} value={t.value}>{t.label}</option>)}
                        </select>
                    </div>
                    {form.type === 'DROPDOWN' && (
                        <div>
                            <p className="text-xs text-gray-500 dark:text-zinc-400 mb-1.5">Options (comma-separated)</p>
                            <input value={form.options} onChange={(e) => setForm({ ...form, options: e.target.value })}
                                placeholder="Option 1, Option 2, Option 3" className={inputClass} />
                        </div>
                    )}
                    <label className="flex items-center gap-2 text-sm text-gray-700 dark:text-zinc-300">
                        <input type="checkbox" checked={form.isRequired} onChange={(e) => setForm({ ...form, isRequired: e.target.checked })}
                            className="accent-blue-600" />
                        Required field
                    </label>
                    <button type="submit" className="px-4 py-1.5 rounded bg-blue-600 hover:bg-blue-700 text-white text-sm transition">
                        {editingId ? 'Update' : 'Create'}
                    </button>
                </form>
            )}

            {/* Field list */}
            <div className="space-y-1">
                {fields.length === 0 ? (
                    <p className="text-sm text-gray-500 dark:text-zinc-400 text-center py-8">No custom fields configured.</p>
                ) : (
                    fields.map((f, index) => {
                        const typeBadge = getTypeBadge(f.type);
                        return (
                            <div key={f.id} className="flex items-center gap-3 px-3 py-2.5 rounded-lg hover:bg-gray-50 dark:hover:bg-zinc-800/50 group transition">
                                <div className="flex-1 min-w-0">
                                    <div className="flex items-center gap-2">
                                        <p className="text-sm font-medium text-gray-900 dark:text-zinc-100">{f.name}</p>
                                        {f.isRequired && (
                                            <span className="text-xs px-1.5 py-0.5 rounded bg-red-100 text-red-700 dark:bg-red-900/50 dark:text-red-300">Required</span>
                                        )}
                                    </div>
                                    <span className={`text-xs px-1.5 py-0.5 rounded ${typeBadge.badge}`}>{typeBadge.label}</span>
                                    {f.type === 'DROPDOWN' && f.options?.length > 0 && (
                                        <span className="ml-2 text-xs text-gray-400 dark:text-zinc-500">{f.options.join(', ')}</span>
                                    )}
                                </div>

                                {/* Reorder + edit + delete */}
                                <div className="flex items-center gap-0.5 opacity-0 group-hover:opacity-100 transition">
                                    <button onClick={() => handleMove(index, -1)} disabled={index === 0}
                                        className="p-1 rounded hover:bg-gray-200 dark:hover:bg-zinc-700 disabled:opacity-30 transition">
                                        <ChevronUpIcon size={14} className="text-gray-500 dark:text-zinc-400" />
                                    </button>
                                    <button onClick={() => handleMove(index, 1)} disabled={index === fields.length - 1}
                                        className="p-1 rounded hover:bg-gray-200 dark:hover:bg-zinc-700 disabled:opacity-30 transition">
                                        <ChevronDownIcon size={14} className="text-gray-500 dark:text-zinc-400" />
                                    </button>
                                    <button onClick={() => handleEdit(f)} className="p-1.5 rounded hover:bg-gray-200 dark:hover:bg-zinc-700 transition">
                                        <PencilIcon size={14} className="text-gray-500 dark:text-zinc-400" />
                                    </button>
                                    <button onClick={() => handleDelete(f.id)} className="p-1.5 rounded hover:bg-red-100 dark:hover:bg-red-900/30 transition">
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
