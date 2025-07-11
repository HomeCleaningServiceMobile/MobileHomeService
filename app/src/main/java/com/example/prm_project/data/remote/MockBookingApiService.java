package com.example.prm_project.data.remote;

import com.example.prm_project.data.model.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MockBookingApiService implements BookingApiService {
    
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    
    // Thread pool for async operations
    private static final ExecutorService executor = Executors.newFixedThreadPool(3);
    
    // Mock data storage
    private List<Booking> mockBookings = new ArrayList<>();
    private List<Service> mockServices = new ArrayList<>();
    private List<ServicePackage> mockServicePackages = new ArrayList<>();
    private int nextBookingId = 1;
    
    public MockBookingApiService() {
        initializeMockData();
    }
    
    private void initializeMockData() {
        // Initialize mock services
        mockServices.add(new Service(1, "House Cleaning", "Professional house cleaning service", 50.0, 1, "Cleaning"));
        mockServices.add(new Service(2, "Office Cleaning", "Commercial office cleaning service", 80.0, 1, "Cleaning"));
        mockServices.add(new Service(3, "Deep Cleaning", "Comprehensive deep cleaning service", 120.0, 1, "Cleaning"));
        mockServices.add(new Service(4, "Plumbing Service", "Professional plumbing repair and maintenance", 75.0, 2, "Maintenance"));
        mockServices.add(new Service(5, "Electrical Service", "Electrical repair and installation", 85.0, 2, "Maintenance"));
        mockServices.add(new Service(6, "Painting Service", "Interior and exterior painting", 100.0, 3, "Renovation"));
        
        // Initialize mock service packages
        // House Cleaning packages
        mockServicePackages.add(new ServicePackage(1, "Basic Cleaning", "Standard house cleaning", 50.0, 120, 1));
        mockServicePackages.add(new ServicePackage(2, "Deep Cleaning Package", "Complete deep cleaning service", 80.0, 180, 1));
        mockServicePackages.add(new ServicePackage(3, "Premium Cleaning", "Premium cleaning with extras", 100.0, 240, 1));
        
        // Office Cleaning packages
        mockServicePackages.add(new ServicePackage(4, "Small Office", "Up to 500 sq ft", 80.0, 90, 2));
        mockServicePackages.add(new ServicePackage(5, "Medium Office", "500-1000 sq ft", 120.0, 150, 2));
        mockServicePackages.add(new ServicePackage(6, "Large Office", "1000+ sq ft", 180.0, 240, 2));
        
        // Plumbing packages
        mockServicePackages.add(new ServicePackage(7, "Basic Plumbing", "Minor repairs and fixes", 75.0, 60, 4));
        mockServicePackages.add(new ServicePackage(8, "Advanced Plumbing", "Major repairs and installation", 150.0, 180, 4));
        
        // Initialize some sample bookings
        initializeSampleBookings();
    }
    
    private void initializeSampleBookings() {
        // Create sample customer
        Customer customer = new Customer(1, "John", "Doe", "john.doe@email.com", "+1234567890");
        
        // Sample booking 1 - Pending
        Booking booking1 = new Booking();
        booking1.setId(1);
        booking1.setBookingNumber("BK-2024-001-001");
        booking1.setStatus(BookingStatus.PENDING.getValue());
        booking1.setScheduledDate("2024-03-15T00:00:00Z");
        booking1.setScheduledTime("14:30:00");
        booking1.setEstimatedDurationMinutes(120);
        booking1.setTotalAmount(50.0);
        booking1.setServiceAddress("123 Main St, District 1, Ho Chi Minh City");
        booking1.setSpecialInstructions("Please bring cleaning supplies");
        booking1.setPreferredPaymentMethod(PaymentMethod.CASH.getValue());
        booking1.setCreatedAt(dateFormatter.format(new Date()));
        booking1.setCustomer(customer);
        booking1.setService(mockServices.get(0)); // House Cleaning
        booking1.setServicePackage(mockServicePackages.get(0)); // Basic Cleaning
        mockBookings.add(booking1);
        
        // Sample booking 2 - Confirmed
        Booking booking2 = new Booking();
        booking2.setId(2);
        booking2.setBookingNumber("BK-2024-001-002");
        booking2.setStatus(BookingStatus.CONFIRMED.getValue());
        booking2.setScheduledDate("2024-03-20T00:00:00Z");
        booking2.setScheduledTime("10:00:00");
        booking2.setEstimatedDurationMinutes(180);
        booking2.setTotalAmount(80.0);
        booking2.setServiceAddress("456 Oak Ave, District 2, Ho Chi Minh City");
        booking2.setSpecialInstructions("Deep cleaning required");
        booking2.setPreferredPaymentMethod(PaymentMethod.CREDIT_CARD.getValue());
        booking2.setCreatedAt(dateFormatter.format(new Date()));
        booking2.setCustomer(customer);
        booking2.setService(mockServices.get(0)); // House Cleaning
        booking2.setServicePackage(mockServicePackages.get(1)); // Deep Cleaning
        mockBookings.add(booking2);
        
        // Sample booking 3 - Completed
        Booking booking3 = new Booking();
        booking3.setId(3);
        booking3.setBookingNumber("BK-2024-001-003");
        booking3.setStatus(BookingStatus.COMPLETED.getValue());
        booking3.setScheduledDate("2024-03-10T00:00:00Z");
        booking3.setScheduledTime("09:00:00");
        booking3.setEstimatedDurationMinutes(150);
        booking3.setTotalAmount(120.0);
        booking3.setFinalAmount(120.0);
        booking3.setServiceAddress("789 Pine St, District 3, Ho Chi Minh City");
        booking3.setSpecialInstructions("Office cleaning after hours");
        booking3.setPreferredPaymentMethod(PaymentMethod.BANK_TRANSFER.getValue());
        booking3.setCreatedAt(dateFormatter.format(new Date()));
        booking3.setStartedAt("2024-03-10T09:05:00Z");
        booking3.setCompletedAt("2024-03-10T11:35:00Z");
        booking3.setCustomer(customer);
        booking3.setService(mockServices.get(1)); // Office Cleaning
        booking3.setServicePackage(mockServicePackages.get(4)); // Medium Office
        
        // Add staff and payment info
        Staff staff = new Staff(1, "Jane", "Smith", "jane.smith@email.com", "+1234567891", 25.0, 4.8);
        booking3.setStaff(staff);
        
        Payment payment = new Payment(1, 120.0, PaymentMethod.BANK_TRANSFER.getValue(), 1, "2024-03-10T11:45:00Z");
        booking3.setPayment(payment);
        
        mockBookings.add(booking3);
        
        nextBookingId = 4;
    }
    
    @Override
    public Call<ApiResponse<Booking>> createBooking(CreateBookingRequest request) {
        return new MockCall<>(() -> {
            // Create new booking from request
            Booking newBooking = new Booking();
            newBooking.setId(nextBookingId++);
            newBooking.setBookingNumber("BK-2024-001-" + String.format("%03d", newBooking.getId()));
            newBooking.setStatus(BookingStatus.PENDING.getValue());
            newBooking.setScheduledDate(request.getScheduledDate());
            newBooking.setScheduledTime(request.getScheduledTime());
            newBooking.setServiceAddress(request.getServiceAddress());
            newBooking.setAddressLatitude(request.getAddressLatitude());
            newBooking.setAddressLongitude(request.getAddressLongitude());
            newBooking.setSpecialInstructions(request.getSpecialInstructions());
            newBooking.setPreferredPaymentMethod(request.getPreferredPaymentMethod());
            newBooking.setCreatedAt(dateFormatter.format(new Date()));
            
            // Find and set service
            Service service = findServiceById(request.getServiceId());
            newBooking.setService(service);
            
            // Find and set service package
            ServicePackage servicePackage = findServicePackageById(request.getServicePackageId());
            newBooking.setServicePackage(servicePackage);
            
            if (servicePackage != null) {
                newBooking.setEstimatedDurationMinutes(servicePackage.getDurationMinutes());
                newBooking.setTotalAmount(servicePackage.getPrice());
            }
            
            // Set current user as customer (mock)
            Customer customer = new Customer(1, "John", "Doe", "john.doe@email.com", "+1234567890");
            newBooking.setCustomer(customer);
            
            mockBookings.add(newBooking);
            
            return new ApiResponse<>(true, "Booking created successfully", newBooking);
        });
    }
    
    @Override
    public Call<ApiResponse<Booking>> getBookingById(int bookingId) {
        return new MockCall<>(() -> {
            Booking booking = findBookingById(bookingId);
            if (booking != null) {
                return new ApiResponse<>(true, "Booking found", booking);
            } else {
                return new ApiResponse<>(false, "Booking not found", null);
            }
        });
    }
    
    @Override
    public Call<ApiResponse<List<Booking>>> getBookings(Integer status, String startDate, String endDate, Integer pageNumber, Integer pageSize) {
        return new MockCall<>(() -> {
            List<Booking> filteredBookings = new ArrayList<>(mockBookings);
            
            // Apply status filter
            if (status != null) {
                filteredBookings.removeIf(booking -> booking.getStatus() != status);
            }
            
            // Apply date filters (simplified implementation)
            // In real implementation, you would parse dates and compare properly
            
            // Apply pagination
            int page = pageNumber != null ? pageNumber : 1;
            int size = pageSize != null ? pageSize : 10;
            int startIndex = (page - 1) * size;
            int endIndex = Math.min(startIndex + size, filteredBookings.size());
            
            if (startIndex < filteredBookings.size()) {
                filteredBookings = filteredBookings.subList(startIndex, endIndex);
            } else {
                filteredBookings = new ArrayList<>();
            }
            
            return new ApiResponse<>(true, "Bookings retrieved successfully", filteredBookings);
        });
    }
    
    @Override
    public Call<ApiResponse<Booking>> updateBooking(int bookingId, CreateBookingRequest request) {
        return new MockCall<>(() -> {
            Booking booking = findBookingById(bookingId);
            if (booking != null && booking.getStatusEnum().canBeModified()) {
                booking.setScheduledDate(request.getScheduledDate());
                booking.setScheduledTime(request.getScheduledTime());
                booking.setServiceAddress(request.getServiceAddress());
                booking.setAddressLatitude(request.getAddressLatitude());
                booking.setAddressLongitude(request.getAddressLongitude());
                booking.setSpecialInstructions(request.getSpecialInstructions());
                booking.setPreferredPaymentMethod(request.getPreferredPaymentMethod());
                
                return new ApiResponse<>(true, "Booking updated successfully", booking);
            } else {
                return new ApiResponse<>(false, "Booking cannot be updated", null);
            }
        });
    }
    
    @Override
    public Call<ApiResponse<Booking>> cancelBooking(int bookingId, CancelBookingRequest request) {
        return new MockCall<>(() -> {
            Booking booking = findBookingById(bookingId);
            if (booking != null && booking.getStatusEnum().canBeCancelled()) {
                booking.setStatus(BookingStatus.CANCELLED.getValue());
                booking.setCancelledAt(dateFormatter.format(new Date()));
                booking.setCancellationReason(request.getReason());
                
                return new ApiResponse<>(true, "Booking cancelled successfully", booking);
            } else {
                return new ApiResponse<>(false, "Booking cannot be cancelled", null);
            }
        });
    }
    
    @Override
    public Call<ApiResponse<List<Service>>> getServices() {
        return new MockCall<>(() -> {
            return new ApiResponse<>(true, "Services retrieved successfully", new ArrayList<>(mockServices));
        });
    }
    
    @Override
    public Call<ApiResponse<List<ServicePackage>>> getServicePackages(int serviceId) {
        return new MockCall<>(() -> {
            List<ServicePackage> packages = new ArrayList<>();
            for (ServicePackage pkg : mockServicePackages) {
                if (pkg.getServiceId() == serviceId) {
                    packages.add(pkg);
                }
            }
            return new ApiResponse<>(true, "Service packages retrieved successfully", packages);
        });
    }
    
    @Override
    public Call<ApiResponse<List<String>>> getAvailableTimeSlots(int serviceId, String date, double latitude, double longitude) {
        return new MockCall<>(() -> {
            // Mock available time slots
            List<String> timeSlots = Arrays.asList(
                "08:00:00", "09:00:00", "10:00:00", "11:00:00", 
                "13:00:00", "14:00:00", "15:00:00", "16:00:00"
            );
            return new ApiResponse<>(true, "Available time slots retrieved successfully", timeSlots);
        });
    }
    
    // Helper methods
    private Booking findBookingById(int id) {
        for (Booking booking : mockBookings) {
            if (booking.getId() == id) {
                return booking;
            }
        }
        return null;
    }
    
    private Service findServiceById(int id) {
        for (Service service : mockServices) {
            if (service.getId() == id) {
                return service;
            }
        }
        return null;
    }
    
    private ServicePackage findServicePackageById(int id) {
        for (ServicePackage pkg : mockServicePackages) {
            if (pkg.getId() == id) {
                return pkg;
            }
        }
        return null;
    }
    
    // Cleanup method for proper resource management
    public void shutdown() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
    
    // Mock Call implementation for testing
    private static class MockCall<T> implements Call<T> {
        private final MockCallSupplier<T> supplier;
        private volatile boolean executed = false;
        private volatile boolean canceled = false;
        
        public MockCall(MockCallSupplier<T> supplier) {
            this.supplier = supplier;
        }
        
        @Override
        public Response<T> execute() {
            try {
                executed = true;
                T result = supplier.get();
                return Response.success(result);
            } catch (Exception e) {
                return Response.error(500, null);
            }
        }
        
        @Override
        public void enqueue(Callback<T> callback) {
            if (canceled) {
                return;
            }
            
            executed = true;
            
            // Run asynchronously to simulate real network call and prevent ANR
            executor.execute(() -> {
                try {
                    // Simulate network delay
                    Thread.sleep(100); // Small delay to simulate network call
                    
                    if (canceled) {
                        return; // Don't execute if canceled
                    }
                    
                    T result = supplier.get();
                    
                    // Post result back to main thread
                    android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                    mainHandler.post(() -> {
                        if (!canceled) {
                            callback.onResponse(this, Response.success(result));
                        }
                    });
                    
                } catch (Exception e) {
                    if (!canceled) {
                        // Post error back to main thread
                        android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                        mainHandler.post(() -> {
                            if (!canceled) {
                                callback.onFailure(this, e);
                            }
                        });
                    }
                }
            });
        }
        
        @Override
        public boolean isExecuted() { 
            return executed; 
        }
        
        @Override
        public void cancel() { 
            canceled = true; 
        }
        
        @Override
        public boolean isCanceled() { 
            return canceled; 
        }
        
        @Override
        public Call<T> clone() { 
            return new MockCall<>(supplier); 
        }
        
        @Override
        public okhttp3.Request request() { 
            return null; 
        }
        
        @Override
        public okio.Timeout timeout() { 
            return okio.Timeout.NONE; 
        }
    }
    
    @FunctionalInterface
    private interface MockCallSupplier<T> {
        T get() throws Exception;
    }
} 