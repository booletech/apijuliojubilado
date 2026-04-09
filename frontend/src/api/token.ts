const TOKEN_KEY = 'accessToken';

export const getToken = (): string | null => {
  return localStorage.getItem(TOKEN_KEY);
};

export const setToken = (token: string): void => {
  localStorage.setItem(TOKEN_KEY, token);
};

export const clearToken = (): void => {
  localStorage.removeItem(TOKEN_KEY);
};

const decodeTokenPayload = (token: string): Record<string, unknown> | null => {
  const parts = token.split('.');
  if (parts.length < 2) {
    return null;
  }

  const payload = parts[1].replace(/-/g, '+').replace(/_/g, '/');
  const padded = payload.padEnd(payload.length + ((4 - (payload.length % 4)) % 4), '=');

  try {
    return JSON.parse(atob(padded));
  } catch {
    return null;
  }
};

export const getTokenRole = (): string | null => {
  const token = getToken();
  if (!token) {
    return null;
  }

  const payload = decodeTokenPayload(token);
  const role = payload?.role;
  return typeof role === 'string' ? role : null;
};
