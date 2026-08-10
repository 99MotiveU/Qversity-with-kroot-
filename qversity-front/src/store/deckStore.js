import { create } from 'zustand';

const useDeckStore = create((set) => ({
  decks: [],
  currentDeck: null,
  cards: [],
  setDecks: (decks) => set({ decks }),
  setCurrentDeck: (deck) => set({ currentDeck: deck }),
  setCards: (cards) => set({ cards }),
  addCard: (card) => set((s) => ({ cards: [...s.cards, card] })),
  updateCard: (updated) =>
    set((s) => ({
      cards: s.cards.map((c) => (c.cardId === updated.cardId ? updated : c)),
    })),
  removeCard: (cardId) =>
    set((s) => ({
      cards: s.cards.filter((c) => c.cardId !== cardId),
    })),
}));

export default useDeckStore;
