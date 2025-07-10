package com.example.prm_project.ui.view.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.prm_project.R;
import com.example.prm_project.databinding.FragmentRegisterAddressBinding;
import com.example.prm_project.ui.viewmodel.AuthViewModel;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RegisterAddressFragment extends Fragment {
    
    private FragmentRegisterAddressBinding binding;
    private AuthViewModel authViewModel;
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegisterAddressBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize ViewModel (same instance from previous fragment)
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        
        // Set ViewModel in binding
        binding.setViewModel(authViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        
        // Initialize UI components
        initializeComponents();
        
        // Observe ViewModel
        observeViewModel();
    }
    
    private void initializeComponents() {
        // Back button click (navigate back to personal info)
        binding.btnBack.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireView());
            navController.navigateUp();
        });
        
        // Back to personal info button
        binding.btnBackToPersonal.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireView());
            navController.navigateUp();
        });
        
        // Complete registration button
        binding.btnCompleteRegistration.setOnClickListener(v -> {
            if (validateAddressInfo()) {
                saveAddressInfoToViewModel();
                completeRegistration();
            }
        });
        
        // Set default country
        binding.etCountry.setText("Vietnam");
    }
    
    private boolean validateAddressInfo() {
        boolean isValid = true;
        
        // Clear previous errors
        binding.tilAddress.setError(null);
        binding.tilWard.setError(null);
        binding.tilDistrict.setError(null);
        binding.tilProvince.setError(null);
        binding.tilCountry.setError(null);
        
        // Validate street address
        String address = binding.etAddress.getText().toString().trim();
        if (TextUtils.isEmpty(address)) {
            binding.tilAddress.setError(getString(R.string.address_required));
            isValid = false;
        }
        
        // Validate ward
        String ward = binding.etWard.getText().toString().trim();
        if (TextUtils.isEmpty(ward)) {
            binding.tilWard.setError(getString(R.string.ward_required));
            isValid = false;
        }
        
        // Validate district
        String district = binding.etDistrict.getText().toString().trim();
        if (TextUtils.isEmpty(district)) {
            binding.tilDistrict.setError(getString(R.string.district_required));
            isValid = false;
        }
        
        // Validate province
        String province = binding.etProvince.getText().toString().trim();
        if (TextUtils.isEmpty(province)) {
            binding.tilProvince.setError(getString(R.string.province_required));
            isValid = false;
        }
        
        // Validate country
        String country = binding.etCountry.getText().toString().trim();
        if (TextUtils.isEmpty(country)) {
            binding.tilCountry.setError(getString(R.string.country_required));
            isValid = false;
        }
        
        return isValid;
    }
    
    private void saveAddressInfoToViewModel() {
        String address = binding.etAddress.getText().toString().trim();
        String ward = binding.etWard.getText().toString().trim();
        String district = binding.etDistrict.getText().toString().trim();
        String province = binding.etProvince.getText().toString().trim();
        String country = binding.etCountry.getText().toString().trim();
        
        // Save address info to ViewModel
        authViewModel.setRegistrationAddressInfo(address, ward, district, province, country);
    }
    
    private void completeRegistration() {
        // Show loading state
        binding.btnCompleteRegistration.setEnabled(false);
        binding.btnCompleteRegistration.setText(getString(R.string.registering));
        
        // Trigger registration in ViewModel
        authViewModel.registerCustomer();
    }
    
    private void observeViewModel() {
        // Observe registration success
        authViewModel.getSuccessMessage().observe(getViewLifecycleOwner(), successMessage -> {
            if (successMessage != null && !successMessage.isEmpty() && successMessage.contains("successfully")) {
                // Navigate back to login after successful registration
                NavController navController = Navigation.findNavController(requireView());
                navController.navigate(R.id.action_registerAddressFragment_to_loginFragment);
            }
        });
        
        // Observe registration errors
        authViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                // Reset button state on error
                binding.btnCompleteRegistration.setEnabled(true);
                binding.btnCompleteRegistration.setText(getString(R.string.complete_registration));
                
                // You might want to show the error in a Snackbar or AlertDialog
            }
        });
        
        // Observe loading state
        authViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (!isLoading) {
                // Reset button state when loading is complete
                binding.btnCompleteRegistration.setEnabled(true);
                binding.btnCompleteRegistration.setText(getString(R.string.complete_registration));
            }
        });
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 