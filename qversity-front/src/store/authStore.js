import { create } from 'zustand';

const useAuthStore = create((set) => ({
  user: null,
  loading: true,
  setUser: (user) => set({ user, loading: false }),
  clearUser: () => set({ user: null, loading: false }),
  setLoading: (loading) => set({ loading }),
}));

export default useAuthStore;
