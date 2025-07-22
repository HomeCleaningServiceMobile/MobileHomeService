// file: ui/view/admin/ManageStaffFragment.java
package com.example.prm_project.ui.view.admin;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.prm_project.R;
import com.example.prm_project.ui.viewmodel.AdminManageStaffViewModel;
import com.example.prm_project.utils.SessionManager;
import java.util.Collections;

public class ManageStaffFragment extends Fragment {
    private AdminManageStaffViewModel adminManageStaffViewModel;
    private StaffAdapter staffAdapter;
    private SessionManager sessionManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_manage_staff, container, false);
        RecyclerView rv = view.findViewById(R.id.rv_staff_list);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        staffAdapter = new StaffAdapter(
                Collections.emptyList(),
                staffId -> {
                    Bundle bundle = new Bundle();
                    bundle.putInt("staffId", staffId);
                    // Navigate to AdminStaffDetailFragment
                    androidx.navigation.fragment.NavHostFragment.findNavController(this)
                            .navigate(R.id.action_manageStaffFragment_to_adminStaffDetailFragment, bundle);
                });
        rv.setAdapter(staffAdapter);

        // Initialize ViewModel
        adminManageStaffViewModel = new ViewModelProvider(this).get(AdminManageStaffViewModel.class);

        // Get access token from SessionManager
        String token = sessionManager.getAccessToken();
        if (token != null) {
            adminManageStaffViewModel.getStaffList("Bearer " + token).observe(getViewLifecycleOwner(), response -> {
                if (response != null && response.data != null && response.data.data != null) {
                    staffAdapter.setStaffList(response.data.data);
                } else {
                    staffAdapter.setStaffList(Collections.emptyList());
                }
            });
        } else {
            staffAdapter.setStaffList(Collections.emptyList());
        }

        return view;
    }
}