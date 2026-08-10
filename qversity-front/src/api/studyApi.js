import api from './axiosInstance';

export const getStudySession = (deckId, limit = 20) =>
  api.get(`/api/study/${deckId}/session?limit=${limit}`);
export const submitReview = (cardId, rating) =>
  api.post('/api/study/review', { cardId, rating });
export const getDeckStats = (deckId) => api.get(`/api/study/${deckId}/stats`);
