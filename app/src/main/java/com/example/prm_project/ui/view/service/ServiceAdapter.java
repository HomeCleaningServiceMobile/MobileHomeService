package com.example.prm_project.ui.view.service;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.prm_project.R;
import com.example.prm_project.data.model.Service;

import java.util.ArrayList;
import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {
    
    private List<Service> services = new ArrayList<>();
    private OnServiceClickListener listener;
    
    public interface OnServiceClickListener {
        void onServiceClick(Service service);
    }
    
    public void setOnServiceClickListener(OnServiceClickListener listener) {
        this.listener = listener;
    }
    
    public void setServices(List<Service> services) {
        this.services = services;
        notifyDataSetChanged();
    }
    
    public void addServices(List<Service> newServices) {
        int startPosition = services.size();
        services.addAll(newServices);
        notifyItemRangeInserted(startPosition, newServices.size());
    }
    
    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service, parent, false);
        return new ServiceViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = services.get(position);
        holder.bind(service);
    }
    
    @Override
    public int getItemCount() {
        return services.size();
    }
    
    class ServiceViewHolder extends RecyclerView.ViewHolder {
        
        private ImageView serviceImage;
        private TextView serviceName;
        private TextView serviceDescription;
        private TextView servicePrice;
        private TextView serviceType;
        private TextView serviceDuration;
        
        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            
            serviceImage = itemView.findViewById(R.id.service_image);
            serviceName = itemView.findViewById(R.id.service_name);
            serviceDescription = itemView.findViewById(R.id.service_description);
            servicePrice = itemView.findViewById(R.id.service_price);
            serviceType = itemView.findViewById(R.id.service_type);
            serviceDuration = itemView.findViewById(R.id.service_duration);
            
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onServiceClick(services.get(position));
                }
            });
        }
        
        public void bind(Service service) {
            serviceName.setText(service.getName());
            serviceDescription.setText(service.getDescription());
            servicePrice.setText(String.format("$%.2f", service.getBasePrice()));
            serviceType.setText(getServiceTypeText(service.getType()));
            serviceDuration.setText(formatDuration(service.getEstimatedDurationMinutes()));
            
            // Load image using Glide
            if (service.getImageUrl() != null && !service.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(service.getImageUrl())
                        .placeholder(R.drawable.service_icon_placeholder)
                        .error(R.drawable.service_icon_placeholder)
                        .into(serviceImage);
            } else {
                serviceImage.setImageResource(R.drawable.service_icon_placeholder);
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
        
        private String formatDuration(int minutes) {
            int hours = minutes / 60;
            int mins = minutes % 60;
            
            if (hours > 0 && mins > 0) {
                return hours + "h " + mins + "m";
            } else if (hours > 0) {
                return hours + "h";
            } else {
                return mins + "m";
            }
        }
    }
} 