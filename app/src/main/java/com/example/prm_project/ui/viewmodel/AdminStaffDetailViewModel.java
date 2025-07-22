package com.example.prm_project.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.prm_project.data.model.AdminStaffDetailResponse;
import com.example.prm_project.data.remote.ManageStaffApi;
import com.example.prm_project.data.repository.AdminRepository;
import com.example.prm_project.utils.Constants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdminStaffDetailViewModel extends ViewModel {
    private final AdminRepository adminRepository;

    public AdminStaffDetailViewModel() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.ADMIN_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ManageStaffApi api = retrofit.create(ManageStaffApi.class);
        this.adminRepository = new AdminRepository(api);
    }

    public LiveData<AdminStaffDetailResponse> getStaffDetail(String token, int staffId) {
        return adminRepository.getStaffDetail(token, staffId);
    }
}