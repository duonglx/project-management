import { Routes, Route, Navigate } from "react-router-dom";
import { useSelector } from "react-redux";
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
import { selectActiveWorkspaceId } from "./features/auth-slice";

function WorkspaceRedirect() {
    const activeWorkspaceId = useSelector(selectActiveWorkspaceId);
    if (activeWorkspaceId) {
        return <Navigate to={`/w/${activeWorkspaceId}/dashboard`} replace />;
    }
    return <Navigate to="/login" replace />;
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
                        <Route path="settings/roles" element={<RoleManagement />} />
                    </Route>
                    <Route path="/admin/users" element={<AdminUsersPage />} />
                    <Route path="/" element={<WorkspaceRedirect />} />
                </Route>
                <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
        </>
    );
};

export default App;
