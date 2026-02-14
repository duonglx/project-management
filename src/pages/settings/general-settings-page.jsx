import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { useGetWorkspaceQuery, useUpdateWorkspaceMutation } from '../../features/api-slice';
import toast from 'react-hot-toast';
import { Loader2Icon } from 'lucide-react';

const TIMEZONES = [
    'UTC', 'America/New_York', 'America/Chicago', 'America/Denver', 'America/Los_Angeles',
    'Europe/London', 'Europe/Paris', 'Europe/Berlin', 'Asia/Tokyo', 'Asia/Ho_Chi_Minh',
    'Asia/Singapore', 'Australia/Sydney',
];

const LANGUAGES = [
    { value: 'en', label: 'English' },
    { value: 'vi', label: 'Vietnamese' },
];

export default function GeneralSettingsPage() {
    const { workspaceId } = useParams();
    const { data: workspace, isLoading } = useGetWorkspaceQuery(workspaceId);
    const [updateWorkspace, { isLoading: isSaving }] = useUpdateWorkspaceMutation();

    const [form, setForm] = useState({ name: '', description: '', imageUrl: '', timezone: 'UTC', language: 'en' });

    // Pre-fill form when workspace data loads
    useEffect(() => {
        if (!workspace) return;
        setForm({
            name: workspace.name || '',
            description: workspace.description || '',
            imageUrl: workspace.imageUrl || '',
            timezone: workspace.settings?.timezone || 'UTC',
            language: workspace.settings?.language || 'en',
        });
    }, [workspace]);

    const handleSave = async (e) => {
        e.preventDefault();
        if (!form.name.trim()) {
            toast.error('Workspace name is required');
            return;
        }
        try {
            await updateWorkspace({
                workspaceId,
                name: form.name,
                description: form.description,
                imageUrl: form.imageUrl,
                settings: { timezone: form.timezone, language: form.language },
            }).unwrap();
            toast.success('Settings saved');
        } catch (err) {
            toast.error(err?.data?.message || 'Failed to save settings');
        }
    };

    if (isLoading) {
        return <div className="flex items-center gap-2 text-gray-500 dark:text-zinc-400"><Loader2Icon className="size-4 animate-spin" /> Loading...</div>;
    }

    const inputClass = 'w-full rounded border border-gray-300 dark:border-zinc-700 bg-white dark:bg-zinc-900 px-3 py-2 text-sm text-gray-900 dark:text-zinc-200 focus:outline-none focus:ring-2 focus:ring-blue-500';
    const labelClass = 'block text-sm font-medium text-gray-700 dark:text-zinc-300 mb-1';

    return (
        <div className="max-w-2xl">
            <h1 className="text-xl font-semibold text-gray-900 dark:text-zinc-100 mb-6">General Settings</h1>
            <form onSubmit={handleSave} className="space-y-5">
                <div>
                    <label className={labelClass}>Workspace Name *</label>
                    <input value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} className={inputClass} required />
                </div>

                <div>
                    <label className={labelClass}>Description</label>
                    <textarea value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} className={`${inputClass} h-24 resize-none`} />
                </div>

                <div>
                    <label className={labelClass}>Image URL</label>
                    <input value={form.imageUrl} onChange={(e) => setForm({ ...form, imageUrl: e.target.value })} className={inputClass} placeholder="https://..." />
                </div>

                <div className="grid grid-cols-2 gap-4">
                    <div>
                        <label className={labelClass}>Timezone</label>
                        <select value={form.timezone} onChange={(e) => setForm({ ...form, timezone: e.target.value })} className={inputClass}>
                            {TIMEZONES.map((tz) => <option key={tz} value={tz}>{tz}</option>)}
                        </select>
                    </div>
                    <div>
                        <label className={labelClass}>Language</label>
                        <select value={form.language} onChange={(e) => setForm({ ...form, language: e.target.value })} className={inputClass}>
                            {LANGUAGES.map((l) => <option key={l.value} value={l.value}>{l.label}</option>)}
                        </select>
                    </div>
                </div>

                <div className="pt-2">
                    <button type="submit" disabled={isSaving}
                        className="px-5 py-2 rounded bg-blue-600 hover:bg-blue-700 text-white text-sm font-medium transition disabled:opacity-50">
                        {isSaving ? 'Saving...' : 'Save Changes'}
                    </button>
                </div>
            </form>
        </div>
    );
}
