package com.example.prm_project.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.prm_project.data.model.Booking;
import com.example.prm_project.data.model.BookingHistoryResponse;
import com.example.prm_project.data.repository.BookingRepository;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import java.util.List;

@HiltViewModel
public class CustomerBookingViewModel extends ViewModel {
    
    private final BookingRepository bookingRepository;
    
    // LiveData for customer bookings
    private final MutableLiveData<List<Booking>> myBookings = new MutableLiveData<>();
    private final MutableLiveData<List<Booking>> upcomingBookings = new MutableLiveData<>();
    private final MutableLiveData<BookingHistoryResponse> bookingHistory = new MutableLiveData<>();
    
    // UI State LiveData
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();
    
    // Search and filter state
    private final MutableLiveData<String> currentSearchQuery = new MutableLiveData<>("");
    private final MutableLiveData<String> currentStatusFilter = new MutableLiveData<>();
    private final MutableLiveData<Integer> currentPage = new MutableLiveData<>(1);
    private final MutableLiveData<Boolean> hasMoreData = new MutableLiveData<>(true);
    
    @Inject
    public CustomerBookingViewModel(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
        
        // Observe repository data
        observeRepositoryData();
    }
    
    private void observeRepositoryData() {
        // Observe loading state from repository
        bookingRepository.getLoadingLiveData().observeForever(isLoading -> {
            if (isLoading != null) {
                loading.postValue(isLoading);
            }
        });
        
        // Observe error messages from repository
        bookingRepository.getErrorLiveData().observeForever(error -> {
            if (error != null) {
                errorMessage.postValue(error);
            }
        });
        
        // Observe success messages from repository
        bookingRepository.getSuccessLiveData().observeForever(success -> {
            if (success != null) {
                successMessage.postValue(success);
            }
        });
        
        // Observe my bookings data
        bookingRepository.getMyBookingsLiveData().observeForever(bookings -> {
            if (bookings != null) {
                myBookings.postValue(bookings);
            }
        });
        
        // Observe upcoming bookings data
        bookingRepository.getUpcomingBookingsLiveData().observeForever(bookings -> {
            if (bookings != null) {
                upcomingBookings.postValue(bookings);
            }
        });
        
        // Observe booking history data
        bookingRepository.getBookingHistoryLiveData().observeForever(history -> {
            if (history != null) {
                bookingHistory.postValue(history);
            }
        });
    }
    
    // Public methods for UI to call
    
    /**
     * Load customer's bookings with enhanced filtering
     */
    public void loadMyBookings(String status, String startDate, String endDate, 
                              Integer serviceId, String serviceName, String searchTerm,
                              String sortBy, String sortDirection, Integer pageNumber, Integer pageSize) {
        
        // Update current filters
        currentStatusFilter.postValue(status);
        currentSearchQuery.postValue(searchTerm != null ? searchTerm : "");
        currentPage.postValue(pageNumber != null ? pageNumber : 1);
        
        // Call repository
        bookingRepository.getMyBookings(status, startDate, endDate, serviceId, serviceName, 
                                       searchTerm, sortBy, sortDirection, pageNumber, pageSize);
    }
    
    /**
     * Load customer's bookings with default parameters
     */
    public void loadMyBookings() {
        loadMyBookings(null, null, null, null, null, null, 
                      "ScheduledDate", "Descending", 1, 20);
    }
    
    /**
     * Load bookings by status
     */
    public void loadMyBookingsByStatus(String status) {
        loadMyBookings(status, null, null, null, null, null,
                      "ScheduledDate", "Descending", 1, 20);
    }
    
    /**
     * Search bookings with query
     */
    public void searchBookings(String query) {
        String currentStatus = currentStatusFilter.getValue();
        loadMyBookings(currentStatus, null, null, null, null, query,
                      "ScheduledDate", "Descending", 1, 20);
    }
    
    /**
     * Load upcoming bookings
     */
    public void loadUpcomingBookings(Integer days) {
        bookingRepository.getMyUpcomingBookings(days != null ? days : 30);
    }
    
