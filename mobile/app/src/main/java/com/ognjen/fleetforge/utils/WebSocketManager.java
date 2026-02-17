package com.ognjen.fleetforge.utils;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.ognjen.fleetforge.BuildConfig;
import com.ognjen.fleetforge.dtos.ChatMessageResponseDTO;
import com.ognjen.fleetforge.dtos.NotificationDTO;
import com.ognjen.fleetforge.dtos.SendMessageDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;

public class WebSocketManager {
    private static final String TAG = "WebSocketManager";
    private static WebSocketManager instance;

    private StompClient stomp;
    private static final String serverUrl = "ws://" + BuildConfig.IP_ADDR + ":8080/ws/websocket";

    private CompositeDisposable compositeDisposable;

    private MutableLiveData<NotificationDTO> notificationData = new MutableLiveData<>();

    private MutableLiveData<ChatMessageResponseDTO> incomingChatMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> connectionStatus = new MutableLiveData<>();

    private Gson gson;
    private boolean isConnected = false;

    private WebSocketManager() {
        compositeDisposable = new CompositeDisposable();
        stomp = Stomp.over(Stomp.ConnectionProvider.OKHTTP, serverUrl);

        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class,
                        (JsonDeserializer<LocalDateTime>) (json, type, ctx) ->
                                LocalDateTime.parse(json.getAsString()))
                .create();
    }

    public static synchronized WebSocketManager getInstance() {
        if (instance == null) {
            instance = new WebSocketManager();
        }
        return instance;
    }

    public MutableLiveData<NotificationDTO> getData() {
        return notificationData;
    }

    public MutableLiveData<ChatMessageResponseDTO> getIncomingMessage() {
        return incomingChatMessage;
    }

    public MutableLiveData<Boolean> getConnectionStatus() {
        return connectionStatus;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void connect(String jwtToken) {
        if (isConnected) {
            connectionStatus.postValue(true);
            return;
        }

        List<StompHeader> headers = new ArrayList<>();
        headers.add(new StompHeader("Authorization", "Bearer " + jwtToken));

        stomp.connect(headers);

        Disposable lifecycle = stomp.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            isConnected = true;
                            connectionStatus.postValue(true);
                            break;
                        case ERROR:
                            isConnected = false;
                            connectionStatus.postValue(false);
                            break;
                        case CLOSED:
                            isConnected = false;
                            connectionStatus.postValue(false);
                            break;
                        case FAILED_SERVER_HEARTBEAT:
                            break;
                    }
                });

        compositeDisposable.add(lifecycle);
    }

    public void subscribeToNotifications() {
        String topicPath = "/user/queue/notifications";
        Log.d(TAG, "📡 Subscribing to: " + topicPath);

        Disposable topic = stomp.topic(topicPath)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    Log.d(TAG, "✅ NOTIFICATION RECEIVED via WebSocket");
                    Log.d(TAG, "📦 Payload: " + topicMessage.getPayload());
                    handleNotificationMessage(topicMessage.getPayload());
                }, throwable -> {
                    Log.e(TAG, "❌ Error subscribing to notifications", throwable);
                });

        compositeDisposable.add(topic);
    }

    public void subscribeToUserMessages() {
        String topicPath = "/user/queue/messages";

        Disposable topic = stomp.topic(topicPath)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    Log.d(TAG, "✅ Received chat message on user queue: " + topicMessage.getPayload());
                    handleChatMessage(topicMessage.getPayload());
                }, throwable -> {
                    Log.e(TAG, "❌ Error subscribing to user messages", throwable);
                });

        compositeDisposable.add(topic);
    }

    public void subscribeToAdminMessages() {
        String topicPath = "/topic/admin/messages";

        Disposable topic = stomp.topic(topicPath)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    Log.d(TAG, "✅ Received chat message on admin topic: " + topicMessage.getPayload());
                    handleChatMessage(topicMessage.getPayload());
                }, throwable -> {
                    Log.e(TAG, "❌ Error subscribing to admin messages", throwable);
                });

        compositeDisposable.add(topic);
    }

    public void subscribeToBothChatChannels() {
        subscribeToUserMessages();
        subscribeToAdminMessages();
    }
    public void sendChatMessage(SendMessageDTO messageDTO) {
        if (!isConnected) {
            return;
        }

        String jsonMessage = gson.toJson(messageDTO);

        stomp.send("/app/chat/send", jsonMessage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Log.d(TAG, "✅ Message sent successfully to backend");
                }, throwable -> {
                    Log.e(TAG, "❌ Error sending message: " + throwable.getMessage(), throwable);
                });
    }

    private void handleNotificationMessage(String payload) {
        try {
            NotificationDTO notification = gson.fromJson(payload, NotificationDTO.class);
            notificationData.postValue(notification);
        } catch (Exception e) {
            Log.e(TAG, "❌ Error parsing notification", e);
        }
    }

    private void handleChatMessage(String payload) {
        try {
            ChatMessageResponseDTO message = gson.fromJson(payload, ChatMessageResponseDTO.class);
            incomingChatMessage.postValue(message);
        } catch (Exception e) {
            Log.e(TAG, "Error parsing chat message", e);
        }
    }

    public void disconnect() {
        if (stomp != null && isConnected) {
            stomp.disconnect();
        }
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
            compositeDisposable = new CompositeDisposable();
        }
        isConnected = false;
        connectionStatus.postValue(false);
    }
}