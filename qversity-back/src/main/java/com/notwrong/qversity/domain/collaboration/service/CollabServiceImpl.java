package com.notwrong.qversity.domain.collaboration.service;

import com.notwrong.qversity.domain.collaboration.dto.CollabSessionResponse;
import com.notwrong.qversity.domain.collaboration.entity.CollabMember;
import com.notwrong.qversity.domain.collaboration.entity.CollabSession;
import com.notwrong.qversity.domain.collaboration.repository.CollabMemberRepository;
import com.notwrong.qversity.domain.collaboration.repository.CollabSessionRepository;
import com.notwrong.qversity.domain.deck.entity.Deck;
import com.notwrong.qversity.domain.deck.service.DeckService;
import com.notwrong.qversity.domain.user.entity.User;
import com.notwrong.qversity.domain.user.repository.UserRepository;
import com.notwrong.qversity.global.exception.BaseException;
import com.notwrong.qversity.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CollabServiceImpl implements CollabService {

    private final CollabSessionRepository sessionRepository;
    private final CollabMemberRepository memberRepository;
    private final UserRepository userRepository;
    private final DeckService deckService;

    @Override
    @Transactional
    public CollabSessionResponse createSession(Long userId, Long deckId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));
        Deck deck = deckService.findById(deckId);

        String roomCode = generateRoomCode();
        CollabSession session = CollabSession.builder()
                .roomCode(roomCode)
                .owner(user)
                .deck(deck)
                .status("WAITING")
                .build();
        session = sessionRepository.save(session);

        CollabMember member = CollabMember.builder()
                .session(session)
                .user(user)
                .ready(false)
                .build();
        memberRepository.save(member);

        return buildResponse(session);
    }

    @Override
    public CollabSessionResponse getSession(String roomCode, Long userId) {
        CollabSession session = findByRoomCode(roomCode);
        return buildResponse(session);
    }

    @Override
    @Transactional
    public CollabSessionResponse joinSession(String roomCode, Long userId) {
        CollabSession session = findByRoomCode(roomCode);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        if (memberRepository.findBySession_IdAndUser_UserId(session.getId(), userId).isPresent()) {
            return buildResponse(session);
        }

        if (memberRepository.countBySession_Id(session.getId()) >= 10) {
            throw new BaseException(ErrorCode.COLLAB_SESSION_FULL);
        }

        CollabMember member = CollabMember.builder()
                .session(session)
                .user(user)
                .ready(false)
                .build();
        memberRepository.save(member);
        return buildResponse(session);
    }

    @Override
    @Transactional
    public void setReady(String roomCode, Long userId, boolean ready) {
        CollabSession session = findByRoomCode(roomCode);
        CollabMember member = memberRepository.findBySession_IdAndUser_UserId(session.getId(), userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));
        member.setReady(ready);
        memberRepository.save(member);
    }

    @Override
    @Transactional
    public CollabSessionResponse startSession(String roomCode, Long userId) {
        CollabSession session = findByRoomCode(roomCode);
        if (!session.getOwner().getUserId().equals(userId)) {
            throw new BaseException(ErrorCode.HANDLE_ACCESS_DENIED);
        }
        session.setStatus("ACTIVE");
        sessionRepository.save(session);
        return buildResponse(session);
    }

    @Override
    @Transactional
    public void leaveSession(String roomCode, Long userId) {
        CollabSession session = findByRoomCode(roomCode);
        memberRepository.findBySession_IdAndUser_UserId(session.getId(), userId)
                .ifPresent(memberRepository::delete);

        if (session.getOwner().getUserId().equals(userId)
                || memberRepository.countBySession_Id(session.getId()) == 0) {
            session.setStatus("FINISHED");
            session.setFinishedAt(LocalDateTime.now());
            sessionRepository.save(session);
        }
    }

    private CollabSession findByRoomCode(String roomCode) {
        return sessionRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new BaseException(ErrorCode.COLLAB_SESSION_NOT_FOUND));
    }

    private CollabSessionResponse buildResponse(CollabSession session) {
        List<CollabSessionResponse.MemberInfo> members = memberRepository.findBySession_Id(session.getId())
                .stream()
                .map(m -> CollabSessionResponse.MemberInfo.builder()
                        .userId(m.getUser().getUserId())
                        .nickname(m.getUser().getNickname())
                        .ready(m.isReady())
                        .build())
                .collect(Collectors.toList());
        return CollabSessionResponse.from(session, members);
    }

    private String generateRoomCode() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        Random rand = new Random();
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) sb.append(chars.charAt(rand.nextInt(chars.length())));
        String code = sb.toString();
        if (sessionRepository.findByRoomCode(code).isPresent()) {
            return generateRoomCode();
        }
        return code;
    }
}
