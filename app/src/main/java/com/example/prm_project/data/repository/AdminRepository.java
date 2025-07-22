// file: data/repository/AdminRepository.java
package com.example.prm_project.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.prm_project.data.model.AdminCreateStaffRequest;
import com.example.prm_project.data.model.AdminServiceCreateRequest;
import com.example.prm_project.data.model.AdminServiceCreateResponse;
import com.example.prm_project.data.model.AdminServiceListResponse;
import com.example.prm_project.data.model.AdminServiceUpdateRequest;
import com.example.prm_project.data.model.AdminStaffDetailResponse;
import com.example.prm_project.data.model.AdminStaffListResponse;
import com.example.prm_project.data.model.AdminStaffUpdateRequest;
import com.example.prm_project.data.model.AdminUpdateStaffResponse;
import com.example.prm_project.data.remote.ManageStaffApi;
import com.example.prm_project.data.remote.RetrofitClient;
import com.example.prm_project.data.remote.ServiceApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository cho các chức năng admin
 * Quản lý staff và services
 */
public class AdminRepository {
    private static final String TAG = "AdminRepository";
    private final ManageStaffApi manageStaffApi;
    private final ServiceApi serviceApi;

    public AdminRepository(ManageStaffApi manageStaffApi) {
        this.manageStaffApi = manageStaffApi;
        this.serviceApi = RetrofitClient.getInstance().create(ServiceApi.class);
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
                Integer.MAX_VALUE,            // PageSize
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

    /**
     * Tạo staff mới
     * @param token
     * @param request
     * @return
     */
    public LiveData<AdminUpdateStaffResponse> createStaff(String token, AdminCreateStaffRequest request) {
        Log.d(TAG, "createStaff called in AdminRepository");
        Log.d(TAG, "Token: " + token);
        Log.d(TAG, "Request: " + request.toString());
        
        MutableLiveData<AdminUpdateStaffResponse> result = new MutableLiveData<>();

        manageStaffApi.createStaff(token, request).enqueue(new Callback<AdminUpdateStaffResponse>() {
            @Override
            public void onResponse(Call<AdminUpdateStaffResponse> call, Response<AdminUpdateStaffResponse> response) {
                Log.d(TAG, "API Response received - Code: " + response.code());
                Log.d(TAG, "API Response successful: " + response.isSuccessful());
                
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Response body: " + response.body().toString());
                    result.setValue(response.body());
                } else {
                    Log.e(TAG, "API call failed - Response code: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e(TAG, "Error body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body", e);
                        }
                    }
                    AdminUpdateStaffResponse errorResponse = new AdminUpdateStaffResponse();
                    errorResponse.setSucceeded(false);
                    result.setValue(errorResponse);
                }
            }

            @Override
            public void onFailure(Call<AdminUpdateStaffResponse> call, Throwable t) {
                Log.e(TAG, "API call failed with exception", t);
                AdminUpdateStaffResponse failResponse = new AdminUpdateStaffResponse();
                failResponse.setSucceeded(false);
                result.setValue(failResponse);
            }
        });

