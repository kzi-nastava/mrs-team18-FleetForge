package com.team18.FleetForge.repository.chat;

import com.team18.FleetForge.model.chat.Chat;
import com.team18.FleetForge.model.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {


    Optional<Chat> findByUser(User user);

    Optional<Chat> findByUserId(Long userId);

    List<Chat> findAllByOrderByLastMessageAtDesc();

    boolean existsByUserId(Long userId);
}