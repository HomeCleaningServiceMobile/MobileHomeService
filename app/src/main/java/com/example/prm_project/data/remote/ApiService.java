package com.example.prm_project.data.remote;

import com.example.prm_project.data.model.User;
import retrofit2.Call;
import retrofit2.http.*;
import java.util.List;

public interface ApiService {
    
    @GET("users")
    Call<List<User>> getUsers();
    
    @GET("users/{id}")
    Call<User> getUser(@Path("id") int userId);
    
    @POST("users")
    Call<User> createUser(@Body User user);
    
    @PUT("users/{id}")
    Call<User> updateUser(@Path("id") int userId, @Body User user);
    
    @DELETE("users/{id}")
    Call<Void> deleteUser(@Path("id") int userId);
    
    @GET("users/search")
    Call<List<User>> searchUsers(@Query("q") String query);
    
    @GET("users")
    Call<List<User>> getUsersByPage(@Query("page") int page, @Query("limit") int limit);
} 