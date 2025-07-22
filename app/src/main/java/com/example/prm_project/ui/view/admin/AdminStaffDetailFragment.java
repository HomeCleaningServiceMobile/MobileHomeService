package com.example.prm_project.ui.view.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.prm_project.R;
import com.example.prm_project.data.model.AdminStaffDetailResponse;
import com.example.prm_project.ui.viewmodel.AdminManageStaffViewModel;
import com.example.prm_project.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AdminStaffDetailFragment extends Fragment {
    private AdminManageStaffViewModel viewModel;

    private ImageView imgProfile, imgCertification, imgIdCard;
    private TextView tvFullName, tvBio, tvEmail, tvPhone, tvAddress, tvSkills, tvHourlyRate, tvHireDate, tvStatus;

    private SessionManager sessionManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_staff_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imgProfile = view.findViewById(R.id.imgProfile);
        imgCertification = view.findViewById(R.id.imgCertification);
        imgIdCard = view.findViewById(R.id.imgIdCard);
        tvFullName = view.findViewById(R.id.tvFullName);
        tvBio = view.findViewById(R.id.tvBio);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvPhone = view.findViewById(R.id.tvPhone);
        tvAddress = view.findViewById(R.id.tvAddress);
        tvSkills = view.findViewById(R.id.tvSkills);
        tvHourlyRate = view.findViewById(R.id.tvHourlyRate);
        tvHireDate = view.findViewById(R.id.tvHireDate);
        tvStatus = view.findViewById(R.id.tvStatus);

        viewModel = new ViewModelProvider(this).get(AdminManageStaffViewModel.class);

        // Lấy staffId từ arguments
        Bundle args = getArguments();
        if (args != null) {
            int staffId = args.getInt("staff_id", -1);
            if (staffId != -1) {
                loadStaffDetail(staffId);
            }
        }
    }

    private void loadStaffDetail(int staffId) {
        String token = sessionManager.getAccessToken();
        token = "Bearer " + token;
        if (token != null) {
            viewModel.getStaffDetail(token, staffId).observe(getViewLifecycleOwner(), response -> {
                if (response != null && response.getData() != null) {
                    AdminStaffDetailResponse.StaffDetail staff = response.getData();

                    tvFullName.setText(staff.getFullName());
                    tvBio.setText(staff.getBio());
                    tvEmail.setText(staff.getEmail());
                    tvPhone.setText(staff.getPhoneNumber());
                    tvAddress.setText(staff.getFullAddress());
                    tvSkills.setText(staff.getSkills());
                    tvHourlyRate.setText(String.valueOf(staff.getHourlyRate()));
                    tvHireDate.setText(staff.getHireDate());
                    tvStatus.setText(staff.isAvailable() ? "Available" : "Unavailable");

                    String originalDateString = staff.getHireDate(); // ví dụ: "2025-07-10T17:57:40.94608"

                    try {
                        // Định dạng đầu vào
                        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

                        // Chuyển từ chuỗi sang Date
                        Date date = inputFormat.parse(originalDateString);

                        // Định dạng đầu ra
                        SimpleDateFormat outputFormat = new SimpleDateFormat("MM-dd-yyyy");
                        String formattedDate = outputFormat.format(date);

                        tvHireDate.setText(formattedDate);
                    } catch (Exception e) {
                        e.printStackTrace();
                        tvHireDate.setText("Invalid date");
                    }

                    Glide.with(this)
                            .load(staff.getProfileImageUrl())
                            .placeholder(R.drawable.ic_profile)
                            .into(imgProfile);

                    Glide.with(this)
                            .load(staff.getCertificationImageUrl())
                            .placeholder(R.drawable.card_membership_24px)
                            .into(imgCertification);

                    Glide.with(this)
                            .load(staff.getIdCardImageUrl())
                            .placeholder(R.drawable.credit_card_24px)
                            .into(imgIdCard);
                }
            });
        }
    }
}