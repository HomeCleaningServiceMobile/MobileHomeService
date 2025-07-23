package com.example.prm_project.ui.view.booking;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.prm_project.R;
import com.example.prm_project.data.model.Booking;
import com.example.prm_project.data.model.BookingStaff;
import com.example.prm_project.data.model.BookingStatus;
import com.example.prm_project.databinding.FragmentBookingDetailBinding;
import com.example.prm_project.ui.viewmodel.CustomerBookingViewModel;
import com.example.prm_project.utils.DateTimeUtils;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BookingDetailFragment extends Fragment {
    
    private FragmentBookingDetailBinding binding;
    private CustomerBookingViewModel bookingViewModel;
    private int bookingId;
    private Booking currentBooking;
    
    public static BookingDetailFragment newInstance(int bookingId) {
        BookingDetailFragment fragment = new BookingDetailFragment();
        Bundle args = new Bundle();
        args.putInt("bookingId", bookingId);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bookingId = getArguments().getInt("bookingId", -1);
        }
    }
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBookingDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize ViewModel
        bookingViewModel = new ViewModelProvider(this).get(CustomerBookingViewModel.class);
        
        setupUI();
        setupObservers();
        
        // Load booking details
        loadBookingDetails();
    }
    
    private void setupUI() {
        // Set up back button
        binding.btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
        
        // Set up action buttons
        binding.btnCallStaff.setOnClickListener(v -> callStaff());
        binding.btnCancelBooking.setOnClickListener(v -> cancelBooking());
        binding.btnRescheduleBooking.setOnClickListener(v -> rescheduleBooking());
        binding.btnRateService.setOnClickListener(v -> rateService());
        binding.btnRebookService.setOnClickListener(v -> rebookService());
    }
    
    private void setupObservers() {
        // Observe booking data
        bookingViewModel.getMyBookings().observe(getViewLifecycleOwner(), bookings -> {
            if (bookings != null && !bookings.isEmpty()) {
                // Find the current booking
                for (Booking booking : bookings) {
                    if (booking.getId() == bookingId) {
                        currentBooking = booking;
                        displayBookingDetails(booking);
                        break;
                    }
                }
            }
        });
        
        // Observe loading state
        bookingViewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                binding.scrollView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
            }
        });
        
        // Observe error messages
        bookingViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
        });
        
        // Observe success messages
        bookingViewModel.getSuccessMessage().observe(getViewLifecycleOwner(), success -> {
            if (success != null && !success.isEmpty()) {
                Toast.makeText(getContext(), success, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void loadBookingDetails() {
        // Load bookings to get the specific booking details
        bookingViewModel.loadMyBookings();
    }
    
    private void displayBookingDetails(Booking booking) {
        // Basic booking information
        binding.tvBookingNumber.setText(booking.getBookingNumber() != null ? booking.getBookingNumber() : "BK-" + booking.getId());
        
        // Service information
        if (booking.getService() != null) {
            binding.tvServiceName.setText(booking.getService().getName());
            binding.tvServiceDescription.setText(booking.getService().getDescription());
        }
        
        // Package information
        if (booking.getServicePackage() != null) {
            binding.tvPackageName.setText(booking.getServicePackage().getName());
            binding.tvPackageDescription.setText(booking.getServicePackage().getDescription());
            binding.tvPackagePrice.setText(String.format("$%.2f", booking.getServicePackage().getPrice()));
        }
        
        // Date and time
        if (booking.getScheduledDate() != null) {
            binding.tvScheduledDate.setText(DateTimeUtils.getFormattedDisplayDate(booking.getScheduledDate()));
        }
        if (booking.getScheduledTime() != null) {
            binding.tvScheduledTime.setText(DateTimeUtils.formatDisplayTime(booking.getScheduledTime()));
        }
        
        // Duration
        int durationHours = booking.getEstimatedDurationMinutes() / 60;
        int durationMinutes = booking.getEstimatedDurationMinutes() % 60;
        if (durationHours > 0) {
            binding.tvDuration.setText(String.format("%dh %dm", durationHours, durationMinutes));
        } else {
            binding.tvDuration.setText(String.format("%dm", durationMinutes));
        }
        
        // Address
        binding.tvServiceAddress.setText(booking.getServiceAddress());
        
        // Special instructions
        if (booking.getSpecialInstructions() != null && !booking.getSpecialInstructions().isEmpty()) {
            binding.tvSpecialInstructions.setText(booking.getSpecialInstructions());
            binding.layoutSpecialInstructions.setVisibility(View.VISIBLE);
        } else {
            binding.layoutSpecialInstructions.setVisibility(View.GONE);
        }
        
        // Status
        BookingStatus status = BookingStatus.fromValue(booking.getStatus());
        binding.tvStatus.setText(getStatusText(status));
        setStatusAppearance(status);
        
        // Pricing
        binding.tvTotalAmount.setText(String.format("$%.2f", booking.getTotalAmount()));
        if (booking.getFinalAmount() != null) {
            binding.tvFinalAmount.setText(String.format("$%.2f", booking.getFinalAmount()));
            binding.layoutFinalAmount.setVisibility(View.VISIBLE);
        } else {
            binding.layoutFinalAmount.setVisibility(View.GONE);
        }
        
        // Staff information
        if (booking.getBookingStaff() != null) {
            binding.tvStaffName.setText(booking.getBookingStaff().getName());
            binding.tvStaffPhone.setText(booking.getBookingStaff().getPhoneNumber());
            binding.layoutStaffInfo.setVisibility(View.VISIBLE);
        } else {
            binding.layoutStaffInfo.setVisibility(View.GONE);
        }
        
        // Timestamps
        if (booking.getCreatedAt() != null) {
            binding.tvCreatedAt.setText(DateTimeUtils.getFormattedDisplayDate(booking.getCreatedAt()));
        }
        
        if (booking.getStartedAt() != null) {
            binding.tvStartedAt.setText(DateTimeUtils.getFormattedDisplayDate(booking.getStartedAt()));
            binding.layoutStartedAt.setVisibility(View.VISIBLE);
        } else {
            binding.layoutStartedAt.setVisibility(View.GONE);
        }
        
        if (booking.getCompletedAt() != null) {
            binding.tvCompletedAt.setText(DateTimeUtils.getFormattedDisplayDate(booking.getCompletedAt()));
            binding.layoutCompletedAt.setVisibility(View.VISIBLE);
        } else {
            binding.layoutCompletedAt.setVisibility(View.GONE);
        }
        
        // Configure action buttons based on status
        configureActionButtons(status);
    }
    
    private String getStatusText(BookingStatus status) {
        switch (status) {
            case PENDING:
                return "Pending Assignment";
            case AUTO_ASSIGNED:
                return "Assigned - Awaiting Confirmation";
            case CONFIRMED:
                return "Confirmed";
            case IN_PROGRESS:
                return "In Progress";
            case COMPLETED:
                return "Completed";
            case CANCELLED:
                return "Cancelled";

            default:
                return "Unknown";
        }
    }
    
    private void setStatusAppearance(BookingStatus status) {
        int colorRes;
        switch (status) {
            case PENDING:
                colorRes = R.color.status_pending;
                break;
            case AUTO_ASSIGNED:
                colorRes = R.color.status_auto_assigned;
                break;
            case CONFIRMED:
                colorRes = R.color.status_confirmed;
                break;
            case IN_PROGRESS:
                colorRes = R.color.status_in_progress;
                break;
            case COMPLETED:
                colorRes = R.color.status_completed;
                break;
            case CANCELLED:
                colorRes = R.color.status_cancelled;
                break;
            default:
                colorRes = R.color.gray_500;
                break;
        }
        
        if (getContext() != null) {
            binding.statusIndicator.setBackgroundColor(getContext().getColor(colorRes));
        }
    }
    
    private void configureActionButtons(BookingStatus status) {
        // Hide all buttons initially
        binding.btnCallStaff.setVisibility(View.GONE);
        binding.btnCancelBooking.setVisibility(View.GONE);
        binding.btnRescheduleBooking.setVisibility(View.GONE);
        binding.btnRateService.setVisibility(View.GONE);
        binding.btnRebookService.setVisibility(View.GONE);
        
        switch (status) {
            case PENDING:
            case AUTO_ASSIGNED:
                binding.btnCancelBooking.setVisibility(View.VISIBLE);
                binding.btnRescheduleBooking.setVisibility(View.VISIBLE);
                break;
                
            case CONFIRMED:
                binding.btnCallStaff.setVisibility(View.VISIBLE);
                binding.btnCancelBooking.setVisibility(View.VISIBLE);
                binding.btnRescheduleBooking.setVisibility(View.VISIBLE);
                break;
                
            case IN_PROGRESS:
                binding.btnCallStaff.setVisibility(View.VISIBLE);
                break;
                
            case COMPLETED:
                binding.btnRateService.setVisibility(View.VISIBLE);
                binding.btnRebookService.setVisibility(View.VISIBLE);
                break;
                
            case CANCELLED:
                binding.btnRebookService.setVisibility(View.VISIBLE);
                break;
        }
    }
    
    private void callStaff() {
        if (currentBooking != null && currentBooking.getBookingStaff() != null && 
            currentBooking.getBookingStaff().getPhoneNumber() != null) {
            
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + currentBooking.getBookingStaff().getPhoneNumber()));
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "Staff contact information not available", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void cancelBooking() {
        if (currentBooking != null) {
            // For now, just show a toast. This should be replaced with a proper cancellation dialog
            bookingViewModel.cancelBooking(currentBooking.getId(), "Cancelled by customer");
            Toast.makeText(getContext(), "Cancellation requested for booking " + currentBooking.getBookingNumber(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void rescheduleBooking() {
        if (currentBooking != null) {
            // Navigate to create booking with pre-filled data for rescheduling
            Toast.makeText(getContext(), "Reschedule feature coming soon", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void rateService() {
        if (currentBooking != null) {
            // Navigate to rating screen
            Toast.makeText(getContext(), "Rating feature coming soon", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void rebookService() {
        if (currentBooking != null && getView() != null) {
            // Navigate to create booking with the same service
            Navigation.findNavController(getView()).navigate(R.id.action_bookingDetailFragment_to_createBookingFragment);
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 