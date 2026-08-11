import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getDecks, createDeck, deleteDeck } from '../api/deckApi';
import { logout } from '../api/userApi';
import useDeckStore from '../store/deckStore';
import useAuthStore from '../store/authStore';

export default function MainPage() {
  const navigate = useNavigate();
  const { user, clearUser } = useAuthStore();
  const { decks: deckList, setDecks } = useDeckStore();
  const decks = deckList ?? [];
  const [showModal, setShowModal] = useState(false);
  const [form, setForm] = useState({ name: '', description: '', isPublic: false });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    loadDecks();
  }, []);

  const loadDecks = async () => {
    try {
      const res = await getDecks();
      setDecks(res.data?.data ?? []);
    } catch {
      setDecks([]);
    }
  };

  const handleCreate = async (e) => {
    e.preventDefault();
    if (!form.name.trim()) {
      setError('덱 이름을 입력해주세요.');
      return;
    }
    setLoading(true);
    try {
      const res = await createDeck(form);
      setDecks([...decks, res.data.data]);
      setShowModal(false);
      setForm({ name: '', description: '', isPublic: false });
      setError('');
    } catch (err) {
      setError(err.response?.data?.message || '생성에 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (e, deckId) => {
    e.stopPropagation();
    if (!confirm('이 덱을 삭제하시겠습니까?')) return;
    try {
      await deleteDeck(deckId);
      setDecks(decks.filter((d) => d.deckId !== deckId));
    } catch {
      alert('삭제에 실패했습니다.');
    }
  };

  const handleLogout = async () => {
    try {
      await logout();
    } catch {}
    clearUser();
    navigate('/');
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* 헤더 */}
      <header className="bg-white shadow-sm">
        <div className="max-w-5xl mx-auto px-4 py-4 flex items-center justify-between">
          <h1 className="text-2xl font-bold text-indigo-600">Qversity</h1>
          <div className="flex items-center gap-4">
            <span className="text-gray-600 text-sm">{user?.nickname}</span>
            <button
              onClick={handleLogout}
              className="text-sm text-gray-500 hover:text-gray-700"
            >
              로그아웃
            </button>
          </div>
        </div>
      </header>

      <main className="max-w-5xl mx-auto px-4 py-8">
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-xl font-semibold text-gray-800">내 학습 덱</h2>
          <button
            onClick={() => setShowModal(true)}
            className="bg-indigo-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-indigo-700 transition"
          >
            + 새 덱 만들기
          </button>
        </div>

        {decks.length === 0 ? (
          <div className="text-center py-20 text-gray-400">
            <p className="text-4xl mb-3">📚</p>
            <p className="text-lg font-medium">아직 학습 덱이 없습니다</p>
            <p className="text-sm mt-1">새 덱을 만들어 학습을 시작하세요!</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {decks.map((deck) => (
              <div
                key={deck.deckId}
                onClick={() => navigate(`/deck/${deck.deckId}`)}
                className="bg-white rounded-xl shadow-sm p-5 cursor-pointer hover:shadow-md transition border border-gray-100"
              >
                <div className="flex justify-between items-start mb-2">
                  <h3 className="font-semibold text-gray-800 text-lg">{deck.name}</h3>
                  <button
                    onClick={(e) => handleDelete(e, deck.deckId)}
                    className="text-gray-300 hover:text-red-400 text-sm"
                  >
                    ✕
                  </button>
                </div>
                {deck.description && (
                  <p className="text-gray-500 text-sm mb-3 line-clamp-2">{deck.description}</p>
                )}
                <div className="flex items-center justify-between mt-3">
                  <span className="text-xs text-gray-400">{deck.totalCards}개 카드</span>
                  <span
                    className={`text-xs px-2 py-0.5 rounded-full ${
                      deck.isPublic
                        ? 'bg-green-100 text-green-600'
                        : 'bg-gray-100 text-gray-500'
                    }`}
                  >
                    {deck.isPublic ? '공개' : '비공개'}
                  </span>
                </div>
              </div>
            ))}
          </div>
        )}
      </main>

      {/* 덱 생성 모달 */}
      {showModal && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
          <div className="bg-white rounded-2xl p-6 w-full max-w-md shadow-xl">
            <h3 className="text-lg font-bold mb-4">새 덱 만들기</h3>
            <form onSubmit={handleCreate} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  덱 이름 *
                </label>
                <input
                  type="text"
                  value={form.name}
                  onChange={(e) => setForm({ ...form, name: e.target.value })}
                  placeholder="예: 영어 단어, 자바 기초..."
                  className="w-full px-4 py-2.5 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">설명</label>
                <textarea
                  value={form.description}
                  onChange={(e) => setForm({ ...form, description: e.target.value })}
                  placeholder="덱에 대한 설명..."
                  rows={3}
                  className="w-full px-4 py-2.5 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400 resize-none"
                />
              </div>
              <label className="flex items-center gap-2 cursor-pointer">
                <input
                  type="checkbox"
                  checked={form.isPublic}
                  onChange={(e) => setForm({ ...form, isPublic: e.target.checked })}
                  className="rounded"
                />
                <span className="text-sm text-gray-600">공개 덱으로 만들기</span>
              </label>
              {error && <p className="text-red-500 text-sm">{error}</p>}
              <div className="flex gap-3 pt-2">
                <button
                  type="button"
                  onClick={() => {
                    setShowModal(false);
                    setError('');
                  }}
                  className="flex-1 py-2.5 border border-gray-200 rounded-lg text-gray-600 hover:bg-gray-50"
                >
                  취소
                </button>
                <button
                  type="submit"
                  disabled={loading}
                  className="flex-1 py-2.5 bg-indigo-600 text-white rounded-lg font-medium hover:bg-indigo-700 disabled:opacity-50"
                >
                  {loading ? '생성 중...' : '만들기'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
