package com.example.prm_project.data.local;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.lifecycle.LiveData;

import com.example.prm_project.data.model.User;
import java.util.List;

@Dao
public interface UserDao {
    
    @Query("SELECT * FROM users")
    LiveData<List<User>> getAllUsers();
    
    @Query("SELECT * FROM users WHERE id = :userId")
    User getUserById(int userId);
    
    @Query("SELECT * FROM users WHERE id = :userId")
    LiveData<User> getUserByIdLiveData(int userId);
    
    @Query("SELECT * FROM users WHERE email = :email")
    User getUserByEmail(String email);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUsers(List<User> users);
    
    @Update
    void updateUser(User user);
    
    @Delete
    void deleteUser(User user);
    
    @Query("DELETE FROM users WHERE id = :userId")
    void deleteUser(int userId);
    
    @Query("DELETE FROM users")
    void deleteAllUsers();
    
    @Query("SELECT COUNT(*) FROM users")
    int getUserCount();
} 