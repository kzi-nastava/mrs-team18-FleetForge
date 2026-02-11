package com.ognjen.fleetforge.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.MutableLiveData;

import com.ognjen.fleetforge.dtos.NotificationDTO;
import com.ognjen.fleetforge.utils.WebSocketManager;

public class WebSocketService  extends Service {
    private static final int FOREGROUND_ID = 1;
    private static final String CHANNEL_ID = "WS_Notifications";
    public static final String ACTION_NEW_NOTIFICATION = "com.team18.NEW_NOTIFICATION";

    private WebSocketManager webSocketManager;
    public static MutableLiveData<String> messageData = new MutableLiveData<>();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID, "WebSocket Service Channel",
                    NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }

        webSocketManager= new WebSocketManager();
    }

    @Override
    public void onDestroy() {
        webSocketManager.disconnect();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String token = intent.getStringExtra("TOKEN");

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("FleetForge je aktivan")
                .setContentText("Čekanje na nove notifikacije...")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .build();

        startForeground(FOREGROUND_ID, notification);

        if (token != null) {
            webSocketManager.connect(token);
            webSocketManager.subscribeToNotifications();
            webSocketManager.getData().observeForever(msg -> {
                messageData.postValue(msg.getMessage());
                showNotification(msg);
            });
        }

        return START_STICKY; //pokrenuce se ponovo ako se ubije
    }
    private void showNotification(NotificationDTO notification) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Notification notif = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("FleetForge")
                .setContentText(notification.getMessage())
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build();

        notificationManager.notify(Math.toIntExact(notification.getId())+1000, notif);
    }
}
