package com.example.prm_project.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.prm_project.data.model.*;
import com.example.prm_project.data.remote.BookingApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class BookingRepository {
    
    private final BookingApiService bookingApiService;
    
    // Call tracking for cancellation
    private Call<ApiResponse<List<TimeSlotDto>>> timeSlotCall;
    
    // LiveData for UI observation
    private final MutableLiveData<List<Booking>> bookingsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Service>> servicesLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<ServicePackage>> servicePackagesLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<String>> timeAvaailableTimeSlotsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Booking> currentBookingLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> successLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>();

    @Inject
    public BookingRepository(BookingApiService bookingApiService) {
        this.bookingApiService = bookingApiService;
    }

    // Create new booking
    public void createBooking(CreateBookingRequest request) {
        loadingLiveData.setValue(true);
        bookingApiService.createBooking(request).enqueue(new Callback<ApiResponse<Booking>>() {
            @Override
            public void onResponse(Call<ApiResponse<Booking>> call, Response<ApiResponse<Booking>> response) {
                loadingLiveData.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Booking> apiResponse = response.body();
                    if (apiResponse.isSucceeded()) {
                        currentBookingLiveData.setValue(apiResponse.getData());
                        successLiveData.setValue("Booking created successfully!");
                        // Refresh bookings list
                        getBookings(null, null, null, 1, 10);
                    } else {
                        errorLiveData.setValue(apiResponse.getMessages().get("Error")[0]);
                    }
                } else {
                    errorLiveData.setValue("Failed to create booking");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Booking>> call, Throwable t) {
                loadingLiveData.setValue(false);
                errorLiveData.setValue("Network error: " + t.getMessage());
            }
        });
    }

    // Get booking by ID
    public void getBookingById(int bookingId) {
        loadingLiveData.setValue(true);
        bookingApiService.getBookingById(bookingId).enqueue(new Callback<ApiResponse<Booking>>() {
            @Override
            public void onResponse(Call<ApiResponse<Booking>> call, Response<ApiResponse<Booking>> response) {
                loadingLiveData.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Booking> apiResponse = response.body();
                    if (apiResponse.isSucceeded()) {
                        currentBookingLiveData.setValue(apiResponse.getData());
                    } else {
                        errorLiveData.setValue("Booking not found");
                    }
                } else {
                    errorLiveData.setValue("Failed to load booking");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Booking>> call, Throwable t) {
                loadingLiveData.setValue(false);
                errorLiveData.setValue("Network error: " + t.getMessage());
            }
        });
    }

    // Get bookings with filters
    public void getBookings(Integer status, String startDate, String endDate, Integer pageNumber, Integer pageSize) {
        loadingLiveData.setValue(true);
        bookingApiService.getBookings(status, startDate, endDate, pageNumber, pageSize)
                .enqueue(new Callback<ApiResponse<List<Booking>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Booking>>> call, Response<ApiResponse<List<Booking>>> response) {
                loadingLiveData.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Booking>> apiResponse = response.body();
                    if (apiResponse.isSucceeded()) {
                        bookingsLiveData.setValue(apiResponse.getData());
                    } else {
                        errorLiveData.setValue("Failed to load bookings");
                    }
                } else {
                    errorLiveData.setValue("Failed to load bookings");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Booking>>> call, Throwable t) {
                loadingLiveData.setValue(false);
                errorLiveData.setValue("Network error: " + t.getMessage());
            }
        });
    }

    // Update booking
    public void updateBooking(int bookingId, CreateBookingRequest request) {
        loadingLiveData.setValue(true);
        bookingApiService.updateBooking(bookingId, request).enqueue(new Callback<ApiResponse<Booking>>() {
            @Override
            public void onResponse(Call<ApiResponse<Booking>> call, Response<ApiResponse<Booking>> response) {
                loadingLiveData.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Booking> apiResponse = response.body();
                    if (apiResponse.isSucceeded()) {
                        currentBookingLiveData.setValue(apiResponse.getData());
                        successLiveData.setValue("Booking updated successfully!");
                        // Refresh bookings list
                        getBookings(null, null, null, 1, 10);
                    } else {
                        errorLiveData.setValue(apiResponse.getMessages().get("Error")[0]);
                    }
                } else {
                    errorLiveData.setValue("Failed to update booking");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Booking>> call, Throwable t) {
                loadingLiveData.setValue(false);
                errorLiveData.setValue("Network error: " + t.getMessage());
            }
        });
    }

    // Cancel booking
    public void cancelBooking(int bookingId, String reason) {
        loadingLiveData.setValue(true);
        BookingApiService.CancelBookingRequest cancelRequest = new BookingApiService.CancelBookingRequest(reason);
        bookingApiService.cancelBooking(bookingId, cancelRequest).enqueue(new Callback<ApiResponse<Booking>>() {
            @Override
            public void onResponse(Call<ApiResponse<Booking>> call, Response<ApiResponse<Booking>> response) {
                loadingLiveData.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Booking> apiResponse = response.body();
                    if (apiResponse.isSucceeded()) {
                        currentBookingLiveData.setValue(apiResponse.getData());
                        successLiveData.setValue("Booking cancelled successfully!");
                        // Refresh bookings list
                        getBookings(null, null, null, 1, 10);
                    } else {
                        errorLiveData.setValue(apiResponse.getMessages().get("Error")[0]);
                    }
                } else {
                    errorLiveData.setValue("Failed to cancel booking");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Booking>> call, Throwable t) {
                loadingLiveData.setValue(false);
                errorLiveData.setValue("Network error: " + t.getMessage());
            }
        });
    }
    
    // Update booking status
    public void updateBookingStatus(int bookingId, BookingStatus status) {
        loadingLiveData.setValue(true);
        bookingApiService.updateBookingStatus(bookingId, status).enqueue(new Callback<ApiResponse<Booking>>() {
            @Override
            public void onResponse(Call<ApiResponse<Booking>> call, Response<ApiResponse<Booking>> response) {
                loadingLiveData.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Booking> apiResponse = response.body();
                    if (apiResponse.isSucceeded()) {
                        currentBookingLiveData.setValue(apiResponse.getData());
                        successLiveData.setValue("Booking status updated successfully!");
                        // Refresh bookings list
                        getBookings(null, null, null, 1, 10);
                    } else {
                        errorLiveData.setValue(apiResponse.getMessages().get("Error")[0]);
                    }
                } else {
                    errorLiveData.setValue("Failed to update booking status");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Booking>> call, Throwable t) {
                loadingLiveData.setValue(false);
                errorLiveData.setValue("Network error: " + t.getMessage());
            }
        });
    }

    // Get available services
    public void getServices() {
        loadingLiveData.setValue(true);
        bookingApiService.getServices(null, true, null, null, null, 1, 10).enqueue(new Callback<ApiResponse<ItemsWrapper<Service>>>() {
            @Override
            public void onResponse(Call<ApiResponse<ItemsWrapper<Service>>> call, Response<ApiResponse<ItemsWrapper<Service>>> response) {
                loadingLiveData.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<ItemsWrapper<Service>> apiResponse = response.body();
                    if (apiResponse.isSucceeded() && apiResponse.getData() != null) {
                        servicesLiveData.setValue(apiResponse.getData().getItems());
                    } else {
                        errorLiveData.setValue("Failed to load services");
                    }
                } else {
                    errorLiveData.setValue("Failed to load services");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ItemsWrapper<Service>>> call, Throwable t) {
                loadingLiveData.setValue(false);
                errorLiveData.setValue("Network error: " + t.getMessage());
            }
        });
    }

    // Get service packages for a service
    public void getServicePackages(int serviceId) {
        loadingLiveData.setValue(true);
        bookingApiService.getServicePackages(serviceId).enqueue(new Callback<ApiResponse<List<ServicePackage>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ServicePackage>>> call, Response<ApiResponse<List<ServicePackage>>> response) {
                loadingLiveData.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<ServicePackage>> apiResponse = response.body();
                    if (apiResponse.isSucceeded()) {
                        servicePackagesLiveData.setValue(apiResponse.getData());
                    } else {
                        errorLiveData.setValue("Failed to load service packages");
                    }
                } else {
                    errorLiveData.setValue("Failed to load service packages");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ServicePackage>>> call, Throwable t) {
                loadingLiveData.setValue(false);
                errorLiveData.setValue("Network error: " + t.getMessage());
            }
        });
    }

    // Get available time slots
