package com.example.prm_project.ui.view;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import com.example.prm_project.R;
import com.example.prm_project.databinding.ActivitySetNewPasswordBinding;

public class SetNewPasswordActivity extends AppCompatActivity {
    
    private ActivitySetNewPasswordBinding binding;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set up data binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_set_new_password);
        
        // Initialize UI components
        initializeComponents();
    }
    
    private void initializeComponents() {
        // TODO: Initialize UI components and set up listeners
        // This will be implemented when adding logic
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (binding != null) {
            binding.unbind();
        }
    }
} 