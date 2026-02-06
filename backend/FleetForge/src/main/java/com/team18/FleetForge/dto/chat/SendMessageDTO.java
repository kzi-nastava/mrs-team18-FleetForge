package com.team18.FleetForge.dto.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for sending a chat message via WebSocket.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendMessageDTO {

    @NotNull(message = "Chat ID is required")
    private Long chatId;

    @NotBlank(message = "Message content cannot be empty")
    private String content;
}