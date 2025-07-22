package com.example.prm_project.ui.view.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_project.R;
import com.example.prm_project.data.model.TopService;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TopServiceAdapter extends RecyclerView.Adapter<TopServiceAdapter.TopServiceViewHolder> {

    private List<TopService> topServices = new ArrayList<>();
    private NumberFormat currencyFormat;

    public TopServiceAdapter() {
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    }

    @NonNull
    @Override
    public TopServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_top_service, parent, false);
        return new TopServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopServiceViewHolder holder, int position) {
        TopService service = topServices.get(position);
        holder.bind(service);
    }

    @Override
    public int getItemCount() {
        return topServices.size();
    }

    public void setTopServices(List<TopService> topServices) {
        this.topServices = topServices != null ? topServices : new ArrayList<>();
        notifyDataSetChanged();
    }

    class TopServiceViewHolder extends RecyclerView.ViewHolder {
        private TextView tvServiceName;
        private TextView tvRevenue;
        private TextView tvBookingCount;
        private TextView tvRank;

        public TopServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvServiceName = itemView.findViewById(R.id.tv_service_name);
            tvRevenue = itemView.findViewById(R.id.tv_revenue);
            tvBookingCount = itemView.findViewById(R.id.tv_booking_count);
            tvRank = itemView.findViewById(R.id.tv_rank);
        }

        public void bind(TopService service) {
            tvServiceName.setText(service.getServiceName());
            tvRevenue.setText(currencyFormat.format(service.getTotalRevenue()));
            tvBookingCount.setText(String.valueOf(service.getBookingCount()) + " bookings");
            tvRank.setText("#" + (getAdapterPosition() + 1));
        }
    }
}