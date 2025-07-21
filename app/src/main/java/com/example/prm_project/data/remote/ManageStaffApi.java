// file: data/remote/ManageStaffApi.java
package com.example.prm_project.data.remote;

import com.example.prm_project.data.model.AdminStaffListResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
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

    @GET("api/admin/StaffManagement/{id}")
    Call<StaffDetailResponse> getStaffDetail(
            @Header("Authorization") String token,
            @Path("id") int staffId
    );
}