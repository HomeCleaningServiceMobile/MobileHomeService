package com.example.prm_project.ui.viewmodel;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.prm_project.data.model.MonthlyRevenue;
import com.example.prm_project.data.model.TopService;
import com.example.prm_project.data.model.UserSummary;
import com.example.prm_project.data.repository.AdminDashboardRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AdminDashboardViewModel extends ViewModel {

    private static final String TAG = "AdminDashboardVM";
    private final AdminDashboardRepository repository;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    
    // LiveData for dashboard data
    private MutableLiveData<List<MonthlyRevenue>> monthlyRevenueLiveData = new MutableLiveData<>();
    private MutableLiveData<List<TopService>> topServicesLiveData = new MutableLiveData<>();
    private MutableLiveData<UserSummary> userSummaryLiveData = new MutableLiveData<>();

    @Inject
    public AdminDashboardViewModel(AdminDashboardRepository repository) {
        this.repository = repository;
        this.isLoading.setValue(false);
        Log.d(TAG, "AdminDashboardViewModel created");
    }

    // Getters for LiveData
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<List<MonthlyRevenue>> getMonthlyRevenue() {
        return monthlyRevenueLiveData;
    }

    public LiveData<List<TopService>> getTopServices() {
        return topServicesLiveData;
    }

    public LiveData<UserSummary> getUserSummary() {
        return userSummaryLiveData;
    }

    // Methods to load data
    public void loadMonthlyRevenue() {
        Log.d(TAG, "Loading monthly revenue data...");
        isLoading.setValue(true);
        LiveData<List<MonthlyRevenue>> data = repository.getMonthlyRevenue();
        data.observeForever(result -> {
            Log.d(TAG, "Monthly revenue data received: " + (result != null ? result.size() + " items" : "null"));
            monthlyRevenueLiveData.setValue(result);
            checkLoadingComplete();
        });
    }

    public void loadTopServices() {
        Log.d(TAG, "Loading top services data...");
        isLoading.setValue(true);
        LiveData<List<TopService>> data = repository.getTopServices();
        data.observeForever(result -> {
            Log.d(TAG, "Top services data received: " + (result != null ? result.size() + " items" : "null"));
            topServicesLiveData.setValue(result);
            checkLoadingComplete();
        });
    }

    public void loadUserSummary() {
        Log.d(TAG, "Loading user summary data...");
        isLoading.setValue(true);
        LiveData<UserSummary> data = repository.getUserSummary();
        data.observeForever(result -> {
            Log.d(TAG, "User summary data received: " + result);
            userSummaryLiveData.setValue(result);
            checkLoadingComplete();
        });
    }

    private int loadingCount = 0;
    private void checkLoadingComplete() {
        loadingCount++;
        Log.d(TAG, "Loading progress: " + loadingCount + "/3");
        if (loadingCount >= 3) {
            isLoading.setValue(false);
            loadingCount = 0;
            Log.d(TAG, "All data loading completed");
        }
    }

    // Load all dashboard data
    public void loadDashboardData() {
        Log.d(TAG, "Starting to load all dashboard data...");
        loadingCount = 0;
        loadUserSummary();
        loadTopServices();
        loadMonthlyRevenue();
    }

    public void clearError() {
        errorMessage.setValue(null);
    }
}