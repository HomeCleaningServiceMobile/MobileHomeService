package com.example.prm_project.ui.view.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.prm_project.R;
import com.example.prm_project.data.model.StaffManagement;

import java.util.List;

public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.StaffViewHolder> {
    public interface OnStaffClickListener {
        void onStaffClick(int staffId);
    }

    private List<StaffManagement> staffList;
    private OnStaffClickListener listener;

    public StaffAdapter(List<StaffManagement> staffList, OnStaffClickListener listener) {
        this.staffList = staffList;
        this.listener = listener;
    }

    public void setStaffList(List<StaffManagement> staffList) {
        this.staffList = staffList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StaffViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_staff, parent, false);
        return new StaffViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StaffViewHolder holder, int position) {
        StaffManagement staff = staffList.get(position);
        holder.tvName.setText(staff.fullName);
        holder.tvEmail.setText(staff.email);
        // Load idCardImageUrl into imgStaff
        Glide.with(holder.itemView.getContext())
            .load(staff.idCardImageUrl)
            .placeholder(R.drawable.credit_card_24px)
            .error(R.drawable.credit_card_24px)
            .into(holder.imgStaff);
        holder.btnView.setOnClickListener(v -> {
            if (listener != null) listener.onStaffClick(staff.id);
        });
    }

    @Override
    public int getItemCount() {
        return staffList == null ? 0 : staffList.size();
    }

    static class StaffViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail;
        ImageButton btnView, btnEdit, btnDelete;

        ImageView imgStaff;
        StaffViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_staff_name);
            tvEmail = itemView.findViewById(R.id.tv_staff_email);
            btnView = itemView.findViewById(R.id.btn_view_detail);
            btnEdit = itemView.findViewById(R.id.btn_update);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            imgStaff = itemView.findViewById(R.id.id_card_imageUrl);
        }
    }
}