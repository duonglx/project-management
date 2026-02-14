import { ExternalLinkIcon } from 'lucide-react';

const inputClass = 'w-full rounded border border-gray-300 dark:border-zinc-700 bg-white dark:bg-zinc-900 px-3 py-2 text-sm text-gray-900 dark:text-zinc-200 focus:outline-none focus:ring-2 focus:ring-blue-500';

export default function CustomFieldRenderer({ field, value, onChange, disabled = false }) {
    const handleChange = (val) => onChange(field.id, val);

    switch (field.type) {
        case 'TEXT':
            return (
                <input type="text" value={value || ''} onChange={(e) => handleChange(e.target.value)}
                    disabled={disabled} maxLength={500} className={inputClass}
                    placeholder={field.name} />
            );
        case 'NUMBER':
            return (
                <input type="number" value={value || ''} onChange={(e) => handleChange(e.target.value)}
                    disabled={disabled} className={inputClass}
                    placeholder={field.name} />
            );
        case 'DROPDOWN':
            return (
                <select value={value || ''} onChange={(e) => handleChange(e.target.value)}
                    disabled={disabled} className={inputClass}>
                    <option value="">Select...</option>
                    {(field.options || []).map(opt => (
                        <option key={opt} value={opt}>{opt}</option>
                    ))}
                </select>
            );
        case 'DATE':
            return (
                <input type="date" value={value || ''} onChange={(e) => handleChange(e.target.value)}
                    disabled={disabled} className={inputClass} />
            );
        case 'CHECKBOX':
            return (
                <label className="flex items-center gap-2 text-sm text-gray-700 dark:text-zinc-300">
                    <input type="checkbox" checked={value === 'true'}
                        onChange={(e) => handleChange(e.target.checked ? 'true' : 'false')}
                        disabled={disabled} className="accent-blue-600" />
                    {field.name}
                </label>
            );
        case 'URL':
            return (
                <div className="flex items-center gap-2">
                    <input type="url" value={value || ''} onChange={(e) => handleChange(e.target.value)}
                        disabled={disabled} maxLength={2000} className={inputClass}
                        placeholder="https://..." />
                    {value && (
                        <a href={value} target="_blank" rel="noopener noreferrer"
                            className="p-2 rounded hover:bg-gray-100 dark:hover:bg-zinc-800 transition shrink-0">
                            <ExternalLinkIcon size={14} className="text-blue-500" />
                        </a>
                    )}
                </div>
            );
        default:
            return <input type="text" value={value || ''} onChange={(e) => handleChange(e.target.value)}
                disabled={disabled} className={inputClass} />;
    }
}
