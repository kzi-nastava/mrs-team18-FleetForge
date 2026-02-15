package com.ognjen.fleetforge.fragments.passenger;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.adapters.ChatMessagesAdapter;
import com.ognjen.fleetforge.api.RetrofitClient;
import com.ognjen.fleetforge.auth.AuthManager;
import com.ognjen.fleetforge.dtos.ChatMessageResponseDTO;
import com.ognjen.fleetforge.dtos.SendMessageDTO;
import com.ognjen.fleetforge.utils.WebSocketManager;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PassengerChatFragment extends Fragment {

    private static final String TAG = "PassengerChatFragment";

    private RecyclerView messagesRecyclerView;
    private ChatMessagesAdapter messagesAdapter;
    private EditText messageInput;
    private ImageButton sendButton;
    private ProgressBar progressBar;
    private TextView emptyView;
    private TextView toolbarTitle;

    private Long chatId;
    private Long currentUserId;

    private WebSocketManager webSocketManager;
    private AuthManager authManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_detail, container, false);

        initViews(view);
        loadCurrentUserId();
        loadOrCreateChat();
        setupWebSocket();
        setupSendButton();

        return view;
    }

    private void initViews(View view) {
        messagesRecyclerView = view.findViewById(R.id.messages_recycler_view);
        messageInput = view.findViewById(R.id.message_input);
        sendButton = view.findViewById(R.id.send_button);
        progressBar = view.findViewById(R.id.progress_bar);
        emptyView = view.findViewById(R.id.empty_view);
        toolbarTitle = view.findViewById(R.id.toolbar_title);

        toolbarTitle.setText("Support Chat");

        authManager = AuthManager.getInstance();
        webSocketManager = WebSocketManager.getInstance();
    }

    private void loadCurrentUserId() {
        RetrofitClient.getInstance().getChatService().getUserId()
                .enqueue(new Callback<Map<String, Long>>() {
                    @Override
                    public void onResponse(Call<Map<String, Long>> call, Response<Map<String, Long>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            currentUserId = response.body().get("userId");
                            setupRecyclerView();
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, Long>> call, Throwable t) {
                        Log.e(TAG, "Error loading user ID", t);
                    }
                });
    }

    private void setupRecyclerView() {
        if (currentUserId != null) {
            messagesAdapter = new ChatMessagesAdapter(requireContext(), currentUserId);
            LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
            layoutManager.setStackFromEnd(true);
            messagesRecyclerView.setLayoutManager(layoutManager);
            messagesRecyclerView.setAdapter(messagesAdapter);
        }
    }

    private void loadOrCreateChat() {
        progressBar.setVisibility(View.VISIBLE);

        RetrofitClient.getInstance().getChatService().getMyChat()
                .enqueue(new Callback<Map<String, Long>>() {
                    @Override
                    public void onResponse(Call<Map<String, Long>> call, Response<Map<String, Long>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            chatId = response.body().get("chatId");
                            loadChatHistory();
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(requireContext(), "Failed to load chat", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, Long>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Log.e(TAG, "Error loading chat", t);
                        Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupWebSocket() {
        String token = authManager.getToken();
        if (token != null && !webSocketManager.isConnected()) {
            webSocketManager.connect(token);
        }

        webSocketManager.getConnectionStatus().observe(getViewLifecycleOwner(), isConnected -> {
            if (isConnected) {
                Log.d(TAG, "WebSocket connected");
                webSocketManager.subscribeToUserMessages();
            }
        });

        webSocketManager.getIncomingMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && chatId != null && message.getChatId().equals(chatId)) {
                Log.d(TAG, "New message received");
                if (messagesAdapter != null) {
                    messagesAdapter.addMessage(message);
                    scrollToBottom();
                }
                markMessagesAsRead();
            }
        });
    }

    private void loadChatHistory() {
        if (chatId == null) return;

        RetrofitClient.getInstance().getChatService().getChatHistory(chatId)
                .enqueue(new Callback<List<ChatMessageResponseDTO>>() {
                    @Override
                    public void onResponse(Call<List<ChatMessageResponseDTO>> call, Response<List<ChatMessageResponseDTO>> response) {
                        progressBar.setVisibility(View.GONE);

                        if (response.isSuccessful() && response.body() != null) {
                            List<ChatMessageResponseDTO> messages = response.body();

                            if (messages.isEmpty()) {
                                emptyView.setVisibility(View.VISIBLE);
                            } else {
                                emptyView.setVisibility(View.GONE);
                                if (messagesAdapter != null) {
                                    messagesAdapter.setMessages(messages);
                                    scrollToBottom();
                                }
                            }

                            markMessagesAsRead();
                        } else {
                            Toast.makeText(requireContext(), "Failed to load messages", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ChatMessageResponseDTO>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Log.e(TAG, "Error loading chat history", t);
                        Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupSendButton() {
        sendButton.setOnClickListener(v -> sendMessage());

        messageInput.setOnEditorActionListener((v, actionId, event) -> {
            sendMessage();
            return true;
        });
    }

    private void sendMessage() {
        String content = messageInput.getText().toString().trim();

        if (content.isEmpty()) {
            Toast.makeText(requireContext(), "Message cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (chatId == null) {
            Toast.makeText(requireContext(), "Chat not initialized", Toast.LENGTH_SHORT).show();
            return;
        }

        messageInput.setText("");

        android.view.inputmethod.InputMethodManager imm =
                (android.view.inputmethod.InputMethodManager) requireContext()
                        .getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(messageInput.getWindowToken(), 0);
        }
        messageInput.clearFocus();

        SendMessageDTO messageDTO = new SendMessageDTO(chatId, content);
        webSocketManager.sendChatMessage(messageDTO);

        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            loadChatHistory();
        }, 500);
    }

    private void markMessagesAsRead() {
        if (chatId == null) return;

        RetrofitClient.getInstance().getChatService().markAsRead(chatId)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "Messages marked as read");
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e(TAG, "Error marking messages as read", t);
                    }
                });
    }

    private void scrollToBottom() {
        if (messagesAdapter != null && messagesAdapter.getItemCount() > 0) {
            messagesRecyclerView.smoothScrollToPosition(messagesAdapter.getItemCount() - 1);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}