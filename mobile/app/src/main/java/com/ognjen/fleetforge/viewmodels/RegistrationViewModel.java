package com.ognjen.fleetforge.viewmodels;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ognjen.fleetforge.api.AuthService;
import com.ognjen.fleetforge.api.RetrofitClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationViewModel extends AndroidViewModel {
    private static final String TAG = "FFLOG";

    public String email, phone, password, firstName, lastName, address;
    public Uri profileImageUri;

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private final AuthService authService;

    public RegistrationViewModel(@NonNull Application application) {
        super(application);
        this.authService = RetrofitClient.getInstance().getAuthService();
    }

    public LiveData<String> getSuccessMessage() { return successMessage; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void registerUser() {
        Log.d(TAG, "RegistrationViewModel: Starting registerUser for " + email);
        isLoading.setValue(true);

        RequestBody emailPart = createPart(email);
        RequestBody passwordPart = createPart(password);
        RequestBody firstNamePart = createPart(firstName);
        RequestBody lastNamePart = createPart(lastName);
        RequestBody phonePart = createPart(phone);
        RequestBody addressPart = createPart(address);

        MultipartBody.Part imagePart = prepareImagePart();

        authService.registerUser(
                emailPart,
                passwordPart,
                firstNamePart,
                lastNamePart,
                phonePart,
                addressPart,
                imagePart
        ).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    successMessage.setValue("Registration request successfully submitted!");
                } else {
                    errorMessage.setValue("Registration failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    private RequestBody createPart(String value) {
        if (value == null) value = "";
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }

    private MultipartBody.Part prepareImagePart() {
        if (profileImageUri == null) {
            return null;
        }

        try {
            InputStream inputStream = getApplication().getContentResolver().openInputStream(profileImageUri);
            if (inputStream == null) return null;

            byte[] bytes = getBytes(inputStream);

            RequestBody requestFile = RequestBody.create(
                    MediaType.parse(getApplication().getContentResolver().getType(profileImageUri)),
                    bytes
            );

            return MultipartBody.Part.createFormData("profilePicture", "profile_image.jpg", requestFile);

        } catch (IOException e) {
            Log.e(TAG, "Error preparing image part: " + e.getMessage());
            return null;
        }
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
}