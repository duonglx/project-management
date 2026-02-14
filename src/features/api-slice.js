import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';
import { clearAuth } from './auth-slice';
import { refreshTokenApi } from '../services/auth-api';

const baseQuery = fetchBaseQuery({
  baseUrl: '/api',
  credentials: 'include',
});

// Wrapper that handles 401 by refreshing the token and retrying
const baseQueryWithReauth = async (args, api, extraOptions) => {
  let result = await baseQuery(args, api, extraOptions);
  if (result.error?.status === 401) {
    try {
      await refreshTokenApi();
      result = await baseQuery(args, api, extraOptions);
    } catch {
      api.dispatch(clearAuth());
    }
  }
  return result;
};

export const apiSlice = createApi({
  reducerPath: 'api',
  baseQuery: baseQueryWithReauth,
  tagTypes: ['Workspace', 'Project', 'Task', 'Comment', 'User', 'RolePermission', 'AdminUser', 'Label', 'TaskStatus', 'CustomField'],
  endpoints: (builder) => ({
    // Users
    getUsers: builder.query({
      query: () => '/users?page=0&size=100',
      transformResponse: (res) => res.content,
      providesTags: ['User'],
    }),

    // Workspaces (auth-based, no userId needed)
    getWorkspaces: builder.query({
      query: () => '/workspaces?page=0&size=100',
      transformResponse: (res) => res.content,
      providesTags: ['Workspace'],
    }),
    getWorkspace: builder.query({
      query: (id) => `/workspaces/${id}`,
      providesTags: (result, error, id) => [{ type: 'Workspace', id }],
    }),
    createWorkspace: builder.mutation({
      query: (body) => ({ url: '/workspaces', method: 'POST', body }),
      invalidatesTags: ['Workspace'],
    }),
    updateWorkspace: builder.mutation({
      query: ({ workspaceId, ...body }) => ({
        url: `/workspaces/${workspaceId}`,
        method: 'PUT',
        body,
      }),
      invalidatesTags: ['Workspace'],
    }),
    getWorkspaceMembers: builder.query({
      query: (workspaceId) => `/workspaces/${workspaceId}/members`,
      providesTags: ['Workspace'],
    }),

    // Projects (nested under workspace)
    createProject: builder.mutation({
      query: ({ workspaceId, ...body }) => ({
        url: `/workspaces/${workspaceId}/projects`,
        method: 'POST',
        body,
      }),
      invalidatesTags: ['Workspace', 'Project'],
    }),
    updateProject: builder.mutation({
      query: ({ workspaceId, id, ...body }) => ({
        url: `/workspaces/${workspaceId}/projects/${id}`,
        method: 'PUT',
        body,
      }),
      invalidatesTags: ['Workspace', 'Project'],
    }),

    // Tasks (nested under workspace > project)
    createTask: builder.mutation({
      query: ({ workspaceId, projectId, ...body }) => ({
        url: `/workspaces/${workspaceId}/projects/${projectId}/tasks`,
        method: 'POST',
        body,
      }),
      invalidatesTags: ['Workspace', 'Project', 'Task'],
    }),
    updateTask: builder.mutation({
      query: ({ workspaceId, projectId, id, ...body }) => ({
        url: `/workspaces/${workspaceId}/projects/${projectId}/tasks/${id}`,
        method: 'PUT',
        body,
      }),
      invalidatesTags: ['Workspace', 'Project', 'Task'],
    }),
    deleteTask: builder.mutation({
      query: ({ workspaceId, projectId, id }) => ({
        url: `/workspaces/${workspaceId}/projects/${projectId}/tasks/${id}`,
        method: 'DELETE',
      }),
      invalidatesTags: ['Workspace', 'Project', 'Task'],
    }),
    deleteTasks: builder.mutation({
      query: ({ workspaceId, projectId, ids }) => ({
        url: `/workspaces/${workspaceId}/projects/${projectId}/tasks/batch`,
        method: 'DELETE',
        body: { ids },
      }),
      invalidatesTags: ['Workspace', 'Project', 'Task'],
    }),

    // Comments (nested under workspace > project > task)
    getComments: builder.query({
      query: ({ workspaceId, projectId, taskId }) =>
        `/workspaces/${workspaceId}/projects/${projectId}/tasks/${taskId}/comments?page=0&size=100`,
      transformResponse: (res) => res.content,
      providesTags: ['Comment'],
    }),
    createComment: builder.mutation({
      query: ({ workspaceId, projectId, taskId, ...body }) => ({
        url: `/workspaces/${workspaceId}/projects/${projectId}/tasks/${taskId}/comments`,
        method: 'POST',
        body,
      }),
      invalidatesTags: ['Comment'],
    }),

    // Role Permissions
    getRolePermissions: builder.query({
      query: (workspaceId) => `/workspaces/${workspaceId}/role-permissions`,
      providesTags: ['RolePermission'],
    }),
    updateRolePermissions: builder.mutation({
      query: ({ workspaceId, role, permissionNames }) => ({
        url: `/workspaces/${workspaceId}/role-permissions/${role}`,
        method: 'PUT',
        body: { permissionNames },
      }),
      invalidatesTags: ['RolePermission'],
    }),

    // Admin Users
    getAdminUsers: builder.query({
      query: () => '/admin/users',
      providesTags: ['AdminUser'],
    }),
    createAdminUser: builder.mutation({
      query: (body) => ({ url: '/admin/users', method: 'POST', body }),
      invalidatesTags: ['AdminUser', 'User'],
    }),
    updateAdminUser: builder.mutation({
      query: ({ id, ...body }) => ({ url: `/admin/users/${id}`, method: 'PUT', body }),
      invalidatesTags: ['AdminUser', 'User'],
    }),
    deleteAdminUser: builder.mutation({
      query: (id) => ({ url: `/admin/users/${id}`, method: 'DELETE' }),
      invalidatesTags: ['AdminUser', 'User'],
    }),

    // Task Statuses
    getTaskStatuses: builder.query({
      query: (workspaceId) => `/workspaces/${workspaceId}/task-statuses`,
      providesTags: ['TaskStatus'],
    }),
    createTaskStatus: builder.mutation({
      query: ({ workspaceId, ...body }) => ({
        url: `/workspaces/${workspaceId}/task-statuses`,
        method: 'POST',
        body,
      }),
      invalidatesTags: ['TaskStatus'],
    }),
    updateTaskStatus: builder.mutation({
      query: ({ workspaceId, statusId, ...body }) => ({
        url: `/workspaces/${workspaceId}/task-statuses/${statusId}`,
        method: 'PUT',
        body,
      }),
      invalidatesTags: ['TaskStatus'],
    }),
    deleteTaskStatus: builder.mutation({
      query: ({ workspaceId, statusId }) => ({
        url: `/workspaces/${workspaceId}/task-statuses/${statusId}`,
        method: 'DELETE',
      }),
      invalidatesTags: ['TaskStatus'],
    }),
    reorderTaskStatuses: builder.mutation({
      query: ({ workspaceId, items }) => ({
        url: `/workspaces/${workspaceId}/task-statuses/reorder`,
        method: 'PUT',
        body: { items },
      }),
      invalidatesTags: ['TaskStatus'],
    }),

    // Labels
    getLabels: builder.query({
      query: (workspaceId) => `/workspaces/${workspaceId}/labels`,
      providesTags: ['Label'],
    }),
    createLabel: builder.mutation({
      query: ({ workspaceId, ...body }) => ({
        url: `/workspaces/${workspaceId}/labels`,
        method: 'POST',
        body,
      }),
      invalidatesTags: ['Label'],
    }),
    updateLabel: builder.mutation({
      query: ({ workspaceId, labelId, ...body }) => ({
        url: `/workspaces/${workspaceId}/labels/${labelId}`,
        method: 'PUT',
        body,
      }),
      invalidatesTags: ['Label'],
    }),
    deleteLabel: builder.mutation({
      query: ({ workspaceId, labelId }) => ({
        url: `/workspaces/${workspaceId}/labels/${labelId}`,
        method: 'DELETE',
      }),
      invalidatesTags: ['Label'],
    }),

    // Workspace Actions
    transferOwnership: builder.mutation({
      query: ({ workspaceId, newOwnerId }) => ({
        url: `/workspaces/${workspaceId}/transfer-ownership`,
        method: 'PUT',
        body: { newOwnerId },
      }),
      invalidatesTags: ['Workspace'],
    }),
    deleteWorkspace: builder.mutation({
      query: (workspaceId) => ({
        url: `/workspaces/${workspaceId}`,
        method: 'DELETE',
      }),
      invalidatesTags: ['Workspace'],
    }),

    // Members
    addWorkspaceMember: builder.mutation({
      query: ({ workspaceId, ...body }) => ({
        url: `/workspaces/${workspaceId}/members`,
        method: 'POST',
        body,
      }),
      invalidatesTags: ['Workspace'],
    }),
    updateMemberRole: builder.mutation({
      query: ({ workspaceId, userId, role }) => ({
        url: `/workspaces/${workspaceId}/members/${userId}`,
        method: 'PUT',
        body: { role },
      }),
      invalidatesTags: ['Workspace'],
    }),
    removeWorkspaceMember: builder.mutation({
      query: ({ workspaceId, userId }) => ({
        url: `/workspaces/${workspaceId}/members/${userId}`,
        method: 'DELETE',
      }),
      invalidatesTags: ['Workspace'],
    }),
    addProjectMember: builder.mutation({
      query: ({ workspaceId, projectId, ...body }) => ({
        url: `/workspaces/${workspaceId}/projects/${projectId}/members`,
        method: 'POST',
        body,
      }),
      invalidatesTags: ['Workspace', 'Project'],
    }),

    // Custom Fields
    getCustomFields: builder.query({
      query: (workspaceId) => `/workspaces/${workspaceId}/custom-fields`,
      providesTags: ['CustomField'],
    }),
    createCustomField: builder.mutation({
      query: ({ workspaceId, ...body }) => ({
        url: `/workspaces/${workspaceId}/custom-fields`,
        method: 'POST',
        body,
      }),
      invalidatesTags: ['CustomField'],
    }),
    updateCustomField: builder.mutation({
      query: ({ workspaceId, fieldId, ...body }) => ({
        url: `/workspaces/${workspaceId}/custom-fields/${fieldId}`,
        method: 'PUT',
        body,
      }),
      invalidatesTags: ['CustomField'],
    }),
    deleteCustomField: builder.mutation({
      query: ({ workspaceId, fieldId }) => ({
        url: `/workspaces/${workspaceId}/custom-fields/${fieldId}`,
        method: 'DELETE',
      }),
      invalidatesTags: ['CustomField'],
    }),
    reorderCustomFields: builder.mutation({
      query: ({ workspaceId, items }) => ({
        url: `/workspaces/${workspaceId}/custom-fields/reorder`,
        method: 'PUT',
        body: { items },
      }),
      invalidatesTags: ['CustomField'],
    }),
    getTaskCustomFieldValues: builder.query({
      query: (taskId) => `/tasks/${taskId}/custom-fields`,
      providesTags: ['CustomField'],
    }),
    updateTaskCustomFieldValues: builder.mutation({
      query: ({ taskId, values }) => ({
        url: `/tasks/${taskId}/custom-fields`,
        method: 'PUT',
        body: values,
      }),
      invalidatesTags: ['CustomField', 'Task'],
    }),
  }),
});

