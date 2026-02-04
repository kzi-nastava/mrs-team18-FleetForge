package com.ognjen.fleetforge.auth;

import android.content.Context;
import android.content.SharedPreferences;
import com.ognjen.fleetforge.model.UserRole;

public class AuthManager {
    private static final String PREF_NAME = "FleetForgePrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ROLE = "userRole";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_AUTH_TOKEN = "authToken";

    private static AuthManager instance;
    private final SharedPreferences sharedPreferences;
    private final Context context;

    private AuthManager(Context context) {
        this.context = context.getApplicationContext();
        sharedPreferences = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized AuthManager getInstance(Context context) {
        if (instance == null) {
            instance = new AuthManager(context);
        }
        return instance;
    }

    public static AuthManager getInstance() {
        if (instance == null) {
            throw new RuntimeException("AuthManager must be initialized in Application class first!");
        }
        return instance;
    }

    public Context getContext() {
        return context;
    }

    public void login(UserRole role, String name, String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_ROLE, role.name());
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_EMAIL, email);
        editor.apply();
    }

    public void saveToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_AUTH_TOKEN, token);
        editor.apply();
    }

    public String getToken() {
        return sharedPreferences.getString(KEY_AUTH_TOKEN, null);
    }

    public void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public UserRole getCurrentRole() {
        if (!isLoggedIn()) {
            return UserRole.UNREGISTERED;
        }

        String roleString = sharedPreferences.getString(KEY_USER_ROLE, UserRole.UNREGISTERED.name());
        try {
            return UserRole.valueOf(roleString);
        } catch (IllegalArgumentException e) {
            return UserRole.UNREGISTERED;
        }
    }

    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, "Guest");
    }

    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, "");
    }
}