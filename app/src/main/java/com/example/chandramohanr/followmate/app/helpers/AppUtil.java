package com.example.chandramohanr.followmate.app.helpers;

public class AppUtil {
    public static String getLoggedInUserId() {
        return SharedPreferenceHelper.getString(SharedPreferenceHelper.KEY_USER_ID);
    }

    public static boolean isAnySessionActive() {
        String string = getSessionId();
        return (string!=null && !string.isEmpty());
    }

    public static String getSessionId() {
         return SharedPreferenceHelper.getString(SharedPreferenceHelper.KEY_ACTIVE_SESSION_ID);
    }

    public static void setNewSessionInfo(String sessionId, boolean isOwner) {
        SharedPreferenceHelper.set(SharedPreferenceHelper.KEY_ACTIVE_SESSION_ID, sessionId);
        SharedPreferenceHelper.set(SharedPreferenceHelper.KEY_ACTIVE_SESSION_OWNER, isOwner);
        SharedPreferenceHelper.deleteSharedPreference(SharedPreferenceHelper.KEY_SESSION_TO_JOIN);
    }

    public static void resetSessionInfo() {
        SharedPreferenceHelper.deleteSharedPreference(SharedPreferenceHelper.KEY_ACTIVE_SESSION_ID);
        SharedPreferenceHelper.deleteSharedPreference(SharedPreferenceHelper.KEY_ACTIVE_SESSION_OWNER);
        SharedPreferenceHelper.deleteSharedPreference(SharedPreferenceHelper.KEY_SESSION_TO_JOIN);
    }
}
