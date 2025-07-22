package com.example.prm_project.ui.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm_project.R;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder> {
    
    private List<String> timeSlots;
    private Context context;
    private OnTimeSlotClickListener listener;
    private int selectedPosition = -1;
    
    public interface OnTimeSlotClickListener {
        void onTimeSlotSelected(String timeSlot, int position);
    }
    
    public TimeSlotAdapter(Context context) {
        this.context = context;
        this.timeSlots = new ArrayList<>();
    }
    
    public void setTimeSlots(List<String> timeSlots) {
        this.timeSlots = timeSlots != null ? timeSlots : new ArrayList<>();
        this.selectedPosition = -1; // Reset selection
        notifyDataSetChanged();
    }
    
    public void setOnTimeSlotClickListener(OnTimeSlotClickListener listener) {
        this.listener = listener;
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
    
    public String getSelectedTimeSlot() {
        if (selectedPosition >= 0 && selectedPosition < timeSlots.size()) {
            return timeSlots.get(selectedPosition);
        }
        return null;
    }
    
    @NonNull
    @Override
    public TimeSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_time_slot_chip, parent, false);
        return new TimeSlotViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull TimeSlotViewHolder holder, int position) {
        String timeSlot = timeSlots.get(position);
        holder.bind(timeSlot, position == selectedPosition);
    }
    
    @Override
    public int getItemCount() {
        return timeSlots.size();
    }
    
    class TimeSlotViewHolder extends RecyclerView.ViewHolder {
        
        private MaterialCardView cardContainer;
        private TextView tvTimeSlot;
        
        public TimeSlotViewHolder(@NonNull View itemView) {
            super(itemView);
            cardContainer = itemView.findViewById(R.id.card_time_slot);
            tvTimeSlot = itemView.findViewById(R.id.tv_time_slot);
            
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    setSelectedPosition(position);
                    listener.onTimeSlotSelected(timeSlots.get(position), position);
                }
            });
        }
        
        public void bind(String timeSlot, boolean isSelected) {
            tvTimeSlot.setText(formatTimeSlot(timeSlot));
            
            if (isSelected) {
                // Selected state
                cardContainer.setCardBackgroundColor(ContextCompat.getColor(context, R.color.primary_color));
                cardContainer.setStrokeColor(ContextCompat.getColor(context, R.color.primary_color));
                cardContainer.setStrokeWidth(3);
                tvTimeSlot.setTextColor(ContextCompat.getColor(context, android.R.color.white));
                
                // Add elevation for selected state
                cardContainer.setCardElevation(8f);
            } else {
                // Normal state
                cardContainer.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white));
                cardContainer.setStrokeColor(ContextCompat.getColor(context, R.color.status_default));
                cardContainer.setStrokeWidth(2);
                tvTimeSlot.setTextColor(ContextCompat.getColor(context, R.color.text_primary));
                
                // Normal elevation
                cardContainer.setCardElevation(4f);
            }
        }
        
        private String formatTimeSlot(String timeSlot) {
            if (timeSlot == null) return "";
            
            try {
                // Handle different time formats
                if (timeSlot.contains(":")) {
                    String[] parts = timeSlot.split(":");
                    if (parts.length >= 2) {
                        int hour = Integer.parseInt(parts[0]);
                        int minute = Integer.parseInt(parts[1]);
                        
                        // Convert to 12-hour format with AM/PM
                        String ampm = hour >= 12 ? "PM" : "AM";
                        if (hour == 0) hour = 12;
                        if (hour > 12) hour -= 12;
                        
                        if (minute == 0) {
                            return String.format("%d %s", hour, ampm);
                        } else {
                            return String.format("%d:%02d %s", hour, minute, ampm);
                        }
                    }
                }
                return timeSlot;
            } catch (NumberFormatException e) {
                return timeSlot;
            }
        }
    }
} 