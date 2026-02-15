package com.ognjen.fleetforge.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ognjen.fleetforge.BuildConfig;
import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.dtos.ChatResponseDTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder> {

    private List<ChatResponseDTO> chatList;
    private Context context;
    private OnChatClickListener listener;

    public interface OnChatClickListener {
        void onChatClick(ChatResponseDTO chat);
    }

    public ChatListAdapter(Context context, OnChatClickListener listener) {
        this.context = context;
        this.chatList = new ArrayList<>();
        this.listener = listener;
    }

    public void setChatList(List<ChatResponseDTO> chatList) {
        this.chatList = chatList != null ? chatList : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void updateChat(ChatResponseDTO updatedChat) {
        for (int i = 0; i < chatList.size(); i++) {
            if (chatList.get(i).getId().equals(updatedChat.getId())) {
                chatList.set(i, updatedChat);
                notifyItemChanged(i);
                return;
            }
        }
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatResponseDTO chat = chatList.get(position);
        holder.bind(chat);
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {
        private ImageView profileImage;
        private TextView userName;
        private TextView lastMessage;
        private TextView timestamp;
        private TextView unreadBadge;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.chat_profile_image);
            userName = itemView.findViewById(R.id.chat_user_name);
            lastMessage = itemView.findViewById(R.id.chat_last_message);
            timestamp = itemView.findViewById(R.id.chat_timestamp);
            unreadBadge = itemView.findViewById(R.id.chat_unread_badge);

            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onChatClick(chatList.get(position));
                }
            });
        }

        public void bind(ChatResponseDTO chat) {
            userName.setText(chat.getUserName());

            // Set last message
            if (chat.getLastMessageContent() != null && !chat.getLastMessageContent().isEmpty()) {
                lastMessage.setText(chat.getLastMessageContent());
                lastMessage.setVisibility(View.VISIBLE);
            } else {
                lastMessage.setText("No messages yet");
                lastMessage.setVisibility(View.VISIBLE);
            }

            // Set timestamp
            if (chat.getLastMessageAt() != null) {
                timestamp.setText(formatTimestamp(chat.getLastMessageAt()));
                timestamp.setVisibility(View.VISIBLE);
            } else {
                timestamp.setVisibility(View.GONE);
            }

            // Set unread badge
            if (chat.getUnreadCount() > 0) {
                unreadBadge.setText(String.valueOf(chat.getUnreadCount()));
                unreadBadge.setVisibility(View.VISIBLE);
            } else {
                unreadBadge.setVisibility(View.GONE);
            }

            // Load profile picture
            if (chat.getUserProfilePicture() != null && !chat.getUserProfilePicture().isEmpty()) {
                String imageUrl = "http://" + BuildConfig.IP_ADDR + ":8080" + chat.getUserProfilePicture();
                Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_profile_placeholder)
                        .error(R.drawable.ic_profile_placeholder)
                        .circleCrop()
                        .into(profileImage);
            } else {
                profileImage.setImageResource(R.drawable.ic_profile_placeholder);
            }
        }

        private String formatTimestamp(LocalDateTime dateTime) {
            LocalDateTime now = LocalDateTime.now();

            if (dateTime.toLocalDate().equals(now.toLocalDate())) {
                // Today - show time
                return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
            } else if (dateTime.toLocalDate().equals(now.toLocalDate().minusDays(1))) {
                // Yesterday
                return "Yesterday";
            } else if (dateTime.getYear() == now.getYear()) {
                // This year - show date without year
                return dateTime.format(DateTimeFormatter.ofPattern("MMM dd"));
            } else {
                // Different year - show full date
                return dateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
            }
        }
    }
}