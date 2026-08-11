package com.notwrong.qversity.domain.collaboration.dto;

import com.notwrong.qversity.domain.collaboration.entity.CollabSession;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class CollabSessionResponse {
    private Long id;
    private String roomCode;
    private String ownerNickname;
    private Long deckId;
    private String deckName;
    private String status;
    private LocalDateTime createdAt;
    private List<MemberInfo> members;

    @Getter
    @Builder
    public static class MemberInfo {
        private Long userId;
        private String nickname;
        private boolean ready;
    }

    public static CollabSessionResponse from(CollabSession session, List<MemberInfo> members) {
        return CollabSessionResponse.builder()
                .id(session.getId())
                .roomCode(session.getRoomCode())
                .ownerNickname(session.getOwner().getNickname())
                .deckId(session.getDeck().getDeckId())
                .deckName(session.getDeck().getName())
                .status(session.getStatus())
                .createdAt(session.getCreatedAt())
                .members(members)
                .build();
    }
}
