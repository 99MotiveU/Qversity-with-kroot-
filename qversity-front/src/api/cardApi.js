import api from './axiosInstance';

export const getCards = (deckId) => api.get(`/api/decks/${deckId}/cards`);
export const addCard = (deckId, data) => api.post(`/api/decks/${deckId}/cards`, data);
export const updateCard = (cardId, data) => api.put(`/api/cards/${cardId}`, data);
export const deleteCard = (cardId) => api.delete(`/api/cards/${cardId}`);
