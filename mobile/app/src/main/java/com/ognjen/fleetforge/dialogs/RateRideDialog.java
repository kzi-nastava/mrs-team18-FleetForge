package com.ognjen.fleetforge.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.ognjen.fleetforge.R;

public class RateRideDialog extends Dialog {

    public interface OnRatingSubmitListener {
        void onSubmit(int driverRating, int vehicleRating, String comment);
        void onNotNow();
    }

    private final String routeInfo;
    private final OnRatingSubmitListener listener;

    private ImageView[] driverStars = new ImageView[5];
    private ImageView[] vehicleStars = new ImageView[5];
    private int driverRating = 0;
    private int vehicleRating = 0;
    private EditText commentEditText;

    public RateRideDialog(@NonNull Context context, String routeInfo, OnRatingSubmitListener listener) {
        super(context);
        this.routeInfo = routeInfo;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_rate_ride);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        TextView tvRouteInfo = findViewById(R.id.tv_route_info);
        tvRouteInfo.setText(routeInfo);

        driverStars[0] = findViewById(R.id.driver_star_1);
        driverStars[1] = findViewById(R.id.driver_star_2);
        driverStars[2] = findViewById(R.id.driver_star_3);
        driverStars[3] = findViewById(R.id.driver_star_4);
        driverStars[4] = findViewById(R.id.driver_star_5);

        vehicleStars[0] = findViewById(R.id.vehicle_star_1);
        vehicleStars[1] = findViewById(R.id.vehicle_star_2);
        vehicleStars[2] = findViewById(R.id.vehicle_star_3);
        vehicleStars[3] = findViewById(R.id.vehicle_star_4);
        vehicleStars[4] = findViewById(R.id.vehicle_star_5);

        commentEditText = findViewById(R.id.et_comment);
    }

    private void setupListeners() {
        for (int i = 0; i < driverStars.length; i++) {
            final int rating = i + 1;
            driverStars[i].setOnClickListener(v -> {
                driverRating = rating;
                updateDriverStars();
            });
        }

        for (int i = 0; i < vehicleStars.length; i++) {
            final int rating = i + 1;
            vehicleStars[i].setOnClickListener(v -> {
                vehicleRating = rating;
                updateVehicleStars();
            });
        }

        ImageButton btnClose = findViewById(R.id.btn_close);
        btnClose.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNotNow();
            }
            dismiss();
        });

        Button btnNotNow = findViewById(R.id.btn_not_now);
        btnNotNow.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNotNow();
            }
            dismiss();
        });

        Button btnSubmit = findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(v -> {
            if (validateRatings()) {
                String comment = commentEditText.getText().toString().trim();
                if (listener != null) {
                    listener.onSubmit(driverRating, vehicleRating, comment);
                }
                dismiss();
            }
        });
    }

    private boolean validateRatings() {
        if (driverRating == 0) {
            Toast.makeText(getContext(), "Please rate the driver", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (vehicleRating == 0) {
            Toast.makeText(getContext(), "Please rate the vehicle", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void updateDriverStars() {
        for (int i = 0; i < driverStars.length; i++) {
            if (i < driverRating) {
                driverStars[i].setImageResource(R.drawable.ic_star_filled);
            } else {
                driverStars[i].setImageResource(R.drawable.ic_star_empty);
            }
        }
    }

    private void updateVehicleStars() {
        for (int i = 0; i < vehicleStars.length; i++) {
            if (i < vehicleRating) {
                vehicleStars[i].setImageResource(R.drawable.ic_star_filled);
            } else {
                vehicleStars[i].setImageResource(R.drawable.ic_star_empty);
            }
        }
    }
}