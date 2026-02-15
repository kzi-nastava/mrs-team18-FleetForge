package com.ognjen.fleetforge.dtos;

public class SendMessageDTO {
    private Long chatId;
    private String content;

    public SendMessageDTO() {}

    public SendMessageDTO(Long chatId, String content) {
        this.chatId = chatId;
        this.content = content;
    }
    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}