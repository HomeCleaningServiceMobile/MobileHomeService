package com.example.prm_project.ui.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm_project.R;
import com.example.prm_project.data.model.Booking;
import com.example.prm_project.data.model.BookingStatus;
import com.example.prm_project.utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

public class CustomerBookingAdapter extends RecyclerView.Adapter<CustomerBookingAdapter.BookingViewHolder> {
    
    private List<Booking> bookings;
    private Context context;
    private OnBookingActionListener listener;
    
    public interface OnBookingActionListener {
        void onBookingClick(Booking booking);
        void onCancelBooking(Booking booking);
        void onCallStaff(Booking booking);
        void onRateBooking(Booking booking);
        void onRebookService(Booking booking);
    }
    
    public CustomerBookingAdapter(Context context) {
        this.context = context;
        this.bookings = new ArrayList<>();
    }
    
    public void setOnBookingActionListener(OnBookingActionListener listener) {
        this.listener = listener;
    }
    
    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings != null ? bookings : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    public void addBookings(List<Booking> newBookings) {
        if (newBookings != null) {
            int startPosition = this.bookings.size();
            this.bookings.addAll(newBookings);
            notifyItemRangeInserted(startPosition, newBookings.size());
        }
    }
    
    public void clearBookings() {
        this.bookings.clear();
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_customer_booking, parent, false);
        return new BookingViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookings.get(position);
        holder.bind(booking);
    }
    
    @Override
    public int getItemCount() {
        return bookings.size();
    }
    
    class BookingViewHolder extends RecyclerView.ViewHolder {
        
        private TextView tvBookingNumber;
        private TextView tvServiceName;
        private TextView tvServiceAddress;
        private TextView tvScheduledDate;
        private TextView tvScheduledTime;
        private TextView tvTotalPrice;
        private TextView tvStatus;
        private TextView tvStaffName;
        private ImageView ivServiceIcon;
        private ImageView ivStatusIcon;
        private Button btnPrimaryAction;
        private Button btnSecondaryAction;
        private View statusIndicator;
        
        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            
            tvBookingNumber = itemView.findViewById(R.id.tvBookingNumber);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            tvServiceAddress = itemView.findViewById(R.id.tvServiceAddress);
            tvScheduledDate = itemView.findViewById(R.id.tvScheduledDate);
            tvScheduledTime = itemView.findViewById(R.id.tvScheduledTime);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvStaffName = itemView.findViewById(R.id.tvStaffName);
            ivServiceIcon = itemView.findViewById(R.id.ivServiceIcon);
            ivStatusIcon = itemView.findViewById(R.id.ivStatusIcon);
            btnPrimaryAction = itemView.findViewById(R.id.btnPrimaryAction);
            btnSecondaryAction = itemView.findViewById(R.id.btnSecondaryAction);
            statusIndicator = itemView.findViewById(R.id.statusIndicator);
            
            // Set click listener for the entire item
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onBookingClick(bookings.get(getAdapterPosition()));
                }
            });
        }
        
        public void bind(Booking booking) {
            // Basic booking info
            tvBookingNumber.setText(booking.getBookingNumber() != null ? booking.getBookingNumber() : "BK-" + booking.getId());
            tvServiceName.setText(booking.getService() != null ? booking.getService().getName() : "Service");
            tvServiceAddress.setText(booking.getServiceAddress());
            
            // Date and time
            if (booking.getScheduledDate() != null) {
                tvScheduledDate.setText(DateTimeUtils.getFormattedDisplayDate(booking.getScheduledDate()));
            }
            if (booking.getScheduledTime() != null) {
                tvScheduledTime.setText(DateTimeUtils.formatDisplayTime(booking.getScheduledTime()));
            }
            
            // Price
            if (booking.getTotalAmount() != 0) {
                tvTotalPrice.setText(String.format("$%.2f", booking.getTotalAmount()));
            }
            
            // Staff info
            if (booking.getStaff() != null) {
                tvStaffName.setText(booking.getStaff().getFirstName() + " " + booking.getStaff().getLastName());
                tvStaffName.setVisibility(View.VISIBLE);
            } else {
                tvStaffName.setVisibility(View.GONE);
            }
            
            // Status
            BookingStatus status = BookingStatus.fromValue(booking.getStatus());
            tvStatus.setText(getStatusText(status));
            
            // Set status colors and icons
            setStatusAppearance(status);
            
            // Set service icon
            setServiceIcon(booking);
            
            // Configure action buttons
            configureActionButtons(booking, status);
        }
        
        private void setStatusAppearance(BookingStatus status) {
            int statusColor;
            int statusIcon;
            
            switch (status) {
                case PENDING:
                    statusColor = ContextCompat.getColor(context, R.color.status_pending);
                    statusIcon = R.drawable.ic_clock;
                    break;
                case CONFIRMED:
                    statusColor = ContextCompat.getColor(context, R.color.status_confirmed);
                    statusIcon = R.drawable.ic_check_circle;
                    break;
                case IN_PROGRESS:
                    statusColor = ContextCompat.getColor(context, R.color.status_in_progress);
                    statusIcon = R.drawable.ic_play_circle;
                    break;
                case COMPLETED:
                    statusColor = ContextCompat.getColor(context, R.color.status_completed);
                    statusIcon = R.drawable.ic_check_circle;
                    break;
                case CANCELLED:
                    statusColor = ContextCompat.getColor(context, R.color.status_cancelled);
                    statusIcon = R.drawable.ic_cancel;
                    break;
                default:
                    statusColor = ContextCompat.getColor(context, R.color.status_default);
                    statusIcon = R.drawable.ic_help;
                    break;
            }
            
            tvStatus.setTextColor(statusColor);
            ivStatusIcon.setImageResource(statusIcon);
            ivStatusIcon.setColorFilter(statusColor);
            statusIndicator.setBackgroundColor(statusColor);
        }
        
        private void setServiceIcon(Booking booking) {
            if (booking.getService() != null) {
                // Set service icon based on service type or name
                String serviceName = booking.getService().getName().toLowerCase();
                int iconRes = R.drawable.ic_services; // default
                
                if (serviceName.contains("cleaning")) {
                    iconRes = R.drawable.ic_cleaning;
                } else if (serviceName.contains("plumbing")) {
                    iconRes = R.drawable.ic_maintenance;
                } else if (serviceName.contains("electrical")) {
                    iconRes = R.drawable.ic_maintenance;
                } else if (serviceName.contains("gardening")) {
                    iconRes = R.drawable.ic_gardening;
                } else if (serviceName.contains("cooking")) {
                    iconRes = R.drawable.ic_cooking;
                }
                
                ivServiceIcon.setImageResource(iconRes);
            }
        }
        
        private void configureActionButtons(Booking booking, BookingStatus status) {
            btnPrimaryAction.setVisibility(View.GONE);
            btnSecondaryAction.setVisibility(View.GONE);
            
            switch (status) {
                case PENDING:
                case CONFIRMED:
                    // Show Cancel button
                    btnPrimaryAction.setText("Cancel");
                    btnPrimaryAction.setVisibility(View.VISIBLE);
                    btnPrimaryAction.setOnClickListener(v -> {
                        if (listener != null) {
                            listener.onCancelBooking(booking);
                        }
                    });
                    
                    // Show Call Staff button if staff is assigned
                    if (booking.getStaff() != null) {
                        btnSecondaryAction.setText("Call Staff");
                        btnSecondaryAction.setVisibility(View.VISIBLE);
                        btnSecondaryAction.setOnClickListener(v -> {
                            if (listener != null) {
                                listener.onCallStaff(booking);
                            }
                        });
                    }
                    break;
                    
                case IN_PROGRESS:
                    // Show Call Staff button
                    btnPrimaryAction.setText("Call Staff");
                    btnPrimaryAction.setVisibility(View.VISIBLE);
                    btnPrimaryAction.setOnClickListener(v -> {
                        if (listener != null) {
                            listener.onCallStaff(booking);
                        }
                    });
                    break;
                    
                case COMPLETED:
                    // Show Rate & Review button
                    btnPrimaryAction.setText("Rate & Review");
                    btnPrimaryAction.setVisibility(View.VISIBLE);
                    btnPrimaryAction.setOnClickListener(v -> {
                        if (listener != null) {
                            listener.onRateBooking(booking);
                        }
                    });
                    
                    // Show Book Again button
                    btnSecondaryAction.setText("Book Again");
                    btnSecondaryAction.setVisibility(View.VISIBLE);
                    btnSecondaryAction.setOnClickListener(v -> {
                        if (listener != null) {
                            listener.onRebookService(booking);
                        }
                    });
                    break;
                    
                case CANCELLED:
                    // Show Book Again button
                    btnPrimaryAction.setText("Book Again");
                    btnPrimaryAction.setVisibility(View.VISIBLE);
                    btnPrimaryAction.setOnClickListener(v -> {
                        if (listener != null) {
                            listener.onRebookService(booking);
                        }
                    });
                    break;
            }
        }
        
        private String getStatusText(BookingStatus status) {
            switch (status) {
                case PENDING: return "Pending";
                case CONFIRMED: return "Confirmed";
                case AUTO_ASSIGNED: return "Staff Assigned";
                case PENDING_SCHEDULE: return "Awaiting Staff";
                case IN_PROGRESS: return "In Progress";
                case COMPLETED: return "Completed";
                case CANCELLED: return "Cancelled";
                case REJECTED: return "Rejected";
                default: return "Unknown";
            }
        }
    }
} 