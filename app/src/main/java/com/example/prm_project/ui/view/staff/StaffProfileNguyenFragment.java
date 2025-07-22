package com.example.prm_project.ui.view.staff;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.widget.TextView;
import androidx.lifecycle.ViewModelProvider;
import com.example.prm_project.data.model.User;
import com.example.prm_project.ui.viewmodel.AuthViewModel;

import com.example.prm_project.R;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class StaffProfileNguyenFragment extends Fragment {

    private AuthViewModel authViewModel;
    private TextView tvName, tvRole, tvEmail, tvPhone, tvSkills, tvExperience;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_staff_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        tvName = view.findViewById(R.id.tvStaffName);
        tvRole = view.findViewById(R.id.tvStaffRole);
        tvEmail = view.findViewById(R.id.tvProfileEmail);
        tvPhone = view.findViewById(R.id.tvProfilePhone);
        tvSkills = view.findViewById(R.id.tvProfileSkills);
        tvExperience = view.findViewById(R.id.tvProfileExperience);
        // Gọi API lấy profile
        authViewModel.loadProfile();
        authViewModel.getProfileLiveData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                String fullName = user.getFirstName() != null ? user.getFirstName() : "";
                if (user.getLastName() != null && !user.getLastName().isEmpty()) {
                    fullName += " " + user.getLastName();
                }
                tvName.setText(fullName);
                tvRole.setText("Senior Service Staff"); // Hoặc user.getRole() nếu muốn
                tvEmail.setText(user.getEmail());
                tvPhone.setText(user.getPhoneNumber());
                tvSkills.setText("N/A"); // Nếu backend chưa trả về skills
                tvExperience.setText("N/A"); // Nếu backend chưa trả về experience
            }
        });
    }
}