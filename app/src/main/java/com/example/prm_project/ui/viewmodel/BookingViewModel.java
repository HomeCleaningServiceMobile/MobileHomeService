package com.example.prm_project.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.prm_project.data.model.*;
import com.example.prm_project.data.repository.BookingRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;
import java.util.List;
import javax.inject.Inject;

@HiltViewModel
public class BookingViewModel extends ViewModel {
    
    private final BookingRepository bookingRepository;
    
    // Form data for creating/editing bookings
    private final MutableLiveData<Service> selectedService = new MutableLiveData<>();
    private final MutableLiveData<ServicePackage> selectedServicePackage = new MutableLiveData<>();
    private final MutableLiveData<String> selectedDate = new MutableLiveData<>();
    private final MutableLiveData<String> selectedTime = new MutableLiveData<>();
    private final MutableLiveData<String> serviceAddress = new MutableLiveData<>();
    private final MutableLiveData<Double> addressLatitude = new MutableLiveData<>();
    private final MutableLiveData<Double> addressLongitude = new MutableLiveData<>();
    private final MutableLiveData<String> specialInstructions = new MutableLiveData<>();
    private final MutableLiveData<PaymentMethod> selectedPaymentMethod = new MutableLiveData<>();
    
    // UI state
    private final MutableLiveData<BookingFormStep> currentStep = new MutableLiveData<>();
    private final MutableLiveData<String> validationError = new MutableLiveData<>();

    @Inject
    public BookingViewModel(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
        // Initialize default values
        currentStep.setValue(BookingFormStep.SELECT_SERVICE);
        selectedPaymentMethod.setValue(PaymentMethod.CASH);
    }

    // Repository LiveData getters
    public LiveData<List<Booking>> getBookings() {
        return bookingRepository.getBookingsLiveData();
    }

    public LiveData<List<Service>> getServices() {
        return bookingRepository.getServicesLiveData();
    }

    public LiveData<List<ServicePackage>> getServicePackages() {
        return bookingRepository.getServicePackagesLiveData();
    }

    public LiveData<List<String>> getAvailableTimeSlots() {
        return bookingRepository.getAvailableTimeSlotsLiveData();
    }

    public LiveData<Booking> getCurrentBooking() {
        return bookingRepository.getCurrentBookingLiveData();
    }

    public LiveData<String> getErrorMessage() {
        return bookingRepository.getErrorLiveData();
    }

    public LiveData<String> getSuccessMessage() {
        return bookingRepository.getSuccessLiveData();
    }

    public LiveData<Boolean> getLoading() {
        return bookingRepository.getLoadingLiveData();
    }

    // Form data getters
    public LiveData<Service> getSelectedService() {
        return selectedService;
    }

    public LiveData<ServicePackage> getSelectedServicePackage() {
        return selectedServicePackage;
    }

    public LiveData<String> getSelectedDate() {
        return selectedDate;
    }

    public LiveData<String> getSelectedTime() {
        return selectedTime;
    }

    public LiveData<String> getServiceAddress() {
        return serviceAddress;
    }

    public LiveData<Double> getAddressLatitude() {
        return addressLatitude;
    }

    public LiveData<Double> getAddressLongitude() {
        return addressLongitude;
    }

    public LiveData<String> getSpecialInstructions() {
        return specialInstructions;
    }

    public LiveData<PaymentMethod> getSelectedPaymentMethod() {
        return selectedPaymentMethod;
    }

    public LiveData<BookingFormStep> getCurrentStep() {
        return currentStep;
    }

    public LiveData<String> getValidationError() {
        return validationError;
    }

    // Repository method delegates
    public void loadAllBookings() {
        bookingRepository.getAllBookings();
    }

    public void loadBookingsByStatus(BookingStatus status) {
        bookingRepository.getBookingsByStatus(status);
    }

    public void loadPendingBookings() {
        bookingRepository.getPendingBookings();
    }

    public void loadConfirmedBookings() {
        bookingRepository.getConfirmedBookings();
    }

    public void loadCompletedBookings() {
        bookingRepository.getCompletedBookings();
    }

    public void loadBookingById(int bookingId) {
        bookingRepository.getBookingById(bookingId);
    }

    public void loadServices() {
        bookingRepository.getServices();
    }

    public void loadServicePackages(int serviceId) {
        bookingRepository.getServicePackages(serviceId);
    }

    public void loadAvailableTimeSlots(int serviceId, String date, double latitude, double longitude) {
        bookingRepository.getAvailableTimeSlots(serviceId, date, latitude, longitude);
    }

