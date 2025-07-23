package com.example.prm_project.ui.view.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.prm_project.R;
import com.example.prm_project.data.model.Booking;
import com.example.prm_project.data.model.BookingStatus;
import com.example.prm_project.databinding.FragmentBookingsBinding;
import com.example.prm_project.ui.viewmodel.CustomerBookingViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BookingsFragment extends Fragment {
    
    private FragmentBookingsBinding binding;
    private CustomerBookingViewModel bookingViewModel;
    private BookingsPagerAdapter pagerAdapter;
    private boolean isSearchVisible = false;
    private String currentSearchQuery = "";
    
    // Tab titles and their corresponding filters
    private static final String[] TAB_TITLES = {
        "All", "Upcoming", "In Progress", "Completed", "Cancelled"
    };
    
    private static final String[] TAB_STATUS_FILTERS = {
        null, // All bookings
        "Confirmed", // Upcoming (confirmed bookings)
        "InProgress", // In Progress
        "Completed", // Completed
        "Cancelled" // Cancelled
    };
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBookingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize ViewModel
        bookingViewModel = new ViewModelProvider(this).get(CustomerBookingViewModel.class);
        
        // Setup UI components
        setupViewPager();
        setupSearchFunctionality();
        setupObservers();
        
        // Load initial data
        loadBookings();
    }
    
    private void setupViewPager() {
        pagerAdapter = new BookingsPagerAdapter(this);
        binding.viewPager.setAdapter(pagerAdapter);
        
        // Setup TabLayout with ViewPager2
        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> tab.setText(TAB_TITLES[position])
        ).attach();
        
        // Handle tab selection
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                loadBookingsForTab(tab.getPosition());
            }
            
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Refresh data when tab is reselected
                loadBookingsForTab(tab.getPosition());
            }
        });
    }
    
    private void setupSearchFunctionality() {
        // Search button click
        binding.btnSearch.setOnClickListener(v -> toggleSearchVisibility());
        
        // Clear search button
        binding.btnClearSearch.setOnClickListener(v -> {
            binding.etSearch.setText("");
            currentSearchQuery = "";
            loadBookingsForCurrentTab();
        });
        
        // Search text change listener
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            
            @Override
            public void afterTextChanged(Editable s) {
                currentSearchQuery = s.toString().trim();
                // Debounce search to avoid too many API calls
                binding.etSearch.removeCallbacks(searchRunnable);
                binding.etSearch.postDelayed(searchRunnable, 500);
            }
        });
        
        // Filter button (placeholder for now)
        binding.btnFilter.setOnClickListener(v -> {
            showToast("Advanced filters coming soon!");
        });
    }
    
    private final Runnable searchRunnable = new Runnable() {
        @Override
        public void run() {
            loadBookingsForCurrentTab();
        }
    };
    
    private void toggleSearchVisibility() {
        isSearchVisible = !isSearchVisible;
        binding.searchLayout.setVisibility(isSearchVisible ? View.VISIBLE : View.GONE);
        
        if (!isSearchVisible) {
            binding.etSearch.setText("");
            currentSearchQuery = "";
            loadBookingsForCurrentTab();
        } else {
            binding.etSearch.requestFocus();
        }
    }
    
    private void setupObservers() {
        // Observe loading state
        bookingViewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });
        
        // Observe error messages
        bookingViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                showToast("Error: " + error);
                bookingViewModel.clearErrorMessage();
            }
        });
        
        // Observe success messages
        bookingViewModel.getSuccessMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                showToast(message);
                bookingViewModel.clearSuccessMessage();
            }
        });
        
        // Observe my bookings data
        bookingViewModel.getMyBookings().observe(getViewLifecycleOwner(), bookings -> {
            if (bookings != null) {
                updateCurrentTabWithBookings(bookings);
            }
        });
        
        // Observe upcoming bookings data
        bookingViewModel.getUpcomingBookings().observe(getViewLifecycleOwner(), bookings -> {
            if (bookings != null && getCurrentTabPosition() == 1) { // Upcoming tab
                updateCurrentTabWithBookings(bookings);
            }
        });
    }
    
    private void loadBookings() {
        loadBookingsForTab(0); // Load "All" bookings by default
    }
    
    private void loadBookingsForCurrentTab() {
        loadBookingsForTab(binding.tabLayout.getSelectedTabPosition());
    }
    
    private void loadBookingsForTab(int tabPosition) {
        String status = TAB_STATUS_FILTERS[tabPosition];
        String searchTerm = currentSearchQuery.isEmpty() ? null : currentSearchQuery;
        
        switch (tabPosition) {
            case 1: // Upcoming
                bookingViewModel.loadUpcomingBookings(30); // Next 30 days
                break;
            default: // All, In Progress, Completed, Cancelled
                bookingViewModel.loadMyBookings(
                    status, // status filter
                    null, // startDate
                    null, // endDate
                    null, // serviceId
                    null, // serviceName
                    searchTerm, // searchTerm
                    "ScheduledDate", // sortBy
                    "Descending", // sortDirection
                    1, // pageNumber
                    20 // pageSize
                );
                break;
        }
    }
    
    private void updateCurrentTabWithBookings(java.util.List<Booking> bookings) {
        int currentTab = getCurrentTabPosition();
        BookingListFragment fragment = getCurrentTabFragment();
        if (fragment != null) {
            fragment.updateBookings(bookings);
        }
    }
    
    private int getCurrentTabPosition() {
        return binding.tabLayout.getSelectedTabPosition();
    }
    
    private BookingListFragment getCurrentTabFragment() {
        return pagerAdapter.getFragment(getCurrentTabPosition());
    }
    
    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    
    // ViewPager2 Adapter for booking tabs
    private static class BookingsPagerAdapter extends FragmentStateAdapter {
        private final Fragment[] fragments = new Fragment[TAB_TITLES.length];
        
        public BookingsPagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }
        
        @NonNull
        @Override
        public Fragment createFragment(int position) {
            BookingListFragment fragment = BookingListFragment.newInstance(
                TAB_TITLES[position], 
                TAB_STATUS_FILTERS[position]
            );
            fragments[position] = fragment;
            return fragment;
        }
        
        @Override
        public int getItemCount() {
            return TAB_TITLES.length;
        }
        
        public BookingListFragment getFragment(int position) {
            if (position >= 0 && position < fragments.length) {
                return (BookingListFragment) fragments[position];
            }
            return null;
        }
    }
    
    // Individual tab fragment for booking lists
    public static class BookingListFragment extends Fragment {
        private static final String ARG_TAB_TITLE = "tab_title";
        private static final String ARG_STATUS_FILTER = "status_filter";
        
        private com.example.prm_project.databinding.FragmentBookingListBinding binding;
        private com.example.prm_project.ui.view.adapters.CustomerBookingAdapter adapter;
        private CustomerBookingViewModel bookingViewModel;
        private String tabTitle;
        private String statusFilter;
        
        public static BookingListFragment newInstance(String tabTitle, String statusFilter) {
            BookingListFragment fragment = new BookingListFragment();
            Bundle args = new Bundle();
            args.putString(ARG_TAB_TITLE, tabTitle);
            args.putString(ARG_STATUS_FILTER, statusFilter);
            fragment.setArguments(args);
            return fragment;
        }
        
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                tabTitle = getArguments().getString(ARG_TAB_TITLE);
                statusFilter = getArguments().getString(ARG_STATUS_FILTER);
            }
        }
        
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            binding = com.example.prm_project.databinding.FragmentBookingListBinding.inflate(inflater, container, false);
            return binding.getRoot();
        }
        
        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            
            bookingViewModel = new ViewModelProvider(requireParentFragment()).get(CustomerBookingViewModel.class);
            
            setupRecyclerView();
            setupSwipeRefresh();
            setupEmptyState();
            
            // Show loading initially
            showLoading(true);
        }
        
        private void setupRecyclerView() {
            adapter = new com.example.prm_project.ui.view.adapters.CustomerBookingAdapter(getContext());
            binding.recyclerViewBookings.setAdapter(adapter);
            
            // Set booking action listener
            adapter.setOnBookingActionListener(new com.example.prm_project.ui.view.adapters.CustomerBookingAdapter.OnBookingActionListener() {
                @Override
                public void onBookingClick(Booking booking) {
                    // Navigate to booking details
                    Bundle args = new Bundle();
                    args.putInt("bookingId", booking.getId());
                    
                    NavController navController = Navigation.findNavController(requireView());
                    navController.navigate(R.id.action_nav_bookings_to_bookingDetailFragment, args);
                }
                
                @Override
                public void onCancelBooking(Booking booking) {
                    // Handle booking cancellation
                    showToast("Cancel booking: " + booking.getBookingNumber());
                }
                
                @Override
                public void onCallStaff(Booking booking) {
                    // Handle staff call
                    if (booking.getStaff() != null && booking.getStaff().getPhoneNumber() != null) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + booking.getStaff().getPhoneNumber()));
                        startActivity(intent);
                    }
                }
                
                @Override
                public void onRateBooking(Booking booking) {
                    // Handle booking rating
                    showToast("Rate booking: " + booking.getBookingNumber());
                }
                
                @Override
                public void onRebookService(Booking booking) {
                    // Handle rebooking
                    showToast("Rebook service: " + booking.getService().getName());
                    // Navigate to service booking with pre-filled data
                    NavController navController = Navigation.findNavController(requireView());
//                    navController.navigate(R.id.action_bookingsFragment_to_servicesFragment);
                }
            });
        }
        
        private void setupSwipeRefresh() {
            binding.swipeRefreshLayout.setOnRefreshListener(() -> {
                // Refresh data
                ((BookingsFragment) getParentFragment()).loadBookingsForCurrentTab();
                binding.swipeRefreshLayout.setRefreshing(false);
            });
        }
        
        private void setupEmptyState() {
            // Customize empty state based on tab
            updateEmptyStateForTab();
            
            binding.btnCreateBooking.setOnClickListener(v -> {
                // Navigate to services to create new booking
                NavController navController = Navigation.findNavController(requireView());
//                navController.navigate(R.id.action_bookingsFragment_to_servicesFragment);
            });
            
            binding.btnRetry.setOnClickListener(v -> {
                ((BookingsFragment) getParentFragment()).loadBookingsForCurrentTab();
            });
        }
        
        private void updateEmptyStateForTab() {
            switch (tabTitle) {
                case "Upcoming":
                    binding.tvEmptyTitle.setText("No upcoming bookings");
                    binding.tvEmptyMessage.setText("You don't have any upcoming service appointments");
                    break;
                case "In Progress":
                    binding.tvEmptyTitle.setText("No services in progress");
                    binding.tvEmptyMessage.setText("No services are currently being performed");
                    binding.btnCreateBooking.setVisibility(View.GONE);
                    break;
                case "Completed":
                    binding.tvEmptyTitle.setText("No completed bookings");
                    binding.tvEmptyMessage.setText("Your completed services will appear here");
                    binding.btnCreateBooking.setText("Book Another Service");
                    break;
                case "Cancelled":
                    binding.tvEmptyTitle.setText("No cancelled bookings");
                    binding.tvEmptyMessage.setText("Your cancelled bookings will appear here");
                    binding.btnCreateBooking.setText("Book a Service");
                    break;
                default: // All
                    binding.tvEmptyTitle.setText("No bookings yet");
                    binding.tvEmptyMessage.setText("Start by booking your first home service");
                    break;
            }
        }
        
        public void updateBookings(java.util.List<Booking> bookings) {
            showLoading(false);
            
            if (bookings == null || bookings.isEmpty()) {
                showEmptyState();
            } else {
                showBookingsList();
                adapter.setBookings(bookings);
            }
        }
        
        private void showLoading(boolean show) {
            binding.progressBarLoading.setVisibility(show ? View.VISIBLE : View.GONE);
            binding.recyclerViewBookings.setVisibility(show ? View.GONE : View.VISIBLE);
            binding.emptyStateLayout.setVisibility(View.GONE);
            binding.errorStateLayout.setVisibility(View.GONE);
        }
        
        private void showEmptyState() {
            binding.progressBarLoading.setVisibility(View.GONE);
            binding.recyclerViewBookings.setVisibility(View.GONE);
            binding.emptyStateLayout.setVisibility(View.VISIBLE);
            binding.errorStateLayout.setVisibility(View.GONE);
        }
        
        private void showBookingsList() {
            binding.progressBarLoading.setVisibility(View.GONE);
            binding.recyclerViewBookings.setVisibility(View.VISIBLE);
            binding.emptyStateLayout.setVisibility(View.GONE);
            binding.errorStateLayout.setVisibility(View.GONE);
        }
        
        private void showError(String message) {
            binding.progressBarLoading.setVisibility(View.GONE);
            binding.recyclerViewBookings.setVisibility(View.GONE);
            binding.emptyStateLayout.setVisibility(View.GONE);
            binding.errorStateLayout.setVisibility(View.VISIBLE);
            binding.tvErrorMessage.setText(message);
        }
        
        private void showToast(String message) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
        
        @Override
        public void onDestroyView() {
            super.onDestroyView();
            binding = null;
        }
    }
} 