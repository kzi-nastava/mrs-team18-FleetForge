package com.ognjen.fleetforge.fragments.unregistered;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mapbox.common.MapboxOptions;
import com.mapbox.maps.MapView;
import com.ognjen.fleetforge.BuildConfig;
import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.utils.MapManager;

public class UnregisteredFragment extends Fragment {

    private MapView mapView;
    private MapManager mapManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Token MUST be set before MapView is inflated, otherwise it crashes.
        // This is a static property — setting it once is enough for the whole app.
        MapboxOptions.setAccessToken(BuildConfig.MAPBOX_API_KEY);

        return inflater.inflate(R.layout.fragment_unregistered, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = view.findViewById(R.id.map_view);

        // v11 uses subscribeMapLoaded instead of setOnMapLoadedListener.
        // Returns a Cancelable but we don't need to cancel it here.
        mapView.getMapboxMap().subscribeMapLoaded(mapLoaded -> {
            if (mapManager == null) {
                mapManager = new MapManager(mapView.getMapboxMap());
                mapManager.centerOnDefault();
            }
        });
    }

    // No lifecycle forwarding needed — v11 MapView handles it automatically
    // via its built-in MapboxLifecyclePlugin.

    @Override
    public void onDestroyView() {
        mapView = null;
        mapManager = null;
        super.onDestroyView();
    }
}