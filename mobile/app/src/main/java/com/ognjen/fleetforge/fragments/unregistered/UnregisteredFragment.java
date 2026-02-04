package com.ognjen.fleetforge.fragments.unregistered;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.osmdroid.config.Configuration;
import org.osmdroid.views.MapView;

import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.model.VehicleLocation;
import com.ognjen.fleetforge.utils.MapManager;
import com.ognjen.fleetforge.utils.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UnregisteredFragment extends Fragment {
    private static final String TAG = "UnregisteredFragment";

    private MapView mapView;
    private MapManager mapManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());

        return inflater.inflate(R.layout.fragment_unregistered, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = view.findViewById(R.id.map_view);

        mapManager = new MapManager(mapView, requireContext());

        mapManager.centerOnDefault();

        loadVehicles();
    }
    private void loadVehicles() {
        Log.d(TAG, "Loading vehicles from backend...");

        RetrofitClient.getInstance()
                .getUnregisteredApiService()
                .getActiveVehicles()
                .enqueue(new Callback<List<VehicleLocation>>() {
                    @Override
                    public void onResponse(Call<List<VehicleLocation>> call,
                                           Response<List<VehicleLocation>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<VehicleLocation> vehicles = response.body();
                            Log.d(TAG, "Vehicles loaded successfuly"+ vehicles.size());

                            mapManager.displayVehicles(vehicles);

                        } else {
                            Log.e(TAG, "Failed to load vehicles. Response code: " + response.code());
                            showError("Failed to load vehicles");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<VehicleLocation>> call, Throwable t) {
                        Log.e(TAG, "Network error while loading vehicles", t);
                        showError("Network error: " + t.getMessage());
                    }
                });
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    public void onDestroyView() {
        if (mapManager != null) {
            mapManager.clearVehicleMarkers();
        }
        mapView = null;
        mapManager = null;

        super.onDestroyView();
    }
}