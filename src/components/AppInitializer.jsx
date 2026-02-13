import { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useGetUsersQuery, useGetWorkspacesQuery, useGetWorkspaceQuery } from '../features/api-slice';
import { setCurrentWorkspaceId, setCurrentWorkspace, setCurrentUserId } from '../features/workspaceSlice';

export default function AppInitializer({ children }) {
    const dispatch = useDispatch();
    const { currentWorkspaceId, currentUserId } = useSelector((state) => state.workspace);

    const { data: users } = useGetUsersQuery();

    // Auto-select first user
    useEffect(() => {
        if (users?.length > 0 && !currentUserId) {
            dispatch(setCurrentUserId(users[0].id));
        }
    }, [users, currentUserId, dispatch]);

    const { data: workspaces } = useGetWorkspacesQuery(currentUserId, { skip: !currentUserId });

    // Auto-select workspace
    useEffect(() => {
        if (workspaces?.length > 0 && !currentWorkspaceId) {
            dispatch(setCurrentWorkspaceId(workspaces[0].id));
        }
    }, [workspaces, currentWorkspaceId, dispatch]);

    const { data: workspaceData } = useGetWorkspaceQuery(currentWorkspaceId, { skip: !currentWorkspaceId });

    // Sync workspace data to redux
    useEffect(() => {
        if (workspaceData) {
            dispatch(setCurrentWorkspace(workspaceData));
        }
    }, [workspaceData, dispatch]);

    if (!currentUserId || !workspaces || !workspaceData) {
        return <div className="flex items-center justify-center h-screen text-gray-500">Loading...</div>;
    }

    return children;
}
