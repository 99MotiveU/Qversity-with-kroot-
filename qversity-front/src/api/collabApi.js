import api from './axiosInstance';

export const createSession = (deckId) => api.post('/api/collab/sessions', { deckId });
export const getSession = (roomCode) => api.get(`/api/collab/sessions/${roomCode}`);
export const joinSession = (roomCode) => api.post(`/api/collab/sessions/${roomCode}/join`);
export const setReady = (roomCode, ready) =>
  api.post(`/api/collab/sessions/${roomCode}/ready`, { ready });
export const startSession = (roomCode) =>
  api.post(`/api/collab/sessions/${roomCode}/start`);
export const leaveSession = (roomCode) =>
  api.post(`/api/collab/sessions/${roomCode}/leave`);
