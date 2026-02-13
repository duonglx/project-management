import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { loginApi, fetchMeApi, logoutApi } from '../services/auth-api';

export const login = createAsyncThunk('auth/login', async ({ username, password }) => {
  const data = await loginApi(username, password);
  return data;
});

export const fetchCurrentUser = createAsyncThunk('auth/fetchCurrentUser', async (workspaceId) => {
  const data = await fetchMeApi(workspaceId);
  return data;
});

export const logout = createAsyncThunk('auth/logout', async () => {
  await logoutApi();
});

const initialState = {
  user: null,
  permissions: [],
  status: 'idle',
  error: null,
  isAuthenticated: false,
  activeWorkspaceId: localStorage.getItem('lastWorkspaceId') || null,
};

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    clearAuth: (state) => {
      state.user = null;
      state.permissions = [];
      state.isAuthenticated = false;
      state.status = 'idle';
      state.error = null;
    },
    setActiveWorkspaceId: (state, action) => {
      state.activeWorkspaceId = action.payload;
      localStorage.setItem('lastWorkspaceId', action.payload);
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(login.pending, (state) => { state.status = 'loading'; state.error = null; })
      .addCase(login.fulfilled, (state, action) => {
        state.status = 'succeeded';
        state.user = action.payload.user;
        state.isAuthenticated = true;
      })
      .addCase(login.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.error.message;
      })
      .addCase(fetchCurrentUser.fulfilled, (state, action) => {
        state.user = action.payload.user;
        state.permissions = action.payload.permissions || [];
        state.isAuthenticated = true;
        state.status = 'succeeded';
      })
      .addCase(fetchCurrentUser.rejected, (state) => {
        state.user = null;
        state.permissions = [];
        state.isAuthenticated = false;
        state.status = 'failed';
      })
      .addCase(logout.fulfilled, (state) => {
        state.user = null;
        state.permissions = [];
        state.isAuthenticated = false;
        state.status = 'idle';
      });
  },
});

export const { clearAuth, setActiveWorkspaceId } = authSlice.actions;

export const selectCurrentUser = (state) => state.auth.user;
export const selectIsAuthenticated = (state) => state.auth.isAuthenticated;
export const selectPermissions = (state) => state.auth.permissions;
export const selectActiveWorkspaceId = (state) => state.auth.activeWorkspaceId;
export const selectAuthStatus = (state) => state.auth.status;
export const selectAuthError = (state) => state.auth.error;

export default authSlice.reducer;
