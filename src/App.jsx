import { useEffect } from "react";
import { Routes, Route, Navigate, useNavigate } from "react-router-dom";
import { useSelector, useDispatch } from "react-redux";
import Layout from "./pages/Layout";
import { Toaster } from "react-hot-toast";
import Dashboard from "./pages/Dashboard";
import Projects from "./pages/Projects";
import Team from "./pages/Team";
import ProjectDetails from "./pages/ProjectDetails";
import TaskDetails from "./pages/TaskDetails";
import LoginPage from "./pages/LoginPage";
import RoleManagement from "./pages/RoleManagement";
import AdminUsersPage from "./pages/AdminUsersPage";
import ProtectedRoute from "./components/ProtectedRoute";
import SettingsLayout from "./components/settings-layout";
import GeneralSettingsPage from "./pages/settings/general-settings-page";
import LabelsSettingsPage from "./pages/settings/labels-settings-page";
import StatusesSettingsPage from "./pages/settings/statuses-settings-page";
import DangerZonePage from "./pages/settings/danger-zone-page";
import MembersSettingsPage from "./pages/settings/members-settings-page";
import CustomFieldsSettingsPage from "./pages/settings/custom-fields-settings-page";
import { selectActiveWorkspaceId, selectIsAuthenticated, setActiveWorkspaceId } from "./features/auth-slice";
import { Loader2Icon } from "lucide-react";
import { useGetWorkspacesQuery } from "./features/api-slice";

function WorkspaceRedirect() {
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const activeWorkspaceId = useSelector(selectActiveWorkspaceId);
    const isAuthenticated = useSelector(selectIsAuthenticated);
    const hasValidWorkspace = activeWorkspaceId && activeWorkspaceId !== 'select';

    // Fetch workspaces only when authenticated without a valid workspace
    const shouldFetch = isAuthenticated && !hasValidWorkspace;
    const { data, isLoading } = useGetWorkspacesQuery({ page: 0, size: 1 }, { skip: !shouldFetch });

    // When workspace data arrives, set it and navigate
    useEffect(() => {
        if (!shouldFetch) return;
        const firstWs = data?.content?.[0];
        if (firstWs) {
            dispatch(setActiveWorkspaceId(firstWs.id));
            navigate(`/w/${firstWs.id}/dashboard`, { replace: true });
        }
    }, [data, shouldFetch, dispatch, navigate]);

    // Has valid workspace — redirect immediately
    if (hasValidWorkspace) {
        return <Navigate to={`/w/${activeWorkspaceId}/dashboard`} replace />;
    }

    // Not authenticated — redirect to login
    if (!isAuthenticated) {
        return <Navigate to="/login" replace />;
    }

    // Loading workspaces
    if (isLoading) {
        return (
            <div className="flex items-center justify-center h-screen bg-white dark:bg-zinc-950">
                <Loader2Icon className="size-7 text-blue-500 animate-spin" />
            </div>
        );
    }

    // No workspaces found — show message instead of looping back to /login
    if (data && !data.content?.length) {
        return (
            <div className="flex items-center justify-center h-screen bg-white dark:bg-zinc-950">
                <p className="text-gray-500 dark:text-gray-400">No workspaces available. Contact your administrator.</p>
            </div>
        );
    }

    return null;
}

const App = () => {
    return (
        <>
            <Toaster />
            <Routes>
                <Route path="/login" element={<LoginPage />} />
                <Route element={<ProtectedRoute />}>
                    <Route path="/w/:workspaceId" element={<Layout />}>
                        <Route path="dashboard" element={<Dashboard />} />
                        <Route path="team" element={<Team />} />
                        <Route path="projects" element={<Projects />} />
                        <Route path="projects/:projectId" element={<ProjectDetails />} />
                        <Route path="projects/:projectId/tasks/:taskId" element={<TaskDetails />} />
                        <Route path="settings" element={<SettingsLayout />}>
                            <Route index element={<Navigate to="general" replace />} />
                            <Route path="general" element={<GeneralSettingsPage />} />
                            <Route path="members" element={<MembersSettingsPage />} />
                            <Route path="statuses" element={<StatusesSettingsPage />} />
                            <Route path="labels" element={<LabelsSettingsPage />} />
                            <Route path="custom-fields" element={<CustomFieldsSettingsPage />} />
                            <Route path="roles" element={<RoleManagement />} />
                            <Route path="admin-users" element={<AdminUsersPage />} />
                            <Route path="danger-zone" element={<DangerZonePage />} />
                        </Route>
                    </Route>
                    <Route path="/" element={<WorkspaceRedirect />} />
                </Route>
                <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
        </>
    );
};

export default App;
