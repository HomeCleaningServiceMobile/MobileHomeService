package com.example.prm_project.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.prm_project.data.model.ApiResponse;
import com.example.prm_project.data.model.StaffAvailabilityDto;
import com.example.prm_project.data.model.TimeSlotDto;
import com.example.prm_project.data.repository.TimeSlotRepository;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class TimeSlotViewModel extends ViewModel {
    
    private final TimeSlotRepository timeSlotRepository;
    
    // LiveData for UI state
    private final MutableLiveData<List<TimeSlotDto>> availableSlots = new MutableLiveData<>();
    private final MutableLiveData<List<StaffAvailabilityDto>> availableStaff = new MutableLiveData<>();
    private final MutableLiveData<Map<String, List<TimeSlotDto>>> availableSlotsRange = new MutableLiveData<>();
    private final MutableLiveData<TimeSlotDto> nextAvailableSlot = new MutableLiveData<>();
    
    // Loading states
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    
    @Inject
    public TimeSlotViewModel(TimeSlotRepository timeSlotRepository) {
        this.timeSlotRepository = timeSlotRepository;
    }
    
    // LiveData getters
    public LiveData<List<TimeSlotDto>> getAvailableSlots() {
        return availableSlots;
    }
    
    public LiveData<List<StaffAvailabilityDto>> getAvailableStaff() {
        return availableStaff;
    }
    
    public LiveData<Map<String, List<TimeSlotDto>>> getAvailableSlotsRange() {
        return availableSlotsRange;
    }
    
    public LiveData<TimeSlotDto> getNextAvailableSlot() {
        return nextAvailableSlot;
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    /**
     * Load available time slots for a specific date
     */
    public void loadAvailableSlots(String date, Integer serviceId, Integer staffId) {
        isLoading.setValue(true);
        errorMessage.setValue(null);
        
        timeSlotRepository.getAvailableSlots(date, serviceId, staffId)
                .enqueue(new Callback<ApiResponse<List<TimeSlotDto>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<TimeSlotDto>>> call, Response<ApiResponse<List<TimeSlotDto>>> response) {
                        isLoading.setValue(false);
                        
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<List<TimeSlotDto>> apiResponse = response.body();
                            if (apiResponse.isSucceeded()) {
                                availableSlots.setValue(apiResponse.getData());
                            } else {
                                errorMessage.setValue("Failed to load time slots: " + apiResponse.getMessages());
                            }
                        } else {
                            errorMessage.setValue("Network error: " + response.code());
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<ApiResponse<List<TimeSlotDto>>> call, Throwable t) {
                        isLoading.setValue(false);
                        errorMessage.setValue("Network error: " + t.getMessage());
                    }
                });
    }
    
    /**
     * Load available staff for a specific time slot
     */
    public void loadAvailableStaffForSlot(String date, String startTime, String endTime, Integer serviceId) {
        isLoading.setValue(true);
        errorMessage.setValue(null);
        
        timeSlotRepository.getAvailableStaffForSlot(date, startTime, endTime, serviceId)
                .enqueue(new Callback<ApiResponse<List<StaffAvailabilityDto>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<StaffAvailabilityDto>>> call, Response<ApiResponse<List<StaffAvailabilityDto>>> response) {
                        isLoading.setValue(false);
                        
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<List<StaffAvailabilityDto>> apiResponse = response.body();
                            if (apiResponse.isSucceeded()) {
                                availableStaff.setValue(apiResponse.getData());
                            } else {
                                errorMessage.setValue("Failed to load staff: " + apiResponse.getMessages());
                            }
                        } else {
                            errorMessage.setValue("Network error: " + response.code());
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<ApiResponse<List<StaffAvailabilityDto>>> call, Throwable t) {
                        isLoading.setValue(false);
                        errorMessage.setValue("Network error: " + t.getMessage());
                    }
                });
    }
    
    /**
     * Load available time slots for a date range
     */
    public void loadAvailableSlotsRange(String startDate, String endDate, Integer serviceId, Integer staffId) {
        isLoading.setValue(true);
        errorMessage.setValue(null);
        
        timeSlotRepository.getAvailableSlotsRange(startDate, endDate, serviceId, staffId)
                .enqueue(new Callback<ApiResponse<Map<String, List<TimeSlotDto>>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Map<String, List<TimeSlotDto>>>> call, Response<ApiResponse<Map<String, List<TimeSlotDto>>>> response) {
                        isLoading.setValue(false);
                        
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<Map<String, List<TimeSlotDto>>> apiResponse = response.body();
                            if (apiResponse.isSucceeded()) {
                                availableSlotsRange.setValue(apiResponse.getData());
                            } else {
                                errorMessage.setValue("Failed to load time slots range: " + apiResponse.getMessages());
                            }
                        } else {
                            errorMessage.setValue("Network error: " + response.code());
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<ApiResponse<Map<String, List<TimeSlotDto>>>> call, Throwable t) {
                        isLoading.setValue(false);
                        errorMessage.setValue("Network error: " + t.getMessage());
                    }
                });
    }
    
    /**
     * Load the next available time slot
     */
    public void loadNextAvailableSlot(Integer serviceId, Integer staffId) {
        isLoading.setValue(true);
        errorMessage.setValue(null);
        
        timeSlotRepository.getNextAvailableSlot(serviceId, staffId)
                .enqueue(new Callback<ApiResponse<TimeSlotDto>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<TimeSlotDto>> call, Response<ApiResponse<TimeSlotDto>> response) {
                        isLoading.setValue(false);
                        
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<TimeSlotDto> apiResponse = response.body();
                            if (apiResponse.isSucceeded()) {
                                nextAvailableSlot.setValue(apiResponse.getData());
                            } else {
                                errorMessage.setValue("Failed to load next available slot: " + apiResponse.getMessages());
                            }
                        } else {
                            errorMessage.setValue("Network error: " + response.code());
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<ApiResponse<TimeSlotDto>> call, Throwable t) {
                        isLoading.setValue(false);
                        errorMessage.setValue("Network error: " + t.getMessage());
                    }
                });
    }
    
    /**
     * Clear error message
     */
    public void clearError() {
        errorMessage.setValue(null);
    }
} 