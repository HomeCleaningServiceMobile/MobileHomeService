package com.example.prm_project.ui.view;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prm_project.R;
import com.example.prm_project.databinding.ActivityResetEmailSentBinding;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ResetEmailSentActivity extends AppCompatActivity {
    
    private ActivityResetEmailSentBinding binding;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set up data binding
        binding = ActivityResetEmailSentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Initialize UI components
        initializeComponents();
    }
    
    private void initializeComponents() {
        // Get email from intent
        String email = getIntent().getStringExtra("email");
        if (email != null && !email.isEmpty()) {
            // Update the message to include the email
            binding.tvResetEmailSubtitle.setText(getString(R.string.reset_email_sent_message, email));
        }
        
        // Return to login button click listener
        binding.btnGoToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(ResetEmailSentActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
} 