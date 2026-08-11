import { create } from 'zustand';

const useStudyStore = create((set) => ({
  cards: [],
  currentIndex: 0,
  isFlipped: false,
  sessionComplete: false,
  stats: null,
  setCards: (cards) =>
    set({ cards, currentIndex: 0, isFlipped: false, sessionComplete: false }),
  nextCard: () =>
    set((s) => {
      const next = s.currentIndex + 1;
      return {
        currentIndex: next,
        isFlipped: false,
        sessionComplete: next >= s.cards.length,
      };
    }),
  flip: () => set((s) => ({ isFlipped: !s.isFlipped })),
  setStats: (stats) => set({ stats }),
  reset: () =>
    set({ cards: [], currentIndex: 0, isFlipped: false, sessionComplete: false }),
}));

export default useStudyStore;
