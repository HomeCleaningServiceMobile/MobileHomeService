package com.example.prm_project.ui.view.admin;

import android.app.DatePickerDialog;
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
import com.example.prm_project.data.model.AdminCreateStaffRequest;
import com.example.prm_project.ui.viewmodel.AdminManageStaffViewModel;
import com.example.prm_project.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AdminAddStaffFragment extends Fragment {
    private EditText edtFirstName, edtLastName, edtEmail, edtPhone, edtPassword, edtEmployeeId;
    private EditText edtHireDate, edtSkills, edtBio, edtHourlyRate, edtServiceRadius;
    private EditText edtFullAddress, edtStreet, edtDistrict, edtCity, edtProvince, edtPostalCode;
    private EditText edtCertification, edtIdCard;
    private ImageView imgCertificationPreview, imgIdCardPreview;
    private Button btnAdd, btnSelectHireDate;
    private ProgressBar progressBar;
    private AdminManageStaffViewModel staffViewModel;
    private SessionManager sessionManager;

    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_add_staff, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupViewModel();
        setupListeners();
    }

    private void initViews(View view) {
        edtFirstName = view.findViewById(R.id.edtFirstName);
        edtLastName = view.findViewById(R.id.edtLastName);
        edtEmail = view.findViewById(R.id.edtEmail);
        edtPhone = view.findViewById(R.id.edtPhone);
        edtPassword = view.findViewById(R.id.edtPassword);
        edtEmployeeId = view.findViewById(R.id.edtEmployeeId);

        edtHireDate = view.findViewById(R.id.edtHireDate);
        edtSkills = view.findViewById(R.id.edtSkills);
        edtBio = view.findViewById(R.id.edtBio);
        edtHourlyRate = view.findViewById(R.id.edtHourlyRate);
        edtServiceRadius = view.findViewById(R.id.edtServiceRadius);

        edtFullAddress = view.findViewById(R.id.edtFullAddress);
        edtStreet = view.findViewById(R.id.edtStreet);
        edtDistrict = view.findViewById(R.id.edtDistrict);
        edtCity = view.findViewById(R.id.edtCity);
        edtProvince = view.findViewById(R.id.edtProvince);
        edtPostalCode = view.findViewById(R.id.edtPostalCode);

        edtCertification = view.findViewById(R.id.edtCertificationImageUrl);
        edtIdCard = view.findViewById(R.id.edtIdCardImageUrl);
        imgCertificationPreview = view.findViewById(R.id.imgCertificationPreview);
        imgIdCardPreview = view.findViewById(R.id.imgIdCardPreview);

        btnAdd = view.findViewById(R.id.btnAdd);
        btnSelectHireDate = view.findViewById(R.id.btnSelectHireDate);
        progressBar = view.findViewById(R.id.progressBar);
    }

    private void setupViewModel() {
        staffViewModel = new ViewModelProvider(this).get(AdminManageStaffViewModel.class);
        sessionManager = new SessionManager(requireContext());
    }

    private void setupListeners() {
        btnSelectHireDate.setOnClickListener(v -> showDatePicker());
        btnAdd.setOnClickListener(v -> addStaff());

        edtCertification.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus)
                loadImagePreview(edtCertification.getText().toString(), imgCertificationPreview);
        });

        edtIdCard.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) loadImagePreview(edtIdCard.getText().toString(), imgIdCardPreview);
        });
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    edtHireDate.setText(dateFormat.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void loadImagePreview(String imageUrl, ImageView imageView) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.credit_card_24px)
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.card_membership_24px);
        }
    }

    private void addStaff() {
        Log.d("AdminAddStaffFragment", "addStaff() called");
        if (!validateInput()) {
            Log.e("AdminAddStaffFragment", "Validation failed, API not called");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnAdd.setEnabled(false);

        AdminCreateStaffRequest request = createStaffRequest();
        Log.d("AdminAddStaffFragment", "Request: " + request.toString());
        String token = "Bearer " + sessionManager.getAccessToken();
        Log.d("AdminAddStaffFragment", "Token: " + token);

        staffViewModel.createStaff(token, request).observe(getViewLifecycleOwner(), response -> {
            progressBar.setVisibility(View.GONE);
            btnAdd.setEnabled(true);

            if (response != null && response.isSucceeded()) {
                Log.d("AdminAddStaffFragment", "Staff creation succeeded");
                Toast.makeText(requireContext(), "Thêm nhân viên thành công", Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack();
            } else {
                Log.e("AdminAddStaffFragment", "Add staff failed: " + response);
                if (response != null) {
                    Log.e("AdminAddStaffFragment", "Response message: " + response.getMessages());
                }
                Toast.makeText(requireContext(), "Thêm nhân viên thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private AdminCreateStaffRequest createStaffRequest() {
        AdminCreateStaffRequest request = new AdminCreateStaffRequest();

        request.setFirstName(edtFirstName.getText().toString().trim());
        request.setLastName(edtLastName.getText().toString().trim());
        request.setEmail(edtEmail.getText().toString().trim());
        request.setPhoneNumber(edtPhone.getText().toString().trim());
        request.setPassword(edtPassword.getText().toString().trim());
        request.setEmployeeId(edtEmployeeId.getText().toString().trim());

        request.setHireDate(edtHireDate.getText().toString().trim());
        request.setSkills(edtSkills.getText().toString().trim());
        request.setBio(edtBio.getText().toString().trim());

        try {
            request.setHourlyRate(Double.parseDouble(edtHourlyRate.getText().toString().trim()));
        } catch (NumberFormatException e) {
            request.setHourlyRate(0.0);
        }

        try {
            request.setServiceRadiusKm(Integer.parseInt(edtServiceRadius.getText().toString().trim()));
        } catch (NumberFormatException e) {
            request.setServiceRadiusKm(0);
        }

        request.setFullAddress(edtFullAddress.getText().toString().trim());
        request.setStreet(edtStreet.getText().toString().trim());
        request.setDistrict(edtDistrict.getText().toString().trim());
        request.setCity(edtCity.getText().toString().trim());
        request.setProvince(edtProvince.getText().toString().trim());
        request.setPostalCode(edtPostalCode.getText().toString().trim());

        request.setCertificationImageUrl(edtCertification.getText().toString().trim());
        request.setIdCardImageUrl(edtIdCard.getText().toString().trim());

        return request;
    }

    private boolean validateInput() {
        if (edtFirstName.getText().toString().trim().isEmpty()) {
            edtFirstName.setError("Tên không được để trống");
            return false;
        }
        if (edtLastName.getText().toString().trim().isEmpty()) {
            edtLastName.setError("Họ không được để trống");
            return false;
        }
        if (edtEmail.getText().toString().trim().isEmpty()) {
            edtEmail.setError("Email không được để trống");
            return false;
        }
        if (edtPhone.getText().toString().trim().isEmpty()) {
            edtPhone.setError("Số điện thoại không được để trống");
            return false;
        }
        if (edtPassword.getText().toString().trim().isEmpty()) {
            edtPassword.setError("Mật khẩu không được để trống");
            return false;
        }
        if (edtEmployeeId.getText().toString().trim().isEmpty()) {
            edtEmployeeId.setError("Mã nhân viên không được để trống");
            return false;
        }
        if (edtHireDate.getText().toString().trim().isEmpty()) {
            edtHireDate.setError("Ngày tuyển dụng không được để trống");
            return false;
        }
        return true;
    }
}