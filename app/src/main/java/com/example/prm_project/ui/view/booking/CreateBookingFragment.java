package com.example.prm_project.ui.view.booking;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.prm_project.R;
import com.example.prm_project.databinding.FragmentCreateBookingBinding;
import com.example.prm_project.data.model.*;
import com.example.prm_project.ui.viewmodel.BookingViewModel;
import com.example.prm_project.ui.viewmodel.TimeSlotViewModel;
import dagger.hilt.android.AndroidEntryPoint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Arrays;

@AndroidEntryPoint
public class CreateBookingFragment extends Fragment {
    
    // DEMO MODE: Date/time selection view visible but functionality disabled due to ANR issues
    // Shows complete UI flow but uses default values: Tomorrow at 10:00 AM
    
    private FragmentCreateBookingBinding binding;
    private BookingViewModel bookingViewModel;
    private TimeSlotViewModel timeSlotViewModel;
    private List<Service> servicesList = new ArrayList<>();
    private List<ServicePackage> packagesList = new ArrayList<>();
    private List<String> timeSlotsList = new ArrayList<>();
    
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
        binding.btnSelectDate.setOnClickListener(v -> showDatePicker());

        binding.btnSelectDate.setEnabled(true);
        binding.btnSelectDate.setText("📅 Select Date");
        
        // Initialize with tomorrow's date
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String tomorrowDate = dateFormat.format(tomorrow.getTime());
        bookingViewModel.setSelectedDate(tomorrowDate);
        binding.tvSelectedDate.setText("Tomorrow at 10:00 AM");
        
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
        
        // Payment methods
        setupPaymentMethodSpinner();
        
