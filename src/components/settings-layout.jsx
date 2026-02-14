import { NavLink, Outlet, useParams, Link } from 'react-router-dom';
import {
    Settings2Icon, UsersIcon, CircleDotIcon, TagIcon, TextCursorInputIcon,
    ShieldIcon, UserCogIcon, AlertTriangleIcon, ArrowLeftIcon
} from 'lucide-react';

// Sidebar navigation sections for settings hub
const sections = [
    {
        heading: 'General', items: [
            { path: 'general', label: 'General', icon: Settings2Icon },
            { path: 'members', label: 'Members', icon: UsersIcon },
        ]
    },
    {
        heading: 'Task Configuration', items: [
            { path: 'statuses', label: 'Statuses', icon: CircleDotIcon },
            { path: 'labels', label: 'Labels', icon: TagIcon },
            { path: 'custom-fields', label: 'Custom Fields', icon: TextCursorInputIcon },
        ]
    },
    {
        heading: 'Access Control', items: [
            { path: 'roles', label: 'Roles & Permissions', icon: ShieldIcon },
            { path: 'admin-users', label: 'Admin Users', icon: UserCogIcon },
        ]
    },
    {
        heading: 'Danger Zone', items: [
            { path: 'danger-zone', label: 'Danger Zone', icon: AlertTriangleIcon },
        ]
    },
];

const navLinkClass = ({ isActive }) =>
    `flex items-center gap-3 py-2 px-3 text-sm rounded transition-all ${isActive
        ? 'bg-gray-100 dark:bg-zinc-800 text-gray-900 dark:text-zinc-100 font-medium'
        : 'text-gray-600 dark:text-zinc-400 hover:bg-gray-50 dark:hover:bg-zinc-800/60 hover:text-gray-900 dark:hover:text-zinc-200'
    }`;

export default function SettingsLayout() {
    const { workspaceId } = useParams();

    return (
        <div className="flex h-full">
            {/* Settings Sidebar */}
            <aside className="w-56 shrink-0 border-r border-gray-200 dark:border-zinc-800 bg-white dark:bg-zinc-900 overflow-y-auto max-sm:hidden">
                <div className="p-4">
                    <Link
                        to={`/w/${workspaceId}/dashboard`}
                        className="flex items-center gap-2 text-sm text-gray-500 dark:text-zinc-400 hover:text-gray-900 dark:hover:text-zinc-200 mb-4 transition-colors"
                    >
                        <ArrowLeftIcon size={14} />
                        Back to workspace
                    </Link>

                    <h2 className="text-lg font-semibold text-gray-900 dark:text-zinc-100 mb-4">Settings</h2>

                    {sections.map((section) => (
                        <div key={section.heading} className="mb-4">
                            <p className="text-xs font-medium uppercase tracking-wider text-gray-400 dark:text-zinc-500 mb-1 px-3">
                                {section.heading}
                            </p>
                            {section.items.map((item) => (
                                <NavLink key={item.path} to={item.path} className={navLinkClass}>
                                    <item.icon size={16} />
                                    {item.label}
                                </NavLink>
                            ))}
                        </div>
                    ))}
                </div>
            </aside>

            {/* Mobile settings nav */}
            <div className="sm:hidden sticky top-0 z-10 bg-white dark:bg-zinc-900 border-b border-gray-200 dark:border-zinc-800 overflow-x-auto">
                <div className="flex gap-1 p-2">
                    {sections.flatMap(s => s.items).map((item) => (
                        <NavLink key={item.path} to={item.path}
                            className={({ isActive }) => `shrink-0 px-3 py-1.5 text-xs rounded-full transition-all ${isActive ? 'bg-gray-900 dark:bg-zinc-100 text-white dark:text-zinc-900' : 'text-gray-600 dark:text-zinc-400 hover:bg-gray-100 dark:hover:bg-zinc-800'}`}
                        >
                            {item.label}
                        </NavLink>
                    ))}
                </div>
            </div>

            {/* Content area */}
            <main className="flex-1 overflow-y-auto p-6">
                <Outlet />
            </main>
        </div>
    );
}
