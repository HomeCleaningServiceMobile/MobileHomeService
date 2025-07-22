package com.example.prm_project.data.remote;

import com.example.prm_project.data.model.AdminCreateStaffRequest;
import com.example.prm_project.data.model.AdminStaffDetailResponse;
import com.example.prm_project.data.model.AdminStaffListResponse;
import com.example.prm_project.data.model.AdminStaffUpdateRequest;
import com.example.prm_project.data.model.AdminUpdateStaffResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ManageStaffApi {

    @GET("StaffManagement")
    Call<AdminStaffListResponse> getStaffList(
        @Header("Authorization") String bearerToken,
        @Query("SearchTerm") String searchTerm,
        @Query("EmployeeId") String employeeId,
        @Query("IsAvailable") Boolean isAvailable,
        @Query("Status") Integer status,
        @Query("IsDeleted") Boolean isDeleted,
        @Query("HireDateFrom") String hireDateFrom,
        @Query("HireDateTo") String hireDateTo,
        @Query("MinRating") Double minRating,
        @Query("MaxRating") Double maxRating,
        @Query("Skills") String skills,
        @Query("SortBy") String sortBy,
        @Query("SortDescending") Boolean sortDescending,
        @Query("PageNumber") Integer pageNumber,
        @Query("PageSize") Integer pageSize,
        @Query("Skip") Integer skip
    );

    @GET("StaffManagement/{id}")
    Call<AdminStaffDetailResponse> getStaffDetail(
            @Header("Authorization") String token,
            @Path("id") int staffId
    );

    @POST("StaffManagement")
    Call<AdminUpdateStaffResponse> createStaff(
            @Header("Authorization") String token,
            @Body AdminCreateStaffRequest request
    );

    @PUT("StaffManagement/{id}")
    Call<AdminUpdateStaffResponse> updateStaff(
            @Header("Authorization") String token,
            @Path("id") int staffId,
            @Body AdminStaffUpdateRequest request
    );

    @DELETE("StaffManagement/{id}")
    Call<AdminUpdateStaffResponse> deleteStaff(
            @Header("Authorization") String token,
            @Path("id") int staffId
    );
}