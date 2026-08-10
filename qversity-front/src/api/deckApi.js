import api from './axiosInstance';

export const getDecks = () => api.get('/api/decks');
export const getDeck = (deckId) => api.get(`/api/decks/${deckId}`);
export const createDeck = (data) => api.post('/api/decks', data);
export const updateDeck = (deckId, data) => api.put(`/api/decks/${deckId}`, data);
export const deleteDeck = (deckId) => api.delete(`/api/decks/${deckId}`);
