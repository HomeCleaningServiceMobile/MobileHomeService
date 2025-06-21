package com.example.prm_project.ui.view;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.databinding.DataBindingUtil;

import com.example.prm_project.R;
import com.example.prm_project.databinding.ActivityMainBinding;
import com.example.prm_project.ui.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity {
    
    private ActivityMainBinding binding;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        
        // Initialize data binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        
        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        
        // Set ViewModel in binding
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        
        // Set up window insets
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        // Observe ViewModel
        observeViewModel();
    }
    
    private void observeViewModel() {
        viewModel.getMessage().observe(this, message -> {
            // Handle message changes if needed
        });
    }
} 