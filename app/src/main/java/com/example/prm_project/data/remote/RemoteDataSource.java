package com.example.prm_project.data.remote;

import com.example.prm_project.data.model.User;
import android.os.Handler;
import android.os.Looper;

public class RemoteDataSource {
    
    private ApiService apiService;
    private Handler mainHandler;
    
    public RemoteDataSource() {
        // Initialize Retrofit API service
        apiService = RetrofitClient.getInstance().create(ApiService.class);
        mainHandler = new Handler(Looper.getMainLooper());
    }
    
    // Callback interfaces
    public interface RemoteCallback {
        void onSuccess(String data);
        void onError(String error);
    }
    
    public interface UserRemoteCallback {
        void onSuccess(User user);
        void onError(String error);
    }
    
    public void fetchData(RemoteCallback callback) {
        // Simulate network call
        new Thread(() -> {
            try {
                // Simulate network delay
                Thread.sleep(2000);
                
                // Simulate successful response
                String data = "Remote: Data fetched from API successfully!";
                
                // Post result back to main thread
                mainHandler.post(() -> callback.onSuccess(data));
                
            } catch (InterruptedException e) {
                mainHandler.post(() -> callback.onError("Network error: " + e.getMessage()));
            }
        }).start();
    }
    
    public void fetchUser(int userId, UserRemoteCallback callback) {
        // Simulate network call
        new Thread(() -> {
            try {
                // Simulate network delay
                Thread.sleep(1500);
                
                // Simulate successful response
                User user = new User(userId, "Remote User " + userId, 
                                   "user" + userId + "@api.com", "555-" + userId + "000");
                user.setCreatedAt("2024-01-01T00:00:00Z");
                
                // Post result back to main thread
                mainHandler.post(() -> callback.onSuccess(user));
                
            } catch (InterruptedException e) {
                mainHandler.post(() -> callback.onError("Network error: " + e.getMessage()));
            }
        }).start();
    }
    
    public void postUser(User user, UserRemoteCallback callback) {
        // Simulate POST request
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                
                // Simulate successful creation
                user.setId(System.currentTimeMillis() > 0 ? 
                          (int)(System.currentTimeMillis() % 1000) : 1);
                user.setCreatedAt("2024-01-01T00:00:00Z");
                
                mainHandler.post(() -> callback.onSuccess(user));
                
            } catch (InterruptedException e) {
                mainHandler.post(() -> callback.onError("Failed to create user: " + e.getMessage()));
            }
        }).start();
    }
    
    public void updateUser(User user, UserRemoteCallback callback) {
        // Simulate PUT request
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                
                // Simulate successful update
                mainHandler.post(() -> callback.onSuccess(user));
                
            } catch (InterruptedException e) {
                mainHandler.post(() -> callback.onError("Failed to update user: " + e.getMessage()));
            }
        }).start();
    }
    
    public void deleteUser(int userId, RemoteCallback callback) {
        // Simulate DELETE request
        new Thread(() -> {
            try {
                Thread.sleep(500);
                
                // Simulate successful deletion
                mainHandler.post(() -> callback.onSuccess("User deleted successfully"));
                
            } catch (InterruptedException e) {
                mainHandler.post(() -> callback.onError("Failed to delete user: " + e.getMessage()));
            }
        }).start();
    }
} 