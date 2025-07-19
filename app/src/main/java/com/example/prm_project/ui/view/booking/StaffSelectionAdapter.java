package com.example.prm_project.ui.view.booking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_project.R;
import com.example.prm_project.data.model.StaffAvailabilityDto;

import java.util.ArrayList;
import java.util.List;

public class StaffSelectionAdapter extends RecyclerView.Adapter<StaffSelectionAdapter.StaffViewHolder> {
    
    private List<StaffAvailabilityDto> staffList = new ArrayList<>();
    private OnStaffClickListener listener;
    private int selectedPosition = -1;
    
    public interface OnStaffClickListener {
        void onStaffClick(StaffAvailabilityDto staff, int position);
    }
    
    public void setOnStaffClickListener(OnStaffClickListener listener) {
        this.listener = listener;
    }
    
    public void setStaffList(List<StaffAvailabilityDto> staffList) {
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
    
    public StaffAvailabilityDto getSelectedStaff() {
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
        private TextView tvStaffName;
        private TextView tvPosition;
        private View rootView;
        
        public StaffViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = itemView;
            tvStaffName = itemView.findViewById(R.id.tv_staff_name);
            tvPosition = itemView.findViewById(R.id.tv_position);
            
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onStaffClick(staffList.get(position), position);
                }
            });
        }
        
        public void bind(StaffAvailabilityDto staff, boolean isSelected) {
            tvStaffName.setText(staff.getStaffName());
            tvPosition.setText(staff.getPosition());
            
            // Update background based on selection state
            if (isSelected) {
                rootView.setBackgroundResource(R.drawable.selected_staff_background);
            } else {
                rootView.setBackgroundResource(R.drawable.unselected_staff_background);
            }
        }
    }
} 