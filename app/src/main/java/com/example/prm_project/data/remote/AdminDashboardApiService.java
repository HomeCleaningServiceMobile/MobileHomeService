package com.example.prm_project.data.remote;

import com.example.prm_project.data.model.ApiResponse;
import com.example.prm_project.data.model.MonthlyRevenue;
import com.example.prm_project.data.model.TopService;
import com.example.prm_project.data.model.UserSummary;
import retrofit2.Call;
import retrofit2.http.GET;
import java.util.List;

public interface AdminDashboardApiService {

    @GET("AdminDashboard/monthly-revenue")
    Call<ApiResponse<List<MonthlyRevenue>>> getMonthlyRevenue();

    @GET("AdminDashboard/top-services")
    Call<ApiResponse<List<TopService>>> getTopServices();

    @GET("AdminDashboard/user-summary")
    Call<ApiResponse<UserSummary>> getUserSummary();
}