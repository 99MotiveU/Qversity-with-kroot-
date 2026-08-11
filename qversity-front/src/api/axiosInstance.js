import axios from 'axios';
import { API_BASE_URL } from '../config';

const api = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true,
  headers: { 'Content-Type': 'application/json' },
});

// 401 응답 시 토큰 자동 갱신
api.interceptors.response.use(
  (res) => res,
  async (error) => {
    const original = error.config;
    if (error.response?.status === 401 && !original._retry) {
      original._retry = true;
      try {
        await axios.post(
          `${API_BASE_URL}/api/user/token/refresh`,
          {},
          { withCredentials: true }
        );
        return api(original);
      } catch {
        window.location.href = '/';
      }
    }
    return Promise.reject(error);
  }
);

export default api;
