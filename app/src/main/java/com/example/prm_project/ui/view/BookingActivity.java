package com.example.prm_project.ui.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm_project.R;
import com.example.prm_project.data.model.ApiResponse;
import com.example.prm_project.data.model.StaffResponseRequest;
import com.example.prm_project.data.remote.BookingAPIService;
import com.example.prm_project.data.remote.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingActivity extends AppCompatActivity {

    private BookingAPIService bookingApiService;
    private EditText etDeclineReason;
    private Button btnAccept, btnDecline;
    private int currentBookingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_response_booking);

        // Initialize API service
        bookingApiService = RetrofitClient.getBookingApiService();

        // Initialize views
        etDeclineReason = findViewById(R.id.etDeclineReason);
        btnAccept = findViewById(R.id.btnAccept);
        btnDecline = findViewById(R.id.btnDecline);

        // Get booking ID from intent
        currentBookingId = getIntent().getIntExtra("booking_id", 0);

        // Set click listeners
        btnAccept.setOnClickListener(v -> acceptBooking());
        btnDecline.setOnClickListener(v -> declineBooking());
    }

    private void acceptBooking() {
        // Create request object for accepting
        StaffResponseRequest request = new StaffResponseRequest();
        request.setBookingId(currentBookingId);
        request.setAccept(true);
        request.setDeclineReason(null); // Not needed for acceptance

        sendResponse(request);
    }

    private void declineBooking() {
        String declineReason = etDeclineReason.getText().toString().trim();

        // Validate decline reason
        if (declineReason.isEmpty()) {
            etDeclineReason.setError("Please provide a reason for declining");
            return;
        }

        if (declineReason.length() > 500) {
            etDeclineReason.setError("Decline reason must be 500 characters or less");
            return;
        }

        // Create request object for declining
        StaffResponseRequest request = new StaffResponseRequest();
        request.setBookingId(currentBookingId);
        request.setAccept(false);
        request.setDeclineReason(declineReason);

        sendResponse(request);
    }

    private void sendResponse(StaffResponseRequest request) {
        // Show loading indicator
        showLoadingDialog();

        // Get auth token
        String authToken = "Bearer " + getAuthToken();

        // Make API call
        Call<ApiResponse> call = bookingApiService.respondToBooking(
                currentBookingId,
                request,
                authToken
        );

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                hideLoadingDialog();

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSucceeded()) {
                        // Handle success
                        String message = request.isAccept() ?
                                "Booking accepted successfully!" :
                                "Booking declined successfully!";
                        showSuccessMessage(message);
                        finish(); // Close activity
                    } else {
                        // Handle API error
                        showErrorMessage(apiResponse.getMessage());
                    }
                } else {
                    // Handle HTTP error
                    handleHttpError(response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                hideLoadingDialog();
                showErrorMessage("Network error: " + t.getMessage());
            }
        });
    }

    private void handleHttpError(int errorCode) {
        switch (errorCode) {
            case 400:
                showErrorMessage("Invalid request. Please check your input.");
                break;
            case 401:
                showErrorMessage("Unauthorized. Please log in again.");
                // Redirect to login
                break;
            case 403:
                showErrorMessage("You don't have permission to perform this action.");
                break;
            case 500:
                showErrorMessage("Server error. Please try again later.");
                break;
            default:
                showErrorMessage("Error: " + errorCode);
        }
    }

    private String getAuthToken() {
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        return prefs.getString("auth_token", "");
    }

    private void showSuccessMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showErrorMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void showLoadingDialog() {
        // Implement loading dialog
    }

    private void hideLoadingDialog() {
        // Hide loading dialog
    }
}
