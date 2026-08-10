package com.notwrong.qversity.domain.collaboration.controller;

import com.notwrong.qversity.domain.collaboration.dto.CollabMessage;
import com.notwrong.qversity.domain.collaboration.service.CollabService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CollabWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final CollabService collabService;

    @MessageMapping("/collab/{roomCode}/join")
    public void handleJoin(@DestinationVariable String roomCode,
                           @Payload CollabMessage message,
                           Principal principal) {
        log.info("User joined collab room: {}", roomCode);
        CollabMessage response = CollabMessage.builder()
                .type("JOIN")
                .senderNickname(message.getSenderNickname())
                .senderId(message.getSenderId())
                .roomCode(roomCode)
                .data(message.getData())
                .build();
        messagingTemplate.convertAndSend("/topic/collab/" + roomCode, response);
    }

    @MessageMapping("/collab/{roomCode}/ready")
    public void handleReady(@DestinationVariable String roomCode,
                            @Payload CollabMessage message) {
        if (message.getSenderId() != null) {
            Boolean ready = message.getData() instanceof Boolean b ? b : true;
            collabService.setReady(roomCode, message.getSenderId(), ready);
        }
        CollabMessage response = CollabMessage.builder()
                .type("READY")
                .senderNickname(message.getSenderNickname())
                .senderId(message.getSenderId())
                .roomCode(roomCode)
                .data(message.getData())
                .build();
        messagingTemplate.convertAndSend("/topic/collab/" + roomCode, response);
    }

    @MessageMapping("/collab/{roomCode}/card-show")
    public void handleCardShow(@DestinationVariable String roomCode,
                               @Payload CollabMessage message) {
        CollabMessage response = CollabMessage.builder()
                .type("CARD_SHOW")
                .senderNickname(message.getSenderNickname())
                .senderId(message.getSenderId())
                .roomCode(roomCode)
                .data(message.getData())
                .build();
        messagingTemplate.convertAndSend("/topic/collab/" + roomCode, response);
    }

    @MessageMapping("/collab/{roomCode}/rate")
    public void handleRate(@DestinationVariable String roomCode,
                           @Payload CollabMessage message) {
        CollabMessage response = CollabMessage.builder()
                .type("RATE")
                .senderNickname(message.getSenderNickname())
                .senderId(message.getSenderId())
                .roomCode(roomCode)
                .data(message.getData())
                .build();
        messagingTemplate.convertAndSend("/topic/collab/" + roomCode, response);
    }

    @MessageMapping("/collab/{roomCode}/next")
    public void handleNext(@DestinationVariable String roomCode,
                           @Payload CollabMessage message) {
        CollabMessage response = CollabMessage.builder()
                .type("NEXT_CARD")
                .senderNickname(message.getSenderNickname())
                .senderId(message.getSenderId())
                .roomCode(roomCode)
                .data(message.getData())
                .build();
        messagingTemplate.convertAndSend("/topic/collab/" + roomCode, response);
    }

    @MessageMapping("/collab/{roomCode}/leave")
    public void handleLeave(@DestinationVariable String roomCode,
                            @Payload CollabMessage message) {
        if (message.getSenderId() != null) {
            collabService.leaveSession(roomCode, message.getSenderId());
        }
        CollabMessage response = CollabMessage.builder()
                .type("LEAVE")
                .senderNickname(message.getSenderNickname())
                .senderId(message.getSenderId())
                .roomCode(roomCode)
                .data(null)
                .build();
        messagingTemplate.convertAndSend("/topic/collab/" + roomCode, response);
    }
}