export const {
  useGetUsersQuery,
  useGetWorkspacesQuery,
  useGetWorkspaceQuery,
  useCreateWorkspaceMutation,
  useUpdateWorkspaceMutation,
  useGetWorkspaceMembersQuery,
  useCreateProjectMutation,
  useUpdateProjectMutation,
  useCreateTaskMutation,
  useUpdateTaskMutation,
  useDeleteTaskMutation,
  useDeleteTasksMutation,
  useGetCommentsQuery,
  useCreateCommentMutation,
  useAddWorkspaceMemberMutation,
  useAddProjectMemberMutation,
  useGetRolePermissionsQuery,
  useUpdateRolePermissionsMutation,
  useGetTaskStatusesQuery,
  useCreateTaskStatusMutation,
  useUpdateTaskStatusMutation,
  useDeleteTaskStatusMutation,
  useReorderTaskStatusesMutation,
  useGetLabelsQuery,
  useCreateLabelMutation,
  useUpdateLabelMutation,
  useDeleteLabelMutation,
  useGetAdminUsersQuery,
  useCreateAdminUserMutation,
  useUpdateAdminUserMutation,
  useDeleteAdminUserMutation,
  useTransferOwnershipMutation,
  useDeleteWorkspaceMutation,
  useUpdateMemberRoleMutation,
  useRemoveWorkspaceMemberMutation,
  useGetCustomFieldsQuery,
  useCreateCustomFieldMutation,
  useUpdateCustomFieldMutation,
  useDeleteCustomFieldMutation,
  useReorderCustomFieldsMutation,
  useGetTaskCustomFieldValuesQuery,
  useUpdateTaskCustomFieldValuesMutation,
} = apiSlice;
