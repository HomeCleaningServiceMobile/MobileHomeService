package com.example.prm_project.ui.view.staff;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.prm_project.R;
import com.example.prm_project.data.model.Booking;
import com.example.prm_project.data.model.BookingStatus;
import com.example.prm_project.data.model.User;
import com.example.prm_project.ui.view.adapters.BookingAdapter;
import com.example.prm_project.ui.view.staff.StaffResponseBookingFragment;
import com.example.prm_project.ui.viewmodel.AuthViewModel;
import com.example.prm_project.ui.viewmodel.BookingViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class StaffAssignmentsFragment extends Fragment implements BookingAdapter.OnBookingClickListener {

    private int currentStaffId;
    private AuthViewModel authViewModel;
    private BookingViewModel bookingViewModel;

    private ListView lvBookings;
    private ProgressBar progressBar;
    private View emptyStateLayout;
    private MaterialButton btnFilter;
    private TextView tvPendingCount, tvInProgressCount, tvCompletedCount;

    private BookingAdapter bookingAdapter;
    private List<Booking> allBookings = new ArrayList<>();
    private List<Booking> staffBookings = new ArrayList<>();

    public StaffAssignmentsFragment() {
        // Required empty public constructor
    }

    public static StaffAssignmentsFragment newInstance() {
        return new StaffAssignmentsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize ViewModel
        bookingViewModel = new ViewModelProvider(this).get(BookingViewModel.class);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        currentStaffId = authViewModel.getUserId();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_staff_assignments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupAdapter();
        setupClickListeners();
        observeViewModel();
        loadBookings();
    }

    private void initViews(View view) {
        lvBookings = view.findViewById(R.id.lvBookings);
        progressBar = view.findViewById(R.id.progressBar);
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout);
        btnFilter = view.findViewById(R.id.btnFilter);
        tvPendingCount = view.findViewById(R.id.tvPendingCount);
        tvInProgressCount = view.findViewById(R.id.tvInProgressCount);
        tvCompletedCount = view.findViewById(R.id.tvCompletedCount);
    }

    private void setupAdapter() {
        bookingAdapter = new BookingAdapter(getContext(), staffBookings);
        bookingAdapter.setOnBookingClickListener(this);
        lvBookings.setAdapter(bookingAdapter);
    }

    private void setupClickListeners() {
        btnFilter.setOnClickListener(v -> showFilterDialog());
    }

    private void observeViewModel() {
        // Observe loading state
        bookingViewModel.getLoading().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                if (isLoading != null) {
                    showLoading(isLoading);
                }
            }
        });

        // Observe bookings list
        bookingViewModel.getBookings().observe(getViewLifecycleOwner(), new Observer<List<Booking>>() {
            @Override
            public void onChanged(List<Booking> bookings) {
                if (bookings != null) {
                    allBookings.clear();
                    allBookings.addAll(bookings);
                    filterStaffBookings();
                }
            }
        });
        // Observe error messages
        bookingViewModel.getErrorMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String errorMessage) {
                if (errorMessage != null && !errorMessage.isEmpty()) {
                    showError(errorMessage);
                    bookingViewModel.clearErrorMessage();
                }
            }
        });

        // Observe success messages
        bookingViewModel.getSuccessMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String successMessage) {
                if (successMessage != null && !successMessage.isEmpty()) {
                    showSuccess(successMessage);
                    bookingViewModel.clearSuccessMessage();
                    // Refresh bookings after successful operation
                    loadBookings();
                }
            }
        });
    }

    private void loadBookings() {
        // Load all bookings - we'll filter them client-side for staff assignments
        bookingViewModel.loadAllBookings();
    }

    private void filterStaffBookings() {
        staffBookings.clear();

        if (currentStaffId == -1) {
            showError("Staff user ID not available");
            updateBookingsDisplay();
            return;
        }

        // Filter bookings assigned to current staff member
        for (Booking booking : allBookings) {
            if (isBookingAssignedToCurrentStaff(booking)) {
                staffBookings.add(booking);
            }
        }

        updateBookingsDisplay();
    }

    private boolean isBookingAssignedToCurrentStaff(Booking booking) {
        // Check if booking is assigned to current staff member
        if (booking.getStaff() != null &&
                booking.getStaff().getId() == currentStaffId) {
            return true;
        }
        return false;
    }

    private void updateBookingsDisplay() {
        bookingAdapter.updateBookings(staffBookings);
        updateEmptyState();
        updateStatistics();
    }


    private void updateEmptyState() {
        if (allBookings.isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            lvBookings.setVisibility(View.GONE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            lvBookings.setVisibility(View.VISIBLE);
        }
    }

    private void updateStatistics() {
        int pendingCount = 0;
        int inProgressCount = 0;
        int completedCount = 0;

        for (Booking booking : staffBookings) {
            BookingStatus status = booking.getStatusEnum();
            switch (status) {
                case AUTO_ASSIGNED:
                    pendingCount++;
                    break;
                case IN_PROGRESS:
                case CONFIRMED:
                    inProgressCount++;
                    break;
                case COMPLETED:
                    completedCount++;
                    break;
                case CANCELLED:
                case REJECTED:
                    // These don't count in active statistics
                    break;
            }
        }

        tvPendingCount.setText(String.valueOf(pendingCount));
        tvInProgressCount.setText(String.valueOf(inProgressCount));
        tvCompletedCount.setText(String.valueOf(completedCount));
    }

    private void showFilterDialog() {
        // Updated filter options to only include the specified statuses
        String[] filterOptions = {"Pending", "Confirmed" ,"Completed", "All Bookings"};

        if (getContext() != null) {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
            builder.setTitle("Filter Bookings")
                    .setItems(filterOptions, (dialog, which) -> {
                        switch (which) {
                            case 0: // Pending
                                bookingViewModel.loadBookingsByStatus(BookingStatus.AUTO_ASSIGNED);
                                break;
                            case 1: // Confirmed
                                bookingViewModel.loadBookingsByStatus(BookingStatus.CONFIRMED);
                                break;
                            case 2: // Completed
                                bookingViewModel.loadBookingsByStatus(BookingStatus.COMPLETED);
                                break;
                            default:
                                bookingViewModel.loadAllBookings();
                                break;
                        }
                    })
                    .show();
        }
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    private void showSuccess(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    // BookingAdapter.OnBookingClickListener implementation - with status check
    @Override
    public void onBookingClick(Booking booking, int position) {
        // Only allow navigation for AUTO_ASSIGNED bookings
        if (booking.getStatusEnum() == BookingStatus.AUTO_ASSIGNED) {
            navigateToResponseFragment(booking);
        }
    }

    @Override
    public void onBookingLongClick(Booking booking, int position) {
        // Only allow navigation for AUTO_ASSIGNED bookings
        if (booking.getStatusEnum() == BookingStatus.AUTO_ASSIGNED) {
            navigateToResponseFragment(booking);
        }
    }

    private void navigateToResponseFragment(Booking booking) {
        // Navigate to StaffResponseBookingFragment
        StaffResponseBookingFragment responseFragment = StaffResponseBookingFragment.newInstance(booking.getId());

        if (getParentFragmentManager() != null) {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.staff_content_container, responseFragment)
                    .addToBackStack("staff_response")
                    .commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh bookings when fragment becomes visible
        loadBookings();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Cancel any ongoing API calls
        if (bookingViewModel != null) {
            bookingViewModel.cancelOngoingCalls();
        }
    }
}