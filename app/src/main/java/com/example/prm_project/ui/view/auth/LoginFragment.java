package com.example.prm_project.ui.view.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.prm_project.R;
import com.example.prm_project.databinding.FragmentLoginBinding;
import com.example.prm_project.ui.viewmodel.AuthViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import dagger.hilt.android.AndroidEntryPoint;
import android.util.Log;

@AndroidEntryPoint
public class LoginFragment extends Fragment {
    
    private FragmentLoginBinding binding;
    private AuthViewModel authViewModel;
    private FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;
    private ActivityResultLauncher<Intent> googleSignInLauncher;
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize ViewModel - Hilt provides all dependencies automatically
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        mAuth = FirebaseAuth.getInstance();

        // Check if user is already logged in
        if (authViewModel.isUserLoggedIn()) {
            navigateBasedOnRole();
            return;
        }
        
        // Initialize UI components
        initializeComponents();
        
        // Observe ViewModel
        observeViewModel();
        
        // Pre-fill email if remembered
        prefillEmailIfRemembered();

        // Google Sign-In options
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso);

        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        handleFirebaseGoogleLogin(task);
                    } else {
                        Log.e("GoogleLogin", "Google Sign-In canceled or failed. ResultCode: " + result.getResultCode());
                    }
                }
        );
    }
    
    private void initializeComponents() {
        // Login button click listener
        binding.btnLogin.setOnClickListener(v -> {
            if (validateLoginInput()) {
                String email = binding.etEmail.getText().toString().trim();
                String password = binding.etPassword.getText().toString().trim();
                boolean rememberMe = binding.cbRememberMe.isChecked();
                
                authViewModel.login(email, password, rememberMe);
            }
        });
        
        // Forgot password click listener
        binding.btnForgotPassword.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_loginFragment_to_forgotPasswordFragment);
        });
        
        // Sign up click listener
        binding.btnSignUp.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_loginFragment_to_registerPersonalInfoFragment);
        });
        
        // Google sign-in click listener
        binding.btnGoogleSignin.setOnClickListener(v -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        });
    }
    
    private boolean validateLoginInput() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        
        // Clear previous errors
        binding.tilEmail.setError(null);
        binding.tilPassword.setError(null);
        
        boolean isValid = true;
        
        // Validate email
        if (email.isEmpty()) {
            binding.tilEmail.setError(getString(R.string.email_required));
            if (isValid) binding.etEmail.requestFocus();
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.setError(getString(R.string.invalid_email_format));
            if (isValid) binding.etEmail.requestFocus();
            isValid = false;
        }
        
        // Validate password
        if (password.isEmpty()) {
            binding.tilPassword.setError(getString(R.string.password_required));
            if (isValid) binding.etPassword.requestFocus();
            isValid = false;
        }
        
        return isValid;
    }

    private void handleFirebaseGoogleLogin(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d("GoogleLogin", "Firebase login success: " + user.getEmail());

                            //Send user info to backend Google login API
                            authViewModel.checkGoogleLogin(
                                    user.getEmail(),
                                    user.getDisplayName(),
                                    user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null
                            );
                        } else {
                            Log.e("GoogleLogin", "Firebase login failed", task.getException());
                        }
                    });

        } catch (ApiException e) {
            Log.e("GoogleLogin", "Google Sign-In failed: " + e.getStatusCode() + " " + e.getMessage());
        }
    }
    
    private void observeViewModel() {
        // Observe loading state
        authViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                binding.btnLogin.setEnabled(false);
                binding.btnLogin.setText(getString(R.string.logging_in));
            } else {
                binding.btnLogin.setEnabled(true);
                binding.btnLogin.setText(getString(R.string.login));
            }
        });
        
        // Observe error messages
        authViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Snackbar.make(binding.getRoot(), errorMessage, Snackbar.LENGTH_LONG).show();
                authViewModel.clearMessages();
            }
        });
        
        // Observe success messages
        authViewModel.getSuccessMessage().observe(getViewLifecycleOwner(), successMessage -> {
            if (successMessage != null && !successMessage.isEmpty()) {
                Toast.makeText(getContext(), successMessage, Toast.LENGTH_SHORT).show();
                authViewModel.clearMessages();
            }
        });
        
        // Observe login success
        authViewModel.getLoginSuccess().observe(getViewLifecycleOwner(), loginSuccess -> {
            if (loginSuccess != null && loginSuccess) {
                navigateBasedOnRole();
            }
        });
    }
    
    private void prefillEmailIfRemembered() {
        if (authViewModel.isRememberMeEnabled()) {
            String savedEmail = authViewModel.getSavedEmail();
            if (!savedEmail.isEmpty()) {
                binding.etEmail.setText(savedEmail);
                binding.cbRememberMe.setChecked(true);
                binding.etPassword.requestFocus();
            }
        }
    }
    
    private void navigateBasedOnRole() {
        NavController navController = Navigation.findNavController(requireView());
        
        // Debug logging to see what role is detected
        String userRole = authViewModel.getUserRole();
        boolean isAdmin = authViewModel.isAdmin();
        boolean isStaff = authViewModel.isStaff();
        boolean isCustomer = authViewModel.isCustomer();
        
        Log.d("LoginFragment", "User Role: " + userRole);
        Log.d("LoginFragment", "isAdmin: " + isAdmin);
        Log.d("LoginFragment", "isStaff: " + isStaff);
        Log.d("LoginFragment", "isCustomer: " + isCustomer);
        
        // TEMPORARY: Force staff navigation for testing if email contains "staff"
//        String userEmail = authViewModel.getUserEmail();
//        if (userEmail != null && userEmail.toLowerCase().contains("staff")) {
//            Log.d("LoginFragment", "TEMP: Forcing staff navigation based on email: " + userEmail);
//            navController.navigate(R.id.action_loginFragment_to_staffFragment);
//            return;
//        }
//
        // Check user role and navigate to appropriate dashboard
        if (authViewModel.isAdmin()) {
            Log.d("LoginFragment", "Navigating to Admin Fragment");
            navController.navigate(R.id.action_loginFragment_to_adminFragment);
        } else if (authViewModel.isStaff()) {
            Log.d("LoginFragment", "Navigating to Staff Fragment");
            navController.navigate(R.id.action_loginFragment_to_staffFragment);
        } else {
            // Default to customer dashboard (includes customer role or any other role)
            Log.d("LoginFragment", "Navigating to Customer Main Fragment");
            navController.navigate(R.id.action_loginFragment_to_mainFragment);
        }
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                String email = account.getEmail();
                String name = account.getDisplayName();
                String avatar = (account.getPhotoUrl() != null) ? account.getPhotoUrl().toString() : "";

                Log.d("GoogleLogin", "Google email: " + email);
                Log.d("GoogleLogin", "Google name: " + name);
                Log.d("GoogleLogin", "Google avatar: " + avatar);

                authViewModel.checkGoogleLogin(email, name, avatar);
            }
        } catch (ApiException e) {
            Snackbar.make(binding.getRoot(), "Google Sign-In failed: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }



    private void navigateToMain() {
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_loginFragment_to_mainFragment);
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 