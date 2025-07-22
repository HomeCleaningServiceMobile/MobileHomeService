package com.example.prm_project.data.remote;

import com.example.prm_project.data.model.AdminServiceListResponse;
import com.example.prm_project.data.model.AdminServiceCreateRequest;
import com.example.prm_project.data.model.AdminServiceCreateResponse;
import com.example.prm_project.data.model.AdminServiceUpdateRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * API interface cho quản lý services
 */
public interface ServiceApi {
    
    /**
     * Lấy danh sách services với phân trang
     * @param authorization Bearer token
     * @param pageNumber Số trang (mặc định 1)
     * @param pageSize Số items mỗi trang (mặc định 10)
     * @return Call với AdminServiceListResponse
     */
    @GET("Services")
    Call<AdminServiceListResponse> getServiceList(
            @Header("Authorization") String authorization,
            @Query("pageNumber") Integer pageNumber,
            @Query("pageSize") Integer pageSize
    );

    /**
     * Tạo mới service (admin)
     * @param authorization Bearer token
     * @param request Thông tin service cần tạo
     * @return Call với response
     */
    @POST("Services")
    Call<AdminServiceCreateResponse> addService(
            @Header("Authorization") String authorization,
            @Body AdminServiceCreateRequest request
    );

    /**
     * Cập nhật service (admin)
     * @param id id của service
     * @param authorization Bearer token
     * @param request Thông tin service cần cập nhật
     * @return Call với response
     */
    @PUT("Services/{id}")
    Call<AdminServiceCreateResponse> updateService(
            @Path("id") int id,
            @Header("Authorization") String authorization,
            @Body AdminServiceUpdateRequest request
    );

    /**
     * Lấy chi tiết service theo id
     */
    @GET("Services/{id}")
    Call<AdminServiceCreateResponse> getServiceById(
            @Path("id") int id,
            @Header("Authorization") String authorization
    );

    /**
     * Xóa service theo id
     */
    @DELETE("Services/{id}")
    Call<AdminServiceCreateResponse> deleteService(
            @Path("id") int id,
            @Header("Authorization") String authorization
    );
}
