package com.ognjen.fleetforge.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.dtos.ChatMessageResponseDTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ChatMessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    private List<ChatMessageResponseDTO> messages;
    private Context context;
    private Long currentUserId;

    public ChatMessagesAdapter(Context context, Long currentUserId) {
        this.context = context;
        this.messages = new ArrayList<>();
        this.currentUserId = currentUserId;
    }

    public void setMessages(List<ChatMessageResponseDTO> messages) {
        this.messages = messages != null ? messages : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addMessage(ChatMessageResponseDTO message) {
        this.messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessageResponseDTO message = messages.get(position);
        if (message.getSenderId().equals(currentUserId)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessageResponseDTO message = messages.get(position);

        if (holder instanceof SentMessageViewHolder) {
            ((SentMessageViewHolder) holder).bind(message);
        } else if (holder instanceof ReceivedMessageViewHolder) {
            ((ReceivedMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    // ViewHolder for sent messages
    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView messageContent;
        private TextView timestamp;

        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageContent = itemView.findViewById(R.id.message_content);
            timestamp = itemView.findViewById(R.id.message_timestamp);
        }

        public void bind(ChatMessageResponseDTO message) {
            messageContent.setText(message.getContent());
            timestamp.setText(formatTimestamp(message.getSentAt()));
        }

        private String formatTimestamp(LocalDateTime dateTime) {
            if (dateTime == null) return "";
            return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        }
    }

    // ViewHolder for received messages
    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView senderName;
        private TextView messageContent;
        private TextView timestamp;

        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            senderName = itemView.findViewById(R.id.sender_name);
            messageContent = itemView.findViewById(R.id.message_content);
            timestamp = itemView.findViewById(R.id.message_timestamp);
        }

        public void bind(ChatMessageResponseDTO message) {
            senderName.setText(message.getSenderName());
            messageContent.setText(message.getContent());
            timestamp.setText(formatTimestamp(message.getSentAt()));
        }

        private String formatTimestamp(LocalDateTime dateTime) {
            if (dateTime == null) return "";
            return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        }
    }
}