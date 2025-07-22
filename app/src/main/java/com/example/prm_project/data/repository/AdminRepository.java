// file: data/repository/AdminRepository.java
 package com.example.prm_project.data.repository;

 import android.util.Log;

 import androidx.lifecycle.LiveData;
 import androidx.lifecycle.MutableLiveData;

 import com.example.prm_project.data.model.AdminStaffDetailResponse;
 import com.example.prm_project.data.model.AdminStaffListResponse;
 import com.example.prm_project.data.remote.ManageStaffApi;

 import retrofit2.Call;
 import retrofit2.Callback;
 import retrofit2.Response;

 public class AdminRepository {
     private final ManageStaffApi manageStaffApi;

     public AdminRepository(ManageStaffApi manageStaffApi) {
         this.manageStaffApi = manageStaffApi;
     }

     /**
      * Get staff list with optional filters
      * @param token
      * @return
      */
     public LiveData<AdminStaffListResponse> getStaffList(String token) {
         MutableLiveData<AdminStaffListResponse> data = new MutableLiveData<>();
         manageStaffApi.getStaffList(token,
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
                 ).enqueue(new Callback<AdminStaffListResponse>() {
             @Override
             public void onResponse(Call<AdminStaffListResponse> call, Response<AdminStaffListResponse> response) {
                 data.setValue(response.body());
             }
             @Override
             public void onFailure(Call<AdminStaffListResponse> call, Throwable t) {
                 data.setValue(null);
             }
         });
         return data;
     }

     /**
      * Get staff detail by ID
      * @param token
      * @param staffId
      * @return
      */
     public LiveData<AdminStaffDetailResponse> getStaffDetail(String token, int staffId) {
         MutableLiveData<AdminStaffDetailResponse> data = new MutableLiveData<>();
         manageStaffApi.getStaffDetail(token, staffId).enqueue(new Callback<AdminStaffDetailResponse>() {
             @Override
             public void onResponse(Call<AdminStaffDetailResponse> call, Response<AdminStaffDetailResponse> response) {
                 data.setValue(response.body());
             }
             @Override
             public void onFailure(Call<AdminStaffDetailResponse> call, Throwable t) {
                 data.setValue(null);
             }
         });
         return data;
     }
 }