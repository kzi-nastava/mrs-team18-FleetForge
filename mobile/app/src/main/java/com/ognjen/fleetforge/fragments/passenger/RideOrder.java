package com.ognjen.fleetforge.fragments.passenger;

import static androidx.appcompat.content.res.AppCompatResources.getDrawable;
import static androidx.core.util.TypedValueCompat.dpToPx;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.WindowDecorActionBar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.ognjen.fleetforge.BuildConfig;
import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.dtos.photon.PhotonResponse;
import com.ognjen.fleetforge.model.CalculatedRoute;
import com.ognjen.fleetforge.utils.MapManager;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.w3c.dom.Text;
import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RideOrder#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RideOrder extends Fragment {
    private static final String MAPBOX_API_KEY = BuildConfig.MAPBOX_API_KEY;
    private MapView mapView;
    private MapManager mapManager;

    private MaterialAutoCompleteTextView startLocation;
    private MaterialAutoCompleteTextView endLocation;
    private LinearLayout waypointsContainer;
    private Button addWaypointBtn;
    private TextInputEditText passengerNum;
    private LinearLayout passengersContainer;
    private Button addPassenger;
    private TextInputEditText dateTime;
    private MaterialCheckBox now;
    private MaterialAutoCompleteTextView vehicleType;
    private MaterialCheckBox babySeat;
    private MaterialCheckBox petFriendly;
    private Button orderBtn;
    private int waypointCounter = 0;
    private int passengerCounter=0;
    private LinearLayout bottomSheet;

    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private RideOrderViewModel viewModel;
    private String sessionToken;
    public RideOrder() {
        // Required empty public constructor
    }

    public static RideOrder newInstance(String param1, String param2) {
        return new RideOrder();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel= new ViewModelProvider(this).get(RideOrderViewModel.class);
        viewModel.init(MAPBOX_API_KEY);
        org.osmdroid.config.Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_ride_order, container, false);
        viewModel.getSuggestionsData().observe(getViewLifecycleOwner(), response -> {
            if (response != null && response.getFeatures() != null) {
                updateDropdown(response);
            }
        });
        viewModel.getRouteData().observe(getViewLifecycleOwner(), calculatedRoute -> {
            if (calculatedRoute != null) {
                List<org.osmdroid.util.GeoPoint> osmPoints = new ArrayList<>();
                for (com.ognjen.fleetforge.model.GeoPoint point : calculatedRoute.getCoordinates()) {
                    osmPoints.add(new org.osmdroid.util.GeoPoint(
                            point.getLatitude(),
                            point.getLongitude()
                    ));
                }
                mapManager.drawRoute(osmPoints, 0xFFFF9900);
            }
        });
        mapView=view.findViewById(R.id.map_view);
        mapManager = new MapManager(mapView, requireContext());
        mapManager.centerOnDefault();
        bottomSheet=view.findViewById(R.id.bottom_sheet);
        BottomSheetBehavior<LinearLayout> behavior= BottomSheetBehavior.from(bottomSheet);
        startLocation=view.findViewById(R.id.start_location);
        endLocation=view.findViewById(R.id.end_destination);
        waypointsContainer=view.findViewById(R.id.waypointsContainer);
        addWaypointBtn=view.findViewById(R.id.add_waypoint_btn);
        passengerNum=view.findViewById(R.id.passengerNum);
        passengersContainer=view.findViewById(R.id.passengersContainer);
        addPassenger=view.findViewById(R.id.add_passenger_btn);
        dateTime=view.findViewById(R.id.dateTime);
        now=view.findViewById(R.id.now);
        vehicleType=view.findViewById(R.id.vehicleTypeSpinner);
        ArrayAdapter<String> adapter=  new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_dropdown_item_1line,
                getResources().getStringArray(R.array.vehicle_types)
        );
        vehicleType.setAdapter(adapter);
        babySeat=view.findViewById(R.id.babySeatCB);
        petFriendly=view.findViewById(R.id.petFriendlyCB);
        orderBtn=view.findViewById(R.id.orderBtn);

        addWaypointBtn.setOnClickListener(v -> {
            addWayPointField();
        });

        addPassenger.setOnClickListener(v -> {
            addPassengerField();
        });
        now.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                dateTime.setEnabled(false);
                dateTime.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.text_primary));
            }else{
                dateTime.setEnabled(true);
                dateTime.setBackgroundColor(Color.WHITE);
            }
        });
        dateTime.setFocusable(false);
        dateTime.setClickable(true);
        dateTime.setOnClickListener(v -> showDateTimePicker());

        startLocation.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        mapView.setOnClickListener(v -> {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        });

        setupAutocomplete(startLocation);
        setupAutocomplete(endLocation);
        startLocation.setOnItemClickListener((parent, view1, position, id)->{
            PhotonResponse.Feature selected = (PhotonResponse.Feature) parent.getItemAtPosition(position);

            double lat = selected.getGeometry().getLat();
            double lon = selected.getGeometry().getLon();
            String name = selected.getProperties().getDisplayName();
            if (startLocation.getTag() instanceof Marker) {
                mapManager.removeMarker((Marker) startLocation.getTag());
            }
            Marker newMarker = mapManager.addMarker(lat, lon, name, R.drawable.ic_map_point);
            startLocation.setTag(newMarker);
            startLocation.setText(name, false);
            mapView.getController().animateTo(new org.osmdroid.util.GeoPoint(lat, lon));
            try {
                drawRoute();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        endLocation.setOnItemClickListener((parent, view1, position, id)->{
            PhotonResponse.Feature selected = (PhotonResponse.Feature) parent.getItemAtPosition(position);

            double lat = selected.getGeometry().getLat();
            double lon = selected.getGeometry().getLon();
            String name = selected.getProperties().getDisplayName();
            if (endLocation.getTag() instanceof Marker) {
                mapManager.removeMarker((Marker) endLocation.getTag());
            }
            Marker newMarker = mapManager.addMarker(lat, lon, name, R.drawable.ic_map_point);
            endLocation.setTag(newMarker);
            endLocation.setText(name, false);
            mapView.getController().animateTo(new org.osmdroid.util.GeoPoint(lat, lon));
            try {
                drawRoute();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return view;
    }
    private void setupAutocomplete(MaterialAutoCompleteTextView field) {
        field.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                searchHandler.removeCallbacks(searchRunnable);
                if (s.length() > 2) {
                    searchRunnable = () -> viewModel.fetchSuggestions(s.toString());
                    searchHandler.postDelayed(searchRunnable, 500);
                }else if(s.length()==0){
                    if(field.getTag()!=null){
                        mapManager.removeMarker((Marker)field.getTag());
                        field.setTag(null);
                        mapManager.clearRoute();
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }
    private void addWayPointField(){
        TextInputLayout fieldLayout= new TextInputLayout(getContext(),null,com.google.android.material.R.attr.textInputOutlinedStyle);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        layoutParams.topMargin = (int) dpToPx(8,metrics);
        fieldLayout.setLayoutParams(layoutParams);
        fieldLayout.setHint("Waypoint " + waypointCounter);
        fieldLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
        fieldLayout.setEndIconMode(TextInputLayout.END_ICON_CUSTOM);
        fieldLayout.setEndIconDrawable(ContextCompat.getDrawable(getContext(), R.drawable.minus));
        fieldLayout.setId(View.generateViewId());

        MaterialAutoCompleteTextView editText = new MaterialAutoCompleteTextView(fieldLayout.getContext());
        LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        editText.setLayoutParams(editTextParams);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setId(View.generateViewId());
        setupAutocomplete(editText);
        editText.setOnItemClickListener((parent, view1, position, id) -> {
            PhotonResponse.Feature selected = (PhotonResponse.Feature) parent.getItemAtPosition(position);
            String displayName = selected.getProperties().getDisplayName();
            editText.setText(displayName, false);
            double lat = selected.getGeometry().getLat();
            double lon = selected.getGeometry().getLon();
            String name = selected.getProperties().getDisplayName();
            if (editText.getTag() instanceof org.osmdroid.views.overlay.Marker) {
                org.osmdroid.views.overlay.Marker oldMarker = (org.osmdroid.views.overlay.Marker) editText.getTag();
                mapView.getOverlays().remove(oldMarker);
            }

            org.osmdroid.views.overlay.Marker newMarker = mapManager.addMarker(lat, lon, name, R.drawable.ic_map_point);

            editText.setTag(newMarker);

            editText.setText(name, false);
            mapView.getController().animateTo(new org.osmdroid.util.GeoPoint(lat, lon));
            try {
                drawRoute();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        fieldLayout.addView(editText);

        fieldLayout.setEndIconOnClickListener(v -> {
            mapManager.removeMarker((Marker) editText.getTag());
            mapManager.clearRoute();
            fieldLayout.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .withEndAction(() -> {
                        waypointsContainer.removeView(fieldLayout);
                        updateWaypointHints();
                        try {
                            drawRoute();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .start();
        });
        int addButtonIndex = waypointsContainer.indexOfChild(addWaypointBtn);
        waypointsContainer.addView(fieldLayout, addButtonIndex);
        updateWaypointHints();
    }
    private void updateWaypointHints() {
        int counter = 1;
        for (int i = 0; i < waypointsContainer.getChildCount(); i++) {
            View view = waypointsContainer.getChildAt(i);
            if (view instanceof TextInputLayout && view.getId() != R.id.add_waypoint_btn) {
                TextInputLayout til = (TextInputLayout) view;
                til.setHint("Waypoint " + counter);
                counter++;
            }
        }
    }

    private void addPassengerField(){
        TextInputLayout fieldLayout= new TextInputLayout(getContext(),null,com.google.android.material.R.attr.textInputOutlinedStyle);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        layoutParams.topMargin = (int) dpToPx(8,metrics);
        fieldLayout.setLayoutParams(layoutParams);
        fieldLayout.setHint("Passenger " + passengerCounter);
        fieldLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
        fieldLayout.setEndIconMode(TextInputLayout.END_ICON_CUSTOM);
        fieldLayout.setEndIconDrawable(ContextCompat.getDrawable(getContext(), R.drawable.minus));
        fieldLayout.setId(View.generateViewId());

        TextInputEditText editText = new TextInputEditText(fieldLayout.getContext());
        LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        editText.setLayoutParams(editTextParams);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setId(View.generateViewId());

        fieldLayout.addView(editText);

        fieldLayout.setEndIconOnClickListener(v -> {
            fieldLayout.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .withEndAction(() -> {
                        passengersContainer.removeView(fieldLayout);
                        updatePassengerHints();
                    })
                    .start();
        });
        int addButtonIndex = passengersContainer.indexOfChild(addPassenger);
        passengersContainer.addView(fieldLayout, addButtonIndex);
        updatePassengerHints();
    }

    private void updatePassengerHints() {
        int counter = 1;
        for (int i = 0; i < passengersContainer.getChildCount(); i++) {
            View view = passengersContainer.getChildAt(i);
            if (view instanceof TextInputLayout && view.getId() != R.id.add_passenger_btn) {
                TextInputLayout til = (TextInputLayout) view;
                til.setHint("Passenger " + counter);
                counter++;
            }
        }
    }

    private void showDateTimePicker() {

        Calendar now = Calendar.getInstance();

        CalendarConstraints constraints = new CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now())
                .build();

        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(constraints)
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(now.get(Calendar.HOUR_OF_DAY))
                    .setMinute(now.get(Calendar.MINUTE))
                    .setTitleText("Select time")
                    .build();

            timePicker.addOnPositiveButtonClickListener(v -> {
                Calendar selectedDateTime = Calendar.getInstance();
                selectedDateTime.setTimeInMillis(selection);
                selectedDateTime.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                selectedDateTime.set(Calendar.MINUTE, timePicker.getMinute());

                if (selectedDateTime.getTimeInMillis() < System.currentTimeMillis()) {
                    Toast.makeText(getContext(), "Selected time is in the past", Toast.LENGTH_SHORT).show();
                    return;
                }

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                dateTime.setText(sdf.format(selectedDateTime.getTime()));
            });

            timePicker.show(getChildFragmentManager(), "TIME_PICKER");
        });

        datePicker.show(getChildFragmentManager(), "DATE_PICKER");
    }

    private void updateDropdown(PhotonResponse response) {
        View focusedView = getActivity().getCurrentFocus();
        if (!(focusedView instanceof MaterialAutoCompleteTextView)) return;

        MaterialAutoCompleteTextView field = (MaterialAutoCompleteTextView) focusedView;

        ArrayAdapter<PhotonResponse.Feature> adapter = new ArrayAdapter<PhotonResponse.Feature>(
                getContext(), android.R.layout.simple_dropdown_item_1line, response.getFeatures()) {
            @Override
            public View getView(int pos, View convert, ViewGroup parent) {
                TextView tv = (TextView) super.getView(pos, convert, parent);
                tv.setText(getItem(pos).getProperties().getDisplayName());
                return tv;
            }

        };

        field.setAdapter(adapter);
        if(!response.getFeatures().isEmpty()) {
            field.showDropDown();
        }
    }
    private void drawRoute() throws IOException {
        if(startLocation.getTag()!=null&&endLocation.getTag()!=null){
            List<com.ognjen.fleetforge.model.GeoPoint> addresses= new ArrayList<>();
            Marker startMarker= (Marker)startLocation.getTag();
            com.ognjen.fleetforge.model.GeoPoint startPoint= new com.ognjen.fleetforge.model.GeoPoint(startMarker.getPosition().getLatitude()
            ,startMarker.getPosition().getLongitude());
            addresses.add(startPoint);
            for (int i = 0; i < waypointsContainer.getChildCount(); i++) {
                View view = waypointsContainer.getChildAt(i);
                if (view instanceof TextInputLayout) {
                    EditText et = ((TextInputLayout) view).getEditText();
                    if (et != null && et.getTag() instanceof Marker) {
                        Marker waypointMarker= (Marker) et.getTag();
                        com.ognjen.fleetforge.model.GeoPoint waypoint= new com.ognjen.fleetforge.model.GeoPoint(waypointMarker.getPosition().getLatitude()
                                ,waypointMarker.getPosition().getLongitude());
                        addresses.add(waypoint);
                    }
                }
            }
            Marker endMarker= (Marker)endLocation.getTag();
            com.ognjen.fleetforge.model.GeoPoint endpoint= new com.ognjen.fleetforge.model.GeoPoint(endMarker.getPosition().getLatitude()
                    ,endMarker.getPosition().getLongitude());
            addresses.add(endpoint);
            viewModel.drawRoute(addresses);
        }
    }
}