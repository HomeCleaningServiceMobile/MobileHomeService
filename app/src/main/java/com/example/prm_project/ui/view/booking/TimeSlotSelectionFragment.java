//package com.example.prm_project.ui.view.booking;
//
//import android.app.DatePickerDialog;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.DatePicker;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.lifecycle.ViewModelProvider;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.prm_project.R;
//import com.example.prm_project.data.model.StaffAvailabilityDto;
//import com.example.prm_project.data.model.TimeSlotDto;
//import com.example.prm_project.ui.viewmodel.TimeSlotViewModel;
//
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//import java.util.Locale;
//
//import dagger.hilt.android.AndroidEntryPoint;
//
//@AndroidEntryPoint
//public class TimeSlotSelectionFragment extends Fragment {
//
//    private TimeSlotViewModel timeSlotViewModel;
//    private TimeSlotAdapter timeSlotAdapter;
//    private StaffSelectionAdapter staffAdapter;
//
//    private TextView tvSelectedDate;
//    private TextView tvSelectedTime;
//    private TextView tvSelectedStaff;
//    private RecyclerView rvTimeSlots;
//    private RecyclerView rvStaff;
//    private Button btnSelectDate;
//    private Button btnConfirm;
//    private ProgressBar progressBar;
//
//    private String selectedDate;
//    private TimeSlotDto selectedTimeSlot;
//    private StaffAvailabilityDto selectedStaff;
//    private Integer serviceId;
//
//    public interface OnTimeSlotSelectionListener {
//        void onTimeSlotConfirmed(String date, TimeSlotDto timeSlot, StaffAvailabilityDto staff);
//    }
//
//    private OnTimeSlotSelectionListener listener;
//
//    public void setOnTimeSlotSelectionListener(OnTimeSlotSelectionListener listener) {
//        this.listener = listener;
//    }
//
//    public void setServiceId(Integer serviceId) {
//        this.serviceId = serviceId;
//    }
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_time_slot_selection, container, false);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        initViews(view);
//        setupViewModel();
//        setupAdapters();
//        setupListeners();
//
//        // Set default date to today
//        setDefaultDate();
//    }
//
//    private void initViews(View view) {
//        tvSelectedDate = view.findViewById(R.id.tv_selected_date);
//        tvSelectedTime = view.findViewById(R.id.tv_selected_time);
//        tvSelectedStaff = view.findViewById(R.id.tv_selected_staff);
//        rvTimeSlots = view.findViewById(R.id.rv_time_slots);
//        rvStaff = view.findViewById(R.id.rv_staff);
//        btnSelectDate = view.findViewById(R.id.btn_select_date);
//        btnConfirm = view.findViewById(R.id.btn_confirm);
//        progressBar = view.findViewById(R.id.progress_bar);
//    }
//
//    private void setupViewModel() {
//        timeSlotViewModel = new ViewModelProvider(this).get(TimeSlotViewModel.class);
//
//        // Observe time slots
//        timeSlotViewModel.getAvailableSlots().observe(getViewLifecycleOwner(), this::updateTimeSlots);
//
//        // Observe staff
//        timeSlotViewModel.getAvailableStaff().observe(getViewLifecycleOwner(), this::updateStaff);
//
//        // Observe loading state
//        timeSlotViewModel.getIsLoading().observe(getViewLifecycleOwner(), this::updateLoadingState);
//
//        // Observe error messages
//        timeSlotViewModel.getErrorMessage().observe(getViewLifecycleOwner(), this::showError);
//    }
//
//    private void setupAdapters() {
//        // Time slot adapter
//        timeSlotAdapter = new TimeSlotAdapter();
//        timeSlotAdapter.setOnTimeSlotClickListener((timeSlot, position) -> {
//            selectedTimeSlot = timeSlot;
//            timeSlotAdapter.setSelectedPosition(position);
//            updateSelectedTimeDisplay();
//
//            // Load available staff for this time slot
//            if (selectedDate != null && timeSlot != null) {
//                timeSlotViewModel.loadAvailableStaffForSlot(
//                    selectedDate,
//                    timeSlot.getStartTime(),
//                    timeSlot.getEndTime(),
//                    serviceId
//                );
//            }
//        });
//
//        rvTimeSlots.setLayoutManager(new GridLayoutManager(requireContext(), 3));
//        rvTimeSlots.setAdapter(timeSlotAdapter);
//
//        // Staff adapter
//        staffAdapter = new StaffSelectionAdapter();
//        staffAdapter.setOnStaffClickListener((staff, position) -> {
//            selectedStaff = staff;
//            staffAdapter.setSelectedPosition(position);
//            updateSelectedStaffDisplay();
//        });
//
//        rvStaff.setLayoutManager(new GridLayoutManager(requireContext(), 2));
//        rvStaff.setAdapter(staffAdapter);
//    }
//
//    private void setupListeners() {
//        btnSelectDate.setOnClickListener(v -> showDatePicker());
//
//        btnConfirm.setOnClickListener(v -> {
//            if (selectedDate != null && selectedTimeSlot != null && selectedStaff != null) {
//                if (listener != null) {
//                    listener.onTimeSlotConfirmed(selectedDate, selectedTimeSlot, selectedStaff);
//                }
//            } else {
//                Toast.makeText(requireContext(), "Please select date, time slot, and staff", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void setDefaultDate() {
//        Calendar calendar = Calendar.getInstance();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//        selectedDate = dateFormat.format(calendar.getTime());
//        updateSelectedDateDisplay();
//        loadTimeSlotsForDate();
//    }
//
//    private void showDatePicker() {
//        Calendar calendar = Calendar.getInstance();
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH);
//        int day = calendar.get(Calendar.DAY_OF_MONTH);
//
//        DatePickerDialog datePickerDialog = new DatePickerDialog(
//            requireContext(),
//            (view, selectedYear, selectedMonth, selectedDay) -> {
//                Calendar selectedCalendar = Calendar.getInstance();
//                selectedCalendar.set(selectedYear, selectedMonth, selectedDay);
//
//                // Check if selected date is not in the past
//                if (selectedCalendar.getTime().before(new Date())) {
//                    Toast.makeText(requireContext(), "Cannot select past dates", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//                selectedDate = dateFormat.format(selectedCalendar.getTime());
//                updateSelectedDateDisplay();
//                loadTimeSlotsForDate();
//
//                // Clear previous selections
//                selectedTimeSlot = null;
//                selectedStaff = null;
//                timeSlotAdapter.setSelectedPosition(-1);
//                staffAdapter.setSelectedPosition(-1);
//                updateSelectedTimeDisplay();
//                updateSelectedStaffDisplay();
//            },
//            year, month, day
//        );
//
//        // Set minimum date to today
//        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
//        datePickerDialog.show();
//    }
//
//    private void loadTimeSlotsForDate() {
//        if (selectedDate != null) {
//            timeSlotViewModel.loadAvailableSlots(selectedDate, serviceId, null);
//        }
//    }
//
//    private void updateTimeSlots(List<TimeSlotDto> timeSlots) {
//        timeSlotAdapter.setTimeSlots(timeSlots);
//        if (timeSlots == null || timeSlots.isEmpty()) {
//            Toast.makeText(requireContext(), "No available time slots for this date", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void updateStaff(List<StaffAvailabilityDto> staff) {
//        staffAdapter.setStaffList(staff);
//        if (staff == null || staff.isEmpty()) {
//            Toast.makeText(requireContext(), "No available staff for this time slot", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void updateLoadingState(Boolean isLoading) {
//        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
//    }
//
//    private void showError(String error) {
//        if (error != null) {
//            Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
//        }
//    }
//
//    private void updateSelectedDateDisplay() {
//        if (selectedDate != null) {
//            tvSelectedDate.setText("Date: " + selectedDate);
//        }
//    }
//
//    private void updateSelectedTimeDisplay() {
//        if (selectedTimeSlot != null) {
//            tvSelectedTime.setText("Time: " + selectedTimeSlot.getDisplayTime());
//        } else {
//            tvSelectedTime.setText("Time: Not selected");
//        }
//    }
//
//    private void updateSelectedStaffDisplay() {
//        if (selectedStaff != null) {
//            tvSelectedStaff.setText("Staff: " + selectedStaff.getStaffName());
//        } else {
//            tvSelectedStaff.setText("Staff: Not selected");
//        }
//    }
//}