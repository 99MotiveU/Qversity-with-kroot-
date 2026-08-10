package com.notwrong.qversity.domain.collaboration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollabMessage {
    private String type; // JOIN, READY, CARD_SHOW, RATE, NEXT_CARD, LEAVE, FINISH
    private String senderNickname;
    private Long senderId;
    private Object data;
    private String roomCode;
}
