import React, { useEffect, useState, useRef, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { joinSession, setReady, leaveSession } from '../api/collabApi';
import { getStudySession, submitReview } from '../api/studyApi';
import useAuthStore from '../store/authStore';

const RATINGS = [
  { value: 1, label: '다시', color: 'bg-red-500 hover:bg-red-600' },
  { value: 2, label: '어려움', color: 'bg-orange-500 hover:bg-orange-600' },
  { value: 3, label: '알맞음', color: 'bg-green-500 hover:bg-green-600' },
  { value: 4, label: '쉬움', color: 'bg-blue-500 hover:bg-blue-600' },
];

export default function CollabPage() {
  const { roomCode } = useParams();
  const navigate = useNavigate();
  const { user } = useAuthStore();
  const [session, setSession] = useState(null);
  const [cards, setCards] = useState([]);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [isFlipped, setIsFlipped] = useState(false);
  const [phase, setPhase] = useState('LOBBY'); // LOBBY | STUDYING | FINISHED
  const [members, setMembers] = useState([]);
  const [messages, setMessages] = useState([]);
  const [connected, setConnected] = useState(false);
  const stompClient = useRef(null);

  const isOwner = session?.ownerNickname === user?.nickname;

  useEffect(() => {
    loadSession();
    return () => {
      stompClient.current?.deactivate();
    };
  }, [roomCode]);

  const loadSession = async () => {
    try {
      const res = await joinSession(roomCode);
      const s = res.data.data;
      setSession(s);
      setMembers(s.members || []);
      connectWebSocket();
    } catch {
      alert('세션을 찾을 수 없습니다.');
      navigate('/main');
    }
  };

  const connectWebSocket = () => {
    const client = new Client({
      webSocketFactory: () => new SockJS('/ws/collab'),
      onConnect: () => {
        setConnected(true);
        client.subscribe(`/topic/collab/${roomCode}`, (msg) => {
          handleMessage(JSON.parse(msg.body));
        });
        client.publish({
          destination: `/app/collab/${roomCode}/join`,
          body: JSON.stringify({
            type: 'JOIN',
            senderNickname: user?.nickname,
            senderId: user?.userId,
            roomCode,
          }),
        });
      },
      onDisconnect: () => setConnected(false),
    });
    stompClient.current = client;
    client.activate();
  };

  const handleMessage = useCallback((msg) => {
    switch (msg.type) {
      case 'JOIN':
        setMessages((prev) => [...prev, `${msg.senderNickname}님이 입장했습니다.`]);
        break;
      case 'READY':
        setMembers((prev) =>
          prev.map((m) => (m.userId === msg.senderId ? { ...m, ready: msg.data } : m))
        );
        setMessages((prev) => [
          ...prev,
          `${msg.senderNickname}님이 ${msg.data ? '준비 완료' : '준비 취소'}했습니다.`,
        ]);
        break;
      case 'CARD_SHOW':
        setCurrentIndex(msg.data?.index || 0);
        setIsFlipped(false);
        break;
      case 'RATE':
        setMessages((prev) => [
          ...prev,
          `${msg.senderNickname}: ${['', '다시', '어려움', '알맞음', '쉬움'][msg.data?.rating]}`,
        ]);
        break;
      case 'NEXT_CARD':
        setCurrentIndex(msg.data?.index ?? 0);
        setIsFlipped(false);
        break;
      case 'LEAVE':
        setMessages((prev) => [...prev, `${msg.senderNickname}님이 퇴장했습니다.`]);
        setMembers((prev) => prev.filter((m) => m.userId !== msg.senderId));
        break;
      default:
        break;
    }
  }, []);

  const publish = (destination, payload) => {
    stompClient.current?.publish({
      destination: `/app/collab/${roomCode}/${destination}`,
      body: JSON.stringify({
        senderNickname: user?.nickname,
        senderId: user?.userId,
        roomCode,
        ...payload,
      }),
    });
  };

  const handleReady = async () => {
    const myMember = members.find((m) => m.userId === user?.userId);
    const newReady = !myMember?.ready;
    try {
      await setReady(roomCode, newReady);
      publish('ready', { type: 'READY', data: newReady });
    } catch {
      // 준비 상태 변경 실패
    }
  };

  const handleStart = async () => {
    try {
      const cardsRes = await getStudySession(session.deckId, 20);
      const loaded = cardsRes.data.data;
      setCards(loaded);
      setPhase('STUDYING');
      setCurrentIndex(0);
      setIsFlipped(false);
      publish('card-show', { type: 'CARD_SHOW', data: { index: 0 } });
    } catch {
      alert('카드 로드에 실패했습니다.');
    }
  };

  const handleFlip = () => setIsFlipped(true);

  const handleRate = async (rating) => {
    const card = cards[currentIndex];
    try {
      await submitReview(card.cardId, rating);
    } catch {
      // 리뷰 저장 실패해도 진행
    }
    publish('rate', { type: 'RATE', data: { rating, cardId: card.cardId } });

    const nextIdx = currentIndex + 1;
    if (nextIdx >= cards.length) {
      setPhase('FINISHED');
    } else {
      publish('next', { type: 'NEXT_CARD', data: { index: nextIdx } });
      setCurrentIndex(nextIdx);
      setIsFlipped(false);
    }
  };

  const handleLeave = async () => {
    try {
      publish('leave', { type: 'LEAVE' });
      await leaveSession(roomCode);
    } catch {
      // 퇴장 처리 실패
    }
    navigate('/main');
  };

  const myMember = members.find((m) => m.userId === user?.userId);
  const allReady = members.length > 1 && members.every((m) => m.ready);
  const card = cards[currentIndex];

  if (phase === 'FINISHED') {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="bg-white rounded-2xl shadow-xl p-8 text-center max-w-sm">
          <p className="text-5xl mb-4">🎉</p>
          <h2 className="text-2xl font-bold mb-6">팀 학습 완료!</h2>
          <button
            onClick={() => navigate('/main')}
            className="w-full bg-indigo-600 text-white py-3 rounded-xl font-semibold hover:bg-indigo-700"
          >
            홈으로
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col">
      <header className="bg-white shadow-sm">
        <div className="max-w-4xl mx-auto px-4 py-4 flex items-center justify-between">
          <div>
            <h1 className="font-bold text-gray-800">팀 학습</h1>
            <p className="text-sm text-gray-400">
              입장 코드:{' '}
              <span className="font-mono font-bold text-indigo-600">{roomCode}</span>
            </p>
          </div>
          <div className="flex items-center gap-3">
            <span
              className={`text-xs px-2 py-1 rounded-full ${
                connected ? 'bg-green-100 text-green-600' : 'bg-red-100 text-red-500'
              }`}
            >
              {connected ? '연결됨' : '연결 중...'}
            </span>
            <button
              onClick={handleLeave}
              className="text-sm text-gray-500 hover:text-gray-700"
            >
              나가기
            </button>
          </div>
        </div>
      </header>

      <main className="max-w-4xl mx-auto px-4 py-6 flex gap-6 flex-1 w-full">
        {/* 왼쪽: 메인 콘텐츠 */}
        <div className="flex-1 min-w-0">
          {phase === 'LOBBY' && (
            <div className="bg-white rounded-2xl p-6 shadow-sm">
              <h2 className="text-lg font-bold mb-2">대기실</h2>
              <p className="text-gray-500 text-sm mb-6">
                모든 참가자가 준비 완료하면 방장이 학습을 시작할 수 있습니다.
              </p>

              <div className="space-y-3 mb-6">
                {members.map((m) => (
                  <div
                    key={m.userId}
                    className="flex items-center justify-between p-3 bg-gray-50 rounded-xl"
                  >
                    <span className="font-medium text-gray-700">{m.nickname}</span>
                    <span
                      className={`text-sm px-3 py-1 rounded-full font-medium ${
                        m.ready
                          ? 'bg-green-100 text-green-600'
                          : 'bg-gray-100 text-gray-400'
                      }`}
                    >
                      {m.ready ? '준비 완료' : '대기중'}
                    </span>
                  </div>
                ))}
              </div>

              <div className="flex gap-3">
                <button
                  onClick={handleReady}
                  className={`flex-1 py-3 rounded-xl font-semibold transition ${
                    myMember?.ready
                      ? 'bg-gray-200 text-gray-600 hover:bg-gray-300'
                      : 'bg-indigo-600 text-white hover:bg-indigo-700'
                  }`}
                >
                  {myMember?.ready ? '준비 취소' : '준비 완료'}
                </button>
                {isOwner && (
                  <button
                    onClick={handleStart}
                    disabled={!allReady}
                    className="flex-1 py-3 bg-purple-600 text-white rounded-xl font-semibold disabled:opacity-40 hover:bg-purple-700 transition"
                  >
                    학습 시작
                  </button>
                )}
              </div>
              {isOwner && !allReady && members.length > 1 && (
                <p className="text-xs text-gray-400 text-center mt-2">
                  모든 참가자가 준비 완료해야 시작할 수 있습니다.
                </p>
              )}
            </div>
          )}

          {phase === 'STUDYING' && card && (
            <div>
              <div className="flex justify-between text-sm text-gray-400 mb-4">
                <span>
                  {currentIndex + 1} / {cards.length}
                </span>
                <span>{session?.deckName}</span>
              </div>

              {/* 플래시카드 */}
              <div
                className="w-full cursor-pointer"
                style={{ perspective: '1000px' }}
                onClick={() => !isFlipped && handleFlip()}
              >
                <div
                  className="relative w-full transition-transform duration-500"
                  style={{
                    transformStyle: 'preserve-3d',
                    transform: isFlipped ? 'rotateY(180deg)' : 'rotateY(0)',
                    minHeight: '200px',
                  }}
                >
                  <div
                    className="absolute inset-0 bg-white rounded-2xl shadow-lg p-8 flex flex-col items-center justify-center"
                    style={{ backfaceVisibility: 'hidden' }}
                  >
                    <p className="text-xs text-gray-400 mb-3">앞면</p>
                    <p className="text-xl font-medium text-gray-800 text-center">
                      {card.frontContent}
                    </p>
                  </div>
                  <div
                    className="absolute inset-0 bg-indigo-50 rounded-2xl shadow-lg p-8 flex flex-col items-center justify-center"
                    style={{ backfaceVisibility: 'hidden', transform: 'rotateY(180deg)' }}
                  >
                    <p className="text-xs text-indigo-400 mb-3">뒷면</p>
                    <p className="text-xl font-medium text-indigo-800 text-center">
                      {card.backContent}
                    </p>
                  </div>
                </div>
              </div>

              {!isFlipped ? (
                <p className="text-center text-gray-400 text-sm mt-6">
                  카드를 클릭해서 답을 확인하세요
                </p>
              ) : (
                <div className="mt-6">
                  <p className="text-center text-sm text-gray-500 mb-3">
                    얼마나 잘 기억했나요?
                  </p>
                  <div className="grid grid-cols-4 gap-2">
                    {RATINGS.map((r) => (
                      <button
                        key={r.value}
                        onClick={() => handleRate(r.value)}
                        className={`${r.color} text-white py-3 rounded-xl font-semibold text-sm transition`}
                      >
                        {r.label}
                      </button>
                    ))}
                  </div>
                </div>
              )}
            </div>
          )}
        </div>

        {/* 오른쪽: 참가자 목록 + 활동 로그 */}
        <div className="w-64 shrink-0 space-y-4">
          <div className="bg-white rounded-xl p-4 shadow-sm">
            <h3 className="font-semibold text-gray-700 mb-3 text-sm">참가자</h3>
            {members.map((m) => (
              <div key={m.userId} className="flex items-center gap-2 py-1.5">
                <div
                  className={`w-2 h-2 rounded-full shrink-0 ${
                    m.ready ? 'bg-green-400' : 'bg-gray-300'
                  }`}
                />
                <span className="text-sm text-gray-700 truncate">{m.nickname}</span>
                {m.nickname === session?.ownerNickname && (
                  <span className="text-xs text-indigo-400 ml-auto shrink-0">방장</span>
                )}
              </div>
            ))}
          </div>

          <div className="bg-white rounded-xl p-4 shadow-sm">
            <h3 className="font-semibold text-gray-700 mb-3 text-sm">활동 로그</h3>
            <div className="space-y-1.5 max-h-40 overflow-y-auto">
              {messages.length === 0 ? (
                <p className="text-xs text-gray-400">활동이 없습니다.</p>
              ) : (
                messages.map((msg, i) => (
                  <p key={i} className="text-xs text-gray-500">
                    {msg}
                  </p>
                ))
              )}
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}
