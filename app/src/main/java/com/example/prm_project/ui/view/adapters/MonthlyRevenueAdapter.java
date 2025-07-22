package com.example.prm_project.ui.view.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_project.R;
import com.example.prm_project.data.model.MonthlyRevenue;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MonthlyRevenueAdapter extends RecyclerView.Adapter<MonthlyRevenueAdapter.MonthlyRevenueViewHolder> {

    private List<MonthlyRevenue> monthlyRevenues = new ArrayList<>();
    private NumberFormat currencyFormat;
    private SimpleDateFormat monthFormat;

    public MonthlyRevenueAdapter() {
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        monthFormat = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public MonthlyRevenueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_monthly_revenue, parent, false);
        return new MonthlyRevenueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MonthlyRevenueViewHolder holder, int position) {
        MonthlyRevenue revenue = monthlyRevenues.get(position);
        holder.bind(revenue);
    }

    @Override
    public int getItemCount() {
        return monthlyRevenues.size();
    }

    public void setMonthlyRevenues(List<MonthlyRevenue> monthlyRevenues) {
        this.monthlyRevenues = monthlyRevenues != null ? monthlyRevenues : new ArrayList<>();
        notifyDataSetChanged();
    }

    class MonthlyRevenueViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMonth;
        private TextView tvRevenue;

        public MonthlyRevenueViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMonth = itemView.findViewById(R.id.tv_month);
            tvRevenue = itemView.findViewById(R.id.tv_revenue);
        }

        public void bind(MonthlyRevenue revenue) {
            tvMonth.setText(formatMonth(revenue.getMonth()));
            tvRevenue.setText(currencyFormat.format(revenue.getRevenue()));
        }

        private String formatMonth(String monthStr) {
            try {
                // Parse "2025-02" format to display as "Feb 2025"
                String[] parts = monthStr.split("-");
                int year = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]) - 1; // Month is 0-based

                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.set(year, month, 1);
                return monthFormat.format(cal.getTime());
            } catch (Exception e) {
                return monthStr;
            }
        }
    }
}