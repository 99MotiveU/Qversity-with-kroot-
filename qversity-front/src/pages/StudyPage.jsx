import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getStudySession, submitReview } from '../api/studyApi';
import useStudyStore from '../store/studyStore';

const RATINGS = [
  { value: 1, label: '다시', color: 'bg-red-500 hover:bg-red-600', desc: '완전히 잊었어요' },
  { value: 2, label: '어려움', color: 'bg-orange-500 hover:bg-orange-600', desc: '힘들었어요' },
  { value: 3, label: '알맞음', color: 'bg-green-500 hover:bg-green-600', desc: '괜찮아요' },
  { value: 4, label: '쉬움', color: 'bg-blue-500 hover:bg-blue-600', desc: '쉬웠어요' },
];

const formatTime = (s) => {
  const m = Math.floor(s / 60).toString().padStart(2, '0');
  const sec = (s % 60).toString().padStart(2, '0');
  return `${m}:${sec}`;
};

export default function StudyPage() {
  const { deckId } = useParams();
  const navigate = useNavigate();
  const { cards, currentIndex, isFlipped, sessionComplete, setCards, nextCard, flip } =
    useStudyStore();
  const [pageLoading, setPageLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [startTime] = useState(Date.now());
  const [elapsed, setElapsed] = useState(0);
  const [sessionStats, setSessionStats] = useState({ again: 0, hard: 0, good: 0, easy: 0 });

  useEffect(() => {
    loadSession();
    const timer = setInterval(
      () => setElapsed(Math.floor((Date.now() - startTime) / 1000)),
      1000
    );
    return () => clearInterval(timer);
  }, [deckId]);

  const loadSession = async () => {
    try {
      const res = await getStudySession(deckId, 20);
      setCards(res.data.data);
    } catch {
      // 세션 로드 실패 시 빈 카드 목록으로 처리
      setCards([]);
    } finally {
      setPageLoading(false);
    }
  };

  const handleRating = async (rating) => {
    if (submitting) return;
    const card = cards[currentIndex];
    setSubmitting(true);

    const ratingLabels = { 1: 'again', 2: 'hard', 3: 'good', 4: 'easy' };
    setSessionStats((prev) => ({
      ...prev,
      [ratingLabels[rating]]: prev[ratingLabels[rating]] + 1,
    }));

    try {
      await submitReview(card.cardId, rating);
    } catch {
      // 리뷰 저장 실패해도 다음 카드로 이동
    } finally {
      setSubmitting(false);
      nextCard();
    }
  };

  if (pageLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <p className="text-gray-400">학습 세션 준비 중...</p>
      </div>
    );
  }

  if (sessionComplete || cards.length === 0) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="bg-white rounded-2xl shadow-xl p-8 max-w-sm w-full text-center">
          <p className="text-5xl mb-4">🎉</p>
          <h2 className="text-2xl font-bold text-gray-800 mb-2">
            {cards.length === 0 ? '오늘 복습할 카드가 없어요!' : '세션 완료!'}
          </h2>
          <p className="text-gray-500 mb-6">학습 시간: {formatTime(elapsed)}</p>

          {cards.length > 0 && (
            <div className="grid grid-cols-4 gap-2 mb-6">
              {[
                { label: '다시', value: sessionStats.again, color: 'text-red-500' },
                { label: '어려움', value: sessionStats.hard, color: 'text-orange-500' },
                { label: '알맞음', value: sessionStats.good, color: 'text-green-500' },
                { label: '쉬움', value: sessionStats.easy, color: 'text-blue-500' },
              ].map((s) => (
                <div key={s.label} className="text-center">
                  <p className={`text-xl font-bold ${s.color}`}>{s.value}</p>
                  <p className="text-xs text-gray-400">{s.label}</p>
                </div>
              ))}
            </div>
          )}

          <div className="flex gap-3">
            <button
              onClick={() => navigate(`/deck/${deckId}`)}
              className="flex-1 py-3 border border-gray-200 rounded-xl text-gray-600 hover:bg-gray-50"
            >
              덱으로
            </button>
            {cards.length > 0 && (
              <button
                onClick={loadSession}
                className="flex-1 py-3 bg-indigo-600 text-white rounded-xl font-semibold hover:bg-indigo-700"
              >
                다시 학습
              </button>
            )}
          </div>
        </div>
      </div>
    );
  }

  const card = cards[currentIndex];
  const progress = (currentIndex / cards.length) * 100;

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col">
      {/* 헤더 */}
      <header className="bg-white shadow-sm">
        <div className="max-w-lg mx-auto px-4 py-3 flex items-center justify-between">
          <button
            onClick={() => navigate(`/deck/${deckId}`)}
            className="text-gray-400 hover:text-gray-600 text-sm"
          >
            ✕ 종료
          </button>
          <span className="text-sm text-gray-500">
            {currentIndex + 1} / {cards.length}
          </span>
          <span className="text-sm font-mono text-gray-500">{formatTime(elapsed)}</span>
        </div>
        {/* 진행 바 */}
        <div className="h-1 bg-gray-100">
          <div
            className="h-1 bg-indigo-500 transition-all duration-300"
            style={{ width: `${progress}%` }}
          />
        </div>
      </header>

      <main className="flex-1 flex flex-col items-center justify-center px-4 py-8">
        {/* 플래시카드 */}
        <div
          className="w-full max-w-lg cursor-pointer"
          style={{ perspective: '1000px' }}
          onClick={() => !isFlipped && flip()}
        >
          <div
            className="relative w-full transition-transform duration-500"
            style={{
              transformStyle: 'preserve-3d',
              transform: isFlipped ? 'rotateY(180deg)' : 'rotateY(0)',
              minHeight: '220px',
            }}
          >
            {/* 앞면 */}
            <div
              className="absolute inset-0 bg-white rounded-2xl shadow-lg p-8 flex flex-col items-center justify-center"
              style={{ backfaceVisibility: 'hidden' }}
            >
              <p className="text-xs text-gray-400 mb-4">앞면 (클릭해서 뒤집기)</p>
              <p className="text-xl font-medium text-gray-800 text-center">
                {card.frontContent}
              </p>
            </div>
            {/* 뒷면 */}
            <div
              className="absolute inset-0 bg-indigo-50 rounded-2xl shadow-lg p-8 flex flex-col items-center justify-center"
              style={{ backfaceVisibility: 'hidden', transform: 'rotateY(180deg)' }}
            >
              <p className="text-xs text-indigo-400 mb-4">뒷면</p>
              <p className="text-xl font-medium text-indigo-800 text-center">
                {card.backContent}
              </p>
            </div>
          </div>
        </div>

        {!isFlipped ? (
          <p className="mt-8 text-gray-400 text-sm">카드를 클릭해서 답을 확인하세요</p>
        ) : (
          <div className="mt-8 w-full max-w-lg">
            <p className="text-center text-sm text-gray-500 mb-4">얼마나 잘 기억했나요?</p>
            <div className="grid grid-cols-4 gap-2">
              {RATINGS.map((r) => (
                <button
                  key={r.value}
                  onClick={() => handleRating(r.value)}
                  disabled={submitting}
                  className={`${r.color} text-white py-3 rounded-xl font-semibold text-sm transition disabled:opacity-50`}
                >
                  <p>{r.label}</p>
                  <p className="text-xs opacity-75 mt-0.5">{r.desc}</p>
                </button>
              ))}
            </div>
          </div>
        )}
      </main>
    </div>
  );
}