    // Form data setters
    public void setSelectedService(Service service) {
        selectedService.setValue(service);
        // Load packages for selected service
        if (service != null) {
            loadServicePackages(service.getId());
        }
    }

    public void setSelectedServicePackage(ServicePackage servicePackage) {
        selectedServicePackage.setValue(servicePackage);
    }

    public void setSelectedDate(String date) {
        selectedDate.setValue(date);
        // Load available time slots when date changes
        Service service = selectedService.getValue();
        Double lat = addressLatitude.getValue();
        Double lng = addressLongitude.getValue();
        if (service != null && date != null && lat != null && lng != null) {
            // Add a small delay to prevent rapid successive calls
            android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());
            handler.postDelayed(() -> {
                loadAvailableTimeSlots(service.getId(), date, lat, lng);
            }, 100); // 100ms delay
        }
    }

    public void setSelectedTime(String time) {
        selectedTime.setValue(time);
    }

    public void setServiceAddress(String address) {
        serviceAddress.setValue(address);
    }

    public void setAddressLatitude(Double latitude) {
        addressLatitude.setValue(latitude);
    }

    public void setAddressLongitude(Double longitude) {
        addressLongitude.setValue(longitude);
    }

    public void setSpecialInstructions(String instructions) {
        specialInstructions.setValue(instructions);
    }

    public void setSelectedPaymentMethod(PaymentMethod paymentMethod) {
        selectedPaymentMethod.setValue(paymentMethod);
    }

    // Set default date and time (temporary workaround)
    public void setDefaultDateAndTime() {
        // Set default date to tomorrow
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.add(java.util.Calendar.DAY_OF_MONTH, 1);
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'00:00:00'Z'", java.util.Locale.getDefault());
        String defaultDate = dateFormat.format(calendar.getTime());
        
        selectedDate.setValue(defaultDate);
        selectedTime.setValue("10:00:00"); // Default time 10:00 AM
    }

    // Form navigation
    public void setCurrentStep(BookingFormStep step) {
        currentStep.setValue(step);
    }

    public void nextStep() {
        BookingFormStep current = currentStep.getValue();
        if (current != null) {
            switch (current) {
                case SELECT_SERVICE:
                    if (validateServiceSelection()) {
                        setCurrentStep(BookingFormStep.SELECT_PACKAGE);
                    }
                    break;
                case SELECT_PACKAGE:
                    if (validatePackageSelection()) {
                        // Show date/time step for demo, but set default values
                        setDefaultDateAndTime();
                        setCurrentStep(BookingFormStep.SELECT_DATE_TIME);
                    }
                    break;
                case SELECT_DATE_TIME:
                    // Skip validation and just proceed (using default values)
                    setCurrentStep(BookingFormStep.ENTER_ADDRESS);
                    break;
                case ENTER_ADDRESS:
                    if (validateAddress()) {
                        setCurrentStep(BookingFormStep.PAYMENT_METHOD);
                    }
                    break;
                case PAYMENT_METHOD:
                    if (validatePaymentMethod()) {
                        setCurrentStep(BookingFormStep.REVIEW_BOOKING);
                    }
                    break;
                case REVIEW_BOOKING:
                    // Final step - create booking
                    createBooking();
                    break;
            }
        }
    }

    public void previousStep() {
        BookingFormStep current = currentStep.getValue();
        if (current != null) {
            switch (current) {
                case SELECT_PACKAGE:
                    setCurrentStep(BookingFormStep.SELECT_SERVICE);
                    break;
                case SELECT_DATE_TIME:
                    setCurrentStep(BookingFormStep.SELECT_PACKAGE);
                    break;
                case ENTER_ADDRESS:
                    // Back to date/time step for demo
                    setCurrentStep(BookingFormStep.SELECT_DATE_TIME);
                    break;
                case PAYMENT_METHOD:
                    setCurrentStep(BookingFormStep.ENTER_ADDRESS);
                    break;
                case REVIEW_BOOKING:
                    setCurrentStep(BookingFormStep.PAYMENT_METHOD);
                    break;
            }
        }
    }

    // Validation methods
    private boolean validateServiceSelection() {
        if (selectedService.getValue() == null) {
            validationError.setValue("Please select a service");
            return false;
        }
        validationError.setValue(null);
        return true;
    }

    private boolean validatePackageSelection() {
        if (selectedServicePackage.getValue() == null) {
            validationError.setValue("Please select a service package");
            return false;
        }
        validationError.setValue(null);
        return true;
    }

    private boolean validateDateTimeSelection() {
        if (selectedDate.getValue() == null || selectedDate.getValue().isEmpty()) {
            validationError.setValue("Please select a date");
            return false;
        }
        if (selectedTime.getValue() == null || selectedTime.getValue().isEmpty()) {
            validationError.setValue("Please select a time");
            return false;
        }
        validationError.setValue(null);
        return true;
    }

    private boolean validateAddress() {
        if (serviceAddress.getValue() == null || serviceAddress.getValue().trim().isEmpty()) {
            validationError.setValue("Please enter service address");
            return false;
        }
        if (addressLatitude.getValue() == null || addressLongitude.getValue() == null) {
            validationError.setValue("Please provide valid address coordinates");
            return false;
        }
        validationError.setValue(null);
        return true;
    }

    private boolean validatePaymentMethod() {
        if (selectedPaymentMethod.getValue() == null) {
            validationError.setValue("Please select a payment method");
            return false;
        }
        validationError.setValue(null);
        return true;
    }

    // Create booking from form data
    public void createBooking() {
        if (!validateAllFields()) {
            return;
        }

        Service service = selectedService.getValue();
        ServicePackage servicePackage = selectedServicePackage.getValue();
        PaymentMethod paymentMethod = selectedPaymentMethod.getValue();

        CreateBookingRequest request = new CreateBookingRequest(
                service.getId(),
                servicePackage.getId(),
                selectedDate.getValue(),
                selectedTime.getValue(),
                serviceAddress.getValue(),
                addressLatitude.getValue(),
                addressLongitude.getValue(),
                specialInstructions.getValue(),
                paymentMethod.getValue()
        );

        bookingRepository.createBooking(request);
    }

    private boolean validateAllFields() {
        return validateServiceSelection() &&
               validatePackageSelection() &&
               validateDateTimeSelection() &&
               validateAddress() &&
               validatePaymentMethod();
    }

    // Update existing booking
    public void updateBooking(int bookingId) {
        if (!validateAllFields()) {
            return;
        }

        Service service = selectedService.getValue();
        ServicePackage servicePackage = selectedServicePackage.getValue();
        PaymentMethod paymentMethod = selectedPaymentMethod.getValue();

        CreateBookingRequest request = new CreateBookingRequest(
                service.getId(),
                servicePackage.getId(),
                selectedDate.getValue(),
                selectedTime.getValue(),
                serviceAddress.getValue(),
                addressLatitude.getValue(),
                addressLongitude.getValue(),
                specialInstructions.getValue(),
                paymentMethod.getValue()
        );

        bookingRepository.updateBooking(bookingId, request);
    }

    // Cancel booking
    public void cancelBooking(int bookingId, String reason) {
        bookingRepository.cancelBooking(bookingId, reason);
    }

    // Reset form
    public void resetForm() {
        selectedService.setValue(null);
        selectedServicePackage.setValue(null);
        selectedDate.setValue(null);
        selectedTime.setValue(null);
        serviceAddress.setValue(null);
        addressLatitude.setValue(null);
        addressLongitude.setValue(null);
        specialInstructions.setValue(null);
        selectedPaymentMethod.setValue(PaymentMethod.CASH);
        currentStep.setValue(BookingFormStep.SELECT_SERVICE);
        validationError.setValue(null);
    }

    // Load form data from existing booking (for editing)
    public void loadBookingForEdit(Booking booking) {
        if (booking != null) {
            selectedService.setValue(booking.getService());
            selectedServicePackage.setValue(booking.getServicePackage());
            selectedDate.setValue(booking.getScheduledDate());
            selectedTime.setValue(booking.getScheduledTime());
            serviceAddress.setValue(booking.getServiceAddress());
            addressLatitude.setValue(booking.getAddressLatitude());
            addressLongitude.setValue(booking.getAddressLongitude());
            specialInstructions.setValue(booking.getSpecialInstructions());
            selectedPaymentMethod.setValue(PaymentMethod.fromValue(booking.getPreferredPaymentMethod()));
        }
    }

    // Clear messages
    public void clearErrorMessage() {
        bookingRepository.clearErrorMessage();
        validationError.setValue(null);
    }

    public void clearSuccessMessage() {
        bookingRepository.clearSuccessMessage();
    }
    
    // Cancel ongoing API calls
    public void cancelOngoingCalls() {
        bookingRepository.cancelOngoingCalls();
    }

    // Booking form steps enum
    public enum BookingFormStep {
        SELECT_SERVICE,
        SELECT_PACKAGE,
        SELECT_DATE_TIME,
        ENTER_ADDRESS,
        PAYMENT_METHOD,
        REVIEW_BOOKING
    }
} 