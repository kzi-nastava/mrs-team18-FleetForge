package com.team18.FleetForge.controller.chat;

import com.team18.FleetForge.dto.chat.ChatMessageResponseDTO;
import com.team18.FleetForge.dto.chat.SendMessageDTO;
import com.team18.FleetForge.model.chat.Chat;
import com.team18.FleetForge.model.chat.ChatMessage;
import com.team18.FleetForge.model.enums.Role;
import com.team18.FleetForge.model.users.User;
import com.team18.FleetForge.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;


@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Handle incoming chat messages via WebSocket.
     * Message flow:
     * 1. User/Admin sends message
     * 2. Save to database
     * 3. Send to recipient via their user-specific queue
     *
     * Recipients subscribe to: /user/queue/messages
     */
    @MessageMapping("/chat/send")
    public void sendMessage(
            @Valid @Payload SendMessageDTO messageDTO,
            Authentication authentication
    ) {
        User sender = (User) authentication.getPrincipal();

        Chat chat = chatService.getChatById(messageDTO.getChatId());

        if (sender.getRole() != Role.ROLE_ADMIN && !chat.getUser().getId().equals(sender.getId())) {
            throw new IllegalArgumentException("You don't have access to this chat");
        }

        ChatMessage savedMessage = chatService.saveMessage(chat, sender, messageDTO.getContent());

        ChatMessageResponseDTO responseDTO = ChatMessageResponseDTO.builder()
                .id(savedMessage.getId())
                .chatId(savedMessage.getChat().getId())
                .senderId(savedMessage.getSender().getId())
                .senderName(savedMessage.getSender().getFirstName() + " " + savedMessage.getSender().getLastName())
                .senderRole(savedMessage.getSender().getRole().name().replace("ROLE_", ""))
                .content(savedMessage.getContent())
                .sentAt(savedMessage.getSentAt())
                .isRead(savedMessage.isRead())
                .readAt(savedMessage.getReadAt())
                .build();

        User recipient;
        if (sender.getRole() == Role.ROLE_ADMIN) {
            recipient = chat.getUser();
        } else {
            messagingTemplate.convertAndSend("/topic/admin/messages", responseDTO);
            return;
        }

        messagingTemplate.convertAndSendToUser(
                recipient.getEmail(),
                "/queue/messages",
                responseDTO
        );
    }
}