package com.example.prm_project.ui.view.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.prm_project.R;
import com.example.prm_project.databinding.FragmentResetEmailSentBinding;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ResetEmailSentFragment extends Fragment {
    
    private FragmentResetEmailSentBinding binding;
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentResetEmailSentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize UI components
        initializeComponents();
    }
    
    private void initializeComponents() {
        // Get email from arguments
        Bundle args = getArguments();
        if (args != null) {
            String email = args.getString("email");
            if (email != null && !email.isEmpty()) {
                // Update the message to include the email
                binding.tvResetEmailMessage.setText(getString(R.string.reset_email_sent_message, email));
            }
        }
        
        // Return to login button click listener
        binding.btnGoToLogin.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            // Navigate back to login fragment and clear the back stack
            navController.navigate(R.id.action_resetEmailSentFragment_to_loginFragment);
        });
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 