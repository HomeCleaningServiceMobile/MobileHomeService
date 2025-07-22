package com.example.prm_project.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.prm_project.data.model.AdminStaffListResponse;
import com.example.prm_project.data.remote.ManageStaffApi;
import com.example.prm_project.data.repository.AdminRepository;
import com.example.prm_project.utils.Constants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdminManageStaffViewModel extends ViewModel {
    private final AdminRepository adminRepository;

    public AdminManageStaffViewModel() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.ADMIN_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ManageStaffApi api = retrofit.create(ManageStaffApi.class);
        this.adminRepository = new AdminRepository(api);
    }

    public LiveData<AdminStaffListResponse> getStaffList(String token) {
        return adminRepository.getStaffList(token);
    }
}