package com.example.prm_project.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.prm_project.data.model.AdminCreateStaffRequest;
import com.example.prm_project.data.model.AdminStaffDetailResponse;
import com.example.prm_project.data.model.AdminStaffListResponse;
import com.example.prm_project.data.model.AdminStaffUpdateRequest;
import com.example.prm_project.data.model.AdminUpdateStaffResponse;
import com.example.prm_project.data.remote.ManageStaffApi;
import com.example.prm_project.data.repository.AdminRepository;
import com.example.prm_project.utils.Constants;
import android.util.Log;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * ViewModel tổng hợp cho tất cả các thao tác quản lý nhân viên
 * Bao gồm: xem danh sách, xem chi tiết, tạo mới, cập nhật và xóa nhân viên
 */
public class AdminManageStaffViewModel extends ViewModel {
    private final AdminRepository adminRepository;

    // LiveData cho các response
    private final MutableLiveData<AdminUpdateStaffResponse> updateResponse = new MutableLiveData<>();
    private final MutableLiveData<AdminStaffDetailResponse> staffDetailResponse = new MutableLiveData<>();
    private final MutableLiveData<AdminStaffListResponse> staffListResponse = new MutableLiveData<>();
    private final MutableLiveData<AdminUpdateStaffResponse> deleteResponse = new MutableLiveData<>();
    private final MutableLiveData<AdminUpdateStaffResponse> createResponse = new MutableLiveData<>();

    public AdminManageStaffViewModel() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.ADMIN_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ManageStaffApi api = retrofit.create(ManageStaffApi.class);
        this.adminRepository = new AdminRepository(api);
    }

    // Getter methods cho các LiveData
    public LiveData<AdminUpdateStaffResponse> getUpdateResponse() {
        return updateResponse;
    }

    public LiveData<AdminStaffDetailResponse> getStaffDetailResponse() {
        return staffDetailResponse;
    }

    public LiveData<AdminStaffListResponse> getStaffListResponse() {
        return staffListResponse;
    }

    public LiveData<AdminUpdateStaffResponse> getDeleteResponse() {
        return deleteResponse;
    }

    public LiveData<AdminUpdateStaffResponse> getCreateResponse() {
        return createResponse;
    }

    /**
     * Lấy danh sách tất cả nhân viên
     * @param token JWT token để xác thực
     * @return LiveData chứa danh sách nhân viên
     */
    public LiveData<AdminStaffListResponse> getStaffList(String token) {
        return adminRepository.getStaffList(token);
    }

    /**
     * Lấy thông tin chi tiết của một nhân viên
     * @param token JWT token để xác thực
     * @param staffId ID của nhân viên cần xem chi tiết
     * @return LiveData chứa thông tin chi tiết nhân viên
     */
    public LiveData<AdminStaffDetailResponse> getStaffDetail(String token, int staffId) {
        return adminRepository.getStaffDetail(token, staffId);
    }

    /**
     * Tạo nhân viên mới
     * @param token JWT token để xác thực
     * @param request Dữ liệu nhân viên mới
     * @return LiveData chứa kết quả tạo nhân viên
     */
    public LiveData<AdminUpdateStaffResponse> createStaff(String token, AdminCreateStaffRequest request) {
        Log.d("AdminManageStaffViewModel", "createStaff called");
        return adminRepository.createStaff(token, request);
    }

    /**
     * Cập nhật thông tin nhân viên
     * @param token JWT token để xác thực
     * @param staffId ID của nhân viên cần cập nhật
     * @param request Dữ liệu cập nhật nhân viên
     * @return LiveData chứa kết quả cập nhật
     */
    public LiveData<AdminUpdateStaffResponse> updateStaff(String token, int staffId, AdminStaffUpdateRequest request) {
        return adminRepository.updateStaff(token, staffId, request);
    }

    /**
     * Xóa nhân viên
     * @param token JWT token để xác thực
     * @param staffId ID của nhân viên cần xóa
     * @return LiveData chứa kết quả xóa
     */
    public LiveData<AdminUpdateStaffResponse> deleteStaff(String token, int staffId) {
        return adminRepository.deleteStaff(token, staffId);
    }
}