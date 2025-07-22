package com.example.prm_project.ui.view.booking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_project.R;
import com.example.prm_project.data.model.StaffAvailabilityResponse;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StaffSelectionAdapter extends RecyclerView.Adapter<StaffSelectionAdapter.StaffViewHolder> {
    
    private List<StaffAvailabilityResponse> staffList = new ArrayList<>();
    private OnStaffClickListener listener;
    private OnStaffProfileClickListener profileClickListener;
    private int selectedPosition = -1;
    
    public interface OnStaffClickListener {
        void onStaffClick(StaffAvailabilityResponse staff, int position);
    }
    
    public interface OnStaffProfileClickListener {
        void onStaffProfileClick(StaffAvailabilityResponse staff);
    }
    
    public void setOnStaffClickListener(OnStaffClickListener listener) {
        this.listener = listener;
    }
    
    public void setOnStaffProfileClickListener(OnStaffProfileClickListener listener) {
        this.profileClickListener = listener;
    }
    
    public void setStaffList(List<StaffAvailabilityResponse> staffList) {
        this.staffList = staffList != null ? staffList : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    public void setSelectedPosition(int position) {
        int previousPosition = selectedPosition;
        selectedPosition = position;
        
        if (previousPosition != -1) {
            notifyItemChanged(previousPosition);
        }
        if (selectedPosition != -1) {
            notifyItemChanged(selectedPosition);
        }
    }
    
    public StaffAvailabilityResponse getSelectedStaff() {
        if (selectedPosition >= 0 && selectedPosition < staffList.size()) {
            return staffList.get(selectedPosition);
        }
        return null;
    }
    
    @NonNull
    @Override
    public StaffViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_staff_selection, parent, false);
        return new StaffViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull StaffViewHolder holder, int position) {
        holder.bind(staffList.get(position), position == selectedPosition);
    }
    
    @Override
    public int getItemCount() {
        return staffList.size();
    }
    
    class StaffViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivStaffProfile;
        private TextView tvStaffName;
        private TextView tvPosition;
        private TextView tvSkills;
        private TextView tvStaffRating;
        private TextView tvCompletedJobs;
        private TextView tvHourlyRate;
        private MaterialButton btnViewProfile;
        private View rootView;
        private View viewSelectionIndicator;
        
        public StaffViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = itemView;
            ivStaffProfile = itemView.findViewById(R.id.iv_staff_profile);
            tvStaffName = itemView.findViewById(R.id.tv_staff_name);
            tvPosition = itemView.findViewById(R.id.tv_position);
            tvSkills = itemView.findViewById(R.id.tv_skills);
            tvStaffRating = itemView.findViewById(R.id.tv_staff_rating);
            tvCompletedJobs = itemView.findViewById(R.id.tv_completed_jobs);
            tvHourlyRate = itemView.findViewById(R.id.tv_hourly_rate);
            btnViewProfile = itemView.findViewById(R.id.btn_view_profile);
            viewSelectionIndicator = itemView.findViewById(R.id.view_selection_indicator);
            
            // Staff selection click
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onStaffClick(staffList.get(position), position);
                }
            });
            
            // Profile button click
            btnViewProfile.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && profileClickListener != null) {
                    profileClickListener.onStaffProfileClick(staffList.get(position));
                }
            });
        }
        
        public void bind(StaffAvailabilityResponse staff, boolean isSelected) {
            // Basic info
            tvStaffName.setText(staff.getStaffName() != null ? staff.getStaffName() : "Unknown Staff");
            tvPosition.setText(getPositionText(staff));
            
            // Skills - use service name if available, otherwise generic text
            if (staff.getServiceName() != null && !staff.getServiceName().isEmpty()) {
                tvSkills.setText("Specializes in " + staff.getServiceName());
            } else {
                tvSkills.setText("Multi-service technician");
            }
            
            // Rating
            if (staff.getAverageRating() != null && staff.getAverageRating() > 0) {
                tvStaffRating.setText(String.format(Locale.getDefault(), "%.1f", staff.getAverageRating()));
            } else {
                tvStaffRating.setText("New");
            }
            
            // Completed jobs
            if (staff.getTotalCompletedJobs() != null && staff.getTotalCompletedJobs() > 0) {
                tvCompletedJobs.setText(String.format(Locale.getDefault(), "%d jobs completed", staff.getTotalCompletedJobs()));
            } else {
                tvCompletedJobs.setText("New staff member");
            }
            
            // Hourly rate
            if (staff.getHourlyRate() != null && staff.getHourlyRate() > 0) {
                tvHourlyRate.setText(String.format(Locale.getDefault(), "$%.0f/hr", staff.getHourlyRate()));
            } else {
                tvHourlyRate.setText("Rate TBD");
            }
            
            // Profile picture - use placeholder for now
            ivStaffProfile.setImageResource(R.drawable.ic_worker);
            
            // Selection state
            if (isSelected) {
                rootView.setBackgroundResource(R.drawable.selected_staff_background);
                viewSelectionIndicator.setVisibility(View.VISIBLE);
            } else {
                rootView.setBackgroundResource(R.drawable.unselected_staff_background);
                viewSelectionIndicator.setVisibility(View.GONE);
            }
            
            // Availability state
            if (!staff.isAvailable()) {
                rootView.setEnabled(false);
                rootView.setAlpha(0.6f);
                btnViewProfile.setEnabled(false);
            } else {
                rootView.setEnabled(true);
                rootView.setAlpha(1.0f);
                btnViewProfile.setEnabled(true);
            }
        }
        
        private String getPositionText(StaffAvailabilityResponse staff) {
            // Generate position based on rating and experience
            if (staff.getAverageRating() != null && staff.getAverageRating() >= 4.5) {
                return "Senior Technician";
            } else if (staff.getAverageRating() != null && staff.getAverageRating() >= 4.0) {
                return "Experienced Technician";
            } else if (staff.getTotalCompletedJobs() != null && staff.getTotalCompletedJobs() > 50) {
                return "Certified Technician";
            } else {
                return "Technician";
            }
        }
    }
} 