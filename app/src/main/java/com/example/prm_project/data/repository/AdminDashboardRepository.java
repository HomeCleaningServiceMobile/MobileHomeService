package com.example.prm_project.data.repository;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.prm_project.data.model.ApiResponse;
import com.example.prm_project.data.model.MonthlyRevenue;
import com.example.prm_project.data.model.TopService;
import com.example.prm_project.data.model.UserSummary;
import com.example.prm_project.data.remote.AdminDashboardApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AdminDashboardRepository {

    private static final String TAG = "AdminDashboardRepo";
    private final AdminDashboardApiService apiService;

    @Inject
    public AdminDashboardRepository(AdminDashboardApiService apiService) {
        this.apiService = apiService;
    }

    public LiveData<List<MonthlyRevenue>> getMonthlyRevenue() {
        MutableLiveData<List<MonthlyRevenue>> liveData = new MutableLiveData<>();
        
        Log.d(TAG, "Fetching monthly revenue data...");

        apiService.getMonthlyRevenue().enqueue(new Callback<ApiResponse<List<MonthlyRevenue>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<MonthlyRevenue>>> call, Response<ApiResponse<List<MonthlyRevenue>>> response) {
                Log.d(TAG, "Monthly revenue response received. Code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSucceeded()) {
                        Log.d(TAG, "Monthly revenue data loaded successfully. Count: " + 
                            (response.body().getData() != null ? response.body().getData().size() : 0));
                        liveData.setValue(response.body().getData());
                    } else {
                        Log.e(TAG, "API returned error for monthly revenue: " + response.body().getMessages());
                        liveData.setValue(null);
                    }
                } else {
                    Log.e(TAG, "Failed to get monthly revenue. Response code: " + response.code());
                    liveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<MonthlyRevenue>>> call, Throwable t) {
                Log.e(TAG, "Network error while fetching monthly revenue: " + t.getMessage(), t);
                liveData.setValue(null);
            }
        });

        return liveData;
    }

    public LiveData<List<TopService>> getTopServices() {
        MutableLiveData<List<TopService>> liveData = new MutableLiveData<>();
        
        Log.d(TAG, "Fetching top services data...");

        apiService.getTopServices().enqueue(new Callback<ApiResponse<List<TopService>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<TopService>>> call, Response<ApiResponse<List<TopService>>> response) {
                Log.d(TAG, "Top services response received. Code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSucceeded()) {
                        Log.d(TAG, "Top services data loaded successfully. Count: " + 
                            (response.body().getData() != null ? response.body().getData().size() : 0));
                        liveData.setValue(response.body().getData());
                    } else {
                        Log.e(TAG, "API returned error for top services: " + response.body().getMessages());
                        liveData.setValue(null);
                    }
                } else {
                    Log.e(TAG, "Failed to get top services. Response code: " + response.code());
                    liveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<TopService>>> call, Throwable t) {
                Log.e(TAG, "Network error while fetching top services: " + t.getMessage(), t);
                liveData.setValue(null);
            }
        });

        return liveData;
    }

    public LiveData<UserSummary> getUserSummary() {
        MutableLiveData<UserSummary> liveData = new MutableLiveData<>();
        
        Log.d(TAG, "Fetching user summary data...");

        apiService.getUserSummary().enqueue(new Callback<ApiResponse<UserSummary>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserSummary>> call, Response<ApiResponse<UserSummary>> response) {
                Log.d(TAG, "User summary response received. Code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSucceeded()) {
                        Log.d(TAG, "User summary data loaded successfully: " + response.body().getData());
                        liveData.setValue(response.body().getData());
                    } else {
                        Log.e(TAG, "API returned error for user summary: " + response.body().getMessages());
                        liveData.setValue(null);
                    }
                } else {
                    Log.e(TAG, "Failed to get user summary. Response code: " + response.code());
                    liveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserSummary>> call, Throwable t) {
                Log.e(TAG, "Network error while fetching user summary: " + t.getMessage(), t);
                liveData.setValue(null);
            }
        });

        return liveData;
    }
}