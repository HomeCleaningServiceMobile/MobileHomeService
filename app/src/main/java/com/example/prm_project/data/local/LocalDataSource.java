package com.example.prm_project.data.local;

import android.content.Context;
import androidx.room.Room;
import com.example.prm_project.data.model.User;

public class LocalDataSource {
    
    private AppDatabase database;
    private UserDao userDao;
    private String cachedData;
    
    public LocalDataSource() {
        // Note: In a real app, you would get context from Application class
        // For now, this is a simplified version
    }
    
    public LocalDataSource(Context context) {
        database = Room.databaseBuilder(
                context.getApplicationContext(),
                AppDatabase.class,
                "prm_database"
        ).build();
        userDao = database.userDao();
    }
    
    // Cache operations
    public void cacheData(String data) {
        this.cachedData = data;
        // In a real app, you might want to persist this to SharedPreferences
        // or Room database
    }
    
    public String getCachedData() {
        // Simulate cached data
        if (cachedData == null) {
            return "Cached: Hello from Local Data Source!";
        }
        return cachedData;
    }
    
    public void clearCache() {
        this.cachedData = null;
    }
    
    // User operations
    public void saveUser(User user) {
        if (userDao != null) {
            // In a real app, this should be done on a background thread
            new Thread(() -> userDao.insertUser(user)).start();
        }
    }
    
//    public User getUser(int userId) {
//        if (userDao != null) {
//            // In a real app, this should be done on a background thread
//            // For now, returning a mock user
//            return new User(userId, "John Doe", "john@example.com", "123-456-7890");
//        }
//
//        // Return mock data for demonstration
//        if (userId == 1) {
//            return new User(1, "Mock", "User", "mock@example.com", "000-000-0000");
//        }
//
//        return null;
//    }
    
    public void deleteUser(int userId) {
        if (userDao != null) {
            new Thread(() -> userDao.deleteUser(userId)).start();
        }
    }
    
    public void updateUser(User user) {
        if (userDao != null) {
            new Thread(() -> userDao.updateUser(user)).start();
        }
    }
} 