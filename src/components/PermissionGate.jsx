import { usePermission } from '../hooks/use-permission';

export default function PermissionGate({ permission, permissions, requireAll = false, fallback = null, children }) {
  const { has, hasAny, hasAll } = usePermission();

  let authorized = false;
  if (permission) {
    authorized = has(permission);
  } else if (permissions) {
    authorized = requireAll ? hasAll(permissions) : hasAny(permissions);
  }

  return authorized ? children : fallback;
}
