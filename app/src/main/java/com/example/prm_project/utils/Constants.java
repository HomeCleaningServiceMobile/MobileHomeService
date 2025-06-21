package com.example.prm_project.utils;

public class Constants {
    
    // API Constants
    public static final String BASE_URL = "https://jsonplaceholder.typicode.com/";
    public static final int NETWORK_TIMEOUT = 30;
    
    // Database Constants
    public static final String DATABASE_NAME = "prm_database";
    public static final int DATABASE_VERSION = 1;
    
    // SharedPreferences Keys
    public static final String SHARED_PREFS_NAME = "prm_preferences";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_NAME = "user_name";
    public static final String KEY_IS_LOGGED_IN = "is_logged_in";
    public static final String KEY_LAST_SYNC = "last_sync";
    
    // Intent Keys
    public static final String EXTRA_USER_ID = "extra_user_id";
    public static final String EXTRA_USER_DATA = "extra_user_data";
    
    // Request Codes
    public static final int REQUEST_CODE_PERMISSION = 1001;
    public static final int REQUEST_CODE_EDIT_USER = 1002;
    
    // Error Messages
    public static final String ERROR_NETWORK = "Network error occurred";
    public static final String ERROR_UNKNOWN = "Unknown error occurred";
    public static final String ERROR_NO_DATA = "No data available";
    
    // Success Messages
    public static final String SUCCESS_DATA_LOADED = "Data loaded successfully";
    public static final String SUCCESS_USER_SAVED = "User saved successfully";
    public static final String SUCCESS_USER_DELETED = "User deleted successfully";
    
    private Constants() {
        // Private constructor to prevent instantiation
    }
} 