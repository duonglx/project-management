import { createSlice } from "@reduxjs/toolkit";

const initialState = {
    currentWorkspaceId: localStorage.getItem("currentWorkspaceId") || null,
    currentWorkspace: null,
    currentUserId: null,
};

const workspaceSlice = createSlice({
    name: "workspace",
    initialState,
    reducers: {
        setCurrentWorkspaceId: (state, action) => {
            state.currentWorkspaceId = action.payload;
            localStorage.setItem("currentWorkspaceId", action.payload);
        },
        setCurrentWorkspace: (state, action) => {
            state.currentWorkspace = action.payload;
        },
        setCurrentUserId: (state, action) => {
            state.currentUserId = action.payload;
        },
    },
});

export const { setCurrentWorkspaceId, setCurrentWorkspace, setCurrentUserId } = workspaceSlice.actions;
export default workspaceSlice.reducer;
