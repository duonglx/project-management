import { CheckIcon } from 'lucide-react';

const LABEL_COLORS = [
    { name: 'Red', value: '#ef4444' },
    { name: 'Orange', value: '#f97316' },
    { name: 'Amber', value: '#f59e0b' },
    { name: 'Green', value: '#22c55e' },
    { name: 'Teal', value: '#14b8a6' },
    { name: 'Blue', value: '#3b82f6' },
    { name: 'Indigo', value: '#6366f1' },
    { name: 'Purple', value: '#8b5cf6' },
    { name: 'Pink', value: '#ec4899' },
    { name: 'Gray', value: '#6b7280' },
];

export { LABEL_COLORS };

export default function ColorPresetPicker({ value, onChange }) {
    return (
        <div className="flex flex-wrap gap-2">
            {LABEL_COLORS.map((color) => (
                <button
                    key={color.value}
                    type="button"
                    title={color.name}
                    onClick={() => onChange(color.value)}
                    className="w-7 h-7 rounded-full flex items-center justify-center transition-transform hover:scale-110 ring-2 ring-offset-1 dark:ring-offset-zinc-900"
                    style={{
                        backgroundColor: color.value,
                        ringColor: value === color.value ? color.value : 'transparent',
                    }}
                >
                    {value === color.value && <CheckIcon size={14} className="text-white" />}
                </button>
            ))}
        </div>
    );
}
