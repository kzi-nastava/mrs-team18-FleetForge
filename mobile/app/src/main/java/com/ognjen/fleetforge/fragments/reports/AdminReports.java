package com.ognjen.fleetforge.fragments.reports;

import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.dtos.reports.ReportsRideDTO;
import com.ognjen.fleetforge.dtos.reports.UserDataReportResponseDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminReports#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminReports extends Fragment {

    private UserReportsViewModel viewModel;
    private UserDataReportResponseDTO data;
    private LineChart rideChart;
    private LineChart distanceChart;
    private LineChart priceChart;
    private TextView totalRidesText;
    private TextView avgRidesText;
    private TextView totalDistanceText;
    private TextView avgDistanceText;
    private TextView totalPriceText;
    private TextView avgPriceText;

    private TextInputEditText emailInput;
    public static AdminReports newInstance(String param1, String param2) {
        return new AdminReports();
    }

    public AdminReports() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel= new ViewModelProvider(this).get(UserReportsViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_admin_reports, container, false);

        TextInputEditText fromDateInput = view.findViewById(R.id.fromDateInput);

        fromDateInput.setOnClickListener(v -> {
            MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker().setTitleText("Select date").build();
            picker.addOnPositiveButtonClickListener(selection -> {
                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(selection));
                fromDateInput.setText(date);
            });

            picker.show(getParentFragmentManager(), "FROM_DATE_PICKER");
        });
        TextInputEditText toDateInput=view.findViewById(R.id.toDateInput);

        toDateInput.setOnClickListener(v -> {
            MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker().setTitleText("Select date").build();
            picker.addOnPositiveButtonClickListener(selection -> {
                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(selection));
                toDateInput.setText(date);
            });

            picker.show(getParentFragmentManager(), "FROM_DATE_PICKER");
        });

        totalRidesText = view.findViewById(R.id.totalRidesText);
        avgRidesText = view.findViewById(R.id.avgRidesText);
        totalDistanceText = view.findViewById(R.id.totalDistanceText);
        avgDistanceText = view.findViewById(R.id.avgDistanceText);
        totalPriceText = view.findViewById(R.id.totalPriceText);
        avgPriceText = view.findViewById(R.id.avgPriceText);
        rideChart = view.findViewById(R.id.rideChart);
        distanceChart=view.findViewById(R.id.distanceChart);
        priceChart=view.findViewById(R.id.priceChart);
        emailInput=view.findViewById(R.id.emailInput);
        Button searchBtn= view.findViewById(R.id.seatchBtn);
        searchBtn.setOnClickListener(v -> {
            String fromDateStr = fromDateInput.getText().toString();
            String toDateStr = toDateInput.getText().toString();


            if (fromDateStr.isEmpty() || toDateStr.isEmpty()) {
                return;
            }
            LocalDate fromDate = LocalDate.parse(fromDateStr);
            LocalDate toDate = LocalDate.parse(toDateStr);
            if (fromDate.isAfter(toDate)) {
                Toast.makeText(getContext(), "From date cannot be later than To date", Toast.LENGTH_SHORT).show();
                return;
            }


            if(emailInput.getText().toString().equals("")){
                viewModel.getAdminAllUsersData(LocalDate.parse(fromDateStr), LocalDate.parse(toDateStr)).observe(getViewLifecycleOwner(),response->{
                    if(response!=null){
                        populateAllCharts(response);
                    }else {
                        Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                viewModel.getAdminUserData(LocalDate.parse(fromDateStr), LocalDate.parse(toDateStr),emailInput.getText().toString()).observe(getViewLifecycleOwner(),response->{
                    if(response!=null){
                        populateAllCharts(response);
                    }else {
                        Toast.makeText(getContext(), "User not found or error loading data", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });
        Button backBtn=view.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });
        return view;
    }
    private void populateAllCharts(UserDataReportResponseDTO response) {
        if (response == null || response.getDataByDay() == null) {
            Toast.makeText(getContext(), "No data available", Toast.LENGTH_SHORT).show();
            rideChart.clear();
            distanceChart.clear();
            priceChart.clear();
            rideChart.invalidate();
            distanceChart.invalidate();
            priceChart.invalidate();

            totalRidesText.setText("0");
            avgRidesText.setText("0.00");
            totalDistanceText.setText("0.00");
            avgDistanceText.setText("0.00");
            totalPriceText.setText("0.00");
            avgPriceText.setText("0.00");
            return;
        }
        List<Entry> rideEntries = new ArrayList<>();
        List<Entry> distanceEntries = new ArrayList<>();
        List<Entry> priceEntries = new ArrayList<>();
        List<String> dateLabels = new ArrayList<>();
        int totalRides = 0;
        float distance = 0;
        float price = 0;
        int index = 0;
        for (Map.Entry<String, List<ReportsRideDTO>> entry : response.getDataByDay().entrySet()) {
            String dateKey = entry.getKey();
            List<ReportsRideDTO> rides = entry.getValue();
            dateLabels.add(dateKey);
            rideEntries.add(new Entry(index, rides.size()));
            totalRides += rides.size();
            float totalDistance = 0;
            float totalPrice = 0;
            for (ReportsRideDTO ride : rides) {
                totalDistance += ride.getTotalDistance();
                totalPrice += ride.getTotalCost();
            }

            distanceEntries.add(new Entry(index, totalDistance));
            priceEntries.add(new Entry(index, totalPrice));
            distance += totalDistance;
            price += totalPrice;
            index++;
        }

        if (rideEntries.isEmpty()) {
            Toast.makeText(getContext(), "No rides found in this date range", Toast.LENGTH_SHORT).show();
            return;
        }
        int numberOfDays = dateLabels.size();
        float avgRides = (float) totalRides / numberOfDays;
        float avgDistance = distance / numberOfDays;
        float avgPrice = price / totalRides;
        totalRidesText.setText(String.valueOf(totalRides));
        avgRidesText.setText(String.format(Locale.getDefault(), "%.2f", avgRides));
        totalDistanceText.setText(String.format(Locale.getDefault(), "%.2f", distance));
        avgDistanceText.setText(String.format(Locale.getDefault(), "%.2f", avgDistance));
        totalPriceText.setText(String.format(Locale.getDefault(), "%.2f", price));
        avgPriceText.setText(String.format(Locale.getDefault(), "%.2f", avgPrice));
        setupChart(rideChart, rideEntries, dateLabels, "Rides per day", Color.BLUE);
        setupChart(distanceChart, distanceEntries, dateLabels, "Distance (km)", Color.GREEN);
        setupChart(priceChart, priceEntries, dateLabels, "Price (RSD)", Color.RED);
    }

    private void setupChart(LineChart chart, List<Entry> entries, List<String> labels, String dataSetLabel, int color) {
        chart.clear();

        LineDataSet dataSet = new LineDataSet(entries, dataSetLabel);
        dataSet.setColor(color);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setCircleColor(color);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1f);
        xAxis.setLabelRotationAngle(-45);

        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);

        chart.notifyDataSetChanged();
        chart.invalidate();
    }
}