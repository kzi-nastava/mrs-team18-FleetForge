package com.ognjen.fleetforge.api;

import com.ognjen.fleetforge.dtos.ChatMessageResponseDTO;
import com.ognjen.fleetforge.dtos.ChatResponseDTO;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ChatService {

    @GET("/api/chats/my-chat")
    Call<Map<String, Long>> getMyChat();

    @GET("/api/chats")
    Call<List<ChatResponseDTO>> getAllChats();

    @GET("/api/chats/{chatId}/messages")
    Call<List<ChatMessageResponseDTO>> getChatHistory(@Path("chatId") Long chatId);

    @GET("/api/chats/{chatId}/unread-count")
    Call<Map<String, Integer>> getUnreadCount(@Path("chatId") Long chatId);

    @POST("/api/chats/{chatId}/mark-as-read")
    Call<Void> markAsRead(@Path("chatId") Long chatId);

    @GET("/api/chats/userId")
    Call<Map<String, Long>> getUserId();
}