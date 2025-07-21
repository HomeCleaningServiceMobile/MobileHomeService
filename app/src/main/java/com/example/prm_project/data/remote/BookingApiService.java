package com.example.prm_project.data.remote;

import com.example.prm_project.data.model.ApiResponse;
import com.example.prm_project.data.model.StaffResponseRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface BookingAPIService {

    @POST("{id}/respond")
    Call<ApiResponse> respondToBooking(
            @Path("id") int bookingId,
            @Body StaffResponseRequest request,
            @Header("Authorization") String token
    );
}
