import { useMemo } from 'react';
import { useSelector } from 'react-redux';
import { selectPermissions } from '../features/auth-slice';

export function usePermission() {
  const permissions = useSelector(selectPermissions);

  return useMemo(() => ({
    permissions,
    has: (perm) => permissions.includes(perm),
    hasAny: (perms) => perms.some((p) => permissions.includes(p)),
    hasAll: (perms) => perms.every((p) => permissions.includes(p)),
  }), [permissions]);
}
