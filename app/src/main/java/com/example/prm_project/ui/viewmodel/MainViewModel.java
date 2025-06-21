package com.example.prm_project.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.prm_project.data.repository.MainRepository;

public class MainViewModel extends ViewModel {
    
    private MainRepository repository;
    private MutableLiveData<String> message = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    
    public MainViewModel() {
        repository = new MainRepository();
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
} 