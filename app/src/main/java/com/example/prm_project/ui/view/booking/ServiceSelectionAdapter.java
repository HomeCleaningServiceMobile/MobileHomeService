package com.example.prm_project.ui.view.booking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm_project.R;
import com.example.prm_project.data.model.Service;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class ServiceSelectionAdapter extends RecyclerView.Adapter<ServiceSelectionAdapter.ServiceViewHolder> {
    
    private final List<Service> services;
    private final OnServiceSelectedListener listener;
    private int selectedPosition = -1;
    
    public interface OnServiceSelectedListener {
        void onServiceSelected(Service service);
    }
    
    public ServiceSelectionAdapter(List<Service> services, OnServiceSelectedListener listener) {
        this.services = services;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service_selection, parent, false);
        return new ServiceViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = services.get(position);
        holder.bind(service, position == selectedPosition);
        
        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = position;
            
            // Notify changes for visual updates
            if (previousSelected != -1) {
                notifyItemChanged(previousSelected);
            }
            notifyItemChanged(selectedPosition);
            
            // Notify listener
            listener.onServiceSelected(service);
        });
    }
    
    @Override
    public int getItemCount() {
        return services.size();
    }
    
    static class ServiceViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardView;
        private final TextView tvServiceName;
        private final TextView tvServiceDescription;
        private final TextView tvServicePrice;
        private final TextView tvServiceCategory;
        
        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_service);
            tvServiceName = itemView.findViewById(R.id.tv_service_name);
            tvServiceDescription = itemView.findViewById(R.id.tv_service_description);
            tvServicePrice = itemView.findViewById(R.id.tv_service_price);
            tvServiceCategory = itemView.findViewById(R.id.tv_service_category);
        }
        
        public void bind(Service service, boolean isSelected) {
            tvServiceName.setText(service.getName());
            tvServiceDescription.setText(service.getDescription());
            tvServicePrice.setText("From $" + String.format("%.0f", service.getBasePrice()));
            tvServiceCategory.setText(getServiceTypeText(service.getType()));
            
            // Update selection state
            if (isSelected) {
                cardView.setCardBackgroundColor(itemView.getContext().getColor(R.color.primary_orange));
                cardView.setStrokeColor(itemView.getContext().getColor(R.color.primary_orange));
                cardView.setStrokeWidth(4);
                tvServiceName.setTextColor(itemView.getContext().getColor(R.color.white));
                tvServiceDescription.setTextColor(itemView.getContext().getColor(R.color.white));
                tvServicePrice.setTextColor(itemView.getContext().getColor(R.color.white));
                tvServiceCategory.setTextColor(itemView.getContext().getColor(R.color.white));
            } else {
                cardView.setCardBackgroundColor(itemView.getContext().getColor(R.color.white));
                cardView.setStrokeColor(itemView.getContext().getColor(R.color.divider_gray));
                cardView.setStrokeWidth(2);
                tvServiceName.setTextColor(itemView.getContext().getColor(R.color.text_primary));
                tvServiceDescription.setTextColor(itemView.getContext().getColor(R.color.text_secondary));
                tvServicePrice.setTextColor(itemView.getContext().getColor(R.color.primary_orange));
                tvServiceCategory.setTextColor(itemView.getContext().getColor(R.color.text_hint));
            }
        }
        private String getServiceTypeText(int type) {
            switch (type) {
                case 1: return "House Cleaning";
                case 2: return "Cooking";
                case 3: return "Laundry";
                case 4: return "Ironing";
                case 5: return "Gardening";
                case 6: return "Babysitting";
                case 7: return "Elder Care";
                case 8: return "Pet Care";
                case 9: return "General Maintenance";
                default: return "Other";
            }
        }
    }
} 