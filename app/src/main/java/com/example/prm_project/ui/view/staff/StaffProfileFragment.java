package com.example.prm_project.ui.view.staff;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.prm_project.R;
import com.example.prm_project.data.model.ApiResponse;
import com.example.prm_project.data.model.GetStaffProfileResponse;
import com.example.prm_project.data.model.StaffAvailabilityResponse;
import com.example.prm_project.data.remote.RetrofitClient;
import com.example.prm_project.data.remote.StaffApiService;
import com.example.prm_project.databinding.FragmentStaffProfileBinding;
import com.example.prm_project.utils.NetworkUtils;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class StaffProfileFragment extends Fragment {
    
    private static final String TAG = "StaffProfileFragment";
    private static final String ARG_STAFF_DATA = "staff_data";
    private static final String ARG_EMPLOYEE_ID = "employee_id";
    
    private FragmentStaffProfileBinding binding;
    private StaffAvailabilityResponse staffData;
    private String employeeId;
    
    public static StaffProfileFragment newInstance(StaffAvailabilityResponse staffData) {
        StaffProfileFragment fragment = new StaffProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_STAFF_DATA, staffData);
        fragment.setArguments(args);
        return fragment;
    }
    
    public static StaffProfileFragment newInstance(String employeeId) {
        StaffProfileFragment fragment = new StaffProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EMPLOYEE_ID, employeeId);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            staffData = (StaffAvailabilityResponse) getArguments().getSerializable(ARG_STAFF_DATA);
            employeeId = getArguments().getString(ARG_EMPLOYEE_ID);
        }
    }
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStaffProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setupUI();
        loadStaffData();
    }
    
    private void setupUI() {
        // Close button
        binding.btnClose.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            } else {
                Navigation.findNavController(v).navigateUp();
            }
        });
        
        // Initial loading state
        showLoading(true);
    }
    
    private void loadStaffData() {
        // First display basic data if we have it
        if (staffData != null) {
            displayBasicStaffData(staffData);
            
            // Then try to load more details if we have employeeId
            if (staffData.getEmployeeId() != null) {
                loadStaffProfileFromApi(staffData.getEmployeeId());
            } else {
                showLoading(false);
            }
        } else if (employeeId != null) {
            // Load from API directly
            loadStaffProfileFromApi(employeeId);
        } else {
            showError("No staff data available");
        }
    }
    
    private void displayBasicStaffData(StaffAvailabilityResponse staff) {
        // Basic information from StaffAvailabilityResponse
        binding.tvStaffName.setText(staff.getStaffName() != null ? staff.getStaffName() : "Unknown Staff");
        binding.tvEmployeeId.setText("ID: " + (staff.getEmployeeId() != null ? staff.getEmployeeId() : "N/A"));
        
        // Rating
        if (staff.getAverageRating() != null && staff.getAverageRating() > 0) {
            binding.ratingBar.setRating(staff.getAverageRating().floatValue());
            binding.tvRatingValue.setText(String.format("%.1f", staff.getAverageRating()));
            binding.layoutRating.setVisibility(View.VISIBLE);
        } else {
            binding.layoutRating.setVisibility(View.GONE);
        }
        
        // Experience
        if (staff.getTotalCompletedJobs() != null && staff.getTotalCompletedJobs() > 0) {
            binding.tvCompletedJobs.setText(String.format("%d jobs completed", staff.getTotalCompletedJobs()));
        } else {
            binding.tvCompletedJobs.setText("New staff member");
        }
        
        // Hourly rate
        if (staff.getHourlyRate() != null && staff.getHourlyRate() > 0) {
            binding.tvHourlyRate.setText(String.format("$%.0f/hour", staff.getHourlyRate()));
        } else {
            binding.tvHourlyRate.setText("Rate to be determined");
        }
        
        // Service specialization
        if (staff.getServiceName() != null && !staff.getServiceName().isEmpty()) {
            binding.tvSpecialization.setText(staff.getServiceName());
            binding.layoutSpecialization.setVisibility(View.VISIBLE);
        } else {
            binding.layoutSpecialization.setVisibility(View.GONE);
        }
        
        // Availability status
        binding.tvAvailabilityStatus.setText(staff.isAvailable() ? "Available" : "Busy");
        binding.tvAvailabilityStatus.setTextColor(
            getResources().getColor(staff.isAvailable() ? R.color.primary : R.color.gray, null)
        );
        
        // Set default profile image
        binding.ivStaffProfile.setImageResource(R.drawable.ic_worker);
    }
    
    private void loadStaffProfileFromApi(String employeeId) {
        if (!NetworkUtils.isNetworkAvailable(requireContext())) {
            showError("No internet connection");
            return;
        }
        
        showLoading(true);
        
        StaffApiService staffApiService = RetrofitClient.getStaffApiService();
        Call<ApiResponse<GetStaffProfileResponse>> call = staffApiService.getStaffProfileByEmployeeId(employeeId);
        
        call.enqueue(new Callback<ApiResponse<GetStaffProfileResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<GetStaffProfileResponse>> call, @NonNull Response<ApiResponse<GetStaffProfileResponse>> response) {
                if (!isAdded()) return;
                
                showLoading(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<GetStaffProfileResponse> apiResponse = response.body();
                    if (apiResponse.isSucceeded() && apiResponse.getData() != null) {
                        displayApiStaffProfile(apiResponse.getData());
                    } else {
                        showError("Staff profile not found");
                    }
                } else {
                    showError("Failed to load staff profile");
                }
            }
            
            @Override
            public void onFailure(@NonNull Call<ApiResponse<GetStaffProfileResponse>> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                
                showLoading(false);
                showError("Network error: " + t.getMessage());
            }
        });
    }
    
    private void displayApiStaffProfile(GetStaffProfileResponse staff) {
        // Display only the fields that come from the GetStaffProfileResponse API:
        // firstName, lastName, profileImageUrl, dateOfBirth, gender, 
        // emergencyContactName, emergencyContactPhone, skills, bio
        
        // Update name if we have firstName and lastName
        if (staff.getFirstName() != null && staff.getLastName() != null) {
            String fullName = staff.getFirstName() + " " + staff.getLastName();
            binding.tvStaffName.setText(fullName);
        }
        
        // Skills from API
        if (staff.getSkills() != null && !staff.getSkills().isEmpty()) {
            binding.tvSkills.setText(staff.getSkills());
            binding.layoutSkills.setVisibility(View.VISIBLE);
        } else {
            binding.layoutSkills.setVisibility(View.GONE);
        }
        
        // Bio from API
        if (staff.getBio() != null && !staff.getBio().isEmpty()) {
            binding.tvBio.setText(staff.getBio());
            binding.layoutBio.setVisibility(View.VISIBLE);
        } else {
            binding.layoutBio.setVisibility(View.GONE);
        }
        
        // Date of birth
        if (staff.getDateOfBirth() != null && !staff.getDateOfBirth().isEmpty()) {
            try {
                // Parse the date string (assuming ISO format like "1990-01-01T00:00:00")
                java.text.SimpleDateFormat inputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault());
                java.util.Date date = inputFormat.parse(staff.getDateOfBirth());
                if (date != null) {
                    String formattedDate = android.text.format.DateFormat.format("MMM dd, yyyy", date).toString();
                    binding.tvDateOfBirth.setText(formattedDate);
                    binding.layoutDateOfBirth.setVisibility(View.VISIBLE);
                } else {
                    binding.layoutDateOfBirth.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                // If parsing fails, try to display as-is or hide
                binding.tvDateOfBirth.setText(staff.getDateOfBirth());
                binding.layoutDateOfBirth.setVisibility(View.VISIBLE);
            }
        } else {
            binding.layoutDateOfBirth.setVisibility(View.GONE);
        }
        
        // Gender
        if (staff.getGender() != null && !staff.getGender().isEmpty()) {
            binding.tvGender.setText(staff.getGender());
            binding.layoutGender.setVisibility(View.VISIBLE);
        } else {
            binding.layoutGender.setVisibility(View.GONE);
        }
        
        // Emergency contact
        if (staff.getEmergencyContactName() != null && !staff.getEmergencyContactName().isEmpty()) {
            String emergencyContact = staff.getEmergencyContactName();
            if (staff.getEmergencyContactPhone() != null && !staff.getEmergencyContactPhone().isEmpty()) {
                emergencyContact += " (" + staff.getEmergencyContactPhone() + ")";
            }
            binding.tvEmergencyContact.setText(emergencyContact);
            binding.layoutEmergencyContact.setVisibility(View.VISIBLE);
        } else {
            binding.layoutEmergencyContact.setVisibility(View.GONE);
        }
        
        // Hide contact information sections since they're not in GetStaffProfileResponse
        binding.layoutEmail.setVisibility(View.GONE);
        binding.layoutPhone.setVisibility(View.GONE);
        
        // Hide unused fields that are not in the API response
        binding.layoutHireDate.setVisibility(View.GONE);
        binding.layoutServiceRadius.setVisibility(View.GONE);
        binding.layoutExperience.setVisibility(View.GONE);
    }
    
    private void showLoading(boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.scrollViewContent.setVisibility(View.GONE);
            binding.layoutError.setVisibility(View.GONE);
        } else {
            binding.progressBar.setVisibility(View.GONE);
            binding.scrollViewContent.setVisibility(View.VISIBLE);
            binding.layoutError.setVisibility(View.GONE);
        }
    }
    
    private void showError(String message) {
        binding.progressBar.setVisibility(View.GONE);
        binding.scrollViewContent.setVisibility(View.GONE);
        binding.layoutError.setVisibility(View.VISIBLE);
        binding.tvErrorMessage.setText(message);
        
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 