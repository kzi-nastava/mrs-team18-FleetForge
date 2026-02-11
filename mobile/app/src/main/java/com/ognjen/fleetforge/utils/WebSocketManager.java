package com.ognjen.fleetforge.utils;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.ognjen.fleetforge.BuildConfig;
import com.ognjen.fleetforge.dtos.NotificationDTO;

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
    private StompClient stomp;
    private static final String serverUrl="ws://"+ BuildConfig.IP_ADDR +":8080/ws/websocket";

    private CompositeDisposable compositeDisposable;
    private MutableLiveData<NotificationDTO> data = new MutableLiveData<>();

    public MutableLiveData<NotificationDTO> getData() {
        return data;
    }

    Gson mGson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class,
                    (JsonDeserializer<LocalDateTime>) (json, type, ctx) ->
                            LocalDateTime.parse(json.getAsString()))
            .create();
    public WebSocketManager(){
        compositeDisposable=new CompositeDisposable();
        stomp= Stomp.over(Stomp.ConnectionProvider.OKHTTP, serverUrl);
    }

    public void connect(String jwtToken){
        List<StompHeader> headers= new ArrayList<>();

        headers.add(new StompHeader("Authorization","Bearer "+jwtToken));

        stomp.connect(headers);

        Disposable lifecycle= stomp.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            Log.d(TAG,"Stomp connection opened");
                            break;
                        case ERROR:
                            Log.e(TAG, "Stomp connection error", lifecycleEvent.getException());
                            break;
                        case CLOSED:
                            Log.d(TAG,"Stomp connection closed");
                            break;
                        case FAILED_SERVER_HEARTBEAT:
                            Log.d(TAG,"Stomp failed server heartbeat");
                            break;
                    }
                });

        compositeDisposable.add(lifecycle);
    }

    public void subscribeToNotifications(){
        String topicPath = "/user/queue/notifications";

        Disposable topic = stomp.topic(topicPath)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    Log.d(TAG, "Primljena poruka: " + topicMessage.getPayload());
                    handleMessage(topicMessage.getPayload());
                }, throwable -> {
                    Log.e(TAG, "Greška pri subscribovanju", throwable);
                });

        compositeDisposable.add(topic);
    }

    private void handleMessage(String payload){
        data.setValue(mGson.fromJson(payload, NotificationDTO.class));
    }

    public void disconnect(){
        if(stomp!=null){
            stomp.disconnect();
        }
        if(compositeDisposable!=null){
            compositeDisposable.dispose();
        }
    }
}
