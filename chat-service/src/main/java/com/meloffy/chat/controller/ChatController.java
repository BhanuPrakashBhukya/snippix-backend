package com.meloffy.chat.controller;

import com.meloffy.chat.model.ChatMessage;
import com.meloffy.chat.service.ChatMessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatMessageService chatService;

    public ChatController(ChatMessageService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/{otherUserId}/messages")
    public ResponseEntity<List<ChatMessage>> getMessages(
            @PathVariable("otherUserId") String otherUserId,
            @RequestHeader("X-User-Id") String currentUserId,
            @RequestParam(name = "limit", defaultValue = "50") int limit
    ) {
        String conversationId =
                chatService.buildConversationId(currentUserId, otherUserId);
        return ResponseEntity.ok(
                chatService.fetchConversation(conversationId, limit)
        );
    }

    private String getAuthenticatedUser(Authentication authentication) {
        Jwt jwt =  (Jwt) authentication.getPrincipal();
        return jwt.getSubject();
    }
}
