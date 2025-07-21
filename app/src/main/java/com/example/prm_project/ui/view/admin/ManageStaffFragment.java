package com.example.prm_project.ui.view.admin;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.prm_project.R;
import com.example.prm_project.data.model.AdminStaffListResponse;
import com.example.prm_project.data.remote.ManageStaffApi;
import com.example.prm_project.utils.Constants;
import com.example.prm_project.utils.SessionManager;

import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ManageStaffFragment extends Fragment {
    private StaffAdapter staffAdapter;
    private SessionManager sessionManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_staff, container, false);
        RecyclerView rv = view.findViewById(R.id.rv_staff_list);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        staffAdapter = new StaffAdapter();
        rv.setAdapter(staffAdapter);

        fetchStaffList();
        return view;
    }

    private void fetchStaffList() {
        // Get token after login
        String token = sessionManager.getAccessToken();
        if (token == null) {
            staffAdapter.setStaffList(Collections.emptyList());
            return;
        }
        token = "Bearer " + token;

        // Create Retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.ADMIN_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create API service
        ManageStaffApi api = retrofit.create(ManageStaffApi.class);
        api.getStaffList(    token,         // Authorization
                null,          // SearchTerm
                null,          // EmployeeId
                null,          // IsAvailable
                null,          // Status
                null,          // IsDeleted
                null,          // HireDateFrom
                null,          // HireDateTo
                null,          // MinRating
                null,          // MaxRating
                null,          // Skills
                null,          // SortBy
                null,          // SortDescending
                1,             // PageNumber
                10,            // PageSize
                null           // Skip
                )
                .enqueue(new Callback<AdminStaffListResponse>() {
            @Override
            public void onResponse(Call<AdminStaffListResponse> call, Response<AdminStaffListResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    staffAdapter.setStaffList(response.body().data.data);
                } else {
                    staffAdapter.setStaffList(Collections.emptyList());
                }
            }
            @Override
            public void onFailure(Call<AdminStaffListResponse> call, Throwable t) {
                staffAdapter.setStaffList(Collections.emptyList());
            }
        });
    }
}