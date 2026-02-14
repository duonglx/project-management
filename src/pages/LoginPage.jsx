import { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate, Navigate } from 'react-router-dom';
import { login, setActiveWorkspaceId, selectIsAuthenticated, selectAuthError, selectAuthStatus, selectActiveWorkspaceId } from '../features/auth-slice';
import { Loader2Icon } from 'lucide-react';

export default function LoginPage() {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const isAuthenticated = useSelector(selectIsAuthenticated);
  const error = useSelector(selectAuthError);
  const status = useSelector(selectAuthStatus);
  const activeWorkspaceId = useSelector(selectActiveWorkspaceId);

  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');

  // Treat placeholder values as no workspace
  const validWorkspaceId = activeWorkspaceId && activeWorkspaceId !== 'select' ? activeWorkspaceId : null;

  if (isAuthenticated) {
    // Always go through WorkspaceRedirect at "/" which handles workspace fetching
    const target = validWorkspaceId ? `/w/${validWorkspaceId}/dashboard` : '/';
    return <Navigate to={target} replace />;
  }


  const handleSubmit = async (e) => {
    e.preventDefault();
    const result = await dispatch(login({ username, password }));
    if (login.fulfilled.match(result)) {
      if (validWorkspaceId) {
        navigate(`/w/${validWorkspaceId}/dashboard`);
        return;
      }
      // First-time login: fetch workspaces to get a valid redirect target
      try {
        const res = await fetch('/api/workspaces?page=0&size=1', { credentials: 'include' });
        const data = await res.json();
        const firstWs = data.content?.[0];
        if (firstWs) {
          dispatch(setActiveWorkspaceId(firstWs.id));
          navigate(`/w/${firstWs.id}/dashboard`);
        } else {
          navigate('/');
        }
      } catch {
        navigate('/');
      }
    }
  };

  return (
    <div className="flex items-center justify-center min-h-screen bg-gray-50 dark:bg-zinc-950">
      <div className="w-full max-w-sm p-8 bg-white dark:bg-zinc-900 rounded-xl shadow-lg">
        <h1 className="text-2xl font-bold text-center mb-6 text-gray-900 dark:text-white">Sign In</h1>

        {error && (
          <div className="mb-4 p-3 bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-lg text-sm text-red-600 dark:text-red-400">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Username</label>
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 dark:border-zinc-700 rounded-lg bg-white dark:bg-zinc-800 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              required
              autoFocus
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Password</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 dark:border-zinc-700 rounded-lg bg-white dark:bg-zinc-800 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              required
            />
          </div>
          <button
            type="submit"
            disabled={status === 'loading'}
            className="w-full py-2.5 bg-blue-600 hover:bg-blue-700 text-white font-medium rounded-lg disabled:opacity-50 flex items-center justify-center gap-2"
          >
            {status === 'loading' && <Loader2Icon className="size-4 animate-spin" />}
            Sign In
          </button>
        </form>

        <p className="mt-4 text-xs text-center text-gray-500 dark:text-gray-400">
          Contact your administrator for account access.
        </p>
      </div>
    </div>
  );
}
