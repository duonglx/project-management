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
  tagTypes: ['Workspace', 'Project', 'Task', 'Comment', 'User', 'RolePermission', 'AdminUser'],
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

    // Members
    addWorkspaceMember: builder.mutation({
      query: ({ workspaceId, ...body }) => ({
        url: `/workspaces/${workspaceId}/members`,
        method: 'POST',
        body,
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
  }),
});

export const {
  useGetUsersQuery,
  useGetWorkspacesQuery,
  useGetWorkspaceQuery,
  useCreateWorkspaceMutation,
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
  useGetAdminUsersQuery,
  useCreateAdminUserMutation,
  useUpdateAdminUserMutation,
  useDeleteAdminUserMutation,
} = apiSlice;
