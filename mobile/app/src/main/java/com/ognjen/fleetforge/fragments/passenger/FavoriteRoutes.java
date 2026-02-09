package com.ognjen.fleetforge.fragments.passenger;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.adapters.FavoriteRoutesListAdapter;
import com.ognjen.fleetforge.dtos.passenger.FavoriteRouteGetResponseDTO;
import com.ognjen.fleetforge.model.FavoriteRoute;
import com.ognjen.fleetforge.model.Ride;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavoriteRoutes#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoriteRoutes extends Fragment {

    private FavoriteRoutesListAdapter adapter;
    private ArrayList<FavoriteRouteGetResponseDTO> favRoutes= new ArrayList<>();
    private ListView favRoutesList;

    private FavoriteRoutesViewModel viewModel;
    public FavoriteRoutes() {
        // Required empty public constructor
    }

    public static FavoriteRoutes newInstance(String param1, String param2) {
        FavoriteRoutes fragment = new FavoriteRoutes();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel= new ViewModelProvider(this).get(FavoriteRoutesViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_favorite_routes, container, false);

        initFavRoutes();
        adapter= new FavoriteRoutesListAdapter(getActivity(),favRoutes);

        favRoutesList=view.findViewById(R.id.favorite_routes_list);
        favRoutesList.setAdapter(adapter);

        adapter.setOnActionListener(new FavoriteRoutesListAdapter.OnActionListener() {
            @Override
            public void onOrder(FavoriteRouteGetResponseDTO request, int position) {
                handleOnOrder(request);
            }

            @Override
            public void onRemove(FavoriteRouteGetResponseDTO request, int position) {
                handleOnRemove(request,position);
            }
        });
        return view;
    }
    private void handleOnRemove(FavoriteRouteGetResponseDTO request, int position){
        favRoutes.remove(position);
        adapter.notifyDataSetChanged();
        viewModel.deleteFavorite(request.getId()).observe(getViewLifecycleOwner(), response->{
            if(response.booleanValue()){
                Toast.makeText(getContext(), "Route removed from favorites", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getContext(), "Error, route was not deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void handleOnOrder(FavoriteRouteGetResponseDTO request){
        Bundle bundle = new Bundle();
        bundle.putString("start", request.getStartAddress());
        bundle.putString("end", request.getEndAddress());
        if(request.getWaypoints() != null && !request.getWaypoints().isEmpty()) {
            bundle.putSerializable("waypoints", new ArrayList<>(request.getWaypoints()));
        }

        RideOrder homePage = new RideOrder();
        homePage.setArguments(bundle);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, homePage)
                .commit();
    }
    private void initFavRoutes(){
        viewModel.getFavorites().observe(getViewLifecycleOwner(), response->{
            if(response!=null){
                favRoutes.clear();
                for(FavoriteRouteGetResponseDTO favRoute: response){
                    favRoutes.add(favRoute);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
}