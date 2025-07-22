package com.example.prm_project.utils;

public class Constants {
    
    // API Constants
    public static final String BASE_URL = "http://10.0.2.2:5233/api/";
    public static final String AUTH_BASE_URL = "http://10.0.2.2:5233/api/auth/";
    public static final String ADMIN_BASE_URL = "http://10.0.2.2:5233/api/admin/";
    public static final int NETWORK_TIMEOUT = 30;
    
    // Authentication Endpoints
    public static final String ENDPOINT_LOGIN = "login";
    public static final String ENDPOINT_REGISTER_CUSTOMER = "register/customer";
    public static final String ENDPOINT_REGISTER_STAFF = "register/staff";
    public static final String ENDPOINT_PROFILE = "profile";
    public static final String ENDPOINT_CHANGE_PASSWORD = "change-password";
    public static final String ENDPOINT_REFRESH_TOKEN = "refresh";
    public static final String ENDPOINT_LOGOUT = "logout";
    public static final String ENDPOINT_FORGOT_PASSWORD = "forgot-password";
    public static final String ENDPOINT_RESET_PASSWORD = "reset-password";
    
    // Database Constants
    public static final String DATABASE_NAME = "prm_database";
    public static final int DATABASE_VERSION = 1;
    
    // SharedPreferences Keys
    public static final String SHARED_PREFS_NAME = "prm_preferences";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_EMAIL = "user_email";
    public static final String KEY_USER_NAME = "user_name";
    public static final String KEY_USER_ROLE = "user_role";
    public static final String KEY_ACCESS_TOKEN = "access_token";
    public static final String KEY_REFRESH_TOKEN = "refresh_token";
    public static final String KEY_TOKEN_EXPIRES_AT = "token_expires_at";
    public static final String KEY_IS_LOGGED_IN = "is_logged_in";
    public static final String KEY_LAST_SYNC = "last_sync";
    public static final String KEY_REMEMBER_ME = "remember_me";
    
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


