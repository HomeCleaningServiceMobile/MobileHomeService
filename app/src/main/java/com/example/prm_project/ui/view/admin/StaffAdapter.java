package com.example.prm_project.ui.view.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm_project.R;
import com.example.prm_project.data.model.StaffManagement;

import java.util.List;

public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.StaffViewHolder> {
    private List<StaffManagement> staffList;

    public void setStaffList(List<StaffManagement> staffList) {
        this.staffList = staffList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StaffViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_staff, parent, false);
        return new StaffViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StaffViewHolder holder, int position) {
        StaffManagement staff = staffList.get(position);
        holder.tvName.setText(staff.fullName);
        holder.tvEmail.setText(staff.email);
    }

    @Override
    public int getItemCount() {
        return staffList == null ? 0 : staffList.size();
    }

    static class StaffViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail;
        StaffViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_staff_name);
            tvEmail = itemView.findViewById(R.id.tv_staff_email);
        }
    }
}