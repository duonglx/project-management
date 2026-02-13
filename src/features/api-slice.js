import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';

export const apiSlice = createApi({
  reducerPath: 'api',
  baseQuery: fetchBaseQuery({ baseUrl: '/api/v1' }),
  tagTypes: ['Workspace', 'Project', 'Task', 'Comment', 'User'],
  endpoints: (builder) => ({
    // Users
    getUsers: builder.query({
      query: () => '/users?page=0&size=100',
      transformResponse: (res) => res.content,
      providesTags: ['User'],
    }),

    // Workspaces
    getWorkspaces: builder.query({
      query: (userId) => `/workspaces?userId=${userId}&page=0&size=100`,
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

    // Projects
    createProject: builder.mutation({
      query: (body) => ({ url: '/projects', method: 'POST', body }),
      invalidatesTags: ['Workspace', 'Project'],
    }),
    updateProject: builder.mutation({
      query: ({ id, ...body }) => ({ url: `/projects/${id}`, method: 'PUT', body }),
      invalidatesTags: ['Workspace', 'Project'],
    }),

    // Tasks
    createTask: builder.mutation({
      query: (body) => ({ url: '/tasks', method: 'POST', body }),
      invalidatesTags: ['Workspace', 'Project', 'Task'],
    }),
    updateTask: builder.mutation({
      query: ({ id, ...body }) => ({ url: `/tasks/${id}`, method: 'PUT', body }),
      invalidatesTags: ['Workspace', 'Project', 'Task'],
    }),
    deleteTask: builder.mutation({
      query: (id) => ({ url: `/tasks/${id}`, method: 'DELETE' }),
      invalidatesTags: ['Workspace', 'Project', 'Task'],
    }),
    deleteTasks: builder.mutation({
      query: (ids) => ({ url: '/tasks/batch', method: 'DELETE', body: { ids } }),
      invalidatesTags: ['Workspace', 'Project', 'Task'],
    }),

    // Comments
    getComments: builder.query({
      query: (taskId) => `/tasks/${taskId}/comments?page=0&size=100`,
      transformResponse: (res) => res.content,
      providesTags: ['Comment'],
    }),
    createComment: builder.mutation({
      query: ({ taskId, ...body }) => ({ url: `/tasks/${taskId}/comments`, method: 'POST', body }),
      invalidatesTags: ['Comment'],
    }),

    // Members
    addWorkspaceMember: builder.mutation({
      query: ({ workspaceId, ...body }) => ({ url: `/workspaces/${workspaceId}/members`, method: 'POST', body }),
      invalidatesTags: ['Workspace'],
    }),
    addProjectMember: builder.mutation({
      query: ({ projectId, ...body }) => ({ url: `/projects/${projectId}/members`, method: 'POST', body }),
      invalidatesTags: ['Workspace', 'Project'],
    }),
  }),
});

export const {
  useGetUsersQuery,
  useGetWorkspacesQuery,
  useGetWorkspaceQuery,
  useCreateWorkspaceMutation,
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
} = apiSlice;
