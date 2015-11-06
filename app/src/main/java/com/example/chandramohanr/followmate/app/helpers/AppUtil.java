package com.example.chandramohanr.followmate.app.helpers;

public class AppUtil {
    public static String getLoggedInUserId() {
        return SharedPreferenceHelper.getString(SharedPreferenceHelper.KEY_USER_ID);
    }

    public static boolean isAnySessionActive() {
        String string = SharedPreferenceHelper.getString(SharedPreferenceHelper.KEY_ACTIVE_SESSION_ID);
        return (string!=null && !string.isEmpty());
    }
}
