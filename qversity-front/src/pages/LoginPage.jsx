import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import useAuthStore from '../store/authStore';
import { localLogin, localRegister } from '../api/localAuthApi';

// 탭: 'social' | 'login' | 'register'
// 소셜 로그인 전환 시 local 관련 state/handler 제거, mode 탭 제거, 소셜 버튼만 남기면 됨
export default function LoginPage() {
  const navigate = useNavigate();
  const { setUser } = useAuthStore();
  const [mode, setMode] = useState('social');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [nickname, setNickname] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSocialLogin = (provider) => {
    window.location.href = `http://localhost:8080/oauth2/authorization/${provider}`;
  };

  const handleLocalLogin = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const res = await localLogin(email, password);
      setUser(res.data.data);
      navigate('/main');
    } catch (err) {
      setError(err.response?.data?.message || '로그인에 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  const handleLocalRegister = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const res = await localRegister(email, password, nickname);
      setUser(res.data.data);
      navigate('/main');
    } catch (err) {
      setError(err.response?.data?.message || '회원가입에 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-indigo-50 to-purple-50 flex items-center justify-center">
      <div className="bg-white rounded-2xl shadow-xl p-8 w-full max-w-sm">
        <h1 className="text-3xl font-bold text-indigo-600 mb-6 text-center">Qversity</h1>

        {/* 탭 — 소셜 로그인 전환 시 이 div 전체 제거 */}
        <div className="flex rounded-xl bg-gray-100 p-1 mb-6">
          {[
            { key: 'social', label: '소셜' },
            { key: 'login', label: '로그인' },
            { key: 'register', label: '회원가입' },
          ].map((tab) => (
            <button
              key={tab.key}
              onClick={() => { setMode(tab.key); setError(''); }}
              className={`flex-1 py-1.5 rounded-lg text-sm font-medium transition ${
                mode === tab.key
                  ? 'bg-white text-indigo-600 shadow-sm'
                  : 'text-gray-500 hover:text-gray-700'
              }`}
            >
              {tab.label}
            </button>
          ))}
        </div>

        {/* 소셜 로그인 */}
        {mode === 'social' && (
          <div className="space-y-3">
            <p className="text-gray-500 text-sm text-center mb-4">소셜 계정으로 간편하게 시작하세요</p>
            <button
              onClick={() => handleSocialLogin('google')}
              className="w-full flex items-center justify-center gap-3 py-3 border border-gray-200 rounded-xl hover:bg-gray-50 transition font-medium"
            >
              <img src="https://www.google.com/favicon.ico" alt="Google" className="w-5 h-5" />
              Google로 계속하기
            </button>
            <button
              onClick={() => handleSocialLogin('kakao')}
              className="w-full flex items-center justify-center gap-3 py-3 bg-yellow-400 rounded-xl hover:bg-yellow-500 transition font-medium"
            >
              카카오로 계속하기
            </button>
            <button
              onClick={() => handleSocialLogin('naver')}
              className="w-full flex items-center justify-center gap-3 py-3 bg-green-500 text-white rounded-xl hover:bg-green-600 transition font-medium"
            >
              네이버로 계속하기
            </button>
          </div>
        )}

        {/* 일반 로그인 — 소셜 로그인 전환 시 이 블록 제거 */}
        {mode === 'login' && (
          <form onSubmit={handleLocalLogin} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">이메일</label>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="email@example.com"
                required
                className="w-full px-4 py-2.5 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-indigo-400 text-sm"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">비밀번호</label>
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="비밀번호 입력"
                required
                className="w-full px-4 py-2.5 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-indigo-400 text-sm"
              />
            </div>
            {error && <p className="text-sm text-red-500">{error}</p>}
            <button
              type="submit"
              disabled={loading}
              className="w-full py-3 bg-indigo-600 text-white rounded-xl font-semibold hover:bg-indigo-700 transition disabled:opacity-50"
            >
              {loading ? '로그인 중...' : '로그인'}
            </button>
          </form>
        )}

        {/* 일반 회원가입 — 소셜 로그인 전환 시 이 블록 제거 */}
        {mode === 'register' && (
          <form onSubmit={handleLocalRegister} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">이메일</label>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="email@example.com"
                required
                className="w-full px-4 py-2.5 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-indigo-400 text-sm"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">비밀번호</label>
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="8자 이상 권장"
                required
                className="w-full px-4 py-2.5 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-indigo-400 text-sm"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">닉네임</label>
              <input
                type="text"
                value={nickname}
                onChange={(e) => setNickname(e.target.value)}
                placeholder="사용할 닉네임"
                required
                className="w-full px-4 py-2.5 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-indigo-400 text-sm"
              />
            </div>
            {error && <p className="text-sm text-red-500">{error}</p>}
            <button
              type="submit"
              disabled={loading}
              className="w-full py-3 bg-indigo-600 text-white rounded-xl font-semibold hover:bg-indigo-700 transition disabled:opacity-50"
            >
              {loading ? '가입 중...' : '회원가입'}
            </button>
          </form>
        )}

        <button
          onClick={() => navigate('/')}
          className="mt-6 w-full text-sm text-gray-400 hover:text-gray-600 text-center"
        >
          돌아가기
        </button>
      </div>
    </div>
  );
}
