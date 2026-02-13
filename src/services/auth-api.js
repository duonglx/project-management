const BASE_URL = '/api/auth';

export async function loginApi(username, password) {
  const res = await fetch(`${BASE_URL}/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include',
    body: JSON.stringify({ username, password }),
  });
  if (!res.ok) {
    const error = await res.json().catch(() => ({}));
    throw new Error(error.message || 'Invalid credentials');
  }
  return res.json();
}

export async function fetchMeApi(workspaceId) {
  const params = workspaceId ? `?workspaceId=${workspaceId}` : '';
  const res = await fetch(`${BASE_URL}/me${params}`, {
    credentials: 'include',
  });
  if (!res.ok) throw new Error('Not authenticated');
  return res.json();
}

export async function logoutApi() {
  await fetch(`${BASE_URL}/logout`, {
    method: 'POST',
    credentials: 'include',
  });
}

export async function refreshTokenApi() {
  const res = await fetch(`${BASE_URL}/refresh`, {
    method: 'POST',
    credentials: 'include',
  });
  if (!res.ok) throw new Error('Refresh failed');
  return res.json();
}
