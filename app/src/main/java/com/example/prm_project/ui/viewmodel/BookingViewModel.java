package com.example.prm_project.ui.viewmodel;



import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.prm_project.data.model.*;
import com.example.prm_project.data.remote.RetrofitClient;
import com.example.prm_project.data.repository.BookingRepository;
import com.example.prm_project.utils.payment.*;
import com.example.prm_project.utils.payment.vnpay.VNPayCallbackData;
import com.example.prm_project.utils.payment.vnpay.VNPayRequest;
import com.example.prm_project.utils.payment.stripe.StripeRequest;
import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;

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
    private final MutableLiveData<StaffAvailabilityResponse> selectedStaff = new MutableLiveData<>();
    
    // UI state
    private final MutableLiveData<BookingFormStep> currentStep = new MutableLiveData<>();
    private final MutableLiveData<String> validationError = new MutableLiveData<>();
    
    // Payment state
    private final MutableLiveData<Boolean> isProcessingPayment = new MutableLiveData<>();
    private final MutableLiveData<String> paymentStatus = new MutableLiveData<>();
    private Booking createdBooking = null;

    @Inject
    public BookingViewModel(
            BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
        // Initialize default values
        currentStep.postValue(BookingFormStep.SELECT_SERVICE);
        selectedPaymentMethod.postValue(PaymentMethod.CASH);
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
    
    // Set created booking for payment processing
    public void setCreatedBooking(Booking booking) {
        this.createdBooking = booking;
    }
    
    // Get created booking
    public Booking getCreatedBooking() {
        return createdBooking;
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

    public LiveData<StaffAvailabilityResponse> getSelectedStaff() {
        return selectedStaff;
    }

    public LiveData<BookingFormStep> getCurrentStep() {
        return currentStep;
    }

    public LiveData<String> getValidationError() {
        return validationError;
    }
    
    public LiveData<Boolean> getIsProcessingPayment() {
        return isProcessingPayment;
    }
    
    public LiveData<String> getPaymentStatus() {
        return paymentStatus;
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

    public void loadAvailableTimeSlots(int serviceId, String date) {
        bookingRepository.getAvailableTimeSlots(serviceId, date);
    }

    // Form data setters
    public void setSelectedService(Service service) {
        selectedService.postValue(service);
        // Load packages for selected service
        if (service != null) {
            loadServicePackages(service.getId());
        }
    }
    
    public void setSelectedServicePackage(ServicePackage servicePackage) {
        selectedServicePackage.postValue(servicePackage);
    }

    public void setSelectedDate(String date) {
        selectedDate.postValue(date);
        // Load available time slots when date changes
        Service service = selectedService.getValue();
        if (service != null && date != null) {
            android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());
            handler.postDelayed(() -> {
                loadAvailableTimeSlots(service.getId(), date);
            }, 100);
        }
    }

    public void setSelectedTime(String time) {
        selectedTime.postValue(time);
    }

    public void setServiceAddress(String address) {
        serviceAddress.postValue(address); // Changed from setValue to postValue
    }

    public void setAddressLatitude(Double latitude) {
        addressLatitude.postValue(latitude); // Changed from setValue to postValue
    }

    public void setAddressLongitude(Double longitude) {
        addressLongitude.postValue(longitude); // Changed from setValue to postValue
    }
    public void setSpecialInstructions(String instructions) {
        specialInstructions.postValue(instructions);
    }

    public void setSelectedPaymentMethod(PaymentMethod paymentMethod) {
        selectedPaymentMethod.postValue(paymentMethod);
    }

    public void setSelectedStaff(StaffAvailabilityResponse staff) {
        selectedStaff.postValue(staff);
    }

    // Set default date and time (temporary workaround)
    public void setDefaultDateAndTime() {
        // Set default date to tomorrow
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.add(java.util.Calendar.DAY_OF_MONTH, 1);
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'00:00:00'Z'", java.util.Locale.getDefault());
        String defaultDate = dateFormat.format(calendar.getTime());
        
        selectedDate.postValue(defaultDate);
        selectedTime.postValue("10:00:00"); // Default time 10:00 AM
    }

    // Form navigation
    public void setCurrentStep(BookingFormStep step) {
        currentStep.postValue(step);
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
            validationError.postValue("Please select a service");
            return false;
        }
        validationError.postValue(null);
        return true;
    }

    private boolean validatePackageSelection() {
        if (selectedServicePackage.getValue() == null) {
            validationError.postValue("Please select a service package");
            return false;
        }
        validationError.postValue(null);
        return true;
    }
    public void loadStaffBookings() {
        bookingRepository.getStaffBookings(null, null, null, 1, 10);
    }

    public void loadStaffBookingsByStatus(BookingStatus status) {
        bookingRepository.getStaffBookings(status.getValue(), null, null, 1, 10);
    }



    private boolean validateDateTimeSelection() {
        if (selectedDate.getValue() == null || selectedDate.getValue().isEmpty()) {
            validationError.postValue("Please select a date");
            return false;
        }
        if (selectedTime.getValue() == null || selectedTime.getValue().isEmpty()) {
            validationError.postValue("Please select a time");
                          return false;
          }
          validationError.postValue(null);
          return true;
      }
    public void respondToBooking(int bookingId, StaffResponseRequest request) {
        bookingRepository.respondToBooking(bookingId, request);
    }

    public void acceptBooking(int bookingId) {
        bookingRepository.acceptBooking(bookingId);
    }

    public void declineBooking(int bookingId, String declineReason) {
        bookingRepository.declineBooking(bookingId, declineReason);
    }

    public void updateBookingStatus(int bookingId, BookingStatus status) {
        bookingRepository.updateBookingStatus(bookingId, status);
    }
      private boolean validateAddress() {
        if (serviceAddress.getValue() == null || serviceAddress.getValue().trim().isEmpty()) {
            validationError.postValue("Please select an address on the map");
            return false;
        }
        if (addressLatitude.getValue() == null || addressLongitude.getValue() == null || 
            (addressLatitude.getValue() == 0.0 && addressLongitude.getValue() == 0.0)) {
            validationError.postValue("Please select a valid location on the map");
            return false;
        }
        validationError.postValue(null);
        return true;
    }

    private boolean validatePaymentMethod() {
        if (selectedPaymentMethod.getValue() == null) {
            validationError.postValue("Please select a payment method");
            return false;
        }
        validationError.postValue(null);
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

//        CreateBookingRequest request = new CreateBookingRequest(
//                service.getId(),
//                servicePackage.getId(),
//                selectedDate.getValue(),
//                selectedTime.getValue(),
//                serviceAddress.getValue(),
//                addressLatitude.getValue(),
//                addressLongitude.getValue(),
//                specialInstructions.getValue(),
//                paymentMethod.getValue()
//        );
        CreateBookingRequest request = new CreateBookingRequest(
                service.getId(),
                servicePackage.getId(),
                selectedDate.getValue(),
                selectedTime.getValue(),
                serviceAddress.getValue(),
                1,
                1,
                specialInstructions.getValue(),
                paymentMethod.getValue()
        );
        // Create booking first, then process payment
        bookingRepository.createBooking(request);
    }
    
    // Process payment after booking creation
    public void processPayment(android.content.Context context) {
        if (createdBooking == null) {
            paymentStatus.postValue("No booking to process payment for");
            return;
        }
        
        PaymentMethod paymentMethod = selectedPaymentMethod.getValue();
        if (paymentMethod == null) {
            paymentStatus.postValue("No payment method selected");
            return;
        }
        
        // Don't process payment for cash method
        if (paymentMethod == PaymentMethod.CASH) {
            paymentStatus.postValue("Cash payment - no processing required");
            return;
        }
        
        isProcessingPayment.postValue(true);
        paymentStatus.postValue("Processing payment...");
        
        // Create payment request based on selected method
        PaymentRequest paymentRequest = createPaymentRequest(paymentMethod);
        
        // Process payment
        PaymentManager.getInstance().processPayment(
            getPaymentProcessorMethod(paymentMethod),
            context,
            paymentRequest,
            new PaymentProcessor.PaymentCallback() {
                @Override
                public void onPaymentSuccess(PaymentResult result) {
                    isProcessingPayment.postValue(false);
                    
                    // Handle different payment methods
                    if (paymentMethod == PaymentMethod.STRIPE) {
                        // For Stripe, we need to confirm the payment with the backend
                        handleStripePaymentConfirmation(context, result.getTransactionId());
                    } else {
                        // For VNPay, the confirmation is handled in handleVNPayPaymentResult
                        paymentStatus.postValue("Payment successful: " + result.getMessage());
                        // Update booking status to confirmed
                        if (createdBooking != null) {
                            bookingRepository.updateBookingStatus(createdBooking.getId(), BookingStatus.CONFIRMED);
                        }
                    }
                }
                
                @Override
                public void onPaymentFailure(PaymentResult result) {
                    isProcessingPayment.postValue(false);
                    paymentStatus.postValue("Payment failed: " + result.getMessage());
                }
                
                @Override
                public void onPaymentCancelled() {
                    isProcessingPayment.postValue(false);
                    paymentStatus.postValue("Payment cancelled by user");
                }
            }
        );
    }
    
    // Handle VNPay payment result from WebView
    public void handleVNPayPaymentResult(android.content.Context context, int requestCode, int resultCode, android.content.Intent data) {
        if (createdBooking == null) {
            paymentStatus.postValue("No booking found for payment confirmation");
            return;
        }
        
        // Get customer ID from session
        com.example.prm_project.utils.SessionManager sessionManager = 
            new com.example.prm_project.utils.SessionManager(context);
        int customerId = sessionManager.getCurrentCustomerId();
        
        if (customerId == -1) {
            paymentStatus.postValue("Customer not found - please login again");
            return;
        }
        
        int bookingId = createdBooking.getId();
        
        // Log the IDs for debugging
        android.util.Log.d("BookingViewModel", "Payment confirmation - Customer ID: " + customerId + ", Booking ID: " + bookingId);
        
        if (requestCode == 1001 && resultCode == android.app.Activity.RESULT_OK && data != null) {
            boolean isSuccess = data.getBooleanExtra("payment_success", false);
            
            if (isSuccess) {
                // Create VNPayCallbackData from intent extras
                VNPayCallbackData callbackData = new VNPayCallbackData();
                callbackData.setVnpResponseCode(data.getStringExtra("response_code"));
                callbackData.setVnpTransactionStatus(data.getStringExtra("transaction_status"));
                callbackData.setVnpTxnRef(data.getStringExtra("txn_ref"));
                callbackData.setVnpAmount(data.getStringExtra("amount"));
                callbackData.setVnpTransactionNo(data.getStringExtra("transaction_no"));
                callbackData.setVnpBankCode(data.getStringExtra("bank_code"));
                callbackData.setVnpPayDate(data.getStringExtra("pay_date"));
                callbackData.setVnpSecureHash(data.getStringExtra("secure_hash"));
                
                // Call confirm payment API to deduct balance
                confirmPaymentAndDeductBalance(customerId, bookingId, callbackData);
            } else {
                paymentStatus.postValue("Payment was not successful");
            }
        } else {
            paymentStatus.postValue("Payment cancelled by user");
        }
    }
    
    private void confirmPaymentAndDeductBalance(int customerId, int bookingId, VNPayCallbackData callbackData) {
        Call<com.example.prm_project.data.model.PaymentConfirmationResponse> call = 
            RetrofitClient.getPaymentApiService()
                .confirmVNPayPayment(customerId, bookingId, callbackData);

        call.enqueue(new retrofit2.Callback<com.example.prm_project.data.model.PaymentConfirmationResponse>() {
            @Override
            public void onResponse(Call<com.example.prm_project.data.model.PaymentConfirmationResponse> call, 
                                 retrofit2.Response<com.example.prm_project.data.model.PaymentConfirmationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    com.example.prm_project.data.model.PaymentConfirmationResponse confirmationResponse = response.body();
                    
                    if (confirmationResponse.isSucceeded() && confirmationResponse.getData() != null) {
                        com.example.prm_project.data.model.PaymentConfirmationResponse.PaymentConfirmationData data = confirmationResponse.getData();
                        
                        paymentStatus.postValue("Payment confirmed and balance deducted successfully. Remaining balance: $" + data.getRemainingBalance());
                        
                        // Update booking status to confirmed
                        bookingRepository.updateBookingStatus(bookingId, BookingStatus.CONFIRMED);
                    } else {
                        paymentStatus.postValue("Payment confirmation failed");
                    }
                } else {
                    paymentStatus.postValue("Failed to confirm payment");
                }
            }

            @Override
            public void onFailure(Call<com.example.prm_project.data.model.PaymentConfirmationResponse> call, Throwable t) {
                android.util.Log.e("BookingViewModel", "Payment confirmation failed", t);
                paymentStatus.postValue("Network error during payment confirmation: " + t.getMessage());
            }
        });
    }
    
    // Handle Stripe payment confirmation
    public void handleStripePaymentConfirmation(android.content.Context context, String paymentIntentId) {
        if (createdBooking == null) {
            paymentStatus.postValue("No booking found for payment confirmation");
            return;
        }
        
        // Get customer ID from session
        com.example.prm_project.utils.SessionManager sessionManager = 
            new com.example.prm_project.utils.SessionManager(context);
        int customerId = sessionManager.getCurrentCustomerId();
        
        if (customerId == -1) {
            paymentStatus.postValue("Customer not found - please login again");
            return;
        }
        
        int bookingId = createdBooking.getId();
        
        // Log the IDs for debugging
        android.util.Log.d("BookingViewModel", "Stripe payment confirmation - Customer ID: " + customerId + ", Booking ID: " + bookingId + ", Payment Intent ID: " + paymentIntentId);
        
        // Call Stripe payment confirmation API
        Call<PaymentConfirmationResponse> call =
            RetrofitClient.getPaymentApiService()
                .confirmStripePayment(paymentIntentId, customerId, bookingId);

        call.enqueue(new retrofit2.Callback<com.example.prm_project.data.model.PaymentConfirmationResponse>() {
            @Override
            public void onResponse(Call<com.example.prm_project.data.model.PaymentConfirmationResponse> call, 
                                 retrofit2.Response<com.example.prm_project.data.model.PaymentConfirmationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    com.example.prm_project.data.model.PaymentConfirmationResponse confirmationResponse = response.body();
                    
                    if (confirmationResponse.isSucceeded() && confirmationResponse.getData() != null) {
                        com.example.prm_project.data.model.PaymentConfirmationResponse.PaymentConfirmationData data = confirmationResponse.getData();
                        
                        paymentStatus.postValue("Stripe payment confirmed and balance deducted successfully. Remaining balance: $" + data.getRemainingBalance());
                        
                        // Update booking status to confirmed
                        bookingRepository.updateBookingStatus(bookingId, BookingStatus.CONFIRMED);
                    } else {
                        paymentStatus.postValue("Stripe payment confirmation failed");
                    }
                } else {
                    paymentStatus.postValue("Failed to confirm Stripe payment");
                }
            }

            @Override
            public void onFailure(Call<com.example.prm_project.data.model.PaymentConfirmationResponse> call, Throwable t) {
                android.util.Log.e("BookingViewModel", "Stripe payment confirmation failed", t);
                paymentStatus.postValue("Network error during Stripe payment confirmation: " + t.getMessage());
            }
        });
    }
    
    private PaymentRequest createPaymentRequest(PaymentMethod paymentMethod) {
        ServicePackage pkg = selectedServicePackage.getValue();
        String orderId = createdBooking != null ? String.valueOf(createdBooking.getId()) : "temp_" + System.currentTimeMillis();
        String description = "Payment for booking #" + orderId;
        
        switch (paymentMethod) {
            case VNPAY:
            case BANK_TRANSFER:
                return new VNPayRequest(pkg.getPrice(), orderId, description);
            case STRIPE:
            case CREDIT_CARD:
            case DEBIT_CARD:
                return new StripeRequest(pkg.getPrice(), "USD", orderId, description);
            case E_WALLET:
                // For E-Wallet, we'll use VNPay as the processor
                return new VNPayRequest(pkg.getPrice(), orderId, description);
            case CASH:
            case QR_CODE:
            default:
                // For cash and QR code, we'll use VNPay as the processor
                return new VNPayRequest(pkg.getPrice(), orderId, description);
        }
    }
    
    private PaymentProcessor.PaymentMethod getPaymentProcessorMethod(PaymentMethod paymentMethod) {
        switch (paymentMethod) {
            case VNPAY:
                return PaymentProcessor.PaymentMethod.VNPAY;
            case STRIPE:
                return PaymentProcessor.PaymentMethod.STRIPE;
            case BANK_TRANSFER:
                return PaymentProcessor.PaymentMethod.VNPAY;
            case CREDIT_CARD:
            case DEBIT_CARD:
                return PaymentProcessor.PaymentMethod.STRIPE;
            case E_WALLET:
                return PaymentProcessor.PaymentMethod.PAYPAL;
            case CASH:
            case QR_CODE:
            default:
                return PaymentProcessor.PaymentMethod.VNPAY; // Default to VNPay
        }
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
                1,
                2,
                specialInstructions.getValue(),
                1
        );
        //addressLatitude.getValue(),
        //                addressLongitude.getValue(),paymentMethod.getValue()
        bookingRepository.updateBooking(bookingId, request);
    }

    // Cancel booking
    public void cancelBooking(int bookingId, String reason) {
        bookingRepository.cancelBooking(bookingId, reason);
    }

    // Reset form
    public void resetForm() {
        selectedService.postValue(null);
        selectedServicePackage.postValue(null);
        selectedDate.postValue(null);
        selectedTime.postValue(null);
        serviceAddress.postValue(null);
        addressLatitude.postValue(null);
        addressLongitude.postValue(null);
        specialInstructions.postValue(null);
        selectedPaymentMethod.postValue(PaymentMethod.CASH);
        selectedStaff.postValue(null);
        currentStep.postValue(BookingFormStep.SELECT_SERVICE);
        validationError.postValue(null);
    }

    // Load form data from existing booking (for editing)
    public void loadBookingForEdit(Booking booking) {
        if (booking != null) {
            selectedService.postValue(booking.getService());
            selectedServicePackage.postValue(booking.getServicePackage());
            selectedDate.postValue(booking.getScheduledDate());
            selectedTime.postValue(booking.getScheduledTime());
            serviceAddress.postValue(booking.getServiceAddress());
            addressLatitude.postValue(booking.getAddressLatitude());
            addressLongitude.postValue(booking.getAddressLongitude());
            specialInstructions.postValue(booking.getSpecialInstructions());
            selectedPaymentMethod.postValue(PaymentMethod.fromValue(booking.getPreferredPaymentMethod()));
        }
    }

    // Clear messages
    public void clearErrorMessage() {
        bookingRepository.clearErrorMessage();
        validationError.postValue(null);
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
