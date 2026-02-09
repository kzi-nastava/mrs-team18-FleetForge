package com.ognjen.fleetforge.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.dtos.passenger.FavoriteRouteGetResponseDTO;
import com.ognjen.fleetforge.model.WayPoint;

import java.util.ArrayList;
import java.util.List;

public class FavoriteRoutesListAdapter extends ArrayAdapter<FavoriteRouteGetResponseDTO> {
    private ArrayList<FavoriteRouteGetResponseDTO> routes;

    public FavoriteRoutesListAdapter(Context context, ArrayList<FavoriteRouteGetResponseDTO> routes){
        super(context, R.layout.favorite_route_card);
        this.routes=routes;
    }

    private FavoriteRoutesListAdapter.OnActionListener listener;
    public interface OnActionListener {
        void onOrder(FavoriteRouteGetResponseDTO request, int position);
        void onRemove(FavoriteRouteGetResponseDTO request, int position);
    }
    public void setOnActionListener(FavoriteRoutesListAdapter.OnActionListener listener) {
        this.listener = listener;
    }
    @Override
    public int getCount(){
        return routes.size();
    }

    @Nullable
    @Override
    public FavoriteRouteGetResponseDTO getItem(int position){
        return routes.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        FavoriteRouteGetResponseDTO favRoute= routes.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.favorite_route_card, parent, false);
        }
        TextView routeName= convertView.findViewById(R.id.favorite_route_name);
        TextView startLocation= convertView.findViewById(R.id.start_location);
        TextView endLocation= convertView.findViewById(R.id.end_location);
        LinearLayout wayPointContainer= convertView.findViewById(R.id.waypoint_container);
        Button btnOrder= convertView.findViewById(R.id.btnOrder);
        Button btnRemove= convertView.findViewById(R.id.btnRemove);


        btnOrder.setOnClickListener(v -> {
            if(listener!=null){
                listener.onOrder(favRoute,position);
            }
        });
        btnRemove.setOnClickListener(v -> {
            if(listener!=null){
                listener.onRemove(favRoute,position);
            }
        });

        if(favRoute!=null){
            routeName.setText(favRoute.getName());
            startLocation.setText(favRoute.getStartAddress());
            endLocation.setText(favRoute.getEndAddress());
            wayPointContainer.removeAllViews();

            List<WayPoint> locations = favRoute.getWaypoints();

            if(locations != null && locations.size() > 2) {
                for(int i = 0; i < locations.size(); i++) {
                    TextView waypointTextView = new TextView(getContext());
                    waypointTextView.setText(locations.get(i).getClass().getName());
                    waypointTextView.setTextColor(getContext().getResources().getColor(R.color.black));
                    waypointTextView.setTextSize(18);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.topMargin = (int) (4 * getContext().getResources().getDisplayMetrics().density);
                    waypointTextView.setLayoutParams(params);

                    wayPointContainer.addView(waypointTextView);
                }
            }else if(locations!= null && locations.size()==0){
                TextView waypointTextView = new TextView(getContext());
                waypointTextView.setText("No waypoints, direct ride.");
                waypointTextView.setTextSize(18);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.topMargin = (int) (4 * getContext().getResources().getDisplayMetrics().density);
                waypointTextView.setLayoutParams(params);

                wayPointContainer.addView(waypointTextView);
            }

        }
        return convertView;
    }
}
