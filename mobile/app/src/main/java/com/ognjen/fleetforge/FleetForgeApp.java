package com.ognjen.fleetforge;

import android.app.Application;
import com.ognjen.fleetforge.auth.AuthManager;

public class FleetForgeApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize AuthManager
        AuthManager.getInstance(this);
    }
}