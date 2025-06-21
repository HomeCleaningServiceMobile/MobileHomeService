package com.example.prm_project.data.repository;

import com.example.prm_project.data.local.LocalDataSource;
import com.example.prm_project.data.remote.RemoteDataSource;
import com.example.prm_project.data.model.User;

public class MainRepository {
    
    private LocalDataSource localDataSource;
    private RemoteDataSource remoteDataSource;
    
    public MainRepository() {
        localDataSource = new LocalDataSource();
        remoteDataSource = new RemoteDataSource();
    }
    
    // Callback interface for async operations
    public interface DataCallback {
        void onSuccess(String data);
        void onError(String error);
    }
    
    public interface UserCallback {
        void onSuccess(User user);
        void onError(String error);
    }
    
    public void getData(DataCallback callback) {
        // First try to get data from local source
        String localData = localDataSource.getCachedData();
        if (localData != null && !localData.isEmpty()) {
            callback.onSuccess(localData);
            return;
        }
        
        // If no local data, fetch from remote
        remoteDataSource.fetchData(new RemoteDataSource.RemoteCallback() {
            @Override
            public void onSuccess(String data) {
                // Cache the data locally
                localDataSource.cacheData(data);
                callback.onSuccess(data);
            }
            
            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }
    
    public void getUser(int userId, UserCallback callback) {
        // First check local database
        User localUser = localDataSource.getUser(userId);
        if (localUser != null) {
            callback.onSuccess(localUser);
            return;
        }
        
        // If not found locally, fetch from remote
        remoteDataSource.fetchUser(userId, new RemoteDataSource.UserRemoteCallback() {
            @Override
            public void onSuccess(User user) {
                // Save to local database
                localDataSource.saveUser(user);
                callback.onSuccess(user);
            }
            
            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }
    
    public void saveUser(User user) {
        localDataSource.saveUser(user);
    }
    
    public void clearCache() {
        localDataSource.clearCache();
    }
} 