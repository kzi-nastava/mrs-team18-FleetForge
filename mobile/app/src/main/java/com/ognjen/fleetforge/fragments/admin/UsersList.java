package com.ognjen.fleetforge.fragments.admin;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.adapters.UsersListAdatper;
import com.ognjen.fleetforge.dtos.user.UserInformationDTO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UsersList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UsersList extends Fragment {

    private UsersListViewModel viewModel;

    private UsersListAdatper adapter;

    private ArrayList<UserInformationDTO> users= new ArrayList<>();

    private ListView listView;

    private Set<Long> blockedUsersIds = new HashSet<>();

    private Button nextBtn;
    private Button prevBtn;
    private TextView pagination;

    private Button search;

    private TextView searchInput;


    public UsersList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UsersList.
     */
    // TODO: Rename and change types and number of parameters
    public static UsersList newInstance(String param1, String param2) {
        return new UsersList();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel=new ViewModelProvider(this).get(UsersListViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users_list, container, false);
        listView=view.findViewById(R.id.user_info_listView);

        adapter= new UsersListAdatper(getContext(),users);
        initUsers();
        listView.setAdapter(adapter);

        adapter.setOnActionListener(new UsersListAdatper.OnActionListener() {
            @Override
            public void onBlock(UserInformationDTO request, int position) {
                showBlockDialog(request, position);
            }
        });

        nextBtn= view.findViewById(R.id.next_button);
        prevBtn= view.findViewById(R.id.prev_button);
        pagination=view.findViewById(R.id.page_info);

        nextBtn.setOnClickListener(v -> loadPage(viewModel.getPage() + 1));
        prevBtn.setOnClickListener(v -> loadPage(viewModel.getPage() - 1));

        search=view.findViewById(R.id.search_button);
        searchInput=view.findViewById(R.id.search_input);
        search.setOnClickListener(v -> {
            viewModel.getAllUsers(0, viewModel.getSize(), searchInput.getText().toString())
                    .observe(getViewLifecycleOwner(), response -> {
                        if (response != null) {
                            viewModel.setPage(0);
                            viewModel.setPageCount(response.getTotalPages());
                            viewModel.setUsers(response.getContent());
                            updatePagination();
                        }
                    });
        });
        return view;
    }
    private void showBlockDialog(UserInformationDTO user, int position) {
        EditText input = new EditText(getContext());
        input.setHint("Enter reason");
        input.setPadding(48, 24, 48, 24);

        new AlertDialog.Builder(getContext())
                .setTitle("Block user")
                .setMessage("Block " + user.getEmail() + "?")
                .setView(input)
                .setPositiveButton("Block", (dialog, which) -> {
                    String reason = input.getText().toString().trim();
                    if (reason.isEmpty()) {
                        Toast.makeText(getContext(), "Reason cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    handleBlock(user, position, reason);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    private void loadPage(int page) {
        viewModel.getAllUsers(page, viewModel.getSize(), null)
                .observe(getViewLifecycleOwner(), response -> {
                    if (response != null) {
                        viewModel.setPage(page);
                        viewModel.setPageCount(response.getTotalPages());
                        viewModel.setUsers(response.getContent());
                        updatePagination();
                    }
                });
    }

    private void updatePagination() {
        int current = viewModel.getPage();
        int total = viewModel.getPageCount();
        pagination.setText((current + 1) + " / " + total);
        prevBtn.setEnabled(current > 0);
        nextBtn.setEnabled(current < total - 1);
    }
    private void handleBlock(UserInformationDTO request, int position,String reason){
        viewModel.blockUser(request.getId(), reason).observe(getViewLifecycleOwner(), response -> {
            if (response != null) {
                blockedUsersIds.add(request.getId());
                adapter.setBlockedUsersIds(blockedUsersIds);
                adapter.notifyDataSetChanged();
            }
        });
    }
    private void initUsers() {
        viewModel.getUsers().observe(getViewLifecycleOwner(), response -> {
            if (response != null) {
                users.clear();
                blockedUsersIds.clear();
                for (UserInformationDTO user : response) {
                    users.add(user);
                    if (user.isBlocked()) {
                        blockedUsersIds.add(user.getId());
                    }
                }
                adapter.setBlockedUsersIds(blockedUsersIds);
                adapter.notifyDataSetChanged();
            }
        });

        loadPage(0);
    }
}