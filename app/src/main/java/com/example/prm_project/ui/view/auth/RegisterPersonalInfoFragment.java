package com.example.prm_project.ui.view.auth;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.prm_project.R;
import com.example.prm_project.databinding.FragmentRegisterPersonalInfoBinding;
import com.example.prm_project.ui.viewmodel.AuthViewModel;
import dagger.hilt.android.AndroidEntryPoint;

import java.util.Calendar;

@AndroidEntryPoint
public class RegisterPersonalInfoFragment extends Fragment {
    
    private FragmentRegisterPersonalInfoBinding binding;
    private AuthViewModel authViewModel;
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegisterPersonalInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize ViewModel
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
        // Back button click
        binding.btnBack.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireView());
            navController.navigateUp();
        });
        
        // Date picker for date of birth
        binding.etDateOfBirth.setOnClickListener(v -> showDatePicker());
        
        // Continue button click
        binding.btnContinue.setOnClickListener(v -> {
            if (validatePersonalInfo()) {
                savePersonalInfoToViewModel();
                navigateToAddressFragment();
            }
        });
    }
    
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            requireContext(),
            (view, selectedYear, selectedMonth, selectedDay) -> {
                String formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                binding.etDateOfBirth.setText(formattedDate);
            },
            year, month, day
        );
        
        // Set max date to today (can't select future dates)
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }
    
    private boolean validatePersonalInfo() {
        boolean isValid = true;
        
        // Clear previous errors
        binding.tilFullName.setError(null);
        binding.tilEmail.setError(null);
        binding.tilPhone.setError(null);
        binding.tilPassword.setError(null);
        binding.tilDateOfBirth.setError(null);
        binding.tilEmergencyName.setError(null);
        binding.tilEmergencyPhone.setError(null);
        
        // Validate full name
        String fullName = binding.etFullName.getText().toString().trim();
        if (TextUtils.isEmpty(fullName)) {
            binding.tilFullName.setError(getString(R.string.full_name_required));
            isValid = false;
        }
        
        // Validate email
        String email = binding.etEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            binding.tilEmail.setError(getString(R.string.email_required));
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.setError(getString(R.string.invalid_email_format));
            isValid = false;
        }
        
        // Validate phone number
        String phone = binding.etPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            binding.tilPhone.setError(getString(R.string.phone_number_required));
            isValid = false;
        } else if (phone.length() < 10) {
            binding.tilPhone.setError(getString(R.string.invalid_phone_format));
            isValid = false;
        }
        
        // Validate password
        String password = binding.etPassword.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            binding.tilPassword.setError(getString(R.string.password_required));
            isValid = false;
        } else if (password.length() < 6) {
            binding.tilPassword.setError(getString(R.string.password_too_short));
            isValid = false;
        }
        
        // Validate date of birth
        String dateOfBirth = binding.etDateOfBirth.getText().toString().trim();
        if (TextUtils.isEmpty(dateOfBirth)) {
            binding.tilDateOfBirth.setError(getString(R.string.date_of_birth_required));
            isValid = false;
        }
        
        // Validate gender selection
        if (binding.rgGender.getCheckedRadioButtonId() == -1) {
            // Show error for gender (you might want to add a TextView for gender error)
            isValid = false;
        }
        
        // Validate emergency contact name
        String emergencyName = binding.etEmergencyName.getText().toString().trim();
        if (TextUtils.isEmpty(emergencyName)) {
            binding.tilEmergencyName.setError(getString(R.string.emergency_contact_name_required));
            isValid = false;
        }
        
        // Validate emergency contact phone
        String emergencyPhone = binding.etEmergencyPhone.getText().toString().trim();
        if (TextUtils.isEmpty(emergencyPhone)) {
            binding.tilEmergencyPhone.setError(getString(R.string.emergency_contact_phone_required));
            isValid = false;
        } else if (emergencyPhone.length() < 10) {
            binding.tilEmergencyPhone.setError(getString(R.string.invalid_phone_format));
            isValid = false;
        }
        
        return isValid;
    }
    
    private void savePersonalInfoToViewModel() {
        String fullName = binding.etFullName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String dateOfBirth = binding.etDateOfBirth.getText().toString().trim();
        String emergencyName = binding.etEmergencyName.getText().toString().trim();
        String emergencyPhone = binding.etEmergencyPhone.getText().toString().trim();
        
        // Get selected gender
        int selectedGenderId = binding.rgGender.getCheckedRadioButtonId();
        RadioButton selectedGenderRadio = binding.getRoot().findViewById(selectedGenderId);
        String gender = selectedGenderRadio.getText().toString();
        
        // Save to ViewModel (you'll need to add these methods to AuthViewModel)
        authViewModel.setRegistrationPersonalInfo(fullName, email, phone, password, dateOfBirth, gender, emergencyName, emergencyPhone);
    }
    
    private void navigateToAddressFragment() {
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_registerPersonalInfoFragment_to_registerAddressFragment);
    }
    
    private void observeViewModel() {
        // Observe any ViewModel states if needed
        authViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                // Show error message if needed
            }
        });
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 