package com.example.prm_project.ui.view.booking;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.prm_project.R;
import com.example.prm_project.databinding.FragmentCreateBookingBinding;
import com.example.prm_project.data.model.*;
import com.example.prm_project.ui.view.adapters.TimeSlotAdapter;
import com.example.prm_project.ui.view.dialogs.StaffSelectionDialog;
import com.example.prm_project.ui.viewmodel.BookingViewModel;
import com.example.prm_project.ui.viewmodel.TimeSlotViewModel;
import com.example.prm_project.utils.DateTimeUtils;
import dagger.hilt.android.AndroidEntryPoint;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@AndroidEntryPoint
public class CreateBookingFragmentEnhanced extends Fragment {

    private static final String TAG = "CreateBookingFragment";
    
    private FragmentCreateBookingBinding binding;
    private BookingViewModel bookingViewModel;
    private TimeSlotViewModel timeSlotViewModel;
    
    // UI Components
    private CalendarView calendarView;
    private TimeSlotAdapter timeSlotAdapter;
    private StaffSelectionDialog staffSelectionDialog;
    
    // Data
    private List<String> availableTimeSlots = new ArrayList<>();
    private List<StaffAvailabilityResponse> availableStaff = new ArrayList<>();
    private String selectedDate;
    private String selectedTimeSlot;
    private StaffAvailabilityResponse selectedStaff;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCreateBookingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViewModels();
        initDateTimeStaffComponents();
        setupObservers();
        setupClickListeners();
    }

    private void initViewModels() {
        bookingViewModel = new ViewModelProvider(this).get(BookingViewModel.class);
        timeSlotViewModel = new ViewModelProvider(this).get(TimeSlotViewModel.class);
    }

    private void initDateTimeStaffComponents() {
        // Find the calendar view
        calendarView = binding.getRoot().findViewById(R.id.calendar_view);
        
        // Setup horizontal time slots RecyclerView
        setupTimeSlotRecyclerView();
        
        // Hide sections initially (show only calendar first)
        hideTimeSlotSection();
        hideStaffSection();
    }

    private void setupTimeSlotRecyclerView() {
        timeSlotAdapter = new TimeSlotAdapter(requireContext());
        
        // Find the RecyclerView in the improved layout
        View improvedLayout = binding.getRoot().findViewById(R.id.rv_time_slots);
        if (improvedLayout instanceof androidx.recyclerview.widget.RecyclerView) {
            androidx.recyclerview.widget.RecyclerView rvTimeSlots = 
                (androidx.recyclerview.widget.RecyclerView) improvedLayout;
            
            rvTimeSlots.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            );
            rvTimeSlots.setAdapter(timeSlotAdapter);
            
            timeSlotAdapter.setOnTimeSlotClickListener((timeSlot, position) -> {
                selectedTimeSlot = timeSlot;
                bookingViewModel.setSelectedTime(timeSlot);
                
                // Load available staff for selected date and time
                loadAvailableStaff();
                
                // Show staff selection section
                showStaffSection();
                
                Toast.makeText(requireContext(), 
                    "Selected: " + timeSlot, Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void setupObservers() {
        // Observe available time slots
        timeSlotViewModel.getAvailableTimeSlots().observe(getViewLifecycleOwner(), timeSlots -> {
            if (timeSlots != null) {
                availableTimeSlots.clear();
                availableTimeSlots.addAll(timeSlots);
                timeSlotAdapter.setTimeSlots(timeSlots);
                
                if (!timeSlots.isEmpty()) {
                    showTimeSlotSection();
                    hideTimeSlotsLoading();
                } else {
                    showNoTimeSlotsAvailable();
                }
            }
        });

        // Observe loading state
        timeSlotViewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                showTimeSlotsLoading();
            } else {
                hideTimeSlotsLoading();
            }
        });

        // Observe errors
        timeSlotViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
                hideTimeSlotSection();
            }
        });

        // Observe selected staff from ViewModel
        bookingViewModel.getSelectedStaff().observe(getViewLifecycleOwner(), staff -> {
            if (staff != null) {
                selectedStaff = staff;
                updateSelectedStaffDisplay();
            }
        });
    }

    private void setupClickListeners() {
        // Calendar date selection
        if (calendarView != null) {
            calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                
                selectedDate = DateTimeUtils.formatDateForApi(calendar.getTime());
                String displayDate = DateTimeUtils.formatDisplayDate(calendar.getTime());
                
                // Update selected date display
                updateSelectedDateDisplay(displayDate);
                
                // Store in ViewModel
                bookingViewModel.setSelectedDate(selectedDate);
                
                // Load time slots for selected date
                loadTimeSlots();
                
                // Hide staff section until time is selected
                hideStaffSection();
                
                Toast.makeText(requireContext(), 
                    "Selected date: " + displayDate, Toast.LENGTH_SHORT).show();
            });
        }

        // Staff picker button
        View btnPickStaff = binding.getRoot().findViewById(R.id.btn_pick_staff);
        if (btnPickStaff != null) {
            btnPickStaff.setOnClickListener(v -> showStaffSelectionDialog());
        }
    }

    private void loadTimeSlots() {
        if (selectedDate != null) {
            // Get the selected service for time slot filtering
            Service selectedService = bookingViewModel.getSelectedService().getValue();
            if (selectedService != null) {
                timeSlotViewModel.loadAvailableTimeSlots(selectedDate, selectedService.getId());
            }
        }
    }

    private void loadAvailableStaff() {
        if (selectedDate != null && selectedTimeSlot != null) {
            // Show loading state
            showStaffLoading();
            
            // Load staff (this would typically call an API)
            // For now, we'll simulate with mock data
            simulateStaffLoading();
        }
    }

    private void simulateStaffLoading() {
        // Simulate API call delay
        new android.os.Handler().postDelayed(() -> {
            // Mock staff data
            availableStaff.clear();
            availableStaff.add(createMockStaff("John Smith", 4.8f, "Cleaning Specialist", 5));
            availableStaff.add(createMockStaff("Sarah Johnson", 4.9f, "House Maintenance", 8));
            availableStaff.add(createMockStaff("Mike Wilson", 4.6f, "General Service", 3));
            
            hideStaffLoading();
        }, 1000);
    }

    private StaffAvailabilityResponse createMockStaff(String name, float rating, 
                                                     String specialization, int experience) {
        StaffAvailabilityResponse staff = new StaffAvailabilityResponse();
        staff.setFullName(name);
        staff.setAverageRating(rating);
        staff.setSpecialization(specialization);
        staff.setExperienceYears(experience);
        return staff;
    }

    private void showStaffSelectionDialog() {
        if (availableStaff.isEmpty()) {
            Toast.makeText(requireContext(), 
                "Please select a date and time first", Toast.LENGTH_SHORT).show();
            return;
        }

        staffSelectionDialog = new StaffSelectionDialog(requireContext(), availableStaff);
        staffSelectionDialog.setOnStaffSelectedListener(staff -> {
            selectedStaff = staff;
            bookingViewModel.setSelectedStaff(staff);
            updateSelectedStaffDisplay();
            
            Toast.makeText(requireContext(), 
                "Selected staff: " + staff.getFullName(), Toast.LENGTH_SHORT).show();
        });
        staffSelectionDialog.show();
    }

    private void updateSelectedDateDisplay(String displayDate) {
        View tvSelectedDate = binding.getRoot().findViewById(R.id.tv_selected_date_display);
        if (tvSelectedDate instanceof android.widget.TextView) {
            ((android.widget.TextView) tvSelectedDate).setText(displayDate);
        }
    }

    private void updateSelectedStaffDisplay() {
        if (selectedStaff != null) {
            // Show selected staff layout
            View layoutSelectedStaff = binding.getRoot().findViewById(R.id.layout_selected_staff);
            if (layoutSelectedStaff != null) {
                layoutSelectedStaff.setVisibility(View.VISIBLE);
            }

            // Update staff name
            View tvStaffName = binding.getRoot().findViewById(R.id.tv_selected_staff_name);
            if (tvStaffName instanceof android.widget.TextView) {
                ((android.widget.TextView) tvStaffName).setText(selectedStaff.getFullName());
            }

            // Update staff rating
            View tvStaffRating = binding.getRoot().findViewById(R.id.tv_selected_staff_rating);
            if (tvStaffRating instanceof android.widget.TextView) {
                String ratingText = String.format("⭐ %.1f rating", selectedStaff.getAverageRating());
                ((android.widget.TextView) tvStaffRating).setText(ratingText);
            }

            // Update button text
            View btnPickStaff = binding.getRoot().findViewById(R.id.btn_pick_staff);
            if (btnPickStaff instanceof com.google.android.material.button.MaterialButton) {
                ((com.google.android.material.button.MaterialButton) btnPickStaff)
                    .setText("Change Staff Member");
            }
        }
    }

    // UI State Management Methods
    private void showTimeSlotSection() {
        View cardTimeSlots = binding.getRoot().findViewById(R.id.card_time_slots);
        if (cardTimeSlots != null) {
            cardTimeSlots.setVisibility(View.VISIBLE);
        }
    }

    private void hideTimeSlotSection() {
        View cardTimeSlots = binding.getRoot().findViewById(R.id.card_time_slots);
        if (cardTimeSlots != null) {
            cardTimeSlots.setVisibility(View.GONE);
        }
    }

    private void showStaffSection() {
        View cardStaffSelection = binding.getRoot().findViewById(R.id.card_staff_selection);
        if (cardStaffSelection != null) {
            cardStaffSelection.setVisibility(View.VISIBLE);
        }
    }

    private void hideStaffSection() {
        View cardStaffSelection = binding.getRoot().findViewById(R.id.card_staff_selection);
        if (cardStaffSelection != null) {
            cardStaffSelection.setVisibility(View.GONE);
        }
    }

    private void showTimeSlotsLoading() {
        View loadingLayout = binding.getRoot().findViewById(R.id.layout_timeslots_loading);
        if (loadingLayout != null) {
            loadingLayout.setVisibility(View.VISIBLE);
        }
        
        View rvTimeSlots = binding.getRoot().findViewById(R.id.rv_time_slots);
        if (rvTimeSlots != null) {
            rvTimeSlots.setVisibility(View.GONE);
        }
    }

    private void hideTimeSlotsLoading() {
        View loadingLayout = binding.getRoot().findViewById(R.id.layout_timeslots_loading);
        if (loadingLayout != null) {
            loadingLayout.setVisibility(View.GONE);
        }
        
        View rvTimeSlots = binding.getRoot().findViewById(R.id.rv_time_slots);
        if (rvTimeSlots != null) {
            rvTimeSlots.setVisibility(View.VISIBLE);
        }
    }

    private void showNoTimeSlotsAvailable() {
        View noTimeSlotsLayout = binding.getRoot().findViewById(R.id.layout_no_timeslots);
        if (noTimeSlotsLayout != null) {
            noTimeSlotsLayout.setVisibility(View.VISIBLE);
        }
        
        View rvTimeSlots = binding.getRoot().findViewById(R.id.rv_time_slots);
        if (rvTimeSlots != null) {
            rvTimeSlots.setVisibility(View.GONE);
        }
    }

    private void showStaffLoading() {
        View staffLoadingLayout = binding.getRoot().findViewById(R.id.layout_staff_loading);
        if (staffLoadingLayout != null) {
            staffLoadingLayout.setVisibility(View.VISIBLE);
        }
        
        View btnPickStaff = binding.getRoot().findViewById(R.id.btn_pick_staff);
        if (btnPickStaff != null) {
            btnPickStaff.setVisibility(View.GONE);
        }
    }

    private void hideStaffLoading() {
        View staffLoadingLayout = binding.getRoot().findViewById(R.id.layout_staff_loading);
        if (staffLoadingLayout != null) {
            staffLoadingLayout.setVisibility(View.GONE);
        }
        
        View btnPickStaff = binding.getRoot().findViewById(R.id.btn_pick_staff);
        if (btnPickStaff != null) {
            btnPickStaff.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (staffSelectionDialog != null && staffSelectionDialog.isShowing()) {
            staffSelectionDialog.dismiss();
        }
        binding = null;
    }
} 