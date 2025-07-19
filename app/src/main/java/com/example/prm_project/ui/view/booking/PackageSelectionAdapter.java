package com.example.prm_project.ui.view.booking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm_project.R;
import com.example.prm_project.data.model.ServicePackage;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class PackageSelectionAdapter extends RecyclerView.Adapter<PackageSelectionAdapter.PackageViewHolder> {
    
    private final List<ServicePackage> packages;
    private final OnPackageSelectedListener listener;
    private int selectedPosition = -1;
    
    public interface OnPackageSelectedListener {
        void onPackageSelected(ServicePackage servicePackage);
    }
    
    public PackageSelectionAdapter(List<ServicePackage> packages, OnPackageSelectedListener listener) {
        this.packages = packages;
        this.listener = listener;
    }
    
    public void setSelectedPosition(int position) {
        int oldPosition = selectedPosition;
        selectedPosition = position;
        
        // Notify changes for visual updates
        if (oldPosition != -1 && oldPosition < packages.size()) {
            notifyItemChanged(oldPosition);
        }
        if (selectedPosition != -1 && selectedPosition < packages.size()) {
            notifyItemChanged(selectedPosition);
        }
    }
    
    public void setSelectedPackage(ServicePackage packageToSelect) {
        for (int i = 0; i < packages.size(); i++) {
            if (packages.get(i).getId() == packageToSelect.getId()) {
                setSelectedPosition(i);
                break;
            }
        }
    }
    
    @NonNull
    @Override
    public PackageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_package_selection, parent, false);
        return new PackageViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull PackageViewHolder holder, int position) {
        ServicePackage servicePackage = packages.get(position);
        holder.bind(servicePackage, position == selectedPosition);
        
        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = position;
            
            // Notify changes for visual updates
            if (previousSelected != -1) {
                notifyItemChanged(previousSelected);
            }
            notifyItemChanged(selectedPosition);
            
            // Notify listener
            listener.onPackageSelected(servicePackage);
        });
    }
    
    @Override
    public int getItemCount() {
        return packages.size();
    }
    
    static class PackageViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardView;
        private final TextView tvPackageName;
        private final TextView tvPackageDescription;
        private final TextView tvPackagePrice;
        private final TextView tvPackageDuration;
        
        public PackageViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_package);
            tvPackageName = itemView.findViewById(R.id.tv_package_name);
            tvPackageDescription = itemView.findViewById(R.id.tv_package_description);
            tvPackagePrice = itemView.findViewById(R.id.tv_package_price);
            tvPackageDuration = itemView.findViewById(R.id.tv_package_duration);
        }
        
        public void bind(ServicePackage servicePackage, boolean isSelected) {
            tvPackageName.setText(servicePackage.getName());
            tvPackageDescription.setText(servicePackage.getDescription());
            tvPackagePrice.setText("$" + String.format("%.0f", servicePackage.getPrice()));
            tvPackageDuration.setText(formatDuration(servicePackage.getDurationMinutes()));
            
            // Update selection state
            if (isSelected) {
                cardView.setCardBackgroundColor(itemView.getContext().getColor(R.color.primary_orange));
                cardView.setStrokeColor(itemView.getContext().getColor(R.color.primary_orange));
                cardView.setStrokeWidth(4);
                tvPackageName.setTextColor(itemView.getContext().getColor(R.color.white));
                tvPackageDescription.setTextColor(itemView.getContext().getColor(R.color.white));
                tvPackagePrice.setTextColor(itemView.getContext().getColor(R.color.white));
                tvPackageDuration.setTextColor(itemView.getContext().getColor(R.color.white));
            } else {
                cardView.setCardBackgroundColor(itemView.getContext().getColor(R.color.white));
                cardView.setStrokeColor(itemView.getContext().getColor(R.color.divider_gray));
                cardView.setStrokeWidth(2);
                tvPackageName.setTextColor(itemView.getContext().getColor(R.color.text_primary));
                tvPackageDescription.setTextColor(itemView.getContext().getColor(R.color.text_secondary));
                tvPackagePrice.setTextColor(itemView.getContext().getColor(R.color.primary_orange));
                tvPackageDuration.setTextColor(itemView.getContext().getColor(R.color.text_hint));
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