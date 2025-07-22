package com.example.prm_project.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.prm_project.data.model.AppResponse;
import com.example.prm_project.data.model.UserResponse;
import com.example.prm_project.data.repository.MainRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javax.inject.Inject;

@HiltViewModel
public class MainViewModel extends ViewModel {
    
    private MainRepository repository;
    private MutableLiveData<String> message = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    private final MutableLiveData<UserResponse> profile = new MutableLiveData<>();
    
    /**
     * Constructor with dependency injection
     * MainRepository is automatically provided by Hilt
     */
    @Inject
    public MainViewModel(MainRepository repository) {
        this.repository = repository;
        message.setValue("Hello World from MVVM!");
        isLoading.setValue(false);
    }
    
    // Getter methods for LiveData
    public LiveData<String> getMessage() {
        return message;
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    // Business logic methods
    public void loadData() {
        isLoading.setValue(true);
        // Simulate data loading
        repository.getData(new MainRepository.DataCallback() {
            @Override
            public void onSuccess(String data) {
                message.setValue(data);
                isLoading.setValue(false);
            }
            
            @Override
            public void onError(String error) {
                message.setValue("Error: " + error);
                isLoading.setValue(false);
            }
        });
    }
    
    public void updateMessage(String newMessage) {
        message.setValue(newMessage);
    }
    
    @Override
    protected void onCleared() {
        super.onCleared();
        // Clean up any resources
    }

    public LiveData<UserResponse> getProfile() {
        return profile;
    }

    public void loadUserProfile() {
        repository.getProfile(new Callback<AppResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<AppResponse<UserResponse>> call, Response<AppResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSucceeded()) {
                    profile.postValue(response.body().getData());
                } else {
                    Log.e("Profile", "Profile load failed: " +
                            (response.body() != null ? response.body().getFirstMessage("Error") : "Unknown error"));
                }
            }

            @Override
            public void onFailure(Call<AppResponse<UserResponse>> call, Throwable t) {
                Log.e("Profile", "Failed to load profile", t);
            }
        });
    }
}