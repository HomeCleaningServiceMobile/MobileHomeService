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
        private TextView serviceProvider;
        private TextView servicePrice;
        private TextView serviceDuration;
        
        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            
            serviceImage = itemView.findViewById(R.id.service_image);
            serviceName = itemView.findViewById(R.id.service_name);
            serviceProvider = itemView.findViewById(R.id.service_provider);
            servicePrice = itemView.findViewById(R.id.service_price);
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
            serviceProvider.setText(service.getDescription());
            servicePrice.setText(String.format("From $%.0f", service.getBasePrice()));
            serviceDuration.setText(formatDuration(service.getEstimatedDurationMinutes()));
            
            // Load image using Glide
            if (service.getImageUrl() != null && !service.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(service.getImageUrl())
                        .placeholder(R.drawable.ic_cleaning)
                        .error(R.drawable.ic_cleaning)
                        .into(serviceImage);
            } else {
                // Set default icon based on service type
                serviceImage.setImageResource(getServiceIcon(service.getType()));
            }
        }
        
        private int getServiceIcon(int type) {
            switch (type) {
                case 1: return R.drawable.ic_cleaning; // House Cleaning
                case 2: return R.drawable.ic_cooking; // Cooking
                case 3: return R.drawable.ic_laundry; // Laundry
                case 4: return R.drawable.ic_ironing; // Ironing
                case 5: return R.drawable.ic_gardening; // Gardening
                case 6: return R.drawable.ic_babysitting; // Babysitting
                case 7: return R.drawable.ic_elder_care; // Elder Care
                case 8: return R.drawable.ic_pet_care; // Pet Care
                case 9: return R.drawable.ic_maintenance; // General Maintenance
                default: return R.drawable.ic_cleaning; // Default
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