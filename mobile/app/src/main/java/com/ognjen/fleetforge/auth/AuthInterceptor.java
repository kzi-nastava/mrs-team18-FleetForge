package com.ognjen.fleetforge.auth;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.ognjen.fleetforge.activities.auth.LoginActivity;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private final AuthManager authManager;

    public AuthInterceptor(AuthManager authManager) {
        this.authManager = authManager;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request.Builder requestBuilder = chain.request().newBuilder();

        String token = authManager.getToken();
        if (token != null && !token.isEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer " + token);
        }

        Response response = chain.proceed(requestBuilder.build());

        if (response.code() == 401) {
            handleUnauthorized();
        }

        return response;
    }

    private void handleUnauthorized() {
        authManager.logout();

        Context context = authManager.getContext();

        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}