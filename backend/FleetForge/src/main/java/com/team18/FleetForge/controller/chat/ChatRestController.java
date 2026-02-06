package com.team18.FleetForge.controller.chat;

import com.team18.FleetForge.dto.chat.ChatMessageResponseDTO;
import com.team18.FleetForge.dto.chat.ChatResponseDTO;
import com.team18.FleetForge.model.chat.Chat;
import com.team18.FleetForge.model.enums.Role;
import com.team18.FleetForge.model.users.User;
import com.team18.FleetForge.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatRestController {

    private final ChatService chatService;

    /**
     * GET /api/chats/my-chat
     * Get or create the current user's chat with admin.
     *
     * Returns: { "chatId": 123 }
     */
    @GetMapping("/my-chat")
    @PreAuthorize("hasAnyRole('PASSENGER', 'DRIVER')")
    public ResponseEntity<Map<String, Long>> getMyChat(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Chat chat = chatService.getOrCreateChatForUser(user);
        return ResponseEntity.ok(Map.of("chatId", chat.getId()));
    }

    /**
     * GET /api/chats
     * Get all chats (admin only).
     *
     * Returns list of chats with unread counts and last message info.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ChatResponseDTO>> getAllChats(Authentication authentication) {
        User admin = (User) authentication.getPrincipal();
        List<ChatResponseDTO> chats = chatService.getAllChats(admin);
        return ResponseEntity.ok(chats);
    }

    /**
     * GET /api/chats/{chatId}/messages
     * Get all messages in a chat (chat history).
     *
     * Returns list of messages ordered by time.
     */
    @GetMapping("/{chatId}/messages")
    @PreAuthorize("hasAnyRole('PASSENGER', 'DRIVER', 'ADMIN')")
    public ResponseEntity<List<ChatMessageResponseDTO>> getChatHistory(
            @PathVariable Long chatId,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        List<ChatMessageResponseDTO> messages = chatService.getChatHistory(chatId, user);
        return ResponseEntity.ok(messages);
    }

    /**
     * GET /api/chats/{chatId}/unread-count
     * Get the count of unread messages in a chat.
     *
     * Returns: { "unreadCount": 5 }
     */
    @GetMapping("/{chatId}/unread-count")
    @PreAuthorize("hasAnyRole('PASSENGER', 'DRIVER', 'ADMIN')")
    public ResponseEntity<Map<String, Integer>> getUnreadCount(
            @PathVariable Long chatId,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        int unreadCount = chatService.getUnreadCount(chatId, user);
        return ResponseEntity.ok(Map.of("unreadCount", unreadCount));
    }

    /**
     * POST /api/chats/{chatId}/mark-as-read
     * Mark all messages in a chat as read.
     *
     * Returns: 204 No Content
     */
    @PostMapping("/{chatId}/mark-as-read")
    @PreAuthorize("hasAnyRole('PASSENGER', 'DRIVER', 'ADMIN')")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long chatId,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        chatService.markAllAsRead(chatId, user);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/chats/user/{userId}
     * Get a specific user's chat (admin only).
     *
     * Returns: { "chatId": 123 }
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Long>> getChatByUserId(@PathVariable Long userId) {
        Chat chat = chatService.getChatById(userId);
        return ResponseEntity.ok(Map.of("chatId", chat.getId()));
    }
}