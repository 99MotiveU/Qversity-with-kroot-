import React, { useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, useNavigate } from 'react-router-dom';
import Cookies from 'js-cookie';
import useAuthStore from './store/authStore';
import { getMe } from './api/userApi';

import WelcomePage from './pages/WelcomePage';
import LoginPage from './pages/LoginPage';
import SignupPage from './pages/SignupPage';
import MainPage from './pages/MainPage';
import DetailPage from './pages/DetailPage';
import StudyPage from './pages/StudyPage';
import CollabPage from './pages/CollabPage';

function AppContent() {
  const navigate = useNavigate();
  const { setUser, clearUser, setLoading } = useAuthStore();

  useEffect(() => {
    // authenticatedUserToken은 비-HttpOnly이므로 js-cookie로 읽을 수 있음
    const authToken = Cookies.get('authenticatedUserToken');
    if (authToken) {
      navigate('/signup');
      setLoading(false);
      return;
    }

    // accessToken은 HttpOnly이므로 API 호출로 인증 상태 확인
    setLoading(true);
    getMe()
      .then((res) => {
        setUser(res.data.data);
        const path = window.location.pathname;
        if (path === '/' || path === '/login') {
          navigate('/main');
        }
      })
      .catch(() => {
        clearUser();
      })
      .finally(() => {
        setLoading(false);
      });
  }, []);

  return (
    <Routes>
      <Route path="/" element={<WelcomePage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/signup" element={<SignupPage />} />
      <Route path="/main" element={<MainPage />} />
      <Route path="/deck/:deckId" element={<DetailPage />} />
      <Route path="/study/:deckId" element={<StudyPage />} />
      <Route path="/collab/:roomCode" element={<CollabPage />} />
    </Routes>
  );
}

export default function App() {
  return (
    <Router>
      <AppContent />
    </Router>
  );
}
