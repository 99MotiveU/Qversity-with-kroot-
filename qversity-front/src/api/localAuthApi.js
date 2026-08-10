// 임시 일반 로그인 API — 소셜 로그인 전환 시 이 파일 삭제
import api from './axiosInstance';

export const localRegister = (email, password, nickname) =>
  api.post('/api/auth/local/register', { email, password, nickname });

export const localLogin = (email, password) =>
  api.post('/api/auth/local/login', { email, password });