    /**
     * Load upcoming bookings with default 30 days
     */
    public void loadUpcomingBookings() {
        loadUpcomingBookings(30);
    }
    
    /**
     * Load booking history with statistics
     */
    public void loadBookingHistory() {
        bookingRepository.getMyBookingHistory();
    }
    
    /**
     * Refresh current bookings
     */
    public void refreshBookings() {
        String status = currentStatusFilter.getValue();
        String searchQuery = currentSearchQuery.getValue();
        loadMyBookings(status, null, null, null, null, searchQuery,
                      "ScheduledDate", "Descending", 1, 20);
    }
    
    /**
     * Load more bookings (pagination)
     */
    public void loadMoreBookings() {
        Integer currentPageValue = currentPage.getValue();
        if (currentPageValue != null && hasMoreData.getValue() == Boolean.TRUE) {
            String status = currentStatusFilter.getValue();
            String searchQuery = currentSearchQuery.getValue();
            loadMyBookings(status, null, null, null, null, searchQuery,
                          "ScheduledDate", "Descending", currentPageValue + 1, 20);
        }
    }
    
    /**
     * Cancel a booking
     */
    public void cancelBooking(int bookingId, String reason) {
        // This would call the cancel booking API
        // For now, we'll just show a placeholder
        successMessage.postValue("Booking cancellation requested");
        // TODO: Implement actual cancellation API call
        // bookingRepository.cancelBooking(bookingId, reason);
    }
    
    /**
     * Confirm booking completion (customer side)
     */
    public void confirmBookingCompletion(int bookingId) {
        // This would call the confirm completion API
        successMessage.postValue("Booking completion confirmed");
        // TODO: Implement actual confirmation API call
        // bookingRepository.confirmBookingCompletion(bookingId);
    }
    
    // Getters for LiveData (for UI to observe)
    
    public LiveData<List<Booking>> getMyBookings() {
        return myBookings;
    }
    
    public LiveData<List<Booking>> getUpcomingBookings() {
        return upcomingBookings;
    }
    
    public LiveData<BookingHistoryResponse> getBookingHistory() {
        return bookingHistory;
    }
    
    public LiveData<Boolean> getLoading() {
        return loading;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }
    
    public LiveData<String> getCurrentSearchQuery() {
        return currentSearchQuery;
    }
    
    public LiveData<String> getCurrentStatusFilter() {
        return currentStatusFilter;
    }
    
    public LiveData<Integer> getCurrentPage() {
        return currentPage;
    }
    
    public LiveData<Boolean> getHasMoreData() {
        return hasMoreData;
    }
    
    // Utility methods
    
    /**
     * Clear error message after showing to user
     */
    public void clearErrorMessage() {
        errorMessage.postValue(null);
        bookingRepository.clearErrorMessage();
    }
    
    /**
     * Clear success message after showing to user
     */
    public void clearSuccessMessage() {
        successMessage.postValue(null);
        bookingRepository.clearSuccessMessage();
    }
    
    /**
     * Reset pagination
     */
    public void resetPagination() {
        currentPage.postValue(1);
        hasMoreData.postValue(true);
    }
    
    /**
     * Clear all filters
     */
    public void clearFilters() {
        currentStatusFilter.postValue(null);
        currentSearchQuery.postValue("");
        resetPagination();
    }
    
    /**
     * Get current filter summary for UI display
     */
    public String getFilterSummary() {
        StringBuilder summary = new StringBuilder();
        
        String status = currentStatusFilter.getValue();
        if (status != null && !status.isEmpty()) {
            summary.append("Status: ").append(status).append(" ");
        }
        
        String search = currentSearchQuery.getValue();
        if (search != null && !search.isEmpty()) {
            summary.append("Search: ").append(search).append(" ");
        }
        
        return summary.toString().trim();
    }
    
    /**
     * Check if any filters are active
     */
    public boolean hasActiveFilters() {
        String status = currentStatusFilter.getValue();
        String search = currentSearchQuery.getValue();
        return (status != null && !status.isEmpty()) || (search != null && !search.isEmpty());
    }
    
    @Override
    protected void onCleared() {
        super.onCleared();
        // Clean up any resources if needed
    }
} 