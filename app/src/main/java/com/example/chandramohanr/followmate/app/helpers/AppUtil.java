package com.example.chandramohanr.followmate.app.helpers;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;

public class AppUtil {
    public static String getLoggedInUserId() {
        return SharedPreferenceHelper.getString(SharedPreferenceHelper.KEY_USER_ID);
    }

    public static boolean isUserMobileNumberVerified() {
        return SharedPreferenceHelper.getBoolean(SharedPreferenceHelper.KEY_MOBILE_VERIFIED);
    }

    public static String getLoggedInUserMobileNumber() {
        return SharedPreferenceHelper.getString(SharedPreferenceHelper.KEY_MOBILE_NUMBER);
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

    public static void resetUserInfo(){
        SharedPreferenceHelper.deleteSharedPreference(SharedPreferenceHelper.KEY_USER_ID);
        SharedPreferenceHelper.deleteSharedPreference(SharedPreferenceHelper.KEY_MOBILE_NUMBER);
    }

    public static void setAccountManager(String userId, Context context) {
        String accountName = userId;
        String accountType = "com.followmate.app";
        final Account account = new Account(accountName, accountType);
        AccountManager mAccountManager =  AccountManager.get(context);
        String authtoken = userId;
        // Creating the account on the device and setting the auth token we got
        // (Not setting the auth token will cause another call to the server to authenticate the user)
        mAccountManager.addAccountExplicitly(account, "", null);
        mAccountManager.setAuthToken(account, "1", authtoken);
    }

    public static void openChooseDialogToSendTextMessage(Context context, String message) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setType("text/plain");
        context.startActivity(Intent.createChooser(sendIntent, "Share location"));
    }
}
