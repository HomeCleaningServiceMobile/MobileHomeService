package com.example.prm_project.ui.view.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.prm_project.R;
import com.google.android.material.button.MaterialButton;

public class AdminDashboardFragment extends Fragment {
    
    private MaterialButton btnManageStaff;
    private MaterialButton btnManageService;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_dashboard, container, false);
        
        initViews(view);
        setupClickListeners();
        
        return view;
    }
    
    private void initViews(View view) {
        btnManageStaff = view.findViewById(R.id.btn_manage_staff);
        btnManageService = view.findViewById(R.id.btn_manage_service);
    }
    
    private void setupClickListeners() {
        btnManageStaff.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_adminDashboardFragment_to_manageStaffFragment);
        });
        
        btnManageService.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_adminDashboardFragment_to_adminServiceListFragment);
        });
    }
}
