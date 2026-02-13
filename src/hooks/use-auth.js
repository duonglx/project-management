import { useSelector, useDispatch } from 'react-redux';
import { selectCurrentUser, selectIsAuthenticated, selectPermissions, logout as logoutAction } from '../features/auth-slice';
import { useNavigate } from 'react-router-dom';

export function useAuth() {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const user = useSelector(selectCurrentUser);
  const isAuthenticated = useSelector(selectIsAuthenticated);
  const permissions = useSelector(selectPermissions);

  const handleLogout = async () => {
    await dispatch(logoutAction());
    navigate('/login');
  };

  return { user, isAuthenticated, permissions, logout: handleLogout };
}
