package com.example.prm_project.ui.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.prm_project.R;
import com.example.prm_project.data.model.AdminServiceListResponse;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adapter cho danh sách services trong admin
 */
public class AdminServiceAdapter extends RecyclerView.Adapter<AdminServiceAdapter.ServiceViewHolder> {
    private List<AdminServiceListResponse.ServiceItem> serviceList;
    private Context context;
    private OnServiceClickListener onServiceClickListener;

    public AdminServiceAdapter(Context context) {
        this.context = context;
        this.serviceList = new ArrayList<>();
    }

    public interface OnServiceClickListener {
        void onServiceClick(AdminServiceListResponse.ServiceItem service);
        void onServiceEditClick(AdminServiceListResponse.ServiceItem service);
        void onServiceDeleteClick(AdminServiceListResponse.ServiceItem service);
    }

    public void setOnServiceClickListener(OnServiceClickListener listener) {
        this.onServiceClickListener = listener;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        AdminServiceListResponse.ServiceItem service = serviceList.get(position);
        holder.bind(service);
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public void updateServiceList(List<AdminServiceListResponse.ServiceItem> newList) {
        this.serviceList.clear();
        if (newList != null) {
            this.serviceList.addAll(newList);
        }
        notifyDataSetChanged();
    }

    /**
     * Xóa một service khỏi danh sách và cập nhật RecyclerView
     */
    public void removeServiceById(int serviceId) {
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).getId() == serviceId) {
                serviceList.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    class ServiceViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivServiceImage;
        private TextView tvServiceName;
        private TextView tvServiceDescription;
        private TextView tvServicePrice;
        private TextView tvServiceType;
        private TextView tvServiceStatus;
        private View btnEdit;
        private View btnDelete;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            ivServiceImage = itemView.findViewById(R.id.iv_service_image);
            tvServiceName = itemView.findViewById(R.id.tv_service_name);
            tvServiceDescription = itemView.findViewById(R.id.tv_service_description);
            tvServicePrice = itemView.findViewById(R.id.tv_service_price);
            tvServiceType = itemView.findViewById(R.id.tv_service_type);
            tvServiceStatus = itemView.findViewById(R.id.tv_service_status);
            btnEdit = itemView.findViewById(R.id.btn_edit_service);
            btnDelete = itemView.findViewById(R.id.btn_delete_service);
        }

        public void bind(AdminServiceListResponse.ServiceItem service) {
            // Set service name
            tvServiceName.setText(service.getName());
            
            // Set service description
            tvServiceDescription.setText(service.getDescription());
            
            // Format and set price
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            tvServicePrice.setText(formatter.format(service.getBasePrice()));
            
            // Set service type
            String typeText = getServiceTypeText(service.getType());
            tvServiceType.setText(typeText);
            
            // Set service status
            if (service.isActive()) {
                tvServiceStatus.setText("Hoạt động");
                tvServiceStatus.setTextColor(context.getResources().getColor(R.color.success_green));
            } else {
                tvServiceStatus.setText("Tạm dừng");
                tvServiceStatus.setTextColor(context.getResources().getColor(R.color.error_red));
            }
            
            // Load service image
            if (service.getImageUrl() != null && !service.getImageUrl().isEmpty()) {
                Glide.with(context)
                    .load(service.getImageUrl())
                    .placeholder(R.drawable.service_icon_placeholder)
                    .error(R.drawable.service_icon_placeholder)
                    .into(ivServiceImage);
            } else {
                ivServiceImage.setImageResource(R.drawable.service_icon_placeholder);
            }
            
            // Set click listeners
            itemView.setOnClickListener(v -> {
                if (onServiceClickListener != null) {
                    onServiceClickListener.onServiceClick(service);
                }
            });
            
            btnEdit.setOnClickListener(v -> {
                if (onServiceClickListener != null) {
                    onServiceClickListener.onServiceEditClick(service);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (onServiceClickListener != null) {
                    new AlertDialog.Builder(context)
                        .setTitle("Xác nhận xóa dịch vụ")
                        .setMessage("Bạn có chắc chắn muốn xóa dịch vụ này?")
                        .setPositiveButton("Có", (dialog, which) -> onServiceClickListener.onServiceDeleteClick(service))
                        .setNegativeButton("Không", null)
                        .show();
                }
            });
        }
        
        private String getServiceTypeText(int type) {
            switch (type) {
                case 1:
                    return "Dọn dẹp cơ bản";
                case 2:
                    return "Dọn dẹp sâu";
                case 3:
                    return "Dọn dẹp văn phòng";
                default:
                    return "Khác";
            }
        }
    }
}
