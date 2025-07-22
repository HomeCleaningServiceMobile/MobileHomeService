package com.example.prm_project.ui.view.admin;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.prm_project.R;
import com.example.prm_project.data.model.AdminServiceCreateRequest;
import com.example.prm_project.ui.viewmodel.AdminServiceViewModel;
import com.example.prm_project.utils.SessionManager;

public class AdminAddServiceFragment extends Fragment {
    private EditText etName, etDescription, etType, etBasePrice, etHourlyRate, etEstimatedDuration, etImageUrl, etRequirements, etRestrictions;
    private AdminServiceViewModel viewModel;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_add_service, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AdminServiceViewModel.class);
        sessionManager = new SessionManager(requireContext());
        etName = view.findViewById(R.id.et_service_name);
        etDescription = view.findViewById(R.id.et_service_description);
        etType = view.findViewById(R.id.et_service_type);
        etBasePrice = view.findViewById(R.id.et_base_price);
        etHourlyRate = view.findViewById(R.id.et_hourly_rate);
        etEstimatedDuration = view.findViewById(R.id.et_estimated_duration);
        etImageUrl = view.findViewById(R.id.et_image_url);
        etRequirements = view.findViewById(R.id.et_requirements);
        etRestrictions = view.findViewById(R.id.et_restrictions);

        view.findViewById(R.id.btn_submit_service).setOnClickListener(v -> submitService());
    }

    private void submitService() {
        String name = etName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String typeStr = etType.getText().toString().trim();
        String basePriceStr = etBasePrice.getText().toString().trim();
        String hourlyRateStr = etHourlyRate.getText().toString().trim();
        String durationStr = etEstimatedDuration.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();
        String requirements = etRequirements.getText().toString().trim();
        String restrictions = etRestrictions.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(typeStr) || TextUtils.isEmpty(basePriceStr) || TextUtils.isEmpty(hourlyRateStr) || TextUtils.isEmpty(durationStr)) {
            Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show();
            return;
        }

        int type = Integer.parseInt(typeStr);
        double basePrice = Double.parseDouble(basePriceStr);
        double hourlyRate = Double.parseDouble(hourlyRateStr);
        int estimatedDuration = Integer.parseInt(durationStr);

        AdminServiceCreateRequest request = new AdminServiceCreateRequest(
                name, description, type, basePrice, hourlyRate, estimatedDuration, imageUrl, requirements, restrictions
        );
        String token = sessionManager.getAccessToken();
        if (token == null) {
            Toast.makeText(requireContext(), "Phiên đăng nhập đã hết hạn", Toast.LENGTH_SHORT).show();
            return;
        }
        viewModel.addService(token, request).observe(getViewLifecycleOwner(), response -> {
            if (response != null && response.isSucceeded) {
                Toast.makeText(requireContext(), "Tạo dịch vụ thành công!", Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack();
            } else {
                Toast.makeText(requireContext(), "Tạo dịch vụ thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

