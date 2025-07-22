package com.example.prm_project.ui.view.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.prm_project.R;
import com.example.prm_project.data.model.Booking;
import com.example.prm_project.data.model.BookingStatus;

import java.util.List;

public class BookingAdapter extends BaseAdapter {

    private Context context;
    private List<Booking> bookingList;
    private LayoutInflater inflater;
    private OnBookingClickListener listener;

    // Interface for handling booking clicks
    public interface OnBookingClickListener {
        void onBookingClick(Booking booking, int position);
        void onBookingLongClick(Booking booking, int position);
    }

    public BookingAdapter(Context context, List<Booking> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return bookingList != null ? bookingList.size() : 0;
    }

    @Override
    public Booking getItem(int position) {
        return bookingList != null && position < bookingList.size() ? bookingList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        Booking booking = getItem(position);
        return booking != null ? booking.getId() : position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_booking, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Booking booking = getItem(position);
        if (booking != null) {
            bindBookingData(holder, booking, position);
        }

        return convertView;
    }

    private void bindBookingData(ViewHolder holder, Booking booking, int position) {
        // Set booking title (service name)
        if (booking.getService() != null) {
            holder.tvTaskTitle.setText(booking.getService().getName());
        } else {
            holder.tvTaskTitle.setText("Service Booking");
        }

        // Set booking address
        holder.tvTaskAddress.setText(booking.getServiceAddress());

        // Set booking time
        holder.tvTaskTime.setText(booking.getScheduledTime());

        // Set booking duration
        int durationHours = booking.getEstimatedDurationMinutes() / 60;
        int durationMinutes = booking.getEstimatedDurationMinutes() % 60;
        String durationText;
        if (durationHours > 0) {
            durationText = String.format("Duration: %dh %dm", durationHours, durationMinutes);
        } else {
            durationText = String.format("Duration: %dm", durationMinutes);
        }
        holder.tvTaskDuration.setText(durationText);

        // Set booking icon based on service type
        setBookingIcon(holder.ivTaskIcon, booking);

        // Set status
        setStatusStyle(holder.tvTaskStatus, booking.getStatusEnum());

        // Set click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBookingClick(booking, position);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onBookingLongClick(booking, position);
            }
            return true;
        });

    }

    private void setBookingIcon(ImageView imageView, Booking booking) {
        int iconRes = R.drawable.ic_cleaning;

        if (booking.getService() != null && booking.getService().getName() != null) {
            String serviceName = booking.getService().getName().toLowerCase();

            if (serviceName.contains("clean")) {
                iconRes = R.drawable.ic_cleaning;
            } else if (serviceName.contains("maintenance") || serviceName.contains("repair")) {
                iconRes = R.drawable.ic_maintenance;
            }  else if (serviceName.contains("garden")) {
                iconRes = R.drawable.ic_gardening;
            }
        }

        imageView.setImageResource(iconRes);
        imageView.setImageTintList(ColorStateList.valueOf(
                ContextCompat.getColor(context, R.color.orange_500)));
    }

    private void setStatusStyle(TextView textView, BookingStatus status) {
        textView.setText(status.getDisplayName());

        int backgroundRes;
        int textColorRes;

        switch (status) {
            case PENDING:
                textColorRes = R.color.orange_500;
                break;
            case CONFIRMED:
                textColorRes = R.color.blue_500;
                break;
            case IN_PROGRESS:
                textColorRes = R.color.purple_500;
                break;
            case COMPLETED:
                textColorRes = R.color.green_500;
                break;
            case CANCELLED:
                textColorRes = R.color.red_500;
                break;
            case REJECTED: // 7
                textColorRes = R.color.red_100;
                break;
            default:
                textColorRes = R.color.orange_500;
                break;
        }

        textView.setTextColor(ContextCompat.getColor(context, textColorRes));
    }


    // ViewHolder pattern for better performance
    private static class ViewHolder {
        View itemView;
        ImageView ivTaskIcon;
        TextView tvTaskTitle;
        TextView tvTaskAddress;
        TextView tvTaskTime;
        TextView tvTaskDuration;
        TextView tvTaskPriority;
        TextView tvTaskStatus;

        ViewHolder(View view) {
            itemView = view;
            ivTaskIcon = view.findViewById(R.id.ivTaskIcon);
            tvTaskTitle = view.findViewById(R.id.tvTaskTitle);
            tvTaskAddress = view.findViewById(R.id.tvTaskAddress);
            tvTaskTime = view.findViewById(R.id.tvTaskTime);
            tvTaskDuration = view.findViewById(R.id.tvTaskDuration);
            tvTaskStatus = view.findViewById(R.id.tvTaskStatus);
        }
    }

    // Public methods for adapter management
    public void updateBookings(List<Booking> newBookings) {
        this.bookingList = newBookings;
        notifyDataSetChanged();
    }

    public void addBooking(Booking booking) {
        if (bookingList != null) {
            bookingList.add(booking);
            notifyDataSetChanged();
        }
    }

    public void removeBooking(int position) {
        if (bookingList != null && position < bookingList.size()) {
            bookingList.remove(position);
            notifyDataSetChanged();
        }
    }

    public void setOnBookingClickListener(OnBookingClickListener listener) {
        this.listener = listener;
    }

    // Filter methods
    public void filterByStatus(BookingStatus status) {
        // Implementation for filtering by status
        // You might want to keep original list and filtered list separate
    }

    public void clearFilters() {
        // Implementation to show all bookings
        notifyDataSetChanged();
    }
}