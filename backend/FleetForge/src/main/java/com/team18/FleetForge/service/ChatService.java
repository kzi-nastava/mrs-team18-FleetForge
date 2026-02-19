package com.team18.FleetForge.service;

import com.team18.FleetForge.dto.chat.ChatMessageResponseDTO;
import com.team18.FleetForge.dto.chat.ChatResponseDTO;
import com.team18.FleetForge.model.chat.Chat;
import com.team18.FleetForge.model.chat.ChatMessage;
import com.team18.FleetForge.model.enums.Role;
import com.team18.FleetForge.model.users.User;
import com.team18.FleetForge.repository.chat.ChatMessageRepository;
import com.team18.FleetForge.repository.chat.ChatRepository;
import com.team18.FleetForge.repository.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    @Transactional
    public Chat getOrCreateChatForUser(User user) {
        return chatRepository.findByUser(user)
                .orElseGet(() -> {
                    Chat newChat = Chat.builder()
                            .user(user)
                            .build();
                    return chatRepository.save(newChat);
                });
    }

    @Transactional(readOnly = true)
    public Chat getChatById(Long chatId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found with ID: " + chatId));
        chat.getUser().getEmail();
        return chat;
    }

    @Transactional(readOnly = true)
    public List<ChatResponseDTO> getAllChats(User admin) {
        if (admin.getRole() != Role.ROLE_ADMIN) {
            throw new IllegalArgumentException("Only admins can view all chats");
        }

        List<Chat> chats = chatRepository.findAllByOrderByLastMessageAtDesc();

        return chats.stream()
                .map(chat -> {
                    int unreadCount = chatMessageRepository.countUnreadMessagesInChat(chat.getId(), admin.getId());
                    ChatMessage lastMessage = chatMessageRepository.findFirstByChatIdOrderBySentAtDesc(chat.getId());

                    return ChatResponseDTO.builder()
                            .id(chat.getId())
                            .userId(chat.getUser().getId())
                            .userName(chat.getUser().getFirstName() + " " + chat.getUser().getLastName())
                            .userRole(chat.getUser().getRole().name().replace("ROLE_", ""))
                            .userProfilePicture(chat.getUser().getProfilePicture())
                            .createdAt(chat.getCreatedAt())
                            .lastMessageAt(chat.getLastMessageAt())
                            .unreadCount(unreadCount)
                            .lastMessageContent(lastMessage != null ? lastMessage.getContent() : null)
                            .build();
                })
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<ChatMessageResponseDTO> getChatHistory(Long chatId, User user) {
        Chat chat = getChatById(chatId);

        if (user.getRole() != Role.ROLE_ADMIN && !chat.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You don't have access to this chat");
        }

        List<ChatMessage> messages = chatMessageRepository.findByChatIdOrderBySentAtAsc(chatId);

        return messages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public int getUnreadCount(Long chatId, User user) {
        Chat chat = getChatById(chatId);

        if (user.getRole() != Role.ROLE_ADMIN && !chat.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You don't have access to this chat");
        }

        return chatMessageRepository.countUnreadMessagesInChat(chatId, user.getId());
    }

    @Transactional
    public void markAllAsRead(Long chatId, User user) {
        Chat chat = getChatById(chatId);

        if (user.getRole() != Role.ROLE_ADMIN && !chat.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You don't have access to this chat");
        }

        chatMessageRepository.markAllAsReadInChat(chatId, user.getId());
    }

    @Transactional
    public ChatMessage saveMessage(Chat chat, User sender, String content) {
        ChatMessage message = ChatMessage.builder()
                .chat(chat)
                .sender(sender)
                .content(content)
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(message);

        chat.setLastMessageAt(savedMessage.getSentAt());
        chatRepository.save(chat);

        return savedMessage;
    }

    private ChatMessageResponseDTO convertToDTO(ChatMessage message) {
        return ChatMessageResponseDTO.builder()
                .id(message.getId())
                .chatId(message.getChat().getId())
                .senderId(message.getSender().getId())
                .senderName(message.getSender().getFirstName() + " " + message.getSender().getLastName())
                .senderRole(message.getSender().getRole().name().replace("ROLE_", ""))
                .content(message.getContent())
                .sentAt(message.getSentAt())
                .isRead(message.isRead())
                .readAt(message.getReadAt())
                .build();
    }
}