package com.example.chandramohanr.followmate.app.helpers;

import com.example.chandramohanr.followmate.app.FollowmateApplication;

public class AppUtil {
    public static String getLoggedInUserId() {
        return SharedPreferenceHelper.getString(SharedPreferenceHelper.KEY_USER_ID);
    }

    public static boolean isAnySessionActive() {
        return FollowmateApplication.sessionContext != null && FollowmateApplication.sessionContext.isSessionActive;
    }
}
