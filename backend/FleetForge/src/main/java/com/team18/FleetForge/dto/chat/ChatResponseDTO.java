package com.team18.FleetForge.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatResponseDTO {

    private Long id;
    private Long userId;
    private String userName;
    private String userRole;
    private String userProfilePicture;
    private LocalDateTime createdAt;
    private LocalDateTime lastMessageAt;
    private int unreadCount;
    private String lastMessageContent;
}