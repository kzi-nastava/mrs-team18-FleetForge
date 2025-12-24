package com.ognjen.fleetforge.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.model.Ride;
import java.util.List;


public class RideHistoryAdapter extends RecyclerView.Adapter<RideHistoryAdapter.RideViewHolder> {

    private List<Ride> rides;

    public RideHistoryAdapter(List<Ride> rides) {
        this.rides = rides;
    }

    @NonNull
    @Override
    public RideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ride_history, parent, false);
        return new RideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideViewHolder holder, int position) {
        Ride ride = rides.get(position);
        holder.bind(ride);
    }

    @Override
    public int getItemCount() {
        return rides.size();
    }

    static class RideViewHolder extends RecyclerView.ViewHolder {

        private TextView tvPassengerName, tvPassengerId, tvPickupAddress, tvDropoffAddress;
        private TextView tvRideDate, tvTotalCost, tvCancellationStatus;
        private ImageView ivPanicIcon;
        private ImageView[] stars = new ImageView[5];

        public RideViewHolder(@NonNull View itemView) {
            super(itemView);

            tvPassengerName = itemView.findViewById(R.id.tv_passenger_name);
            tvPassengerId = itemView.findViewById(R.id.tv_passenger_id);
            tvPickupAddress = itemView.findViewById(R.id.tv_pickup_address);
            tvDropoffAddress = itemView.findViewById(R.id.tv_dropoff_address);
            tvRideDate = itemView.findViewById(R.id.tv_ride_date);
            tvTotalCost = itemView.findViewById(R.id.tv_total_cost);
            tvCancellationStatus = itemView.findViewById(R.id.tv_cancellation_status);
            ivPanicIcon = itemView.findViewById(R.id.iv_panic_icon);

            stars[0] = itemView.findViewById(R.id.iv_star1);
            stars[1] = itemView.findViewById(R.id.iv_star2);
            stars[2] = itemView.findViewById(R.id.iv_star3);
            stars[3] = itemView.findViewById(R.id.iv_star4);
            stars[4] = itemView.findViewById(R.id.iv_star5);
        }

        public void bind(Ride ride) {
            tvPassengerName.setText(ride.getPassengerName());
            tvPassengerId.setText("#" + ride.getPassengerId());
            tvPickupAddress.setText(ride.getPickupAddress());
            tvDropoffAddress.setText(ride.getDropoffAddress());
            tvRideDate.setText(ride.getRideDate());
            tvTotalCost.setText(String.format("%.2f", ride.getTotalCost()));
            tvCancellationStatus.setText(ride.getCancellationStatus());

            // Set panic icon
            if (ride.isPanicActivated()) {
                ivPanicIcon.setImageResource(R.drawable.ic_panic_red);
            } else {
                ivPanicIcon.setImageResource(R.drawable.ic_panic_green);
            }

            setRating(ride.getRating());
        }

        private void setRating(float rating) {
            int fullStars = (int) rating;

            for (int i = 0; i < 5; i++) {
                if (i < fullStars) {
                    stars[i].setImageResource(R.drawable.ic_star_filled);
                } else {
                    stars[i].setImageResource(R.drawable.ic_star_empty);
                }
            }
        }
    }

    public void updateRides(List<Ride> newRides) {
        this.rides = newRides;
        notifyDataSetChanged();
    }
}