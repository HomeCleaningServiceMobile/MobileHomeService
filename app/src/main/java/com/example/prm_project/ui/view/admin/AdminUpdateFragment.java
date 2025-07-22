package com.example.prm_project.ui.view.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.prm_project.R;
import com.example.prm_project.data.model.AdminStaffDetailResponse;
import com.example.prm_project.data.model.AdminStaffUpdateRequest;
import com.example.prm_project.ui.viewmodel.AdminManageStaffViewModel;
import com.example.prm_project.utils.SessionManager;

public class AdminUpdateFragment extends Fragment {
    private EditText edtFirstName, edtLastName, edtEmail, edtPhone, edtSkills, edtBio, edtHourlyRate, edtCertification, edtIdCard;
    private ImageView imgCertificationPreview, imgIdCardPreview;
    private Button btnUpdate;
    private ProgressBar progressBar;
    private AdminManageStaffViewModel staffViewModel;
    private SessionManager sessionManager;
    private int staffId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_update, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        edtFirstName = view.findViewById(R.id.edtFirstName);
        edtLastName = view.findViewById(R.id.edtLastName);
        edtEmail = view.findViewById(R.id.edtEmail);
        edtPhone = view.findViewById(R.id.edtPhone);
        edtSkills = view.findViewById(R.id.edtSkills);
        edtBio = view.findViewById(R.id.edtBio);
        edtHourlyRate = view.findViewById(R.id.edtHourlyRate);
        edtCertification = view.findViewById(R.id.edtCertificationImageUrl);
        edtIdCard = view.findViewById(R.id.edtIdCardImageUrl);
        imgCertificationPreview = view.findViewById(R.id.imgCertificationPreview);
        imgIdCardPreview = view.findViewById(R.id.imgIdCardPreview);
        btnUpdate = view.findViewById(R.id.btnUpdate);
        progressBar = view.findViewById(R.id.progressBar);

        staffViewModel = new ViewModelProvider(this).get(AdminManageStaffViewModel.class);
        sessionManager = new SessionManager(requireContext());

        staffId = getArguments() != null ? getArguments().getInt("staffId", -1) : -1;
        String token = "Bearer " + sessionManager.getAccessToken();

        // Fetch and display staff details
        if (staffId != -1 && token != null) {
            var id = staffId;
            staffViewModel.getStaffDetail(token, staffId).observe(getViewLifecycleOwner(), response -> {
                Log.d("AdminUpdateFragment", "Staff detail response: " + response);
                if (response != null && response.getData() != null) {
                    AdminStaffDetailResponse.StaffDetail staff = response.getData();
                    String fullName = staff.getFullName() != null ? staff.getFullName() : "";
                    String[] nameParts = fullName.split(" ", 2);
                    edtFirstName.setText(nameParts[0]);
                    edtLastName.setText(nameParts.length > 1 ? nameParts[1] : "");
                    edtEmail.setText(staff.getEmail());
                    edtPhone.setText(staff.getPhoneNumber());
                    edtSkills.setText(staff.getSkills());
                    edtBio.setText(staff.getBio());
                    edtCertification.setText(staff.getCertificationImageUrl());
                    edtIdCard.setText(staff.getIdCardImageUrl());
                    edtHourlyRate.setText(String.valueOf(staff.getHourlyRate()));

                    String certificationUrl = staff.getCertificationImageUrl();
                    if (certificationUrl != null && !certificationUrl.isEmpty()) {
                        Glide.with(this)
                                .load(certificationUrl)
                                .placeholder(R.drawable.credit_card_24px)
                                .into(imgCertificationPreview);
                    } else {
                        imgCertificationPreview.setImageResource(R.drawable.card_membership_24px);
                    }

                    String cardIdImageUrl = staff.getIdCardImageUrl();
                    if (cardIdImageUrl != null && !cardIdImageUrl.isEmpty()) {
                        Glide.with(this)
                                .load(cardIdImageUrl)
                                .placeholder(R.drawable.credit_card_24px)
                                .into(imgIdCardPreview);
                    } else {
                        imgIdCardPreview.setImageResource(R.drawable.card_membership_24px);
                    }
                } else {
                    Toast.makeText(requireContext(), "Không thể tải thông tin nhân viên", Toast.LENGTH_SHORT).show();
                }
            });
        }

        btnUpdate.setOnClickListener(v -> {
            if (!validateInput()) return;

            progressBar.setVisibility(View.VISIBLE);
            btnUpdate.setEnabled(false);

            AdminStaffUpdateRequest request = new AdminStaffUpdateRequest();
            request.setFirstName(edtFirstName.getText().toString().trim());
            request.setLastName(edtLastName.getText().toString().trim());
            request.setEmail(edtEmail.getText().toString().trim());
            request.setPhoneNumber(edtPhone.getText().toString().trim());
            request.setSkills(edtSkills.getText().toString().trim());
            request.setBio(edtBio.getText().toString().trim());
            try {
                request.setHourlyRate(Double.parseDouble(edtHourlyRate.getText().toString().trim()));
            } catch (NumberFormatException e) {
                request.setHourlyRate(0.0);
            }
            request.setCertificationImageUrl(edtCertification.getText().toString().trim());
            request.setIdCardImageUrl(edtIdCard.getText().toString().trim());

            staffViewModel.updateStaff(token, staffId, request).observe(getViewLifecycleOwner(), response -> {
                progressBar.setVisibility(View.GONE);
                btnUpdate.setEnabled(true);

                if (response != null && response.isSucceeded()) {
                    Toast.makeText(requireContext(), "Update thành công", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                } else {
                    Toast.makeText(requireContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private boolean validateInput() {
        // Add input validation if needed
        return true;
    }
}