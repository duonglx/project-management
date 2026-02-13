import { useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { Navigate, Outlet } from 'react-router-dom';
import { fetchCurrentUser, selectIsAuthenticated, selectAuthStatus, selectActiveWorkspaceId } from '../features/auth-slice';
import { Loader2Icon } from 'lucide-react';

export default function ProtectedRoute() {
  const dispatch = useDispatch();
  const isAuthenticated = useSelector(selectIsAuthenticated);
  const status = useSelector(selectAuthStatus);
  const activeWorkspaceId = useSelector(selectActiveWorkspaceId);

  useEffect(() => {
    if (!isAuthenticated && status !== 'loading') {
      dispatch(fetchCurrentUser(activeWorkspaceId));
    }
  }, [isAuthenticated, status, dispatch, activeWorkspaceId]);

  if (status === 'loading' || (status === 'idle' && !isAuthenticated)) {
    return (
      <div className="flex items-center justify-center h-screen bg-white dark:bg-zinc-950">
        <Loader2Icon className="size-7 text-blue-500 animate-spin" />
      </div>
    );
  }

  if (!isAuthenticated && status === 'failed') {
    return <Navigate to="/login" replace />;
  }

  return <Outlet />;
}
