package com.example.prm_project.ui.view.service;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_project.R;
import com.example.prm_project.data.model.ServicePackage;

import java.util.ArrayList;
import java.util.List;

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.PackageViewHolder> {
    
    private List<ServicePackage> packages = new ArrayList<>();
    private OnPackageClickListener listener;
    private int selectedPosition = 0;
    
    public interface OnPackageClickListener {
        void onPackageClick(ServicePackage servicePackage, int position);
    }
    
    public void setOnPackageClickListener(OnPackageClickListener listener) {
        this.listener = listener;
    }
    
    public void setPackages(List<ServicePackage> packages) {
        this.packages = packages != null ? packages : new ArrayList<>();
        System.out.println("PackageAdapter: Setting " + this.packages.size() + " packages");
        notifyDataSetChanged();
    }
    
    public void setSelectedPosition(int position) {
        int oldPosition = selectedPosition;
        selectedPosition = position;
        
        // Only notify if positions are valid and different
        if (oldPosition != selectedPosition) {
            if (oldPosition >= 0 && oldPosition < packages.size()) {
                notifyItemChanged(oldPosition);
            }
            if (selectedPosition >= 0 && selectedPosition < packages.size()) {
                notifyItemChanged(selectedPosition);
            }
        }
        
        // Log for debugging
        if (selectedPosition >= 0 && selectedPosition < packages.size()) {
            ServicePackage selected = packages.get(selectedPosition);
            System.out.println("Package selected: " + selected.getName() + " at position " + selectedPosition);
        }
    }
    
    @NonNull
    @Override
    public PackageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_package_detail, parent, false);
        return new PackageViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull PackageViewHolder holder, int position) {
        ServicePackage servicePackage = packages.get(position);
        holder.bind(servicePackage, position == selectedPosition);
    }
    
    @Override
    public int getItemCount() {
        return packages.size();
    }
    
    class PackageViewHolder extends RecyclerView.ViewHolder {
        
        private TextView packageName;
        private TextView packageDescription;
        private TextView packagePrice;
        private TextView packageDuration;
        private TextView includedItems;
        private View selectionIndicator;
        private android.widget.ImageView checkmarkIcon;
        
        public PackageViewHolder(@NonNull View itemView) {
            super(itemView);
            
            packageName = itemView.findViewById(R.id.package_name);
            packageDescription = itemView.findViewById(R.id.package_description);
            packagePrice = itemView.findViewById(R.id.package_price);
            packageDuration = itemView.findViewById(R.id.package_duration);
            includedItems = itemView.findViewById(R.id.included_items);
            selectionIndicator = itemView.findViewById(R.id.selection_indicator);
            checkmarkIcon = itemView.findViewById(R.id.checkmark_icon);
            
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onPackageClick(packages.get(position), position);
                }
            });
        }
        
        public void bind(ServicePackage servicePackage, boolean isSelected) {
            packageName.setText(servicePackage.getName());
            packageDescription.setText(servicePackage.getDescription());
            packagePrice.setText(String.format("$%.0f", servicePackage.getPrice()));
            packageDuration.setText(formatDuration(servicePackage.getDurationMinutes()));
            
            if (servicePackage.getIncludedItems() != null && !servicePackage.getIncludedItems().isEmpty()) {
                includedItems.setVisibility(View.VISIBLE);
                includedItems.setText("Includes: " + servicePackage.getIncludedItems());
            } else {
                includedItems.setVisibility(View.GONE);
            }
            
            // Update selection state with better visual feedback
            selectionIndicator.setVisibility(isSelected ? View.VISIBLE : View.GONE);
            checkmarkIcon.setVisibility(isSelected ? View.VISIBLE : View.GONE);
            itemView.setSelected(isSelected);
            
            // Log for debugging
            System.out.println("Binding package: " + servicePackage.getName() + " - Selected: " + isSelected);
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