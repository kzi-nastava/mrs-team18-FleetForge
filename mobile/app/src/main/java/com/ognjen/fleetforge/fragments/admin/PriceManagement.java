package com.ognjen.fleetforge.fragments.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.dtos.price.PriceConfigurationDTO;
import com.ognjen.fleetforge.enums.VehicleType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PriceManagement extends Fragment {

    private PriceManagementViewModel viewModel;
    private ProgressBar progressBar;

    private TextInputEditText etStandardBasePrice;
    private TextInputEditText etStandardPricePerKm;
    private MaterialButton btnStandardSave;

    private TextInputEditText etLuxuryBasePrice;
    private TextInputEditText etLuxuryPricePerKm;
    private MaterialButton btnLuxurySave;

    private TextInputEditText etVanBasePrice;
    private TextInputEditText etVanPricePerKm;
    private MaterialButton btnVanSave;

    private Map<VehicleType, PriceConfigurationDTO> originalConfigs = new HashMap<>();

    public PriceManagement() {}

    public static PriceManagement newInstance() {
        return new PriceManagement();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(PriceManagementViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_price_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupClickListeners();
        loadPriceConfigurations();
    }

    private void initializeViews(View view) {
        progressBar = view.findViewById(R.id.progressBar);

        etStandardBasePrice = view.findViewById(R.id.etStandardBasePrice);
        etStandardPricePerKm = view.findViewById(R.id.etStandardPricePerKm);
        btnStandardSave = view.findViewById(R.id.btnStandardSave);


        etLuxuryBasePrice = view.findViewById(R.id.etLuxuryBasePrice);
        etLuxuryPricePerKm = view.findViewById(R.id.etLuxuryPricePerKm);
        btnLuxurySave = view.findViewById(R.id.btnLuxurySave);

        etVanBasePrice = view.findViewById(R.id.etVanBasePrice);
        etVanPricePerKm = view.findViewById(R.id.etVanPricePerKm);
        btnVanSave = view.findViewById(R.id.btnVanSave);
    }

    private void setupClickListeners() {
        btnStandardSave.setOnClickListener(v -> savePriceConfiguration(VehicleType.STANDARD,
                etStandardBasePrice, etStandardPricePerKm));

        btnLuxurySave.setOnClickListener(v -> savePriceConfiguration(VehicleType.LUXURY,
                etLuxuryBasePrice, etLuxuryPricePerKm));

        btnVanSave.setOnClickListener(v -> savePriceConfiguration(VehicleType.VAN,
                etVanBasePrice, etVanPricePerKm));
    }

    private void loadPriceConfigurations() {
        progressBar.setVisibility(View.VISIBLE);

        viewModel.getAllPriceConfigurations().observe(getViewLifecycleOwner(), configurations -> {
            progressBar.setVisibility(View.GONE);

            if (configurations != null && !configurations.isEmpty()) {
                for (PriceConfigurationDTO config : configurations) {
                    originalConfigs.put(config.getVehicleType(), config);
                    populateFields(config);
                }
            } else {
                Toast.makeText(getContext(), "Failed to load price configurations", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateFields(PriceConfigurationDTO config) {
        switch (config.getVehicleType()) {
            case STANDARD:
                etStandardBasePrice.setText(String.valueOf(config.getBasePrice()));
                etStandardPricePerKm.setText(String.valueOf(config.getPricePerKm()));
                break;
            case LUXURY:
                etLuxuryBasePrice.setText(String.valueOf(config.getBasePrice()));
                etLuxuryPricePerKm.setText(String.valueOf(config.getPricePerKm()));
                break;
            case VAN:
                etVanBasePrice.setText(String.valueOf(config.getBasePrice()));
                etVanPricePerKm.setText(String.valueOf(config.getPricePerKm()));
                break;
        }
    }

    private void savePriceConfiguration(VehicleType vehicleType,
                                       TextInputEditText basePriceField,
                                       TextInputEditText pricePerKmField) {
        String basePriceStr = basePriceField.getText().toString().trim();
        String pricePerKmStr = pricePerKmField.getText().toString().trim();

        if (basePriceStr.isEmpty() || pricePerKmStr.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double basePrice = Double.parseDouble(basePriceStr);
            double pricePerKm = Double.parseDouble(pricePerKmStr);

            if (basePrice <= 0 || pricePerKm <= 0) {
                Toast.makeText(getContext(), "Prices must be positive numbers", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            viewModel.updatePriceConfiguration(vehicleType, basePrice, pricePerKm)
                    .observe(getViewLifecycleOwner(), updatedConfig -> {
                        progressBar.setVisibility(View.GONE);

                        if (updatedConfig != null) {
                            originalConfigs.put(vehicleType, updatedConfig);
                            Toast.makeText(getContext(),
                                    vehicleType.name() + " pricing updated successfully!",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(),
                                    "Failed to update pricing. Please try again.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Please enter valid numbers", Toast.LENGTH_SHORT).show();
        }
    }

}

