package com.example.prm_project.ui.view.booking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_project.R;
import com.example.prm_project.data.model.TimeSlotDto;

import java.util.ArrayList;
import java.util.List;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder> {
    
    private List<TimeSlotDto> timeSlots = new ArrayList<>();
    private OnTimeSlotClickListener listener;
    private int selectedPosition = -1;
    
    public interface OnTimeSlotClickListener {
        void onTimeSlotClick(TimeSlotDto timeSlot, int position);
    }
    
    public void setOnTimeSlotClickListener(OnTimeSlotClickListener listener) {
        this.listener = listener;
    }
    
    public void setTimeSlots(List<TimeSlotDto> timeSlots) {
        this.timeSlots = timeSlots != null ? timeSlots : new ArrayList<>();
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
    
    public TimeSlotDto getSelectedTimeSlot() {
        if (selectedPosition >= 0 && selectedPosition < timeSlots.size()) {
            return timeSlots.get(selectedPosition);
        }
        return null;
    }
    
    @NonNull
    @Override
    public TimeSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_time_slot, parent, false);
        return new TimeSlotViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull TimeSlotViewHolder holder, int position) {
        holder.bind(timeSlots.get(position), position == selectedPosition);
    }
    
    @Override
    public int getItemCount() {
        return timeSlots.size();
    }
    
    class TimeSlotViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTime;
        private TextView tvStaffCount;
        private View rootView;
        
        public TimeSlotViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = itemView;
            tvTime = itemView.findViewById(R.id.tv_time);
            tvStaffCount = itemView.findViewById(R.id.tv_staff_count);
            
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onTimeSlotClick(timeSlots.get(position), position);
                }
            });
        }
        
        public void bind(TimeSlotDto timeSlot, boolean isSelected) {
            tvTime.setText(timeSlot.getDisplayTime());
            
            int staffCount = timeSlot.getAvailableStaff() != null ? timeSlot.getAvailableStaff().size() : 0;
            tvStaffCount.setText(staffCount + " staff available");
            
            // Update background based on selection state
            if (isSelected) {
                rootView.setBackgroundResource(R.drawable.selected_time_slot_background);
            } else {
                rootView.setBackgroundResource(R.drawable.unselected_time_slot_background);
            }
            
            // Disable if not available
            if (!timeSlot.isAvailable()) {
                rootView.setEnabled(false);
                rootView.setAlpha(0.5f);
            } else {
                rootView.setEnabled(true);
                rootView.setAlpha(1.0f);
            }
        }
    }
} 