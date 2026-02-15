package com.ognjen.fleetforge.fragments.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.adapters.ChatListAdapter;
import com.ognjen.fleetforge.api.RetrofitClient;
import com.ognjen.fleetforge.auth.AuthManager;
import com.ognjen.fleetforge.dtos.ChatResponseDTO;
import com.ognjen.fleetforge.utils.WebSocketManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminChatFragment extends Fragment {

    private static final String TAG = "AdminChatFragment";

    private RecyclerView recyclerView;
    private ChatListAdapter adapter;
    private ProgressBar progressBar;
    private TextView emptyView;

    private WebSocketManager webSocketManager;
    private AuthManager authManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_chat, container, false);

        initViews(view);
        setupRecyclerView();
        setupWebSocket();
        loadChats();

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.chats_recycler_view);
        progressBar = view.findViewById(R.id.progress_bar);
        emptyView = view.findViewById(R.id.empty_view);

        authManager = AuthManager.getInstance();
        webSocketManager = WebSocketManager.getInstance();
    }

    private void setupRecyclerView() {
        adapter = new ChatListAdapter(requireContext(), chat -> {
            openChatDetail(chat);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupWebSocket() {
        String token = authManager.getToken();
        if (token != null && !webSocketManager.isConnected()) {
            webSocketManager.connect(token);
        }

        webSocketManager.getConnectionStatus().observe(getViewLifecycleOwner(), isConnected -> {
            if (isConnected) {
                Log.d(TAG, "WebSocket connected");
                webSocketManager.subscribeToBothChatChannels();
            } else {
                Log.d(TAG, "WebSocket disconnected");
            }
        });

        webSocketManager.getIncomingMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Log.d(TAG, "New message received, refreshing chat list");
                loadChats();
            }
        });
    }

    private void loadChats() {
        progressBar.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);

        RetrofitClient.getInstance().getChatService().getAllChats()
                .enqueue(new Callback<List<ChatResponseDTO>>() {
                    @Override
                    public void onResponse(Call<List<ChatResponseDTO>> call, Response<List<ChatResponseDTO>> response) {
                        progressBar.setVisibility(View.GONE);

                        if (response.isSuccessful() && response.body() != null) {
                            List<ChatResponseDTO> chats = response.body();

                            if (chats.isEmpty()) {
                                emptyView.setVisibility(View.VISIBLE);
                            } else {
                                adapter.setChatList(chats);
                            }
                        } else {
                            Toast.makeText(requireContext(), "Failed to load chats", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ChatResponseDTO>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Log.e(TAG, "Error loading chats", t);
                        Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openChatDetail(ChatResponseDTO chat) {
        Fragment chatDetailFragment = ChatDetailFragment.newInstance(chat.getId(), chat.getUserName());

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, chatDetailFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadChats();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}