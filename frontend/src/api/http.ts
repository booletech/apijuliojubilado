import axios from 'axios';
import type { AxiosError } from 'axios';

import { clearToken, getToken } from './token';

export type ApiError = {
  status?: number;
  message: string;
  fieldErrors?: Record<string, string>;
};

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081',
});

api.interceptors.request.use((config) => {
  const token = getToken();
  if (token) {
    config.headers = config.headers ?? {};
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error: AxiosError) => {
    const status = error.response?.status;
    if (status === 401) {
      clearToken();
      window.location.assign('/login');
    }

    const data = error.response?.data as
      | { message?: string; fieldErrors?: Record<string, string> }
      | undefined;

    const apiError: ApiError = {
      status,
      message: data?.message || 'Request failed.',
      fieldErrors: data?.fieldErrors,
    };

    if (!apiError.message && status) {
      if (status === 403) apiError.message = 'Forbidden.';
      if (status === 500) apiError.message = 'Server error.';
    }

    return Promise.reject(apiError);
  }
);

export default api;
