package com.ognjen.fleetforge.dtos;

import java.time.LocalDateTime;

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

    public ChatResponseDTO() {}

    public ChatResponseDTO(Long id, Long userId, String userName, String userRole,
                           String userProfilePicture, LocalDateTime createdAt,
                           LocalDateTime lastMessageAt, int unreadCount, String lastMessageContent) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.userRole = userRole;
        this.userProfilePicture = userProfilePicture;
        this.createdAt = createdAt;
        this.lastMessageAt = lastMessageAt;
        this.unreadCount = unreadCount;
        this.lastMessageContent = lastMessageContent;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getUserProfilePicture() {
        return userProfilePicture;
    }

    public void setUserProfilePicture(String userProfilePicture) {
        this.userProfilePicture = userProfilePicture;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastMessageAt() {
        return lastMessageAt;
    }

    public void setLastMessageAt(LocalDateTime lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getLastMessageContent() {
        return lastMessageContent;
    }

    public void setLastMessageContent(String lastMessageContent) {
        this.lastMessageContent = lastMessageContent;
    }
}