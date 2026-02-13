import { useState } from 'react';

const roles = [
  { value: 'OWNER', label: 'Owner' },
  { value: 'ADMIN', label: 'Admin' },
  { value: 'MEMBER', label: 'Member' },
];

export default function MemberRoleSelector({ currentRole, onRoleChange, disabled = false }) {
  const [confirming, setConfirming] = useState(false);
  const [pendingRole, setPendingRole] = useState(null);

  const handleChange = (e) => {
    const newRole = e.target.value;
    if (newRole === currentRole) return;
    setPendingRole(newRole);
    setConfirming(true);
  };

  const confirmChange = () => {
    onRoleChange(pendingRole);
    setConfirming(false);
    setPendingRole(null);
  };

  const cancelChange = () => {
    setConfirming(false);
    setPendingRole(null);
  };

  return (
    <div className="relative inline-flex items-center gap-2">
      <select
        value={currentRole}
        onChange={handleChange}
        disabled={disabled || currentRole === 'OWNER'}
        className="text-xs px-2 py-1 rounded border border-gray-300 dark:border-zinc-700 bg-white dark:bg-zinc-800 text-gray-900 dark:text-zinc-200 disabled:opacity-50"
      >
        {roles.map((r) => (
          <option key={r.value} value={r.value}>{r.label}</option>
        ))}
      </select>

      {confirming && (
        <div className="absolute top-full left-0 mt-1 z-50 bg-white dark:bg-zinc-900 border border-gray-200 dark:border-zinc-700 rounded-lg shadow-lg p-3 min-w-48">
          <p className="text-xs text-gray-700 dark:text-zinc-300 mb-2">
            Change role to <span className="font-semibold">{pendingRole}</span>?
          </p>
          <div className="flex gap-2">
            <button onClick={confirmChange} className="text-xs px-3 py-1 rounded bg-blue-600 text-white hover:bg-blue-700">
              Confirm
            </button>
            <button onClick={cancelChange} className="text-xs px-3 py-1 rounded border border-gray-300 dark:border-zinc-700 hover:bg-gray-100 dark:hover:bg-zinc-800">
              Cancel
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
