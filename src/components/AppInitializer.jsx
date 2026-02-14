import { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useParams } from 'react-router-dom';
import { useGetWorkspaceQuery } from '../features/api-slice';
import { setCurrentWorkspaceId, setCurrentWorkspace } from '../features/workspaceSlice';
import { setActiveWorkspaceId } from '../features/auth-slice';
import { Loader2Icon } from 'lucide-react';

export default function AppInitializer({ children }) {
    const dispatch = useDispatch();
    const { workspaceId } = useParams();
    const { currentWorkspaceId } = useSelector((state) => state.workspace);

    // Sync URL workspaceId to redux stores (skip placeholder values)
    useEffect(() => {
        if (workspaceId && workspaceId !== 'select' && workspaceId !== currentWorkspaceId) {
            dispatch(setCurrentWorkspaceId(workspaceId));
            dispatch(setActiveWorkspaceId(workspaceId));
        }
    }, [workspaceId, currentWorkspaceId, dispatch]);

    const isValidWorkspaceId = workspaceId && workspaceId !== 'select';
    const { data: workspaceData, isLoading } = useGetWorkspaceQuery(workspaceId, {
        skip: !isValidWorkspaceId,
    });

    // Sync workspace data to redux
    useEffect(() => {
        if (workspaceData) {
            dispatch(setCurrentWorkspace(workspaceData));
        }
    }, [workspaceData, dispatch]);

    if (isLoading || (workspaceId && !workspaceData)) {
        return (
            <div className="flex items-center justify-center h-screen bg-white dark:bg-zinc-950">
                <Loader2Icon className="size-7 text-blue-500 animate-spin" />
            </div>
        );
    }

    return children;
}
