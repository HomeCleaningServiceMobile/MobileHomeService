package com.example.prm_project.ui.view.staff;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.prm_project.R;
import com.example.prm_project.ui.viewmodel.BookingViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class StaffResponseBookingFragment extends Fragment {

    private BookingViewModel bookingViewModel;

    private EditText etDeclineReason;
    private Button btnAccept, btnDecline;
    private int currentBookingId;

    // Loading dialog reference (implement as needed)
    // private ProgressDialog loadingDialog;

    // Interface for fragment communication
    public interface OnBookingResponseListener {
        void onBookingResponseSuccess(String message);
        void onBookingResponseError(String error);
    }

    private OnBookingResponseListener listener;

    public StaffResponseBookingFragment() {
        // Required empty public constructor
    }

    // Factory method to create fragment with booking ID
    public static StaffResponseBookingFragment newInstance(int bookingId) {
        StaffResponseBookingFragment fragment = new StaffResponseBookingFragment();
        Bundle args = new Bundle();
        args.putInt("booking_id", bookingId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Optionally implement listener interface
        if (context instanceof OnBookingResponseListener) {
            listener = (OnBookingResponseListener) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewModel
        bookingViewModel = new ViewModelProvider(this).get(BookingViewModel.class);

        // Get booking ID from arguments
        if (getArguments() != null) {
            currentBookingId = getArguments().getInt("booking_id", 0);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_staff_reponse_booking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        initViews(view);

        // Set click listeners
        setupClickListeners();

        // Observe ViewModel LiveData
        observeViewModel();
    }

    private void initViews(View view) {
        etDeclineReason = view.findViewById(R.id.etDeclineReason);
        btnAccept = view.findViewById(R.id.btnAccept);
        btnDecline = view.findViewById(R.id.btnDecline);
    }

    private void setupClickListeners() {
        btnAccept.setOnClickListener(v -> acceptBooking());
        btnDecline.setOnClickListener(v -> declineBooking());
    }

    private void observeViewModel() {
        // Observe loading state
        bookingViewModel.getLoading().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                if (isLoading != null) {
                    if (isLoading) {
                        showLoadingDialog();
                    } else {
                        hideLoadingDialog();
                    }
                }
            }
        });

        // Observe success messages
        bookingViewModel.getSuccessMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String successMessage) {
                if (successMessage != null && !successMessage.isEmpty()) {
                    showSuccessMessage(successMessage);
                    bookingViewModel.clearSuccessMessage(); // Clear after showing

                    // Notify parent activity/fragment
                    if (listener != null) {
                        listener.onBookingResponseSuccess(successMessage);
                    }

                    // Navigate back or close fragment
                    navigateBack();
                }
            }
        });

        // Observe error messages
        bookingViewModel.getErrorMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String errorMessage) {
                if (errorMessage != null && !errorMessage.isEmpty()) {
                    showErrorMessage(errorMessage);
                    bookingViewModel.clearErrorMessage(); // Clear after showing

                    // Notify parent activity/fragment
                    if (listener != null) {
                        listener.onBookingResponseError(errorMessage);
                    }
                }
            }
        });
    }

    private void acceptBooking() {
        if (currentBookingId == 0) {
            showErrorMessage("Invalid booking ID");
            return;
        }

//        String authToken = "Bearer " + getAuthToken();
//        if (authToken.equals("Bearer ")) {
//            showErrorMessage("Authentication token not found. Please log in again.");
//            return;
//        }

        bookingViewModel.acceptBooking(currentBookingId);
    }

    private void declineBooking() {
        String declineReason = etDeclineReason.getText().toString().trim();

        // Validate decline reason
        if (declineReason.isEmpty()) {
            etDeclineReason.setError("Please provide a reason for declining");
            etDeclineReason.requestFocus();
            return;
        }

        if (declineReason.length() > 500) {
            etDeclineReason.setError("Decline reason must be 500 characters or less");
            etDeclineReason.requestFocus();
            return;
        }

        if (currentBookingId == 0) {
            showErrorMessage("Invalid booking ID");
            return;
        }

//        String authToken = "Bearer " + getAuthToken();
//        if (authToken.equals("Bearer ")) {
//            showErrorMessage("Authentication token not found. Please log in again.");
//            return;
//        }

        bookingViewModel.declineBooking(currentBookingId, declineReason);
    }

    private String getAuthToken() {
        if (getContext() == null) {
            return "";
        }
        SharedPreferences prefs = getContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        return prefs.getString("auth_token", "");
    }

    private void showSuccessMessage(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private void showErrorMessage(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    private void showLoadingDialog() {
        // Implement loading dialog - you can use ProgressDialog or custom dialog
        // Make sure to check if fragment is still attached
        if (isAdded() && getContext() != null) {
            // Example:
            // if (loadingDialog == null) {
            //     loadingDialog = new ProgressDialog(getContext());
            //     loadingDialog.setMessage("Processing...");
            //     loadingDialog.setCancelable(false);
            // }
            // if (!loadingDialog.isShowing()) {
            //     loadingDialog.show();
            // }
        }
    }

    private void hideLoadingDialog() {
        // Hide loading dialog
        // Make sure to check if fragment is still attached
        if (isAdded()) {
            // Example:
            // if (loadingDialog != null && loadingDialog.isShowing()) {
            //     loadingDialog.dismiss();
            // }
        }
    }

    private void navigateBack() {
        // Navigate back using FragmentManager
        if (getParentFragmentManager() != null) {
            getParentFragmentManager().popBackStack();
        }
    }

    // Public method to set the booking ID (alternative to factory method)
    public void setBookingId(int bookingId) {
        this.currentBookingId = bookingId;
    }

    // Public method to set the listener
    public void setOnBookingResponseListener(OnBookingResponseListener listener) {
        this.listener = listener;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null; // Prevent memory leaks
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Cancel any ongoing API calls when fragment view is destroyed
        if (bookingViewModel != null) {
            bookingViewModel.cancelOngoingCalls();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Clean up resources
        etDeclineReason = null;
        btnAccept = null;
        btnDecline = null;
    }
}