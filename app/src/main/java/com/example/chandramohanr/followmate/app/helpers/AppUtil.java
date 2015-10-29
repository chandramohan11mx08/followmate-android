package com.example.chandramohanr.followmate.app.helpers;

public class AppUtil {
    public static String getLoggedInUserId() {
        return SharedPreferenceHelper.getString(SharedPreferenceHelper.KEY_USER_ID);
    }

    public static boolean isAnySessionActive() {
        return SharedPreferenceHelper.getBoolean(SharedPreferenceHelper.KEY_IS_SESSION_ACTIVE);
    }
}
