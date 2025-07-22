//package com.example.prm_project.ui.view.booking;
//
//import android.Manifest;
//import android.content.pm.PackageManager;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.StrictMode;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.core.app.ActivityCompat;
//import androidx.fragment.app.Fragment;
//import androidx.lifecycle.ViewModelProvider;
//import androidx.recyclerview.widget.LinearLayoutManager;
//
//import com.android.volley.BuildConfig;
//import com.example.prm_project.R;
//import com.example.prm_project.databinding.FragmentAddressSelectionBinding;
//import com.example.prm_project.ui.viewmodel.BookingViewModel;
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.MapView;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.android.libraries.places.api.Places;
//import com.google.android.libraries.places.api.model.AutocompletePrediction;
//import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
//import com.google.android.libraries.places.api.model.Place;
//import com.google.android.libraries.places.api.model.RectangularBounds;
//import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
//import com.google.android.libraries.places.api.net.PlacesClient;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//
//import dagger.hilt.android.AndroidEntryPoint;
//
//@AndroidEntryPoint
//public class AddressSelectionFragment extends Fragment implements OnMapReadyCallback {
//
//    private static final String TAG = "AddressSelectionFragment";
//    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
//    private static final int SEARCH_DELAY_MS = 300; // Reduced delay for MapView
//    private static final int MAP_INIT_DELAY_MS = 100; // Much shorter delay for MapView
//    private static final long CAMERA_IDLE_DEBOUNCE_MS = 500;
//    private static final long LOCATION_REQUEST_COOLDOWN_MS = 1000;
//
//    private FragmentAddressSelectionBinding binding;
//    private BookingViewModel bookingViewModel;
//    private GoogleMap googleMap;
//    private MapView mapView;
//    private FusedLocationProviderClient fusedLocationClient;
//
//    // Google Places API components
//    private PlacesClient placesClient;
//    private AutocompleteSessionToken sessionToken;
//    private PlacePredictionAdapter predictionAdapter;
//
//    // Threading
//    private Handler mainHandler;
//    private ExecutorService executor; // Added Executor for background tasks
//    private long lastSearchRequestTime = 0; // For debouncing search in Executor
//    private Runnable mapInitRunnable;
//
//    // Address data
//    private double selectedLatitude = 10.762622; // Default to Ho Chi Minh City
//    private double selectedLongitude = 106.660172;
//    private String selectedAddress = "";
//    private LatLng lastMarkerPosition = null;
//
//    // State flags
//    private boolean isMapReady = false;
//    private boolean isPlacesInitialized = false;
//    private boolean isMapInitStarted = false;
//    private long lastCameraIdleTime = 0;
//    private long lastLocationRequestTime = 0;
//
//    // Interface for callback
//    public interface OnAddressSelectedListener {
//        void onAddressSelected(String address, double latitude, double longitude);
//    }
//
//    private OnAddressSelectedListener addressSelectedListener;
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        binding = FragmentAddressSelectionBinding.inflate(inflater, container, false);
//        return binding.getRoot();
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        // Enable StrictMode in debug mode
//        if (BuildConfig.DEBUG) {
//            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//                    .detectAll()
//                    .penaltyLog()
//                    .build());
//        }
//
//        // Initialize threading components
//        mainHandler = new Handler(Looper.getMainLooper());
//        executor = Executors.newSingleThreadExecutor(); // Single-thread executor for sequential tasks
//
//        // Show immediate usable state
//        showInitialState();
//
//        // Initialize ViewModel
//        bookingViewModel = new ViewModelProvider(requireActivity()).get(BookingViewModel.class);
//
//        // Initialize location services
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
//
//        // Setup UI listeners
//        setupUIListeners();
//
//        // Setup suggestions RecyclerView
//        setupSuggestionsRecyclerView();
//
//        // Initialize Places API
//        initializePlacesApiQuick();
//
//        // Delay map initialization
//        scheduleMapInitialization();
//
//        // Check location permission
//        checkLocationPermission();
//    }
//
//    private void showInitialState() {
//        if (binding != null) {
//            binding.progressMap.setVisibility(View.GONE);
//            binding.btnConfirmAddress.setEnabled(true);
//            binding.tvSelectedAddress.setText("Default location: Ho Chi Minh City");
//            binding.tvCoordinates.setText(String.format("Lat: %.6f, Lng: %.6f", selectedLatitude, selectedLongitude));
//            binding.tvCoordinates.setVisibility(View.VISIBLE);
//
//            // Ensure map container is visible and has proper size
//            binding.mapView.setVisibility(View.VISIBLE);
//
//            // Update status
//            binding.tvMapStatus.setText("Fragment Loaded - Initializing Map...");
//
//            selectedAddress = "Ho Chi Minh City, Vietnam";
//
//            Log.d(TAG, "Initial state set - UI should be visible");
//        }
//    }
//
//    private void initializePlacesApiQuick() {
//        try {
//            if (!Places.isInitialized()) {
//                Places.initialize(requireContext().getApplicationContext(), getString(R.string.google_maps_key));
//            }
//            placesClient = Places.createClient(requireContext());
//            sessionToken = AutocompleteSessionToken.newInstance();
//            isPlacesInitialized = true;
//            Log.d(TAG, "Places API initialized quickly");
//        } catch (Exception e) {
//            Log.e(TAG, "Failed to initialize Places API", e);
//        }
//    }
//
//    private void scheduleMapInitialization() {
//        mapInitRunnable = () -> {
//            if (isAdded() && binding != null && !isMapInitStarted) {
//                isMapInitStarted = true;
//                initializeMapGradually();
//            }
//        };
//        mainHandler.postDelayed(mapInitRunnable, MAP_INIT_DELAY_MS);
//    }
//
//    private void initializeMapGradually() {
//        try {
//            binding.progressMap.setVisibility(View.VISIBLE);
//            binding.tvMapStatus.setText("Starting MapView initialization...");
//
//            // Initialize MapView directly
//            mapView = binding.mapView;
//            mapView.onCreate(null);
//            mapView.onResume(); // needed to get the map to display immediately
//
//            binding.tvMapStatus.setText("MapView created - getting map async...");
//            mapView.getMapAsync(this);
//
//            Log.d(TAG, "MapView initialization started");
//        } catch (Exception e) {
//            Log.e(TAG, "Failed to initialize map", e);
//            binding.tvMapStatus.setText("Map initialization failed: " + e.getMessage());
//            hideMapLoadingAndShowFallback();
//        }
//    }
//
//    @Override
//    public void onMapReady(@NonNull GoogleMap map) {
//        try {
//            googleMap = map;
//            isMapReady = true;
//
//            Log.d(TAG, "Google Map ready - starting configuration");
//
//            configureMapLightweight();
//            setDefaultLocation();
//            setupLightweightMapListeners();
//
//            // Hide loading and ensure map container is visible
//            if (binding != null) {
//                binding.progressMap.setVisibility(View.GONE);
//                // Ensure map container is visible
//                binding.mapView.setVisibility(View.VISIBLE);
//                // Update status
//                binding.tvMapStatus.setText("✅ Google Map Ready!");
//            }
//
//            Log.d(TAG, "Google Map ready with lightweight config - map should be visible now");
//        } catch (Exception e) {
//            Log.e(TAG, "Error in onMapReady", e);
//            hideMapLoadingAndShowFallback();
//        }
//    }
//
//    private void configureMapLightweight() {
//        if (googleMap == null) return;
//        try {
//            googleMap.getUiSettings().setMapToolbarEnabled(false);
//            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
//            googleMap.getUiSettings().setCompassEnabled(false);
//            googleMap.getUiSettings().setRotateGesturesEnabled(false);
//            googleMap.getUiSettings().setTiltGesturesEnabled(false);
//            googleMap.setBuildingsEnabled(false);
//            googleMap.setIndoorEnabled(false);
//            googleMap.setTrafficEnabled(false);
//        } catch (Exception e) {
//            Log.e(TAG, "Error configuring map", e);
//        }
//    }
//
//    private void setDefaultLocation() {
//        if (googleMap == null) return;
//        try {
//            LatLng defaultLocation = new LatLng(selectedLatitude, selectedLongitude);
//            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12));
//            googleMap.addMarker(new MarkerOptions().position(defaultLocation).title("Default Location"));
//            lastMarkerPosition = defaultLocation;
//        } catch (Exception e) {
//            Log.e(TAG, "Error setting default location", e);
//        }
//    }
//
//    private void setupLightweightMapListeners() {
//        if (googleMap == null) return;
//        try {
//            googleMap.setOnMapClickListener(latLng -> {
//                if (latLng != null) {
//                    updateLocationSelection(latLng);
//                }
//            });
//
//            googleMap.setOnCameraIdleListener(() -> {
//                long currentTime = System.currentTimeMillis();
//                if (currentTime - lastCameraIdleTime > CAMERA_IDLE_DEBOUNCE_MS && isMapReady && googleMap != null) {
//                    try {
//                        LatLng center = googleMap.getCameraPosition().target;
//                        if (center != null) {
//                            updateLocationSelection(center);
//                        }
//                        lastCameraIdleTime = currentTime;
//                    } catch (Exception e) {
//                        Log.e(TAG, "Error in camera idle", e);
//                    }
//                }
//            });
//        } catch (Exception e) {
//            Log.e(TAG, "Error setting up map listeners", e);
//        }
//    }
//
//    private void hideMapLoadingAndShowFallback() {
//        if (binding != null) {
//            binding.progressMap.setVisibility(View.GONE);
//            binding.tvSelectedAddress.setText("Map unavailable - using coordinate selection");
//            Toast.makeText(getContext(), "Map loading failed, but you can still select coordinates", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void setupUIListeners() {
//        binding.etSearchAddress.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                // Debounce search using Executor
//                long currentTime = System.currentTimeMillis();
//                if (currentTime - lastSearchRequestTime < SEARCH_DELAY_MS) {
//                    return;
//                }
//                lastSearchRequestTime = currentTime;
//
//                if (isAdded() && s.length() > 3) {
//                    executor.execute(() -> searchPlacesLightweight(s.toString()));
//                } else {
//                    mainHandler.post(() -> hideSuggestions());
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {}
//        });
//
//        binding.btnCurrentLocation.setOnClickListener(v -> getCurrentLocationLightweight());
//        binding.btnConfirmAddress.setOnClickListener(v -> confirmAddress());
//    }
//
//    private void setupSuggestionsRecyclerView() {
//        predictionAdapter = new PlacePredictionAdapter(prediction -> {
//            mainHandler.post(() -> {
//                hideSuggestions();
//                binding.etSearchAddress.setText("");
//            });
//            executor.execute(() -> getPlaceDetailsLightweight(prediction.getPlaceId()));
//        });
//
//        binding.rvAddressSuggestions.setLayoutManager(new LinearLayoutManager(getContext()));
//        binding.rvAddressSuggestions.setAdapter(predictionAdapter);
//    }
//
//    private void searchPlacesLightweight(String query) {
//        if (!isPlacesInitialized || placesClient == null) {
//            Log.w(TAG, "Places API not ready");
//            return;
//        }
//        try {
//            FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
//                    .setLocationBias(RectangularBounds.newInstance(
//                            new LatLng(8.0, 102.0),
//                            new LatLng(24.0, 110.0)
//                    ))
//                    .setCountries("VN")
//                    .setSessionToken(sessionToken)
//                    .setQuery(query)
//                    .build();
//
//            placesClient.findAutocompletePredictions(request)
//                    .addOnSuccessListener(response -> {
//                        if (isAdded() && binding != null) {
//                            mainHandler.post(() -> {
//                                try {
//                                    List<AutocompletePrediction> predictions = response.getAutocompletePredictions();
//                                    predictionAdapter.updatePredictions(predictions);
//                                    showSuggestions(!predictions.isEmpty());
//                                } catch (Exception e) {
//                                    Log.e(TAG, "Error processing predictions", e);
//                                }
//                            });
//                        }
//                    })
//                    .addOnFailureListener(exception -> {
//                        Log.e(TAG, "Place prediction error", exception);
//                        mainHandler.post(() -> hideSuggestions());
//                    });
//        } catch (Exception e) {
//            Log.e(TAG, "Search places error", e);
//            mainHandler.post(() -> hideSuggestions());
//        }
//    }
//
//    private void getPlaceDetailsLightweight(String placeId) {
//        if (!isPlacesInitialized || placesClient == null) return;
//        try {
//            List<Place.Field> placeFields = Arrays.asList(
//                    Place.Field.LAT_LNG,
//                    Place.Field.ADDRESS
//            );
//
//            com.google.android.libraries.places.api.net.FetchPlaceRequest request =
//                    com.google.android.libraries.places.api.net.FetchPlaceRequest.newInstance(placeId, placeFields);
//
//            placesClient.fetchPlace(request)
//                    .addOnSuccessListener(response -> {
//                        if (isAdded() && binding != null) {
//                            mainHandler.post(() -> {
//                                try {
//                                    Place place = response.getPlace();
//                                    if (place.getLatLng() != null) {
//                                        LatLng latLng = place.getLatLng();
//                                        String address = place.getAddress() != null ? place.getAddress() : "Selected location";
//                                        updateLocationAndMap(latLng, address);
//                                    }
//                                } catch (Exception e) {
//                                    Log.e(TAG, "Error processing place details", e);
//                                }
//                            });
//                        }
//                    })
//                    .addOnFailureListener(exception -> {
//                        Log.e(TAG, "Place details error", exception);
//                        mainHandler.post(() -> Toast.makeText(getContext(), "Failed to get location details", Toast.LENGTH_SHORT).show());
//                    });
//        } catch (Exception e) {
//            Log.e(TAG, "Get place details error", e);
//        }
//    }
//
//    private void updateLocationSelection(LatLng latLng) {
//        selectedLatitude = latLng.latitude;
//        selectedLongitude = latLng.longitude;
//        selectedAddress = String.format("Lat: %.6f, Lng: %.6f", latLng.latitude, latLng.longitude);
//        mainHandler.post(this::updateUI);
//        mainHandler.post(() -> updateMapMarker(latLng));
//    }
//
//    private void updateLocationAndMap(LatLng latLng, String address) {
//        selectedLatitude = latLng.latitude;
//        selectedLongitude = latLng.longitude;
//        selectedAddress = address;
//        mainHandler.post(this::updateUI);
//        mainHandler.post(() -> {
//            updateMapMarker(latLng);
//            moveMapCamera(latLng);
//        });
//    }
//
//    private void updateUI() {
//        if (binding != null) {
//            binding.tvSelectedAddress.setText(selectedAddress);
//            binding.tvCoordinates.setText(String.format("Lat: %.6f, Lng: %.6f", selectedLatitude, selectedLongitude));
//            binding.tvCoordinates.setVisibility(View.VISIBLE);
//            binding.btnConfirmAddress.setEnabled(true);
//        }
//    }
//
//    private void updateMapMarker(LatLng latLng) {
//        if (googleMap != null && isMapReady) {
//            try {
//                if (lastMarkerPosition == null ||
//                        Math.abs(lastMarkerPosition.latitude - latLng.latitude) > 0.0001 ||
//                        Math.abs(lastMarkerPosition.longitude - latLng.longitude) > 0.0001) {
//                    googleMap.clear();
//                    googleMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
//                    lastMarkerPosition = latLng;
//                }
//            } catch (Exception e) {
//                Log.e(TAG, "Error updating map marker", e);
//            }
//        }
//    }
//
//    private void moveMapCamera(LatLng latLng) {
//        if (googleMap != null && isMapReady) {
//            try {
//                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
//            } catch (Exception e) {
//                Log.e(TAG, "Error moving camera", e);
//            }
//        }
//    }
//
//    private void showSuggestions(boolean show) {
//        if (binding != null) {
//            binding.rvAddressSuggestions.setVisibility(show ? View.VISIBLE : View.GONE);
//        }
//    }
//
//    private void hideSuggestions() {
//        showSuggestions(false);
//    }
//
//    private void getCurrentLocationLightweight() {
//        long currentTime = System.currentTimeMillis();
//        if (currentTime - lastLocationRequestTime < LOCATION_REQUEST_COOLDOWN_MS) {
//            return;
//        }
//        lastLocationRequestTime = currentTime;
//
//        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            requestLocationPermission();
//            return;
//        }
//
//        mainHandler.post(() -> binding.progressMap.setVisibility(View.VISIBLE));
//        executor.execute(() -> {
//            try {
//                fusedLocationClient.getLastLocation()
//                        .addOnSuccessListener(location -> {
//                            if (!isAdded() || binding == null) return;
//                            mainHandler.post(() -> {
//                                binding.progressMap.setVisibility(View.GONE);
//                                if (location != null) {
//                                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
//                                    updateLocationAndMap(currentLatLng, "Current location");
//                                } else {
//                                    Toast.makeText(getContext(), "Unable to get current location", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                        })
//                        .addOnFailureListener(e -> {
//                            if (!isAdded() || binding == null) return;
//                            mainHandler.post(() -> {
//                                binding.progressMap.setVisibility(View.GONE);
//                                Log.e(TAG, "Failed to get current location", e);
//                                Toast.makeText(getContext(), "Failed to get current location", Toast.LENGTH_SHORT).show();
//                            });
//                        });
//            } catch (Exception e) {
//                Log.e(TAG, "Error getting current location", e);
//                mainHandler.post(() -> {
//                    if (binding != null) {
//                        binding.progressMap.setVisibility(View.GONE);
//                    }
//                });
//            }
//        });
//    }
//
//    private void confirmAddress() {
//        if (selectedAddress.isEmpty()) {
//            selectedAddress = "Ho Chi Minh City, Vietnam";
//        }
//        hideSuggestions();
//        bookingViewModel.setServiceAddress(selectedAddress);
//        bookingViewModel.setAddressLatitude(selectedLatitude);
//        bookingViewModel.setAddressLongitude(selectedLongitude);
//
//        if (addressSelectedListener != null) {
//            addressSelectedListener.onAddressSelected(selectedAddress, selectedLatitude, selectedLongitude);
//        }
//
//        String successMessage = "Address confirmed: " +
//                (selectedAddress.length() > 30 ? selectedAddress.substring(0, 27) + "..." : selectedAddress);
//        Toast.makeText(getContext(), successMessage, Toast.LENGTH_SHORT).show();
//
//        if (getParentFragmentManager().getBackStackEntryCount() > 0) {
//            getParentFragmentManager().popBackStack();
//        }
//    }
//
//    private void checkLocationPermission() {
//        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            requestLocationPermission();
//        }
//    }
//
//    private void requestLocationPermission() {
//        requestPermissions(
//                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
//                LOCATION_PERMISSION_REQUEST_CODE
//        );
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(getContext(), "Location permission granted", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(getContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    public void setOnAddressSelectedListener(OnAddressSelectedListener listener) {
//        this.addressSelectedListener = listener;
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (mapView != null) {
//            mapView.onResume();
//        }
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        if (mapView != null) {
//            mapView.onPause();
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (mapView != null) {
//            mapView.onDestroy();
//        }
//    }
//
//    @Override
//    public void onLowMemory() {
//        super.onLowMemory();
//        if (mapView != null) {
//            mapView.onLowMemory();
//        }
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//
//        // Clean up MapView
//        if (mapView != null) {
//            mapView.onDestroy();
//        }
//
//        if (mainHandler != null) {
//            if (mapInitRunnable != null) {
//                mainHandler.removeCallbacks(mapInitRunnable);
//            }
//        }
//        // Shutdown Executor to prevent memory leaks
//        if (executor != null) {
//            executor.shutdown();
//            try {
//                if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
//                    executor.shutdownNow();
//                }
//            } catch (InterruptedException e) {
//                executor.shutdownNow();
//                Thread.currentThread().interrupt();
//            }
//        }
//        binding = null;
//    }
//}