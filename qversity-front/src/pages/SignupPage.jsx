import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { signup } from '../api/userApi';
import useAuthStore from '../store/authStore';

export default function SignupPage() {
  const [nickname, setNickname] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const { setUser } = useAuthStore();

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!nickname.trim() || nickname.length > 20) {
      setError('닉네임은 1~20자로 입력해주세요.');
      return;
    }
    setLoading(true);
    try {
      const res = await signup(nickname.trim());
      setUser(res.data.data);
      navigate('/main');
    } catch (err) {
      setError(err.response?.data?.message || '닉네임 설정에 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-indigo-50 to-purple-50 flex items-center justify-center">
      <div className="bg-white rounded-2xl shadow-xl p-8 w-full max-w-md">
        <h1 className="text-2xl font-bold text-gray-800 mb-2">환영합니다!</h1>
        <p className="text-gray-500 mb-6">Qversity에서 사용할 닉네임을 설정해주세요.</p>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">닉네임</label>
            <input
              type="text"
              value={nickname}
              onChange={(e) => setNickname(e.target.value)}
              placeholder="예: 열공하는사자"
              maxLength={20}
              className="w-full px-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-indigo-400"
            />
            <p className="text-xs text-gray-400 mt-1">{nickname.length}/20자</p>
          </div>

          {error && <p className="text-red-500 text-sm">{error}</p>}

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-indigo-600 text-white py-3 rounded-xl font-semibold hover:bg-indigo-700 transition disabled:opacity-50"
          >
            {loading ? '처리중...' : '시작하기'}
          </button>
        </form>
      </div>
    </div>
  );
}
