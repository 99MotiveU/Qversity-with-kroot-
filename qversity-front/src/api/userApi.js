import api from './axiosInstance';

export const getMe = () => api.get('/api/user/me');
export const signup = (nickname) => api.post('/api/user/signup', { nickname });
export const logout = () => api.post('/api/user/logout');
export const updateNickname = (nickname) => api.patch('/api/user/nickname', { nickname });
