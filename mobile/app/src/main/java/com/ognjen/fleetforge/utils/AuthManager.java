package com.ognjen.fleetforge.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.ognjen.fleetforge.model.UserRole;

public class AuthManager {

    private static final String PREFS_NAME = "FleetForge";

    private static final String KEY_USER_ROLE = "user_role";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";

    private static AuthManager instance;

    private SharedPreferences prefs;

    private AuthManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static AuthManager getInstance(Context context) {
        if (instance == null) {
            synchronized (AuthManager.class) {
                if (instance == null) {
                    instance = new AuthManager(context);
                }
            }
        }
        return instance;
    }

    public void login(UserRole role, String name, String email) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_USER_ROLE, role.name());
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_EMAIL, email);
        editor.apply(); // Apply changes asynchronously
    }

    public void logout() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    public UserRole getCurrentRole() {
        String roleName = prefs.getString(KEY_USER_ROLE, UserRole.UNREGISTERED.name());
        try {
            return UserRole.valueOf(roleName);
        } catch (IllegalArgumentException e) {
            // If invalid role stored, return UNREGISTERED
            return UserRole.UNREGISTERED;
        }
    }

    public boolean isLoggedIn() {
        return getCurrentRole() != UserRole.UNREGISTERED;
    }

    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, "Guest User");
    }

    public String getUserEmail() {
        return prefs.getString(KEY_USER_EMAIL, "guest@example.com");
    }

    public void updateProfile(String name, String email) {
        if (isLoggedIn()) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_USER_NAME, name);
            editor.putString(KEY_USER_EMAIL, email);
            editor.apply();
        }
    }

    public boolean hasRole(UserRole role) {
        return getCurrentRole() == role;
    }
}