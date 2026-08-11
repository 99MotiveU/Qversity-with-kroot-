import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getDeck } from '../api/deckApi';
import { getCards, addCard, updateCard, deleteCard } from '../api/cardApi';
import { getDeckStats } from '../api/studyApi';
import { createSession } from '../api/collabApi';
import useDeckStore from '../store/deckStore';

export default function DetailPage() {
  const { deckId } = useParams();
  const navigate = useNavigate();
  const {
    currentDeck,
    setCurrentDeck,
    cards,
    setCards,
    addCard: addToStore,
    updateCard: updateInStore,
    removeCard,
  } = useDeckStore();
  const [stats, setStats] = useState(null);
  const [showAddModal, setShowAddModal] = useState(false);
  const [editingCard, setEditingCard] = useState(null);
  const [form, setForm] = useState({ frontContent: '', backContent: '' });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    loadAll();
  }, [deckId]);

  const loadAll = async () => {
    try {
      const [deckRes, cardsRes, statsRes] = await Promise.all([
        getDeck(deckId),
        getCards(deckId),
        getDeckStats(deckId),
      ]);
      setCurrentDeck(deckRes.data.data);
      setCards(cardsRes.data.data);
      setStats(statsRes.data.data);
    } catch {
      // 로드 실패 시 빈 상태 유지
    }
  };

  const handleAddCard = async (e) => {
    e.preventDefault();
    if (!form.frontContent.trim() || !form.backContent.trim()) {
      setError('앞면과 뒷면을 모두 입력해주세요.');
      return;
    }
    setLoading(true);
    try {
      const res = await addCard(deckId, form);
      addToStore(res.data.data);
      setForm({ frontContent: '', backContent: '' });
      setShowAddModal(false);
      setError('');
    } catch (err) {
      setError(err.response?.data?.message || '카드 추가에 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateCard = async (e) => {
    e.preventDefault();
    if (!form.frontContent.trim() || !form.backContent.trim()) {
      setError('앞면과 뒷면을 모두 입력해주세요.');
      return;
    }
    setLoading(true);
    try {
      const res = await updateCard(editingCard.cardId, form);
      updateInStore(res.data.data);
      setEditingCard(null);
      setForm({ frontContent: '', backContent: '' });
      setError('');
    } catch (err) {
      setError(err.response?.data?.message || '수정에 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteCard = async (cardId) => {
    if (!confirm('이 카드를 삭제하시겠습니까?')) return;
    try {
      await deleteCard(cardId);
      removeCard(cardId);
    } catch {
      alert('삭제에 실패했습니다.');
    }
  };

  const handleStartCollab = async () => {
    try {
      const res = await createSession(Number(deckId));
      const roomCode = res.data.data.roomCode;
      navigate(`/collab/${roomCode}`);
    } catch {
      alert('협업 세션 생성에 실패했습니다.');
    }
  };

  const openEdit = (card) => {
    setEditingCard(card);
    setForm({ frontContent: card.frontContent, backContent: card.backContent });
    setError('');
  };

  const closeModal = () => {
    setShowAddModal(false);
    setEditingCard(null);
    setError('');
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-white shadow-sm">
        <div className="max-w-4xl mx-auto px-4 py-4 flex items-center gap-3">
          <button
            onClick={() => navigate('/main')}
            className="text-gray-400 hover:text-gray-600"
          >
            ← 뒤로
          </button>
          <h1 className="text-xl font-bold text-gray-800">{currentDeck?.name || '...'}</h1>
        </div>
      </header>

      <main className="max-w-4xl mx-auto px-4 py-6">
        {/* 통계 */}
        {stats && (
          <div className="grid grid-cols-4 gap-3 mb-6">
            {[
              { label: '전체', value: stats.totalCards, color: 'bg-gray-100 text-gray-700' },
              { label: '신규', value: stats.newCards, color: 'bg-blue-100 text-blue-700' },
              { label: '학습중', value: stats.learningCards, color: 'bg-orange-100 text-orange-700' },
              { label: '복습', value: stats.reviewCards, color: 'bg-green-100 text-green-700' },
            ].map((s) => (
              <div key={s.label} className={`${s.color} rounded-xl p-3 text-center`}>
                <div className="text-2xl font-bold">{s.value}</div>
                <div className="text-xs">{s.label}</div>
              </div>
            ))}
          </div>
        )}

        {/* 액션 버튼 */}
        <div className="flex gap-3 mb-6">
          <button
            onClick={() => navigate(`/study/${deckId}`)}
            disabled={cards.length === 0}
            className="flex-1 bg-indigo-600 text-white py-3 rounded-xl font-semibold hover:bg-indigo-700 disabled:opacity-40 transition"
          >
            학습 시작 ({stats?.dueCards || 0}개 예정)
          </button>
          <button
            onClick={handleStartCollab}
            disabled={cards.length === 0}
            className="flex-1 bg-purple-600 text-white py-3 rounded-xl font-semibold hover:bg-purple-700 disabled:opacity-40 transition"
          >
            팀 학습
          </button>
          <button
            onClick={() => {
              setShowAddModal(true);
              setForm({ frontContent: '', backContent: '' });
              setError('');
            }}
            className="px-6 py-3 border-2 border-indigo-600 text-indigo-600 rounded-xl font-semibold hover:bg-indigo-50 transition"
          >
            + 카드 추가
          </button>
        </div>

        {/* 카드 목록 */}
        {cards.length === 0 ? (
          <div className="text-center py-16 text-gray-400">
            <p className="text-3xl mb-2">🃏</p>
            <p>카드가 없습니다. 첫 카드를 추가해보세요!</p>
          </div>
        ) : (
          <div className="space-y-3">
            {cards.map((card) => (
              <div
                key={card.cardId}
                className="bg-white rounded-xl border border-gray-100 p-4 shadow-sm"
              >
                <div className="flex justify-between items-start gap-4">
                  <div className="flex-1 grid grid-cols-2 gap-4">
                    <div>
                      <p className="text-xs text-gray-400 mb-1">앞면</p>
                      <p className="text-gray-800">{card.frontContent}</p>
                    </div>
                    <div>
                      <p className="text-xs text-gray-400 mb-1">뒷면</p>
                      <p className="text-gray-600">{card.backContent}</p>
                    </div>
                  </div>
                  <div className="flex gap-2 shrink-0">
                    <button
                      onClick={() => openEdit(card)}
                      className="text-sm text-indigo-500 hover:text-indigo-700"
                    >
                      수정
                    </button>
                    <button
                      onClick={() => handleDeleteCard(card.cardId)}
                      className="text-sm text-red-400 hover:text-red-600"
                    >
                      삭제
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </main>

      {/* 카드 추가/수정 모달 */}
      {(showAddModal || editingCard) && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
          <div className="bg-white rounded-2xl p-6 w-full max-w-lg shadow-xl">
            <h3 className="text-lg font-bold mb-4">
              {editingCard ? '카드 수정' : '카드 추가'}
            </h3>
            <form
              onSubmit={editingCard ? handleUpdateCard : handleAddCard}
              className="space-y-4"
            >
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  앞면 (문제)
                </label>
                <textarea
                  value={form.frontContent}
                  onChange={(e) => setForm({ ...form, frontContent: e.target.value })}
                  placeholder="앞면 내용..."
                  rows={3}
                  className="w-full px-4 py-2.5 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400 resize-none"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  뒷면 (답)
                </label>
                <textarea
                  value={form.backContent}
                  onChange={(e) => setForm({ ...form, backContent: e.target.value })}
                  placeholder="뒷면 내용..."
                  rows={3}
                  className="w-full px-4 py-2.5 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400 resize-none"
                />
              </div>
              {error && <p className="text-red-500 text-sm">{error}</p>}
              <div className="flex gap-3">
                <button
                  type="button"
                  onClick={closeModal}
                  className="flex-1 py-2.5 border border-gray-200 rounded-lg text-gray-600 hover:bg-gray-50"
                >
                  취소
                </button>
                <button
                  type="submit"
                  disabled={loading}
                  className="flex-1 py-2.5 bg-indigo-600 text-white rounded-lg font-medium hover:bg-indigo-700 disabled:opacity-50"
                >
                  {loading ? '처리중...' : editingCard ? '저장' : '추가'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
