package com.example.prm_project.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtils {
    
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME_FORMAT = "HH:mm";
    private static final String DISPLAY_TIME_FORMAT = "HH:mm";
    private static final String FULL_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    /**
     * Format date to YYYY-MM-DD format
     */
    public static String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        return dateFormat.format(date);
    }
    
    /**
     * Format date to YYYY-MM-DD format
     */
    public static String formatDate(Calendar calendar) {
        return formatDate(calendar.getTime());
    }
    
    /**
     * Format time to HH:mm format
     */
    public static String formatTime(Date date) {
        SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());
        return timeFormat.format(date);
    }
    
    /**
     * Format time to HH:mm format
     */
    public static String formatTime(Calendar calendar) {
        return formatTime(calendar.getTime());
    }
    
    /**
     * Format display time to HH:mm format
     */
    public static String formatDisplayTime(String timeString) {
        try {
            // Parse time string like "09:00:00" and return "09:00"
            if (timeString != null && timeString.length() >= 5) {
                return timeString.substring(0, 5);
            }
        } catch (Exception e) {
            // Return original string if parsing fails
        }
        return timeString;
    }
    
    /**
     * Get today's date in YYYY-MM-DD format
     */
    public static String getTodayDate() {
        return formatDate(Calendar.getInstance());
    }
    
    /**
     * Get tomorrow's date in YYYY-MM-DD format
     */
    public static String getTomorrowDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return formatDate(calendar);
    }
    
    /**
     * Check if a date is in the past
     */
    public static boolean isPastDate(String dateString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
            Date date = dateFormat.parse(dateString);
            return date != null && date.before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if a date is today
     */
    public static boolean isToday(String dateString) {
        return dateString != null && dateString.equals(getTodayDate());
    }
    
    /**
     * Get date after specified number of days
     */
    public static String getDateAfterDays(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return formatDate(calendar);
    }
    
    /**
     * Parse date string to Calendar
     */
    public static Calendar parseDate(String dateString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
            Date date = dateFormat.parse(dateString);
            if (date != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                return calendar;
            }
        } catch (Exception e) {
            // Return null if parsing fails
        }
        return null;
    }
    
    /**
     * Get day of week name
     */
    public static String getDayOfWeek(String dateString) {
        Calendar calendar = parseDate(dateString);
        if (calendar != null) {
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
            return dayFormat.format(calendar.getTime());
        }
        return "";
    }
    
    /**
     * Get formatted date for display (e.g., "Monday, Jan 15")
     */
    public static String getFormattedDisplayDate(String dateString) {
        Calendar calendar = parseDate(dateString);
        if (calendar != null) {
            SimpleDateFormat displayFormat = new SimpleDateFormat("EEEE, MMM dd", Locale.getDefault());
            return displayFormat.format(calendar.getTime());
        }
        return dateString;
    }
} 