package com.example.prm_project.ui.view.admin;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.prm_project.R;
import com.example.prm_project.data.model.AdminServiceUpdateRequest;
import com.example.prm_project.ui.viewmodel.AdminServiceViewModel;
import com.example.prm_project.utils.SessionManager;

public class AdminEditServiceFragment extends Fragment {
    private EditText etName, etDescription, etBasePrice, etHourlyRate, etEstimatedDuration, etImageUrl, etRequirements, etRestrictions;
    private CheckBox cbIsActive;
    private AdminServiceViewModel viewModel;
    private SessionManager sessionManager;
    private int serviceId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_edit_service, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AdminServiceViewModel.class);
        sessionManager = new SessionManager(requireContext());
        etName = view.findViewById(R.id.et_service_name);
        etDescription = view.findViewById(R.id.et_service_description);
        etBasePrice = view.findViewById(R.id.et_base_price);
        etHourlyRate = view.findViewById(R.id.et_hourly_rate);
        etEstimatedDuration = view.findViewById(R.id.et_estimated_duration);
        etImageUrl = view.findViewById(R.id.et_image_url);
        etRequirements = view.findViewById(R.id.et_requirements);
        etRestrictions = view.findViewById(R.id.et_restrictions);
        cbIsActive = view.findViewById(R.id.cb_is_active);

        if (getArguments() != null) {
            serviceId = getArguments().getInt("serviceId", -1);
            if (serviceId != -1) {
                loadServiceDetail(serviceId);
            }
        }

        view.findViewById(R.id.btn_update_service).setOnClickListener(v -> submitUpdate());
    }

    private void loadServiceDetail(int id) {
        String token = sessionManager.getAccessToken();
        viewModel.getServiceDetail(token, id).observe(getViewLifecycleOwner(), response -> {
            if (response != null && response.data != null) {
                etName.setText(response.data.name);
                etDescription.setText(response.data.description);
                etBasePrice.setText(String.valueOf(response.data.basePrice));
                etHourlyRate.setText(String.valueOf(response.data.hourlyRate));
                etEstimatedDuration.setText(String.valueOf(response.data.estimatedDurationMinutes));
                etImageUrl.setText(response.data.imageUrl);
                etRequirements.setText(response.data.requirements);
                etRestrictions.setText(response.data.restrictions);
                cbIsActive.setChecked(response.data.isActive);
            } else {
                Toast.makeText(requireContext(), "Không lấy được thông tin dịch vụ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitUpdate() {
        String name = etName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String basePriceStr = etBasePrice.getText().toString().trim();
        String hourlyRateStr = etHourlyRate.getText().toString().trim();
        String durationStr = etEstimatedDuration.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();
        String requirements = etRequirements.getText().toString().trim();
        String restrictions = etRestrictions.getText().toString().trim();
        boolean isActive = cbIsActive.isChecked();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(basePriceStr) || TextUtils.isEmpty(hourlyRateStr) || TextUtils.isEmpty(durationStr)) {
            Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show();
            return;
        }

        double basePrice = Double.parseDouble(basePriceStr);
        double hourlyRate = Double.parseDouble(hourlyRateStr);
        int estimatedDuration = Integer.parseInt(durationStr);

        AdminServiceUpdateRequest request = new AdminServiceUpdateRequest(
                name, description, basePrice, hourlyRate, estimatedDuration, imageUrl, requirements, restrictions, isActive
        );
        String token = sessionManager.getAccessToken();
        viewModel.updateService(token, serviceId, request).observe(getViewLifecycleOwner(), response -> {
            if (response != null && response.isSucceeded) {
                Toast.makeText(requireContext(), "Cập nhật dịch vụ thành công!", Toast.LENGTH_SHORT).show();
                requireActivity().onBackPressed();
            } else {
                Toast.makeText(requireContext(), "Cập nhật dịch vụ thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
