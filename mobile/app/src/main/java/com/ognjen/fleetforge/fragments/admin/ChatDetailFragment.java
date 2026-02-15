package com.ognjen.fleetforge.fragments.admin;

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

public class ChatDetailFragment extends Fragment {

    private static final String TAG = "ChatDetailFragment";
    private static final String ARG_CHAT_ID = "chat_id";
    private static final String ARG_USER_NAME = "user_name";

    private RecyclerView messagesRecyclerView;
    private ChatMessagesAdapter messagesAdapter;
    private EditText messageInput;
    private ImageButton sendButton;
    private ProgressBar progressBar;
    private TextView emptyView;
    private TextView toolbarTitle;

    private Long chatId;
    private String userName;
    private Long currentUserId;

    private WebSocketManager webSocketManager;
    private AuthManager authManager;

    public static ChatDetailFragment newInstance(Long chatId, String userName) {
        ChatDetailFragment fragment = new ChatDetailFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_CHAT_ID, chatId);
        args.putString(ARG_USER_NAME, userName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            chatId = getArguments().getLong(ARG_CHAT_ID);
            userName = getArguments().getString(ARG_USER_NAME);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_detail, container, false);

        initViews(view);
        loadCurrentUserId();
        setupRecyclerView();
        setupWebSocket();
        loadChatHistory();
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

        if (userName != null) {
            toolbarTitle.setText(userName);
        }

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

    private void setupWebSocket() {
        String token = authManager.getToken();
        if (token != null && !webSocketManager.isConnected()) {
            webSocketManager.connect(token);
        }

        webSocketManager.getConnectionStatus().observe(getViewLifecycleOwner(), isConnected -> {
            if (isConnected) {
                Log.d(TAG, "WebSocket connected");
                if (authManager.getCurrentRole().name().equals("ADMIN")) {
                    webSocketManager.subscribeToBothChatChannels();
                } else {
                    webSocketManager.subscribeToUserMessages();
                }
            }
        });

        webSocketManager.getIncomingMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && message.getChatId().equals(chatId)) {
                if (messagesAdapter != null) {
                    messagesAdapter.addMessage(message);
                    scrollToBottom();
                }
                markMessagesAsRead();
            }
        });
    }

    private void loadChatHistory() {
        progressBar.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);

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