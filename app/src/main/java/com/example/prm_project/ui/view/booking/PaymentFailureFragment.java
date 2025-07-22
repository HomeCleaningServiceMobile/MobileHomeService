package com.example.prm_project.ui.view.booking;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.prm_project.R;

public class PaymentFailureFragment extends Fragment {
    
    private static final int REDIRECT_DELAY = 3000; // 3 seconds
    
    private TextView tvErrorDetails;
    private TextView tvRedirectTimer;
    private Button btnRetryPayment;
    private Button btnBackToHome;
    private Handler redirectHandler;
    private Runnable redirectRunnable;
    private int countdownSeconds = 3;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_failure, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize views
        tvErrorDetails = view.findViewById(R.id.tvErrorDetails);
        tvRedirectTimer = view.findViewById(R.id.tvRedirectTimer);
        btnRetryPayment = view.findViewById(R.id.btnRetryPayment);
        btnBackToHome = view.findViewById(R.id.btnBackToHome);
        
        // Display error details if available
        displayErrorDetails();
        
        // Setup click listeners
        btnBackToHome.setOnClickListener(v -> {
            cancelAutoRedirect();
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_paymentFailureFragment_to_mainFragment);
        });
        
        btnRetryPayment.setOnClickListener(v -> {
            cancelAutoRedirect();
            // Navigate back to booking creation to retry payment
            NavController navController = Navigation.findNavController(view);
            try {
                navController.navigateUp(); // Go back to previous screen
                navController.navigateUp(); // Go back to booking screen
            } catch (Exception e) {
                // Fallback to main screen
                navController.navigate(R.id.action_paymentFailureFragment_to_mainFragment);
            }
        });
        
        // Start auto-redirect countdown
        startAutoRedirect();
    }
    
    private void displayErrorDetails() {
        Bundle args = getArguments();
        if (args != null) {
            String errorMessage = args.getString("error_message");
            if (errorMessage != null && !errorMessage.isEmpty()) {
                tvErrorDetails.setText("Error: " + errorMessage);
                tvErrorDetails.setVisibility(View.VISIBLE);
            }
        }
    }
    
    private void startAutoRedirect() {
        redirectHandler = new Handler(Looper.getMainLooper());
        redirectRunnable = new Runnable() {
            @Override
            public void run() {
                if (countdownSeconds > 0) {
                    tvRedirectTimer.setText("Redirecting in " + countdownSeconds + " seconds...");
                    countdownSeconds--;
                    redirectHandler.postDelayed(this, 1000);
                } else {
                    // Auto redirect to home
                    if (isAdded()) {
                        NavController navController = Navigation.findNavController(requireView());
                        navController.navigate(R.id.action_paymentFailureFragment_to_mainFragment);
                    }
                }
            }
        };
        redirectHandler.post(redirectRunnable);
    }
    
    private void cancelAutoRedirect() {
        if (redirectHandler != null && redirectRunnable != null) {
            redirectHandler.removeCallbacks(redirectRunnable);
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cancelAutoRedirect();
    }
} 