package com.team18.FleetForge.repository.chat;

import com.team18.FleetForge.model.chat.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {


    List<ChatMessage> findByChatIdOrderBySentAtAsc(Long chatId);

    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.chat.id = :chatId AND m.sender.id != :userId AND m.isRead = false")
    int countUnreadMessagesInChat(@Param("chatId") Long chatId, @Param("userId") Long userId);

    @Modifying
    @Query("UPDATE ChatMessage m SET m.isRead = true, m.readAt = CURRENT_TIMESTAMP WHERE m.chat.id = :chatId AND m.sender.id != :userId AND m.isRead = false")
    void markAllAsReadInChat(@Param("chatId") Long chatId, @Param("userId") Long userId);

    ChatMessage findFirstByChatIdOrderBySentAtDesc(Long chatId);
}