        return result;
    }

    public LiveData<AdminUpdateStaffResponse> updateStaff(String token, int staffId, AdminStaffUpdateRequest request) {
        MutableLiveData<AdminUpdateStaffResponse> result = new MutableLiveData<>();

        manageStaffApi.updateStaff(token, staffId, request).enqueue(new Callback<AdminUpdateStaffResponse>() {
            @Override
            public void onResponse(Call<AdminUpdateStaffResponse> call, Response<AdminUpdateStaffResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(response.body());
                } else {
                    AdminUpdateStaffResponse errorResponse = new AdminUpdateStaffResponse();
                    errorResponse.setSucceeded(false);
                    result.setValue(errorResponse);
                }
            }

            @Override
            public void onFailure(Call<AdminUpdateStaffResponse> call, Throwable t) {
                AdminUpdateStaffResponse failResponse = new AdminUpdateStaffResponse();
                failResponse.setSucceeded(false);
                result.setValue(failResponse);
            }
        });

        return result;
    }

    public LiveData<AdminUpdateStaffResponse> deleteStaff(String token, int staffId) {
        MutableLiveData<AdminUpdateStaffResponse> result = new MutableLiveData<>();

        manageStaffApi.deleteStaff(token, staffId).enqueue(new Callback<AdminUpdateStaffResponse>() {
            @Override
            public void onResponse(Call<AdminUpdateStaffResponse> call, Response<AdminUpdateStaffResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(response.body());
                } else {
                    AdminUpdateStaffResponse errorResponse = new AdminUpdateStaffResponse();
                    errorResponse.setSucceeded(false);
                    result.setValue(errorResponse);
                }
            }

            @Override
            public void onFailure(Call<AdminUpdateStaffResponse> call, Throwable t) {
                AdminUpdateStaffResponse failResponse = new AdminUpdateStaffResponse();
                failResponse.setSucceeded(false);
                result.setValue(failResponse);
            }
        });

        return result;
    }

    /**
     * Lấy danh sách services cho admin
     * @param token Bearer token
     * @param pageNumber Số trang
     * @param pageSize Số items mỗi trang
     * @return MutableLiveData với AdminServiceListResponse
     */
    public MutableLiveData<AdminServiceListResponse> getServiceList(String token, Integer pageNumber, Integer pageSize) {
        MutableLiveData<AdminServiceListResponse> result = new MutableLiveData<>();
        
        Call<AdminServiceListResponse> call = serviceApi.getServiceList("Bearer " + token, pageNumber, pageSize);
        call.enqueue(new Callback<AdminServiceListResponse>() {
            @Override
            public void onResponse(Call<AdminServiceListResponse> call, Response<AdminServiceListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Service list loaded successfully");
                    result.setValue(response.body());
                } else {
                    Log.e(TAG, "Failed to load service list: " + response.code());
                    AdminServiceListResponse failResponse = new AdminServiceListResponse();
                    failResponse.setSucceeded(false);
                    result.setValue(failResponse);
                }
            }

            @Override
            public void onFailure(Call<AdminServiceListResponse> call, Throwable t) {
                Log.e(TAG, "Error loading service list: " + t.getMessage());
                AdminServiceListResponse failResponse = new AdminServiceListResponse();
                failResponse.setSucceeded(false);
                result.setValue(failResponse);
            }
        });

        return result;
    }

    /**
     * Tạo mới service (admin)
     * @param token Bearer token
     * @param request Thông tin service cần tạo
     * @return MutableLiveData với AdminServiceCreateResponse
     */
    public MutableLiveData<AdminServiceCreateResponse> addService(String token, AdminServiceCreateRequest request) {
        MutableLiveData<AdminServiceCreateResponse> result = new MutableLiveData<>();
        Call<AdminServiceCreateResponse> call = serviceApi.addService("Bearer " + token, request);
        call.enqueue(new Callback<AdminServiceCreateResponse>() {
            @Override
            public void onResponse(Call<AdminServiceCreateResponse> call, Response<AdminServiceCreateResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(response.body());
                } else {
                    result.setValue(null);
                }
            }
            @Override
            public void onFailure(Call<AdminServiceCreateResponse> call, Throwable t) {
                Log.e(TAG, "Add service failed", t);
                result.setValue(null);
            }
        });
        return result;
    }

    /**
     * Lấy chi tiết service theo id (dùng GET)
     */
    public MutableLiveData<AdminServiceCreateResponse> getServiceDetail(String token, int serviceId) {
        MutableLiveData<AdminServiceCreateResponse> result = new MutableLiveData<>();
        Call<AdminServiceCreateResponse> call = serviceApi.getServiceById(serviceId, "Bearer " + token);
        call.enqueue(new Callback<AdminServiceCreateResponse>() {
            @Override
            public void onResponse(Call<AdminServiceCreateResponse> call, Response<AdminServiceCreateResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(response.body());
                } else {
                    result.setValue(null);
                }
            }
            @Override
            public void onFailure(Call<AdminServiceCreateResponse> call, Throwable t) {
                Log.e(TAG, "Get service detail failed", t);
                result.setValue(null);
            }
        });
        return result;
    }

    /**
     * Cập nhật service
     */
    public MutableLiveData<AdminServiceCreateResponse> updateService(String token, int serviceId, AdminServiceUpdateRequest request) {
        MutableLiveData<AdminServiceCreateResponse> result = new MutableLiveData<>();
        Call<AdminServiceCreateResponse> call = serviceApi.updateService(serviceId, "Bearer " + token, request);
        call.enqueue(new Callback<AdminServiceCreateResponse>() {
            @Override
            public void onResponse(Call<AdminServiceCreateResponse> call, Response<AdminServiceCreateResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(response.body());
                } else {
                    result.setValue(null);
                }
            }
            @Override
            public void onFailure(Call<AdminServiceCreateResponse> call, Throwable t) {
                Log.e(TAG, "Update service failed", t);
                result.setValue(null);
            }
        });
        return result;
    }

    /**
     * Xóa service theo id
     */
    public MutableLiveData<AdminServiceCreateResponse> deleteService(String token, int serviceId) {
        Log.d(TAG, "Calling deleteService API with id: " + serviceId);
        MutableLiveData<AdminServiceCreateResponse> result = new MutableLiveData<>();
        Call<AdminServiceCreateResponse> call = serviceApi.deleteService(serviceId, "Bearer " + token);
        call.enqueue(new Callback<AdminServiceCreateResponse>() {
            @Override
            public void onResponse(Call<AdminServiceCreateResponse> call, Response<AdminServiceCreateResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(response.body());
                } else {
                    result.setValue(null);
                }
            }
            @Override
            public void onFailure(Call<AdminServiceCreateResponse> call, Throwable t) {
                Log.e(TAG, "Delete service failed", t);
                result.setValue(null);
            }
        });
        return result;
    }
}