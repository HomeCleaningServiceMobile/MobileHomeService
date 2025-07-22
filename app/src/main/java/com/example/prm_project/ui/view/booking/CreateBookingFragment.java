package com.example.prm_project.ui.view.booking;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Log;
import com.example.prm_project.R;
import com.example.prm_project.databinding.FragmentCreateBookingBinding;
import com.example.prm_project.data.model.*;
import com.example.prm_project.ui.viewmodel.BookingViewModel;
import com.example.prm_project.ui.viewmodel.TimeSlotViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import dagger.hilt.android.AndroidEntryPoint;
import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import com.example.prm_project.data.remote.RetrofitClient;
import com.example.prm_project.data.remote.TimeSlotApiService;
import com.example.prm_project.data.model.ApiResponse;
import com.example.prm_project.data.model.StaffAvailabilityResponse;
import com.example.prm_project.ui.view.staff.StaffProfileFragment;

import retrofit2.Call;
import retrofit2.Response;

@AndroidEntryPoint
public class CreateBookingFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "CreateBookingFragment";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int SEARCH_DELAY_MS = 300;

    private FragmentCreateBookingBinding binding;
    private BookingViewModel bookingViewModel;
    private TimeSlotViewModel timeSlotViewModel;
    private List<Service> servicesList = new ArrayList<>();
    private List<ServicePackage> packagesList = new ArrayList<>();
    private List<String> timeSlotsList = new ArrayList<>();
    private ArrayAdapter<String> timeSlotAdapter;

    // Add staff selection components
    private StaffSelectionAdapter staffSelectionAdapter;
    private List<StaffAvailabilityResponse> availableStaffList = new ArrayList<>();
    private StaffAvailabilityResponse selectedStaff;

    // Map and location components
    private GoogleMap googleMap;
    private MapView mapView;
    private FusedLocationProviderClient fusedLocationClient;
    private PlacesClient placesClient;
    private AutocompleteSessionToken sessionToken;
    private PlacePredictionAdapter predictionAdapter;
    private Handler mainHandler;
    private ExecutorService backgroundExecutor; // Add background executor
    private Runnable searchRunnable;

    // Address data
    private double selectedLatitude = 10.762622; // Default to Ho Chi Minh City
    private double selectedLongitude = 106.660172;
    private String selectedAddress = "";
    
    // Add debouncing for camera updates
    private static final int CAMERA_UPDATE_DELAY_MS = 1000; // 1 second delay
    private Runnable cameraUpdateRunnable;

    // Add map hiding functionality
    private boolean isMapHidden = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCreateBookingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize ViewModels
        bookingViewModel = new ViewModelProvider(this).get(BookingViewModel.class);
        timeSlotViewModel = new ViewModelProvider(this).get(TimeSlotViewModel.class);
        
        // Setup UI
        setupUI();
        setupObservers();
        
        // Handle navigation arguments for auto-selection
        handleNavigationArguments();
        
        // Inform user that date picker is enabled
        showToast("Date picker is now enabled! Select your preferred date and time.");
        
        // Load initial data
        bookingViewModel.loadServices();
        bookingViewModel.resetForm();
    }
    
    private void setupUI() {
        // Step navigation buttons
        binding.btnNext.setOnClickListener(v -> bookingViewModel.nextStep());
        binding.btnPrevious.setOnClickListener(v -> bookingViewModel.previousStep());
        
        // Date picker - ENABLED
        binding.btnSelectDateTime.setOnClickListener(v -> showDatePicker());

        binding.btnSelectDateTime.setEnabled(true);
        binding.btnSelectDateTime.setText("📅 Select Date");
        
        // Initialize with tomorrow's date
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String tomorrowDate = dateFormat.format(tomorrow.getTime());
        bookingViewModel.setSelectedDate(tomorrowDate);
        binding.tvSelectedDateDisplay.setText("Tomorrow at 10:00 AM");
        
        // Service selection
        binding.servicesRecyclerView.setAdapter(new ServiceSelectionAdapter(
            servicesList, 
            service -> bookingViewModel.setSelectedService(service)
        ));
        
        // Package selection
        binding.packagesRecyclerView.setAdapter(new PackageSelectionAdapter(
            packagesList,
            pkg -> bookingViewModel.setSelectedServicePackage(pkg)
        ));
        
        // Time slots
        setupTimeSlotSpinner();
        
        // Staff selection
        setupStaffSelection();
        
        // Payment methods
        setupPaymentMethodSpinner();
        
        // Address selection with map
        setupAddressSelection();
    }
    
    private void setupObservers() {
        // Observe current step to update UI
        bookingViewModel.getCurrentStep().observe(getViewLifecycleOwner(), this::updateStepUI);
        
        // Observe services
        bookingViewModel.getServices().observe(getViewLifecycleOwner(), services -> {
            if (services != null) {
                servicesList.clear();
                servicesList.addAll(services);
                binding.servicesRecyclerView.getAdapter().notifyDataSetChanged();
                
                // Debug: Log service data
                System.out.println("=== Services Loaded ===");
                for (Service service : services) {
                    System.out.println("Service: " + service.getName() + 
                                     " - Duration: " + service.getEstimatedDurationMinutes() + " minutes" +
                                     " - Price: $" + service.getBasePrice());
                }
                System.out.println("======================");
            }
        });
        
        // Observe service packages
        bookingViewModel.getServicePackages().observe(getViewLifecycleOwner(), packages -> {
            if (packages != null) {
                packagesList.clear();
                packagesList.addAll(packages);
                binding.packagesRecyclerView.getAdapter().notifyDataSetChanged();
                
                // Debug: Log package data
                System.out.println("=== Packages Loaded ===");
                for (ServicePackage pkg : packagesList) {
                    System.out.println("Package: " + pkg.getName() + 
                                     " - Duration: " + pkg.getDurationMinutes() + " minutes" +
                                     " - Price: $" + pkg.getPrice());
                }
                System.out.println("======================");
            }
        });
        
        // Observe available time slots from TimeSlotViewModel
//        timeSlotViewModel.getAvailableSlots().observe(getViewLifecycleOwner(), timeSlots -> {
//            if (timeSlots != null && !timeSlots.isEmpty()) {
//                timeSlotsList.clear();
//                for (TimeSlotDto slot : timeSlots) {
//                    if (slot.isAvailable()) {
//                        timeSlotsList.add(slot.getDisplayTime());
//                    }
//                }
//                updateTimeSlotSpinner();
//                showToast("Loaded " + timeSlotsList.size() + " available time slots");
//            } else {
//                // Fallback to default time slots if no data from API
//                timeSlotsList.clear();
//                timeSlotsList.addAll(Arrays.asList(
//                    "10:00 AM", "08:00 AM", "09:00 AM", "11:00 AM",
//                    "01:00 PM", "02:00 PM", "03:00 PM", "04:00 PM"
//                ));
//                updateTimeSlotSpinner();
//                showToast("Using default time slots (API not available)");
//            }
//        });

        timeSlotViewModel.getAvailableSlots().observe(getViewLifecycleOwner(), timeSlots -> {
            if (binding == null || !isAdded()) return;

            timeSlotsList.clear();
            if (timeSlots != null && !timeSlots.isEmpty()) {
                // Use streams for efficient filtering
                timeSlotsList.addAll(timeSlots.stream()
                        .filter(TimeSlotDto::isAvailable)
                        .map(TimeSlotDto::getDisplayTime)
                        .collect(Collectors.toList()));
                // Debug log: show mapped timeSlotsList
                android.util.Log.d("TimeSlotDebug", "Mapped timeSlotsList: " + timeSlotsList);
                updateTimeSlotSpinner();
                // Debug log: show spinner count after update
                android.util.Log.d("TimeSlotDebug", "Spinner count after update: " + timeSlotAdapter.getCount());
                showToast("Loaded " + timeSlotsList.size() +" available time slots");
            } else {
                // Fallback to default time slots
                timeSlotsList.addAll(Arrays.asList(
                        "08:00 AM", "09:00 AM", "10:00 AM", "11:00 AM",
                        "01:00 PM", "02:00 PM", "03:00 PM", "04:00 PM"
                ));
                updateTimeSlotSpinner();
                showToast("Using default time slots (API not available)");
            }
        });
        
        // Observe loading state from TimeSlotViewModel
        timeSlotViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                showToast("Loading available time slots...");
            }
        });
        
        // Observe error messages from TimeSlotViewModel
        timeSlotViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                showToast("Error loading time slots: " + error);
            }
        });
        
        // Observe selected service
        bookingViewModel.getSelectedService().observe(getViewLifecycleOwner(), service -> {
            if (service != null) {
                binding.tvSelectedService.setText(service.getName());
            }
        });
        
        // Observe selected package
        bookingViewModel.getSelectedServicePackage().observe(getViewLifecycleOwner(), pkg -> {
            if (pkg != null) {
                binding.tvSelectedPackage.setText(pkg.getName() + " - $" + pkg.getPrice());
                
                // Auto-select in adapter
                PackageSelectionAdapter adapter = (PackageSelectionAdapter) binding.packagesRecyclerView.getAdapter();
                if (adapter != null) {
                    adapter.setSelectedPackage(pkg);
                }
            }
        });
        
        // Observe selected date
        bookingViewModel.getSelectedDate().observe(getViewLifecycleOwner(), date -> {
            if (date != null) {
                try {
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    SimpleDateFormat displayFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String displayDate = displayFormat.format(inputFormat.parse(date));
                    binding.tvSelectedDateDisplay.setText(displayDate);
                } catch (Exception e) {
                    binding.tvSelectedDateDisplay.setText(date);
                }
            }
        });
        
        observeFormDataForReview();
        
        bookingViewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            
            binding.btnNext.setEnabled(!isLoading);
            binding.btnPrevious.setEnabled(!isLoading);
            binding.btnSelectDate.setEnabled(!isLoading);
            
            binding.servicesRecyclerView.setEnabled(!isLoading);
            binding.packagesRecyclerView.setEnabled(!isLoading);
            
            // Show loading overlay
            if (isLoading) {
                binding.getRoot().setAlpha(0.7f);
            } else {
                binding.getRoot().setAlpha(1.0f);
            }
        });
        
        // Observe validation errors
        bookingViewModel.getValidationError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                showToast(error);
            }
        });
        
        // Observe success messages
        bookingViewModel.getSuccessMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                showToast(message);
                // Check if we need to process payment
                if (message.contains("Booking created successfully")) {
                    // Get the created booking and process payment
                    Booking createdBooking = bookingViewModel.getCurrentBooking().getValue();
                    if (createdBooking != null) {
                        bookingViewModel.setCreatedBooking(createdBooking);
                        processPaymentAfterBooking();
                    }
                } else {
                    // Navigate back after successful booking (for non-payment operations)
                    Navigation.findNavController(requireView()).navigateUp();
                }
                bookingViewModel.clearSuccessMessage();
            }
        });
        
        // Observe error messages
        bookingViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                showToast("Error: " + error);
                bookingViewModel.clearErrorMessage();
            }
        });
        
        // Observe payment processing state
        bookingViewModel.getIsProcessingPayment().observe(getViewLifecycleOwner(), isProcessing -> {
            if (isProcessing != null && isProcessing) {
                showPaymentProcessingOverlay();
            } else {
                hidePaymentProcessingOverlay();
            }
        });
        
        // Observe payment status
        bookingViewModel.getPaymentStatus().observe(getViewLifecycleOwner(), status -> {
            if (status != null) {
                showToast(status);
                if (status.contains("Payment confirmed") || status.contains("Cash payment") || status.contains("Payment successful")) {
                    // Navigate to payment success screen after successful payment
                    Navigation.findNavController(requireView()).navigate(R.id.action_createBookingFragment_to_paymentSuccessFragment);
                } else if (status.contains("Payment failed") || status.contains("Payment was not successful") || status.contains("Network error") || status.contains("cancelled") || status.contains("confirmation failed") || status.contains("Failed to confirm")) {
                    // Navigate to payment failure screen after failed payment
                    Navigation.findNavController(requireView()).navigate(R.id.action_createBookingFragment_to_paymentFailureFragment);
                }
            }
        });
        
        // Observe selected time changes to load staff
        bookingViewModel.getSelectedTime().observe(getViewLifecycleOwner(), selectedTime -> {
            if (selectedTime != null) {
                // Load staff when time changes
                String selectedDate = bookingViewModel.getSelectedDate().getValue();
                Service selectedService = bookingViewModel.getSelectedService().getValue();
                Integer serviceId = selectedService != null ? selectedService.getId() : null;
                
                loadAvailableStaff(selectedDate, selectedTime, serviceId);
            }
        });
        
        // Observe selected service changes to reload staff
        bookingViewModel.getSelectedService().observe(getViewLifecycleOwner(), selectedService -> {
            if (selectedService != null) {
                String selectedDate = bookingViewModel.getSelectedDate().getValue();
                String selectedTime = bookingViewModel.getSelectedTime().getValue();
                
                if (selectedDate != null && selectedTime != null) {
                    loadAvailableStaff(selectedDate, selectedTime, selectedService.getId());
                }
            }
        });
        
        // Observe selected date changes to reload staff
        bookingViewModel.getSelectedDate().observe(getViewLifecycleOwner(), selectedDate -> {
            if (selectedDate != null) {
                String selectedTime = bookingViewModel.getSelectedTime().getValue();
                Service selectedService = bookingViewModel.getSelectedService().getValue();
                Integer serviceId = selectedService != null ? selectedService.getId() : null;
                
                if (selectedTime != null) {
                    loadAvailableStaff(selectedDate, selectedTime, serviceId);
                }
            }
        });
        
        // Observe selected staff for validation
        bookingViewModel.getSelectedStaff().observe(getViewLifecycleOwner(), staff -> {
            if (staff != null) {
                selectedStaff = staff;
                updateSelectedStaffDisplay();
            }
        });
    }
    
    private void updateStepUI(BookingViewModel.BookingFormStep step) {
        if (step == null) return;
        
        // Hide all step layouts
        binding.layoutSelectService.setVisibility(View.GONE);
        binding.layoutSelectPackage.setVisibility(View.GONE);
        binding.layoutSelectDateTime.setVisibility(View.GONE);
        binding.layoutEnterAddress.setVisibility(View.GONE);
        binding.layoutPaymentMethod.setVisibility(View.GONE);
        binding.layoutReviewBooking.setVisibility(View.GONE);
        
        // Update step indicator
        updateStepIndicator(step);
        
        // Show current step layout
        switch (step) {
            case SELECT_SERVICE:
                binding.layoutSelectService.setVisibility(View.VISIBLE);
                binding.tvStepTitle.setText("Select Service");
                binding.btnPrevious.setVisibility(View.GONE);
                binding.btnNext.setText("Next");
                break;
                
            case SELECT_PACKAGE:
                binding.layoutSelectPackage.setVisibility(View.VISIBLE);
                binding.tvStepTitle.setText("Choose Package");
                binding.btnPrevious.setVisibility(View.VISIBLE);
                binding.btnNext.setText("Next");
                break;
                
            case SELECT_DATE_TIME:
                // KEEP VIEW VISIBLE for demo but functionality disabled
                binding.layoutSelectDateTime.setVisibility(View.VISIBLE);
                binding.tvStepTitle.setText("Select Date & Time (Demo Mode - Tomorrow 10:00 AM)");
                binding.btnNext.setText("Next");
                break;
                
            case ENTER_ADDRESS:
                binding.layoutEnterAddress.setVisibility(View.VISIBLE);
                binding.tvStepTitle.setText("Service Address");
                binding.btnNext.setText("Next");
                break;
                
            case PAYMENT_METHOD:
                binding.layoutPaymentMethod.setVisibility(View.VISIBLE);
                binding.tvStepTitle.setText("Payment Method");
                binding.btnNext.setText("Review");
                break;
                
            case REVIEW_BOOKING:
                binding.layoutReviewBooking.setVisibility(View.VISIBLE);
                binding.tvStepTitle.setText("Review Booking");
                binding.btnNext.setText("Confirm Booking");
                populateReviewData();
                break;
        }
    }
    
    private void updateStepIndicator(BookingViewModel.BookingFormStep step) {
        // Get colors for active and inactive states
        int activeColor = getResources().getColor(R.color.primary_color, null);
        int inactiveColor = getResources().getColor(R.color.status_default, null);
        int completedColor = getResources().getColor(R.color.status_completed, null);
        
        // Reset all step indicators to inactive
        binding.step1.setBackgroundTintList(android.content.res.ColorStateList.valueOf(inactiveColor));
        binding.step2.setBackgroundTintList(android.content.res.ColorStateList.valueOf(inactiveColor));
        binding.step3.setBackgroundTintList(android.content.res.ColorStateList.valueOf(inactiveColor));
        binding.step4.setBackgroundTintList(android.content.res.ColorStateList.valueOf(inactiveColor));
        binding.step5.setBackgroundTintList(android.content.res.ColorStateList.valueOf(inactiveColor));
        binding.step6.setBackgroundTintList(android.content.res.ColorStateList.valueOf(inactiveColor));
        
        // Reset text colors to white for better contrast
        binding.step1.setTextColor(getResources().getColor(android.R.color.white, null));
        binding.step2.setTextColor(getResources().getColor(android.R.color.white, null));
        binding.step3.setTextColor(getResources().getColor(android.R.color.white, null));
        binding.step4.setTextColor(getResources().getColor(android.R.color.white, null));
        binding.step5.setTextColor(getResources().getColor(android.R.color.white, null));
        binding.step6.setTextColor(getResources().getColor(android.R.color.white, null));

        // Get current step number
        int currentStepNumber = step.ordinal() + 1;
        
        // Set completed steps (all steps before current) to completed color
        for (int i = 1; i < currentStepNumber; i++) {
            switch (i) {
                case 1:
                    binding.step1.setBackgroundTintList(android.content.res.ColorStateList.valueOf(completedColor));
                    break;
                case 2:
                    binding.step2.setBackgroundTintList(android.content.res.ColorStateList.valueOf(completedColor));
                    break;
                case 3:
                    binding.step3.setBackgroundTintList(android.content.res.ColorStateList.valueOf(completedColor));
                    break;
                case 4:
                    binding.step4.setBackgroundTintList(android.content.res.ColorStateList.valueOf(completedColor));
                    break;
                case 5:
                    binding.step5.setBackgroundTintList(android.content.res.ColorStateList.valueOf(completedColor));
                    break;
                case 6:
                    binding.step6.setBackgroundTintList(android.content.res.ColorStateList.valueOf(completedColor));
                    break;
            }
        }
        
        // Set current active step
        switch (currentStepNumber) {
            case 1:
                binding.step1.setBackgroundTintList(android.content.res.ColorStateList.valueOf(activeColor));
                break;
            case 2:
                binding.step2.setBackgroundTintList(android.content.res.ColorStateList.valueOf(activeColor));
                break;
            case 3:
                binding.step3.setBackgroundTintList(android.content.res.ColorStateList.valueOf(activeColor));
                break;
            case 4:
                binding.step4.setBackgroundTintList(android.content.res.ColorStateList.valueOf(activeColor));
                break;
            case 5:
                binding.step5.setBackgroundTintList(android.content.res.ColorStateList.valueOf(activeColor));
                break;
            case 6:
                binding.step6.setBackgroundTintList(android.content.res.ColorStateList.valueOf(activeColor));
                break;
        }
        
        // Update step connector lines
        updateStepConnectors(currentStepNumber);
    }
    
    private void updateStepConnectors(int currentStep) {
        // For now, we'll focus on the step buttons themselves
        // The connector lines can be enhanced later if needed
        // The main step indicator functionality is working with the button colors
    }

    // Optimized showDatePicker method
    private void showDatePicker() {
        if (bookingViewModel.getLoading().getValue() == Boolean.TRUE) {
            return;
        }

        try {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view, year, month, dayOfMonth) -> {
                        // Use Handler to prevent blocking
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(() -> handleDateSelectionOptimized(year, month, dayOfMonth));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            // Set minimum date to tomorrow to prevent past date selection
            Calendar tomorrow = Calendar.getInstance();
            tomorrow.add(Calendar.DAY_OF_MONTH, 1);
            datePickerDialog.getDatePicker().setMinDate(tomorrow.getTimeInMillis());

            datePickerDialog.setTitle("Select Booking Date");
            datePickerDialog.show();

        } catch (Exception e) {
            Toast.makeText(getContext(), "Error opening date picker", Toast.LENGTH_SHORT).show();
        }
    }

    // Optimized date selection handler
    private void handleDateSelectionOptimized(int year, int month, int dayOfMonth) {
        // Quick validation checks
        if (!isAdded() || getContext() == null || binding == null) {
            return;
        }

        try {
            // Create selected date efficiently
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, dayOfMonth);

            // Format date once
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String formattedDate = dateFormat.format(selectedDate.getTime());

            // Update ViewModel and UI immediately
            bookingViewModel.setSelectedDate(formattedDate);
            binding.tvSelectedDateDisplay.setText(formattedDate);

            // Show immediate feedback
            Toast.makeText(getContext(), "Date selected: " + formattedDate, Toast.LENGTH_SHORT).show();

            // Load time slots in background
            loadTimeSlots(formattedDate);

        } catch (Exception e) {
            Toast.makeText(getContext(), "Error processing selected date", Toast.LENGTH_SHORT).show();
        }
    }

    // Async time slot loading
    private void loadTimeSlots(String selectedDate) {
        // Use AsyncTask alternative or ExecutorService for better performance
        java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newSingleThreadExecutor();
        Handler mainHandler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                // Get service ID (this should be quick as it's in memory)
                Service selectedService = bookingViewModel.getSelectedService().getValue();
                Integer serviceId = selectedService != null ? selectedService.getId() : null;

                // Switch back to main thread for ViewModel call
                mainHandler.post(() -> {
                    if (isAdded()) {
                        // Make sure your TimeSlotViewModel handles this asynchronously
                        timeSlotViewModel.loadAvailableSlots(selectedDate, serviceId, null);
                    }
                });

            } catch (Exception e) {
                mainHandler.post(() -> {
                    if (isAdded()) {
                        Log.d("Fuck this shit", e.getMessage());
                        Toast.makeText(getContext(), "Error loading time slots", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

//private void showDatePicker() {
//    if (bookingViewModel.getLoading().getValue() == Boolean.TRUE) {
//        return;
//    }
//    Calendar calendar = Calendar.getInstance();
//    DatePickerDialog datePickerDialog = new DatePickerDialog(
//            requireContext(),
//            (view, year, month, dayOfMonth) -> handleDateSelection(year, month, dayOfMonth),
//            calendar.get(Calendar.YEAR),
//            calendar.get(Calendar.MONTH),
//            calendar.get(Calendar.DAY_OF_MONTH)
//    );
//    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
//    datePickerDialog.setTitle("Select Booking Date");
//    datePickerDialog.show();
//}
//
//    private void handleDateSelection(int year, int month, int dayOfMonth) {
//        if (!isAdded() || getContext() == null) {
//            return;
//        }
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(year, month, dayOfMonth);
//        Calendar today = Calendar.getInstance();
//        today.set(Calendar.HOUR_OF_DAY, 0);
//        today.set(Calendar.MINUTE, 0);
//        today.set(Calendar.SECOND, 0);
//        today.set(Calendar.MILLISECOND, 0);
//        if (calendar.before(today)) {
//            Toast.makeText(getContext(), "Cannot select past dates", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//        String selectedDate = dateFormat.format(calendar.getTime());
//        SimpleDateFormat displayFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()); // Align with showDatePicker
//        String displayDate = displayFormat.format(calendar.getTime());
////        if (binding != null && binding.etAddress != null) {
////            bookingViewModel.setServiceAddress(binding.etAddress.getText().toString());
////        }
////        bookingViewModel.setAddressLatitude(10.762622);
////        bookingViewModel.setAddressLongitude(106.660172);
//        bookingViewModel.setSelectedDate(selectedDate);
//        if (binding != null && binding.tvSelectedDate != null) {
//            binding.tvSelectedDate.setText(displayDate);
//        }
//        Service selectedService = bookingViewModel.getSelectedService().getValue();
//        if (selectedService != null) {
//            timeSlotViewModel.loadAvailableSlots(selectedDate, selectedService.getId(), null);
//        } else {
//            timeSlotViewModel.loadAvailableSlots(selectedDate, null, null);
//        }
//        Toast.makeText(getContext(), "Date selected: " + displayDate + "\nLoading time slots...", Toast.LENGTH_SHORT).show();
//    }

    private void setupTimeSlotSpinner() {
        // Only initialize the adapter ONCE
        if (timeSlotAdapter == null) {
            timeSlotAdapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    new ArrayList<>() // Start with empty list, always update via addAll
            );
            timeSlotAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.spinnerTimeSlot.setAdapter(timeSlotAdapter);
            Log.d("TimeSlotDebug", "Adapter initialized: " + timeSlotAdapter);
        }
        // Set default time
        bookingViewModel.setSelectedTime("10:00 AM");
        // Load initial time slots for tomorrow
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String tomorrowDate = dateFormat.format(tomorrow.getTime());
        timeSlotViewModel.loadAvailableSlots(tomorrowDate, 1, null);
    }

    private void updateTimeSlotSpinner() {
        if (binding == null || !isAdded()) return;
        Log.d("TimeSlotDebug", "Updating spinner with adapter: " + timeSlotAdapter);
        timeSlotAdapter.clear();
        timeSlotAdapter.addAll(timeSlotsList);
        timeSlotAdapter.notifyDataSetChanged();
        Log.d("TimeSlotDebug", "Spinner count after update (in updateTimeSlotSpinner): " + timeSlotAdapter.getCount());
        // Restore previous selection if possible
        String selectedTime = bookingViewModel.getSelectedTime().getValue();
        if (selectedTime != null && timeSlotsList.contains(selectedTime)) {
            binding.spinnerTimeSlot.setSelection(timeSlotsList.indexOf(selectedTime));
        } else {
            binding.spinnerTimeSlot.setSelection(0);
        }
    }


    private void setupPaymentMethodSpinner() {
        PaymentMethod[] methods = PaymentMethod.getAllMethods();
        String[] methodNames = new String[methods.length];
        for (int i = 0; i < methods.length; i++) {
            methodNames[i] = methods[i].getDisplayName();
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            methodNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerPaymentMethod.setAdapter(adapter);
        
        binding.spinnerPaymentMethod.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < methods.length) {
                    bookingViewModel.setSelectedPaymentMethod(methods[position]);
                }
            }
            
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }
    
    private void setupAddressSelection() {
        // Initialize threading components
        mainHandler = new Handler(Looper.getMainLooper());
        backgroundExecutor = Executors.newSingleThreadExecutor(); // Initialize background executor
        
        // Show immediate feedback that address section is loading
        if (binding != null) {
            binding.tvMapStatus.setText("Loading address selection...");
            binding.btnConfirmAddress.setEnabled(false);
            binding.tvSelectedAddressDisplay.setText("Initializing map and search...");
        }
        
        // Initialize location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        
        // Initialize Places API first (lightweight)
        initializePlacesApi();
        
        // Delay map setup to prevent initial ANR
        mainHandler.postDelayed(() -> {
            if (isAdded() && binding != null) {
                setupMap();
            }
        }, 500); // 500ms delay to let UI settle
        
        // Setup search functionality
        setupAddressSearch();
        
        // Setup buttons
        setupAddressButtons();
        
        // Make main address field read-only
        binding.etAddress.setFocusable(false);
        binding.etAddress.setClickable(false);
        binding.etAddress.setHint("Address will appear here after map selection");
        
        // Observe address changes from ViewModel
        bookingViewModel.getServiceAddress().observe(getViewLifecycleOwner(), address -> {
            if (address != null && !address.isEmpty()) {
                binding.etAddress.setText(address);
                binding.tvSelectedAddressDisplay.setText("✓ Selected: " + address);
            }
        });
    }
    
    private void initializePlacesApi() {
        // Move Places API initialization to background thread
        backgroundExecutor.execute(() -> {
            try {
                if (!Places.isInitialized()) {
                    Places.initialize(requireContext().getApplicationContext(), getString(R.string.google_maps_key));
                }
                
                // Switch back to main thread for UI updates
                mainHandler.post(() -> {
                    try {
                        placesClient = Places.createClient(requireContext());
                        sessionToken = AutocompleteSessionToken.newInstance();
                        Log.d(TAG, "Places API initialized");
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to create Places client", e);
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Failed to initialize Places API", e);
            }
        });
    }

    private void setupMap() {
        // Initialize map on main thread but with delayed async loading
        try {
            mapView = binding.mapView;
            
            // Create map asynchronously to prevent blocking
            backgroundExecutor.execute(() -> {
                try {
                    // Initialize map components on background thread
                    mainHandler.post(() -> {
                        try {
                            mapView.onCreate(null);
                            mapView.onResume();
                            mapView.getMapAsync(this);
                            binding.tvMapStatus.setText("Initializing map...");
                        } catch (Exception e) {
                            Log.e(TAG, "Failed to setup map on main thread", e);
                            binding.tvMapStatus.setText("Map failed to load");
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "Failed to setup map", e);
                    mainHandler.post(() -> binding.tvMapStatus.setText("Map failed to load"));
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Failed to setup map", e);
            binding.tvMapStatus.setText("Map failed to load");
        }
    }

    private void setupAddressSearch() {
        // Setup suggestions RecyclerView
        predictionAdapter = new PlacePredictionAdapter(prediction -> {
            binding.rvAddressSuggestions.setVisibility(View.GONE);
            binding.etSearchAddress.setText("");
            getPlaceDetails(prediction.getPlaceId());
        });

        binding.rvAddressSuggestions.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvAddressSuggestions.setAdapter(predictionAdapter);

        // Setup search text watcher
        binding.etSearchAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) {
                    mainHandler.removeCallbacks(searchRunnable);
                }

                searchRunnable = () -> {
                    // Emergency throttling - check system load
                    if (!isAdded() || binding == null || placesClient == null) {
                        return;
                    }
                    
                    // Only search if sufficient characters and not too frequent
                    if (s.length() > 2) {
                        searchPlaces(s.toString());
                    } else {
                        binding.rvAddressSuggestions.setVisibility(View.GONE);
                    }
                };

                mainHandler.postDelayed(searchRunnable, SEARCH_DELAY_MS);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupAddressButtons() {
        binding.btnCurrentLocation.setOnClickListener(v -> {
            // Emergency check - if too many operations queued, ignore click
            if (backgroundExecutor.isShutdown() || backgroundExecutor.isTerminated()) {
                Log.w(TAG, "Background executor not available - ignoring current location request");
                return;
            }
            getCurrentLocation();
        });
        
        binding.btnConfirmAddress.setOnClickListener(v -> {
            // Emergency check - prevent multiple rapid clicks
            if (!binding.btnConfirmAddress.isEnabled()) {
                Log.d(TAG, "Button already clicked - ignoring duplicate");
                return;
            }
            confirmSelectedAddress();
        });
        
        // Add change location button handler
        binding.btnChangeLocation.setOnClickListener(v -> {
            Log.d(TAG, "User requested to change location - re-enabling map");
            showMapForLocationChange();
        });
    }

    private void observeFormDataForReview() {
        // Address
        bookingViewModel.getServiceAddress().observe(getViewLifecycleOwner(), address -> {
            if (address != null) {
                bookingViewModel.setServiceAddress(binding.etAddress.getText().toString());
            }
        });
        
        // Special instructions
        bookingViewModel.getSpecialInstructions().observe(getViewLifecycleOwner(), instructions -> {
            if (instructions != null) {
                bookingViewModel.setSpecialInstructions(binding.etSpecialInstructions.getText().toString());
            }
        });
    }
    
    private void populateReviewData() {
        PaymentMethod paymentMethod = bookingViewModel.getSelectedPaymentMethod().getValue();
        Service service = bookingViewModel.getSelectedService().getValue();
        ServicePackage pkg = bookingViewModel.getSelectedServicePackage().getValue();
        String date = bookingViewModel.getSelectedDate().getValue();
        String time = bookingViewModel.getSelectedTime().getValue();

        if (service != null) {
            binding.tvReviewService.setText(service.getName());
        }
        if (pkg != null) {
            binding.tvReviewPackage.setText(pkg.getName());
            binding.tvReviewPrice.setText("$" + pkg.getPrice());
            binding.tvReviewDuration.setText(formatDuration(pkg.getDurationMinutes()));
        }
        if (date != null) {
            binding.tvReviewDate.setText(formatDateForDisplay(date));
        }
        if (time != null) {
            binding.tvReviewTime.setText(formatTimeForDisplay(time));
        }
        if (paymentMethod != null) {
            binding.tvReviewPayment.setText(paymentMethod.getDisplayName());
        }
    }

    private String formatDuration(int minutes) {
        int hours = minutes / 60;
        int mins = minutes % 60;
        if (hours > 0 && mins > 0) {
            return hours + "h " + mins + "m";
        } else if (hours > 0) {
            return hours + "h";
        } else {
            return mins + "m";
        }
    }
    
    private String formatDateForDisplay(String isoDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            return outputFormat.format(inputFormat.parse(isoDate));
        } catch (Exception e) {
            return isoDate;
        }
    }
    
    private String formatTimeForDisplay(String time) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            return outputFormat.format(inputFormat.parse(time));
        } catch (Exception e) {
            return time;
        }
    }
    
    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Show the TimeSlotSelectionFragment for advanced time slot selection
     * This can be called from a button or menu option
     * Note: This requires a fragment container in the layout
     */
    private void showTimeSlotSelection() {
        Service selectedService = bookingViewModel.getSelectedService().getValue();
        if (selectedService == null) {
            showToast("Please select a service first");
            return;
        }
        
        // For now, show a message that this feature requires layout modification
        showToast("Advanced time slot selection requires layout modification.\n" +
                 "Currently using basic time slot spinner.");
        
        // TODO: To enable this feature, add a FrameLayout with id "fragment_container" 
        // to the fragment_create_booking.xml layout file
        /*
        TimeSlotSelectionFragment timeSlotFragment = new TimeSlotSelectionFragment();
        timeSlotFragment.setServiceId(selectedService.getId());
        timeSlotFragment.setOnTimeSlotSelectionListener((date, timeSlot, staff) -> {
            // Handle the selection
            bookingViewModel.setSelectedDate(date);
            bookingViewModel.setSelectedTime(timeSlot.getDisplayTime());
            
            // Show success message
            showToast("Time slot selected: " + date + " at " + timeSlot.getDisplayTime() + 
                     " with " + staff.getStaffName());
            
            // Close the fragment
            getChildFragmentManager().popBackStack();
        });
        
        // Add to fragment container
        getChildFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, timeSlotFragment)
            .addToBackStack(null)
            .commit();
        */
    }
    
    private void processPaymentAfterBooking() {
        PaymentMethod paymentMethod = bookingViewModel.getSelectedPaymentMethod().getValue();
        if (paymentMethod == null) {
            showToast("No payment method selected");
            return;
        }
        
        // Don't process payment for cash method
        if (paymentMethod == PaymentMethod.CASH) {
            showToast("Cash payment selected - booking confirmed!");
            Navigation.findNavController(requireView()).navigateUp();
            return;
        }
        
        // Process payment for online payment methods
        bookingViewModel.processPayment(requireActivity());
    }
    
    private void handleNavigationArguments() {
        Bundle args = getArguments();
        if (args != null) {
            int serviceId = args.getInt("serviceId", -1);
            int packageId = args.getInt("packageId", -1);
            
            System.out.println("Navigation args - serviceId: " + serviceId + ", packageId: " + packageId);
            
            // Flow 1: User selected service first (serviceId provided, no packageId)
            if (serviceId != -1 && packageId == -1) {
                System.out.println("Flow 1: Service selected first");
                // Auto-select service when services are loaded
                bookingViewModel.getServices().observe(getViewLifecycleOwner(), services -> {
                    if (services != null) {
                        for (Service service : services) {
                            if (service.getId() == serviceId) {
                                bookingViewModel.setSelectedService(service);
                                // Start at step 1 (SELECT_SERVICE)
                                bookingViewModel.setCurrentStep(BookingViewModel.BookingFormStep.SELECT_SERVICE);
                                break;
                            }
                        }
                    }
                });
            }
            
            // Flow 2: User selected package first (both serviceId and packageId provided)
            else if (serviceId != -1 && packageId != -1) {
                System.out.println("Flow 2: Package selected first");
                // Auto-select service and package when data is loaded
                bookingViewModel.getServices().observe(getViewLifecycleOwner(), services -> {
                    if (services != null) {
                        for (Service service : services) {
                            if (service.getId() == serviceId) {
                                bookingViewModel.setSelectedService(service);
                                // Load packages for this service
                                bookingViewModel.loadServicePackages(serviceId);
                                break;
                            }
                        }
                    }
                });
                
                // Auto-select package when packages are loaded from API
                bookingViewModel.getServicePackages().observe(getViewLifecycleOwner(), packages -> {
                    if (packages != null) {
                        for (ServicePackage pkg : packages) {
                            if (pkg.getId() == packageId) {
                                bookingViewModel.setSelectedServicePackage(pkg);
                                // Start at step 2 (SELECT_PACKAGE) with auto-selection
                                bookingViewModel.setCurrentStep(BookingViewModel.BookingFormStep.SELECT_PACKAGE);
                                System.out.println("Auto-selected package: " + pkg.getName());
                                break;
                            }
                        }
                    }
                });
            }
        }
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        // Handle VNPay payment result
        if (requestCode == 1001) { // VNPay payment request code
            bookingViewModel.handleVNPayPaymentResult(requireActivity(), requestCode, resultCode, data);
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        
        // Clean up MapView
        if (mapView != null) {
            Log.d(TAG, "Destroying MapView - freeing Google Maps resources");
            mapView.onDestroy();
        }
        
        // Clean up callbacks
        if (mainHandler != null) {
            if (searchRunnable != null) {
                mainHandler.removeCallbacks(searchRunnable);
            }
            if (cameraUpdateRunnable != null) {
                mainHandler.removeCallbacks(cameraUpdateRunnable);
            }
        }
        
        // Clean up background executor
        if (backgroundExecutor != null && !backgroundExecutor.isShutdown()) {
            backgroundExecutor.shutdown();
            try {
                // Wait for existing tasks to complete
                if (!backgroundExecutor.awaitTermination(2, java.util.concurrent.TimeUnit.SECONDS)) {
                    backgroundExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                backgroundExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        // Cancel any ongoing API calls to prevent memory leaks
        if (bookingViewModel != null) {
            bookingViewModel.cancelOngoingCalls();
        }
        
        Log.d(TAG, "CreateBookingFragment destroyed - all Google Maps resources should be freed");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }
    
    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        try {
            googleMap = map;
            binding.tvMapStatus.setText("✓ Map Ready");

            // Configure map
            googleMap.getUiSettings().setMapToolbarEnabled(false);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            googleMap.setBuildingsEnabled(false);
            googleMap.setIndoorEnabled(false);
            googleMap.setTrafficEnabled(false);

            // Set default location
            LatLng defaultLocation = new LatLng(selectedLatitude, selectedLongitude);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12));
            googleMap.addMarker(new MarkerOptions().position(defaultLocation).title("Default Location"));

            // Setup map listeners
            googleMap.setOnMapClickListener(this::onMapClick);
            // DISABLED: Camera idle listener causes frequent ANR
            // googleMap.setOnCameraIdleListener(this::onCameraIdle);

            // Enable confirm button
            binding.btnConfirmAddress.setEnabled(true);
            binding.tvSelectedAddressDisplay.setText("Default: Ho Chi Minh City, Vietnam");
            selectedAddress = "Ho Chi Minh City, Vietnam";

        } catch (Exception e) {
            Log.e(TAG, "Error in onMapReady", e);
            binding.tvMapStatus.setText("Map error");
        }
    }

    private void onMapClick(LatLng latLng) {
        updateSelectedLocation(latLng, "Selected location");
    }

    private void onCameraIdle() {
        if (googleMap != null) {
            // Cancel previous camera update
            if (cameraUpdateRunnable != null) {
                mainHandler.removeCallbacks(cameraUpdateRunnable);
            }
            
            // Debounce camera updates to prevent frequent ANR-causing operations
            cameraUpdateRunnable = () -> {
                try {
                    if (googleMap != null && isAdded()) {
                        LatLng center = googleMap.getCameraPosition().target;
                        updateSelectedLocation(center, "Map center location");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error in camera update", e);
                }
            };
            
            mainHandler.postDelayed(cameraUpdateRunnable, CAMERA_UPDATE_DELAY_MS);
        }
    }
    private void updateSelectedLocation(LatLng latLng, String description) {
        try {
            selectedLatitude = latLng.latitude;
            selectedLongitude = latLng.longitude;
            selectedAddress = String.format("%s (%.4f, %.4f)", description, latLng.latitude, latLng.longitude);

            mainHandler.post(() -> {
                if (binding == null || !isAdded()) return;

                String shortAddress = selectedAddress.length() > 50 ?
                        selectedAddress.substring(0, 47) + "..." : selectedAddress;
                binding.tvSelectedAddressDisplay.setText(shortAddress);
                binding.btnConfirmAddress.setEnabled(true);

                // Only update map if not hidden
                if (!isMapHidden && googleMap != null) {
                    googleMap.clear();
                    googleMap.addMarker(new MarkerOptions().position(latLng).title("Selected"));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error in updateSelectedLocation", e);
        }
    }
//    private void updateSelectedLocation(LatLng latLng, String description) {
//        try {
//            // ULTRA-LIGHTWEIGHT: Only update essential data
//            selectedLatitude = latLng.latitude;
//            selectedLongitude = latLng.longitude;
//            selectedAddress = String.format("%s (%.4f, %.4f)", description, latLng.latitude, latLng.longitude); // Reduced precision
//
//            // MINIMAL UI updates
//            if (binding != null) {
//                // Update text only (no heavy operations)
//                String shortAddress = selectedAddress.length() > 50 ?
//                    selectedAddress.substring(0, 47) + "..." : selectedAddress;
//                binding.tvSelectedAddressDisplay.setText(shortAddress);
//
//                // Enable button immediately (no validation)
//                binding.btnConfirmAddress.setEnabled(true);
//            }
//
//            // OPTIONAL map update in background (non-blocking)
//            if (googleMap != null ) {
//                backgroundExecutor.execute(() -> {
//                    try {
//                        mainHandler.post(() -> {
//                            try {
//                                if (googleMap != null && isAdded()) {
//                                    googleMap.clear();
//                                    googleMap.addMarker(new MarkerOptions().position(latLng).title("Selected"));
//                                }
//                            } catch (Exception e) {
//                                Log.d(TAG, "Minor map update error", e);
//                                // Don't show error - this is optional visual update
//                            }
//                        });
//                    } catch (Exception e) {
//                        Log.d(TAG, "Background map update error", e);
//                    }
//                });
//            }
//
//        } catch (Exception e) {
//            Log.e(TAG, "Error in updateSelectedLocation", e);
//        }
//    }

    private void searchPlaces(String query) {
        if (placesClient == null) return;

        // Move search API call to background thread
        backgroundExecutor.execute(() -> {
            try {
                FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                        .setLocationBias(RectangularBounds.newInstance(
                                new LatLng(8.0, 102.0),  // Vietnam bounds
                                new LatLng(24.0, 110.0)
                        ))
                        .setCountries("VN")
                        .setSessionToken(sessionToken)
                        .setQuery(query)
                        .build();

                placesClient.findAutocompletePredictions(request)
                        .addOnSuccessListener(response -> {
                            // Handle response on main thread
                            mainHandler.post(() -> {
                                if (isAdded() && binding != null) {
                                    try {
                                        List<AutocompletePrediction> predictions = response.getAutocompletePredictions();
                                        predictionAdapter.updatePredictions(predictions);
                                        binding.rvAddressSuggestions.setVisibility(predictions.isEmpty() ? View.GONE : View.VISIBLE);
                                    } catch (Exception e) {
                                        Log.e(TAG, "Error processing predictions", e);
                                    }
                                }
                            });
                        })
                        .addOnFailureListener(exception -> {
                            Log.e(TAG, "Place prediction error", exception);
                            mainHandler.post(() -> {
                                if (binding != null) {
                                    binding.rvAddressSuggestions.setVisibility(View.GONE);
                                }
                            });
                        });
            } catch (Exception e) {
                Log.e(TAG, "Search places error", e);
            }
        });
    }

    private void getPlaceDetails(String placeId) {
        if (placesClient == null) return;

        // Move place details API call to background thread
        backgroundExecutor.execute(() -> {
            try {
                List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG, Place.Field.ADDRESS);
                com.google.android.libraries.places.api.net.FetchPlaceRequest request = 
                        com.google.android.libraries.places.api.net.FetchPlaceRequest.newInstance(placeId, placeFields);

                placesClient.fetchPlace(request)
                        .addOnSuccessListener(response -> {
                            // Handle response on main thread
                            mainHandler.post(() -> {
                                if (isAdded() && binding != null) {
                                    try {
                                        Place place = response.getPlace();
                                        if (place.getLatLng() != null) {
                                            LatLng latLng = place.getLatLng();
                                            String address = place.getAddress() != null ? place.getAddress() : "Selected location";
                                            updateSelectedLocation(latLng, address);
                                            
                                            // Move camera to selected place
                                            if (googleMap != null) {
                                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                                            }
                                        }
                                    } catch (Exception e) {
                                        Log.e(TAG, "Error processing place details", e);
                                    }
                                }
                            });
                        })
                        .addOnFailureListener(exception -> {
                            Log.e(TAG, "Place details error", exception);
                            mainHandler.post(() -> {
                                if (isAdded()) {
                                    showToast("Failed to get location details");
                                }
                            });
                        });
            } catch (Exception e) {
                Log.e(TAG, "Get place details error", e);
            }
        });
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        binding.progressMap.setVisibility(View.VISIBLE);
        
        // Move location request to background thread
        backgroundExecutor.execute(() -> {
            try {
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(location -> {
                            // Handle response on main thread
                            mainHandler.post(() -> {
                                if (isAdded() && binding != null) {
                                    binding.progressMap.setVisibility(View.GONE);
                                    if (location != null) {
                                        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                        updateSelectedLocation(currentLatLng, "Current location");
                                        if (googleMap != null) {
                                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                                        }
                                    } else {
                                        showToast("Unable to get current location");
                                    }
                                }
                            });
                        })
                        .addOnFailureListener(e -> {
                            // Handle error on main thread
                            mainHandler.post(() -> {
                                if (isAdded() && binding != null) {
                                    binding.progressMap.setVisibility(View.GONE);
                                    showToast("Failed to get current location");
                                }
                            });
                        });
            } catch (Exception e) {
                Log.e(TAG, "Error getting current location", e);
                mainHandler.post(() -> {
                    if (binding != null) {
                        binding.progressMap.setVisibility(View.GONE);
                    }
                });
            }
        });
    }
    private void confirmSelectedAddress() {
        if (binding == null || !isAdded()) return;

        try {
            // Immediate UI feedback (minimal)
            binding.btnConfirmAddress.setEnabled(false);
            binding.btnConfirmAddress.setText("Saving...");

            // Cache values immediately
            final String cachedAddress = selectedAddress != null ? selectedAddress : "Ho Chi Minh City, Vietnam";
            final double cachedLat = selectedLatitude;
            final double cachedLng = selectedLongitude;

            // Offload all work to background thread
            backgroundExecutor.execute(() -> {
                try {
                    // Update ViewModel
                    bookingViewModel.setServiceAddress(cachedAddress);
                    bookingViewModel.setAddressLatitude(cachedLat);
                    bookingViewModel.setAddressLongitude(cachedLng);

                    // Hide map and update UI on main thread
                    mainHandler.post(() -> {
                        if (!isAdded() || binding == null) return;

                        // Complete UI updates
                        binding.btnConfirmAddress.setText("✓ Saved");
                        binding.tvSelectedAddressDisplay.setText("📍 " + cachedAddress);
                        hideMapAndShowSummary();

                        showToast("Address saved successfully!");
                    });
                } catch (Exception e) {
                    Log.e(TAG, "Error in confirmSelectedAddress background task", e);
                    mainHandler.post(() -> {
                        if (isAdded() && binding != null) {
                            binding.btnConfirmAddress.setEnabled(true);
                            binding.btnConfirmAddress.setText("Try Again");
                            showToast("Failed to save address");
                        }
                    });
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Critical error in confirmSelectedAddress", e);
            binding.btnConfirmAddress.setEnabled(true);
            binding.btnConfirmAddress.setText("Error");
            showToast("Error saving address");
        }
    }
//    private void confirmSelectedAddress() {
//        // ULTRA-LIGHTWEIGHT: Immediate UI feedback with minimal processing
//        try {
//            if (binding == null || !isAdded()) return;
//
//            // Immediate UI changes - no heavy operations
//            binding.btnConfirmAddress.setEnabled(false);
//            binding.btnConfirmAddress.setText("✓ Saved");
//
//            // HIDE MAP to eliminate expensive rendering operations
//            hideMapAndShowSummary();
//
//            // Cache values immediately (no processing)
//            final String cachedAddress = selectedAddress != null ? selectedAddress : "Ho Chi Minh City, Vietnam";
//            final double cachedLat = selectedLatitude;
//            final double cachedLng = selectedLongitude;
//
//            // Show immediate success feedback
//            showToast("Address saved! Map hidden to improve performance.");
//
//            // MINIMAL background operation with aggressive timeout
//            backgroundExecutor.execute(() -> {
//                try {
//                    // Minimal delay to prevent UI blocking
//                    Thread.sleep(50); // Very short delay
//
//                    // Ultra-lightweight ViewModel update
//                    mainHandler.post(() -> {
//                        try {
//                            if (isAdded() && bookingViewModel != null) {
//                                // Direct value setting with no side effects
//                                bookingViewModel.setServiceAddress(cachedAddress);
//                                bookingViewModel.setAddressLatitude(cachedLat);
//                                bookingViewModel.setAddressLongitude(cachedLng);
//                            }
//                        } catch (Exception e) {
//                            Log.e(TAG, "Minor error updating ViewModel", e);
//                            // Don't show error to user - address is already "saved" from UI perspective
//                        }
//                    });
//                } catch (Exception e) {
//                    Log.e(TAG, "Background address confirmation error", e);
//                    // Don't update UI - user already sees success
//                }
//            });
//
//        } catch (Exception e) {
//            Log.e(TAG, "Critical error in confirmSelectedAddress", e);
//            // Fallback: at minimum show user something happened
//            if (binding != null) {
//                binding.btnConfirmAddress.setText("Error");
//            }
//        }
//    }

    private void hideMapAndShowSummary() {
        try {
            if (binding == null || isMapHidden) return;
            
            // Hide the expensive map rendering
            binding.mapView.setVisibility(View.GONE);
            binding.progressMap.setVisibility(View.GONE);
            binding.tvMapStatus.setVisibility(View.GONE);
            
            // Destroy map to free resources
            if (mapView != null) {
                backgroundExecutor.execute(() -> {
                    try {
                        mainHandler.post(() -> {
                            try {
                                if (mapView != null) {
                                    mapView.onPause();
                                    mapView.onDestroy();
                                }
                                googleMap = null;
                            } catch (Exception e) {
                                Log.d(TAG, "Error destroying map", e);
                            }
                        });
                    } catch (Exception e) {
                        Log.d(TAG, "Background map destruction error", e);
                    }
                });
            }
            
            // Show lightweight summary instead
            binding.tvSelectedAddressDisplay.setVisibility(View.VISIBLE);
            binding.tvSelectedAddressDisplay.setText("📍 " + selectedAddress + "\n✅ Location confirmed - map hidden for better performance");
            binding.tvSelectedAddressDisplay.setBackgroundResource(android.R.color.transparent);
            binding.tvSelectedAddressDisplay.setPadding(16, 16, 16, 16);
            
            // Show change location button
            binding.btnChangeLocation.setVisibility(View.VISIBLE);
            
            // Update button to show location is locked
            binding.btnConfirmAddress.setText("✓ Location Set");
            binding.btnCurrentLocation.setEnabled(false);
            binding.btnCurrentLocation.setText("📍 Saved");
            
            // Disable search to prevent re-enabling map
            binding.etSearchAddress.setEnabled(false);
            binding.etSearchAddress.setHint("Location confirmed - search disabled");
            
            isMapHidden = true;
            
            Log.d(TAG, "Map hidden successfully - performance should improve dramatically");
            
        } catch (Exception e) {
            Log.e(TAG, "Error hiding map", e);
        }
    }

    // Add method to re-enable map if user wants to change location
    private void showMapForLocationChange() {
        try {
            if (binding == null || !isMapHidden) return;
            
            binding.mapView.setVisibility(View.VISIBLE);
            binding.tvMapStatus.setVisibility(View.VISIBLE);
            binding.tvMapStatus.setText("Map reloading...");
            
            // Hide change location button
            binding.btnChangeLocation.setVisibility(View.GONE);
            
            // Re-enable controls
            binding.btnCurrentLocation.setEnabled(true);
            binding.btnCurrentLocation.setText("📍 Current");
            binding.btnConfirmAddress.setText("✓ Use This");
            binding.btnConfirmAddress.setEnabled(true);
            binding.etSearchAddress.setEnabled(true);
            binding.etSearchAddress.setHint("Search address");
            
            // Reset address display
            binding.tvSelectedAddressDisplay.setText("Tap on map to select location");
            
            // Reinitialize map
            setupMap();
            
            isMapHidden = false;
            
            showToast("Map reloaded - you can select a new location");
            
        } catch (Exception e) {
            Log.e(TAG, "Error showing map", e);
        }
    }
    
    private void setupStaffSelection() {
        // Initialize staff adapter
        staffSelectionAdapter = new StaffSelectionAdapter();
        staffSelectionAdapter.setOnStaffClickListener((staff, position) -> {
            selectedStaff = staff;
            staffSelectionAdapter.setSelectedPosition(position);
            updateSelectedStaffDisplay();
            
            // Save to ViewModel
            bookingViewModel.setSelectedStaff(staff);
        });
        
        staffSelectionAdapter.setOnStaffProfileClickListener(staff -> {
            // Open staff profile fragment
            openStaffProfile(staff);
        });
        
        // Setup RecyclerView
        binding.rvStaffSelection.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(requireContext()));
        binding.rvStaffSelection.setAdapter(staffSelectionAdapter);
        
        // Initially hidden - will show when time slot is selected
        binding.layoutStaffSelection.setVisibility(View.GONE);
    }
    
    private void openStaffProfile(StaffAvailabilityResponse staff) {
        try {
            // Create staff profile fragment
            StaffProfileFragment staffProfileFragment = StaffProfileFragment.newInstance(staff);
            
            // Navigate to staff profile
            getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, staffProfileFragment)
                .addToBackStack("staff_profile")
                .commit();
                
        } catch (Exception e) {
            Log.e(TAG, "Error opening staff profile", e);
            showToast("Unable to view staff profile");
        }
    }
    
    private void updateSelectedStaffDisplay() {
        if (selectedStaff != null) {
            String displayText = String.format("✓ %s - %s", 
                selectedStaff.getStaffName()
            );
            binding.tvSelectedStaffDisplay.setText(displayText);
            binding.tvSelectedStaffDisplay.setVisibility(View.VISIBLE);
        } else {
            binding.tvSelectedStaffDisplay.setText("No staff selected");
            binding.tvSelectedStaffDisplay.setVisibility(View.GONE);
        }
    }
    
    private void loadAvailableStaff(String date, String selectedTime, Integer serviceId) {
        if (date == null || selectedTime == null) {
            Log.d(TAG, "Date or time not selected, hiding staff selection");
            binding.layoutStaffSelection.setVisibility(View.GONE);
            return;
        }
        
        // Show loading state
        binding.layoutStaffSelection.setVisibility(View.VISIBLE);
        binding.progressStaffLoading.setVisibility(View.VISIBLE);
        binding.rvStaffSelection.setVisibility(View.GONE);
        binding.tvNoStaffAvailable.setVisibility(View.GONE);
        
        // Parse time slot to get start and end times
        String[] timeParts = parseTimeSlot(selectedTime);
        if (timeParts == null) {
            showStaffError("Invalid time format");
            return;
        }
        
        String startTime = timeParts[0];
        String endTime = timeParts[1];
        
        // Load staff using TimeSlotApiService
        backgroundExecutor.execute(() -> {
            try {
                TimeSlotApiService timeSlotApiService = RetrofitClient.getTimeSlotApiService();
                Call<ApiResponse<List<StaffAvailabilityResponse>>> call = timeSlotApiService.getAvailableStaffForSlot(
                    date, startTime, endTime, serviceId
                );
                
                Response<ApiResponse<List<StaffAvailabilityResponse>>> response = call.execute();
                
                mainHandler.post(() -> {
                    if (!isAdded()) return;
                    
                    binding.progressStaffLoading.setVisibility(View.GONE);
                    
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<List<StaffAvailabilityResponse>> apiResponse = response.body();
                        if (apiResponse.isSucceeded() && apiResponse.getData() != null) {
                            displayAvailableStaff(apiResponse.getData());
                        } else {
                        }
                    } else {
                        showStaffError("Failed to load staff availability");
                    }
                });
                
            } catch (Exception e) {
                Log.e(TAG, "Error loading staff", e);
                mainHandler.post(() -> {
                    if (isAdded()) {
                        binding.progressStaffLoading.setVisibility(View.GONE);
                        showStaffError("Network error: " + e.getMessage());
                    }
                });
            }
        });
    }
    
    private String[] parseTimeSlot(String timeSlot) {
        try {
            // Handle formats like "09:00 AM" or "09:00 AM - 11:00 AM"
            if (timeSlot.contains(" - ")) {
                // Already has start and end time
                String[] parts = timeSlot.split(" - ");
                return new String[]{
                    convertTo24Hour(parts[0].trim()),
                    convertTo24Hour(parts[1].trim())
                };
            } else {
                // Single time, assume 2-hour duration
                String startTime24 = convertTo24Hour(timeSlot.trim());
                String endTime24 = addHours(startTime24, 2);
                return new String[]{startTime24, endTime24};
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing time slot: " + timeSlot, e);
            return null;
        }
    }
    
    private String convertTo24Hour(String time12Hour) {
        try {
            SimpleDateFormat input = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            SimpleDateFormat output = new SimpleDateFormat("HH:mm", Locale.getDefault());
            return output.format(input.parse(time12Hour));
        } catch (Exception e) {
            Log.e(TAG, "Error converting time: " + time12Hour, e);
            return time12Hour; // Return as-is if conversion fails
        }
    }
    
    private String addHours(String time24Hour, int hours) {
        try {
            String[] parts = time24Hour.split(":");
            int hour = Integer.parseInt(parts[0]) + hours;
            int minute = Integer.parseInt(parts[1]);
            
            // Handle day overflow
            if (hour >= 24) {
                hour = hour - 24;
            }
            
            return String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
        } catch (Exception e) {
            Log.e(TAG, "Error adding hours to time: " + time24Hour, e);
            return time24Hour;
        }
    }
    
    private void displayAvailableStaff(List<StaffAvailabilityResponse> staffList) {
        availableStaffList.clear();
        availableStaffList.addAll(staffList);
        
        if (staffList.isEmpty()) {
            binding.rvStaffSelection.setVisibility(View.GONE);
            binding.tvNoStaffAvailable.setVisibility(View.VISIBLE);
        } else {
            staffSelectionAdapter.setStaffList(staffList);
            binding.rvStaffSelection.setVisibility(View.VISIBLE);
            binding.tvNoStaffAvailable.setVisibility(View.GONE);
            
            showToast(String.format("Found %d available staff members", staffList.size()));
        }
    }
    
    private void showStaffError(String message) {
        binding.rvStaffSelection.setVisibility(View.GONE);
        binding.tvNoStaffAvailable.setText(message);
        binding.tvNoStaffAvailable.setVisibility(View.VISIBLE);
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showPaymentProcessingOverlay() {
        if (binding != null) {
            binding.paymentProcessingOverlay.setVisibility(View.VISIBLE);
            binding.paymentStatusText.setText("Processing payment...");
            binding.paymentSubStatusText.setText("Please wait while we process your payment");
            
            // Add fade-in animation
            binding.paymentProcessingOverlay.setAlpha(0f);
            binding.paymentProcessingOverlay.animate()
                .alpha(1f)
                .setDuration(300)
                .start();
        }
    }
    
    private void hidePaymentProcessingOverlay() {
        if (binding != null && binding.paymentProcessingOverlay.getVisibility() == View.VISIBLE) {
            // Add fade-out animation
            binding.paymentProcessingOverlay.animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction(() -> {
                    if (binding != null) {
                        binding.paymentProcessingOverlay.setVisibility(View.GONE);
                    }
                })
                .start();
        }
    }
    
    private void updatePaymentProcessingStatus(String status, String subStatus) {
        if (binding != null) {
            binding.paymentStatusText.setText(status);
            binding.paymentSubStatusText.setText(subStatus);
        }
    }
} 