        // Address coordinates (mock values for demo)
        binding.etAddress.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && !binding.etAddress.getText().toString().trim().isEmpty()) {
                // Mock coordinates for demo - in real app, use geocoding
                bookingViewModel.setAddressLatitude(10.762622);
                bookingViewModel.setAddressLongitude(106.660172);
            }
        });
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
        timeSlotViewModel.getAvailableSlots().observe(getViewLifecycleOwner(), timeSlots -> {
            if (timeSlots != null && !timeSlots.isEmpty()) {
                timeSlotsList.clear();
                for (TimeSlotDto slot : timeSlots) {
                    if (slot.isAvailable()) {
                        timeSlotsList.add(slot.getDisplayTime());
                    }
                }
                updateTimeSlotSpinner();
                showToast("Loaded " + timeSlotsList.size() + " available time slots");
            } else {
                // Fallback to default time slots if no data from API
                timeSlotsList.clear();
                timeSlotsList.addAll(Arrays.asList(
                    "10:00 AM", "08:00 AM", "09:00 AM", "11:00 AM", 
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
                    SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                    String displayDate = displayFormat.format(inputFormat.parse(date));
                    binding.tvSelectedDate.setText(displayDate);
                } catch (Exception e) {
                    binding.tvSelectedDate.setText(date);
                }
            }
        });
        
        // Observe form data for review step
        observeFormDataForReview();
        
        // Observe loading state - Enhanced with UI blocking
        bookingViewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            
            // Disable UI interactions during loading
            binding.btnNext.setEnabled(!isLoading);
            binding.btnPrevious.setEnabled(!isLoading);
            binding.btnSelectDate.setEnabled(!isLoading);
            
            // Disable recycler views during loading
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
                showToast("Processing payment...");
            }
        });
        
        // Observe payment status
        bookingViewModel.getPaymentStatus().observe(getViewLifecycleOwner(), status -> {
            if (status != null) {
                showToast(status);
                if (status.contains("Payment successful") || status.contains("Cash payment")) {
                    // Navigate back after successful payment
                    Navigation.findNavController(requireView()).navigateUp();
                }
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
        // Reset all step indicators
        binding.step1.setBackgroundResource(R.drawable.step_inactive);
        binding.step2.setBackgroundResource(R.drawable.step_inactive);
        binding.step3.setBackgroundResource(R.drawable.step_inactive);
        binding.step4.setBackgroundResource(R.drawable.step_inactive);
        binding.step5.setBackgroundResource(R.drawable.step_inactive);
        binding.step6.setBackgroundResource(R.drawable.step_inactive);
        
        // Set active step
        int stepNumber = step.ordinal() + 1;
        switch (stepNumber) {
            case 1: binding.step1.setBackgroundResource(R.drawable.step_active); break;
            case 2: binding.step2.setBackgroundResource(R.drawable.step_active); break;
            case 3: binding.step3.setBackgroundResource(R.drawable.step_active); break;
            case 4: binding.step4.setBackgroundResource(R.drawable.step_active); break;
            case 5: binding.step5.setBackgroundResource(R.drawable.step_active); break;
            case 6: binding.step6.setBackgroundResource(R.drawable.step_active); break;
        }
    }
    
    private void showDatePicker() {
        // Prevent multiple date picker dialogs
        if (bookingViewModel.getLoading().getValue() == Boolean.TRUE) {
            return;
        }
        
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            requireContext(),
            (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                
                // Check if selected date is not in the past
                if (calendar.getTime().before(new Date())) {
                    Toast.makeText(requireContext(), "Cannot select past dates", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String selectedDate = dateFormat.format(calendar.getTime());
                
                // Format date for display
                SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                String displayDate = displayFormat.format(calendar.getTime());
                
                // Set address coordinates before setting date (needed for time slots loading)
                bookingViewModel.setServiceAddress(binding.etAddress.getText().toString());
                bookingViewModel.setAddressLatitude(10.762622); // Mock coordinates
                bookingViewModel.setAddressLongitude(106.660172);
                
                // Set the date - this will trigger time slots loading in ViewModel
                bookingViewModel.setSelectedDate(selectedDate);
                
                // Update UI to show selected date
                binding.tvSelectedDate.setText(displayDate);
                
                // Load real time slots from API
                Service selectedService = bookingViewModel.getSelectedService().getValue();
                if (selectedService != null) {
                    timeSlotViewModel.loadAvailableSlots(selectedDate, selectedService.getId(), null);
                } else {
                    // If no service selected, load time slots without service filter
                    timeSlotViewModel.loadAvailableSlots(selectedDate, null, null);
                }
                
                // Show success message
                Toast.makeText(requireContext(), "Date selected: " + displayDate + "\nLoading time slots...", Toast.LENGTH_SHORT).show();
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        
        // Set minimum date to today
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        
        // Set title
        datePickerDialog.setTitle("Select Booking Date");
        
        datePickerDialog.show();
    }


    private void handleDateSelection(int year, int month, int dayOfMonth) {
        if (!isAdded() || getContext() == null) {
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);

        // Check if selected date is not in the past
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        if (calendar.before(today)) {
            Toast.makeText(getContext(), "Cannot select past dates", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String selectedDate = dateFormat.format(calendar.getTime());

        // Format date for display
        SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        String displayDate = displayFormat.format(calendar.getTime());

        // Set address coordinates before setting date (needed for time slots loading)
        if (binding != null && binding.etAddress != null) {
            bookingViewModel.setServiceAddress(binding.etAddress.getText().toString());
        }
        bookingViewModel.setAddressLatitude(10.762622); // Mock coordinates
        bookingViewModel.setAddressLongitude(106.660172);

        // Update ViewModel and UI
        bookingViewModel.setSelectedDate(selectedDate);

        if (binding != null && binding.tvSelectedDate != null) {
            binding.tvSelectedDate.setText(displayDate);
        }

        // Load time slots
        Service selectedService = bookingViewModel.getSelectedService().getValue();
        if (selectedService != null) {
            timeSlotViewModel.loadAvailableSlots(selectedDate, selectedService.getId(), null);
        } else {
            timeSlotViewModel.loadAvailableSlots(selectedDate, null, null);
        }

        Toast.makeText(getContext(), "Date selected: " + displayDate + "\nLoading time slots...", Toast.LENGTH_SHORT).show();
    }
    private void setupTimeSlotSpinner() {
        // Initialize with default time slots (fallback)
        List<String> defaultTimeSlots = Arrays.asList(
            "10:00 AM", "08:00 AM", "09:00 AM", "11:00 AM", 
            "01:00 PM", "02:00 PM", "03:00 PM", "04:00 PM"
        );
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            defaultTimeSlots
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerTimeSlot.setAdapter(adapter);
        binding.spinnerTimeSlot.setSelection(0); // Select the first time
        binding.spinnerTimeSlot.setEnabled(true); // Enable for real use
        
        // Set default time
        bookingViewModel.setSelectedTime("10:00 AM");
        
        // Load initial time slots for tomorrow
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String tomorrowDate = dateFormat.format(tomorrow.getTime());
        
        // Load time slots for tomorrow
        timeSlotViewModel.loadAvailableSlots(tomorrowDate, null, null);
    }
    
    private void updateTimeSlotSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            timeSlotsList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerTimeSlot.setAdapter(adapter);
        
        binding.spinnerTimeSlot.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < timeSlotsList.size()) {
                    bookingViewModel.setSelectedTime(timeSlotsList.get(position));
                }
            }
            
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
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
        bookingViewModel.processPayment(requireContext());
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
            bookingViewModel.handleVNPayPaymentResult(requireContext(), requestCode, resultCode, data);
        }
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Cancel any ongoing API calls to prevent memory leaks
        if (bookingViewModel != null) {
            bookingViewModel.cancelOngoingCalls();
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 