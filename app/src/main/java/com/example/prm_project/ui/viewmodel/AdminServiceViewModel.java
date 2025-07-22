package com.example.prm_project.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.prm_project.data.model.AdminServiceListResponse;
import com.example.prm_project.data.model.AdminServiceCreateRequest;
import com.example.prm_project.data.model.AdminServiceCreateResponse;
import com.example.prm_project.data.model.AdminServiceUpdateRequest;
import com.example.prm_project.data.repository.AdminRepository;
import com.example.prm_project.data.remote.ManageStaffApi;
import com.example.prm_project.utils.Constants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * ViewModel cho quản lý services trong admin
 */
public class AdminServiceViewModel extends ViewModel {
    private final AdminRepository adminRepository;
    private MutableLiveData<AdminServiceListResponse> serviceListLiveData;
    private MutableLiveData<AdminServiceCreateResponse> addServiceLiveData = new MutableLiveData<>();
    private MutableLiveData<AdminServiceCreateResponse> serviceDetailLiveData = new MutableLiveData<>();
    private MutableLiveData<AdminServiceCreateResponse> updateServiceLiveData = new MutableLiveData<>();
    private MutableLiveData<AdminServiceCreateResponse> deleteServiceLiveData = new MutableLiveData<>();

    public AdminServiceViewModel() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.ADMIN_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ManageStaffApi api = retrofit.create(ManageStaffApi.class);
        this.adminRepository = new AdminRepository(api);
        serviceListLiveData = new MutableLiveData<>();
    }

    /**
     * Lấy danh sách services
     * @param token Bearer token
     * @param pageNumber Số trang (mặc định 1)
     * @param pageSize Số items mỗi trang (mặc định 10)
     */
    public void getServiceList(String token, Integer pageNumber, Integer pageSize) {
        // Set default values if null
        if (pageNumber == null) pageNumber = 1;
        if (pageSize == null) pageSize = Integer.MAX_VALUE;
        
        LiveData<AdminServiceListResponse> result = adminRepository.getServiceList(token, pageNumber, pageSize);
        result.observeForever(response -> {
            serviceListLiveData.setValue(response);
        });
    }

    /**
     * Lấy danh sách services với tham số mặc định
     * @param token Bearer token
     */
    public void getServiceList(String token) {
        getServiceList(token, 1, 10);
    }

    /**
     * Getter cho service list LiveData
     * @return LiveData với AdminServiceListResponse
     */
    public LiveData<AdminServiceListResponse> getServiceListLiveData() {
        return serviceListLiveData;
    }

    /**
     * Tạo mới service
     * @param token Bearer token
     * @param request Thông tin service
     * @return LiveData với AdminServiceCreateResponse
     */
    public LiveData<AdminServiceCreateResponse> addService(String token, AdminServiceCreateRequest request) {
        LiveData<AdminServiceCreateResponse> result = adminRepository.addService(token, request);
        result.observeForever(response -> {
            addServiceLiveData.setValue(response);
        });
        return addServiceLiveData;
    }

    /**
     * Lấy chi tiết service
     */
    public LiveData<AdminServiceCreateResponse> getServiceDetail(String token, int serviceId) {
        LiveData<AdminServiceCreateResponse> result = adminRepository.getServiceDetail(token, serviceId);
        result.observeForever(response -> {
            serviceDetailLiveData.setValue(response);
        });
        return serviceDetailLiveData;
    }

    /**
     * Cập nhật service
     */
    public LiveData<AdminServiceCreateResponse> updateService(String token, int serviceId, AdminServiceUpdateRequest request) {
        LiveData<AdminServiceCreateResponse> result = adminRepository.updateService(token, serviceId, request);
        result.observeForever(response -> {
            updateServiceLiveData.setValue(response);
        });
        return updateServiceLiveData;
    }

    /**
     * Xóa service
     */
    public LiveData<AdminServiceCreateResponse> deleteService(String token, int serviceId) {
        LiveData<AdminServiceCreateResponse> result = adminRepository.deleteService(token, serviceId);
        result.observeForever(response -> {
            deleteServiceLiveData.setValue(response);
        });
        return deleteServiceLiveData;
    }
}
