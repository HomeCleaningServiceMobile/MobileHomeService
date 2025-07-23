package com.example.prm_project.ui.view.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_project.R;
import com.example.prm_project.data.model.StaffAvailabilityResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class StaffSelectionDialog extends Dialog {
    
    private List<StaffAvailabilityResponse> staffList;
    private List<StaffAvailabilityResponse> filteredStaffList;
    private StaffDialogAdapter adapter;
    private OnStaffSelectedListener listener;
    private EditText etSearch;
    private RecyclerView rvStaff;
    private ProgressBar progressBar;
    private TextView tvNoStaff;
    private TextView tvLoadingText;
    
    public interface OnStaffSelectedListener {
        void onStaffSelected(StaffAvailabilityResponse staff);
    }
    
    public StaffSelectionDialog(@NonNull Context context, List<StaffAvailabilityResponse> staffList) {
        super(context);
        this.staffList = staffList != null ? staffList : new ArrayList<>();
        this.filteredStaffList = new ArrayList<>(this.staffList);
    }
    
    public void setOnStaffSelectedListener(OnStaffSelectedListener listener) {
        this.listener = listener;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_staff_selection);
        
        // Make dialog full width
        if (getWindow() != null) {
            getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        
        initViews();
        setupRecyclerView();
        setupSearch();
        updateUI();
    }
    
    private void initViews() {
        etSearch = findViewById(R.id.et_search_staff);
        rvStaff = findViewById(R.id.rv_staff_dialog);
        progressBar = findViewById(R.id.progress_staff);
        tvNoStaff = findViewById(R.id.tv_no_staff);
        tvLoadingText = findViewById(R.id.tv_loading_text);
        
        MaterialButton btnClose = findViewById(R.id.btn_close_dialog);
        btnClose.setOnClickListener(v -> dismiss());
    }
    
    private void setupRecyclerView() {
        adapter = new StaffDialogAdapter();
        rvStaff.setLayoutManager(new LinearLayoutManager(getContext()));
        rvStaff.setAdapter(adapter);
    }
    
    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterStaff(s.toString());
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    
    private void filterStaff(String query) {
        filteredStaffList.clear();
        
        if (query.trim().isEmpty()) {
            filteredStaffList.addAll(staffList);
        } else {
            String lowerQuery = query.toLowerCase().trim();
            for (StaffAvailabilityResponse staff : staffList) {
                if (staff.getStaffName().toLowerCase().contains(lowerQuery) ||
                    (staff.getEmployeeId() != null && staff.getEmployeeId().toLowerCase().contains(lowerQuery))) {
                    filteredStaffList.add(staff);
                }
            }
        }
        
        adapter.notifyDataSetChanged();
        updateUI();
    }
    
    private void updateUI() {
        if (filteredStaffList.isEmpty()) {
            rvStaff.setVisibility(View.GONE);
            tvNoStaff.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            tvLoadingText.setVisibility(View.GONE);
        } else {
            rvStaff.setVisibility(View.VISIBLE);
            tvNoStaff.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            tvLoadingText.setVisibility(View.GONE);
        }
    }
    
    public void showLoading() {
        rvStaff.setVisibility(View.GONE);
        tvNoStaff.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        tvLoadingText.setVisibility(View.VISIBLE);
    }
    
    public void updateStaffList(List<StaffAvailabilityResponse> newStaffList) {
        this.staffList = newStaffList != null ? newStaffList : new ArrayList<>();
        this.filteredStaffList.clear();
        this.filteredStaffList.addAll(this.staffList);
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        updateUI();
    }
    
    private class StaffDialogAdapter extends RecyclerView.Adapter<StaffDialogAdapter.StaffViewHolder> {
        
        @NonNull
        @Override
        public StaffViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.item_staff_dialog, parent, false);
            return new StaffViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(@NonNull StaffViewHolder holder, int position) {
            holder.bind(filteredStaffList.get(position));
        }
        
        @Override
        public int getItemCount() {
            return filteredStaffList.size();
        }
        
        class StaffViewHolder extends RecyclerView.ViewHolder {
            
            private MaterialCardView cardContainer;
            private ImageView ivStaffAvatar;
            private TextView tvStaffName;
            private TextView tvStaffRating;
            private TextView tvStaffSpecialization;
            private TextView tvStaffExperience;
            
            public StaffViewHolder(@NonNull View itemView) {
                super(itemView);
                cardContainer = itemView.findViewById(R.id.card_staff_item);
                ivStaffAvatar = itemView.findViewById(R.id.iv_staff_avatar);
                tvStaffName = itemView.findViewById(R.id.tv_staff_name);
                tvStaffRating = itemView.findViewById(R.id.tv_staff_rating);
                tvStaffSpecialization = itemView.findViewById(R.id.tv_staff_specialization);
                tvStaffExperience = itemView.findViewById(R.id.tv_staff_experience);
                
                itemView.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onStaffSelected(filteredStaffList.get(position));
                        dismiss();
                    }
                });
            }
            
            public void bind(StaffAvailabilityResponse staff) {
                tvStaffName.setText(staff.getStaffName());
                
                // Format rating
                if (staff.getAverageRating() != null && staff.getAverageRating() > 0) {
                    tvStaffRating.setText(String.format("⭐ %.1f rating", staff.getAverageRating()));
                    tvStaffRating.setVisibility(View.VISIBLE);
                } else {
                    tvStaffRating.setText("⭐ New staff");
                    tvStaffRating.setVisibility(View.VISIBLE);
                }
                
                // Specialization
                if (staff.getEmployeeId() != null && !staff.getEmployeeId().isEmpty()) {
                    tvStaffSpecialization.setText(staff.getEmployeeId());
                    tvStaffSpecialization.setVisibility(View.VISIBLE);
                } else {
                    tvStaffSpecialization.setVisibility(View.GONE);
                }
                
                // Experience
                if (staff.getTotalCompletedJobs() != null && staff.getTotalCompletedJobs() > 0) {
                    tvStaffExperience.setText(String.format("%d years experience", staff.getTotalCompletedJobs()));
                    tvStaffExperience.setVisibility(View.VISIBLE);
                } else {
                    tvStaffExperience.setVisibility(View.GONE);
                }
                
                // Set default avatar
                ivStaffAvatar.setImageResource(R.drawable.ic_worker);
            }
        }
    }
} 