//    public void getAvailableTimeSlots(int serviceId, String date, double latitude, double longitude) {
//        // Add null checks and validation
//        if (date == null || date.trim().isEmpty()) {
//            errorLiveData.setValue("Date is required");
//            return;
//        }
//
//        // Cancel previous call if still running
//        if (timeSlotCall != null && !timeSlotCall.isCanceled()) {
//            timeSlotCall.cancel();
//        }
//
//        loadingLiveData.setValue(true);
//
//        try {
//            timeSlotCall = bookingApiService.getAvailableTimeSlots(serviceId, date, latitude, longitude);
//            timeSlotCall.enqueue(new Callback<ApiResponse<List<String>>>() {
//                @Override
//                public void onResponse(Call<ApiResponse<List<String>>> call, Response<ApiResponse<List<String>>> response) {
//                    loadingLiveData.setValue(false);
//
//                    if (call.isCanceled()) {
//                        return; // Request was cancelled, don't process response
//                    }
//
//                    if (response.isSuccessful() && response.body() != null) {
//                        ApiResponse<List<String>> apiResponse = response.body();
//                        if (apiResponse.isSucceeded() && apiResponse.getData() != null) {
//                            timeAvaailableTimeSlotsLiveData.setValue(apiResponse.getData());
//                        } else {
//                            errorLiveData.setValue("No time slots available");
//                        }
//                    } else {
//                        errorLiveData.setValue("Failed to load available time slots");
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ApiResponse<List<String>>> call, Throwable t) {
//                    loadingLiveData.setValue(false);
//
//                    if (call.isCanceled()) {
//                        return; // Request was cancelled, don't show error
//                    }
//
//                    errorLiveData.setValue("Network error: " + (t.getMessage() != null ? t.getMessage() : "Unknown error"));
//                }
//            });
//        } catch (Exception e) {
//            loadingLiveData.setValue(false);
//            errorLiveData.setValue("Error loading time slots: " + e.getMessage());
//        }
//    }
// In BookingRepository.java
    public void getAvailableTimeSlots(int serviceId, String date) {
        if (date == null || date.trim().isEmpty()) {
            errorLiveData.postValue("Date is required");
            return;
        }

        loadingLiveData.postValue(true); // Use postValue for safety

        try {
            Call<ApiResponse<List<TimeSlotDto>>> call = bookingApiService.getAvailableSlot(date, serviceId);
            timeSlotCall = call; // Track the call
            call.enqueue(new Callback<ApiResponse<List<TimeSlotDto>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<TimeSlotDto>>> call, Response<ApiResponse<List<TimeSlotDto>>> response) {
                    loadingLiveData.postValue(false);
                    if (call.isCanceled()) {
                        return;
                    }

                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<List<TimeSlotDto>> apiResponse = response.body();
                        if (apiResponse.isSucceeded() && apiResponse.getData() != null) {
                            // Map List<TimeSlotDto> to List<String> using displayTime
                            List<String> displayTimes = new ArrayList<>();
                            for (TimeSlotDto timeSlot : apiResponse.getData()) {
                                if (timeSlot.getDisplayTime() != null) {
                                    displayTimes.add(timeSlot.getDisplayTime());
                                }
                            }
                            timeAvaailableTimeSlotsLiveData.postValue(displayTimes); // Post the list of displayTime strings
                        } else {
                            errorLiveData.postValue("No time slots available");
                        }
                    } else {
                        errorLiveData.postValue("Failed to load available time slots");
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<List<TimeSlotDto>>> call, Throwable t) {
                    loadingLiveData.postValue(false);
                    if (call.isCanceled()) {
                        return;
                    }
                    errorLiveData.postValue("Network error: " + (t.getMessage() != null ? t.getMessage() : "Unknown error"));
                }
            });
        } catch (Exception e) {
            loadingLiveData.postValue(false);
            errorLiveData.postValue("Error loading time slotshehehe: " + e.getMessage());
        }
    }
    // LiveData getters for UI observation
    public LiveData<List<Booking>> getBookingsLiveData() {
        return bookingsLiveData;
    }

    public LiveData<List<Service>> getServicesLiveData() {
        return servicesLiveData;
    }

    public LiveData<List<ServicePackage>> getServicePackagesLiveData() {
        return servicePackagesLiveData;
    }

    public LiveData<List<String>> getAvailableTimeSlotsLiveData() {
        return timeAvaailableTimeSlotsLiveData;
    }

    public LiveData<Booking> getCurrentBookingLiveData() {
        return currentBookingLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public LiveData<String> getSuccessLiveData() {
        return successLiveData;
    }

    public LiveData<Boolean> getLoadingLiveData() {
        return loadingLiveData;
    }

    // Clear error/success messages
    public void clearErrorMessage() {
        errorLiveData.setValue(null);
    }

    public void clearSuccessMessage() {
        successLiveData.setValue(null);
    }

    // Convenience methods for common operations
    public void getAllBookings() {
        getBookings(null, null, null, 1, 10);
    }

    public void getBookingsByStatus(BookingStatus status) {
        getBookings(status.getValue(), null, null, 1, 10);
    }

    public void getPendingBookings() {
        getBookingsByStatus(BookingStatus.PENDING);
    }

    public void getConfirmedBookings() {
        getBookingsByStatus(BookingStatus.CONFIRMED);
    }

    public void getCompletedBookings() {
        getBookingsByStatus(BookingStatus.COMPLETED);
    }
    
    // Cancel ongoing API calls
    public void cancelOngoingCalls() {
        if (timeSlotCall != null && !timeSlotCall.isCanceled()) {
            timeSlotCall.cancel();
        }
    }
} 