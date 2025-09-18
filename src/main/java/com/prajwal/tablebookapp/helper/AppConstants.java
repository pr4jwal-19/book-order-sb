package com.prajwal.tablebookapp.helper;

import java.time.LocalDateTime;

// Don't publish on GitHub
public class AppConstants {

    public static final String APP_NAME = "Table Booking App";
    public static final String APP_BASE_URL = "http://localhost:8080/api/v1";
    public static final LocalDateTime EXPIRY_DATE = LocalDateTime.now().plusMinutes(30);
}
