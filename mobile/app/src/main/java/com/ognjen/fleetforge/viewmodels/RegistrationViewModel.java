package com.ognjen.fleetforge.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ognjen.fleetforge.api.AuthService;
import com.ognjen.fleetforge.api.RetrofitClient;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationViewModel extends ViewModel {
    private static final String TAG = "FFLOG";

    public String email, phone, password, firstName, lastName, address;

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private final AuthService authService;

    public RegistrationViewModel() {
        this.authService = RetrofitClient.getInstance().getLoginService();
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

        authService.registerUser(
                emailPart,
                passwordPart,
                firstNamePart,
                lastNamePart,
                phonePart,
                addressPart
        ).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    Log.d(TAG, "RegistrationViewModel: Success (200 OK)");
                    successMessage.setValue("Registration request successfully submitted!");
                } else {
                    Log.e(TAG, "RegistrationViewModel: API Error Code " + response.code());
                    errorMessage.setValue("Registration failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                isLoading.setValue(false);
                Log.e(TAG, "RegistrationViewModel: Network Failure: " + t.getMessage());
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    private RequestBody createPart(String value) {
        if (value == null) value = "";
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }
}