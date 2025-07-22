package com.example.prm_project.ui.view.booking;

import android.os.Bundle;
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

public class PaymentSuccessFragment extends Fragment {
    
    private TextView tvTransactionId;
    private TextView tvOrderId;
    private TextView tvAmount;
    private Button btnBackToHome;
    private Button btnViewOrders;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_success, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize views
        tvTransactionId = view.findViewById(R.id.tvTransactionId);
        tvOrderId = view.findViewById(R.id.tvOrderId);
        tvAmount = view.findViewById(R.id.tvAmount);
        btnBackToHome = view.findViewById(R.id.btnBackToHome);
        btnViewOrders = view.findViewById(R.id.btnViewOrders);
        
        // Display payment information (you can pass this via arguments)
        displayPaymentInfo();
        
        // Setup click listeners
        btnBackToHome.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_paymentSuccessFragment_to_mainFragment);
        });
        
        btnViewOrders.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            // Navigate to bookings fragment
            try {
                navController.navigate(R.id.nav_bookings);
            } catch (Exception e) {
                // Fallback to main if bookings navigation fails
                navController.navigate(R.id.action_paymentSuccessFragment_to_mainFragment);
            }
        });
    }
    
    private void displayPaymentInfo() {
        // In a real implementation, you would get this data from arguments
        Bundle args = getArguments();
        if (args != null) {
            String transactionId = args.getString("transaction_id", "TXN_" + System.currentTimeMillis());
            String orderId = args.getString("order_id", "ORDER_" + System.currentTimeMillis());
            double amount = args.getDouble("amount", 0.0);
            
            tvTransactionId.setText("Transaction ID: " + transactionId);
            tvOrderId.setText("Order ID: " + orderId);
            tvAmount.setText(String.format("Amount Paid: $%.2f", amount));
        } else {
            // Default values for testing
            tvTransactionId.setText("Transaction ID: TXN_" + System.currentTimeMillis());
            tvOrderId.setText("Order ID: ORDER_" + System.currentTimeMillis());
            tvAmount.setText("Amount Paid: $50.00");
        }
    }
} 