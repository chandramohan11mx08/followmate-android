package com.example.chandramohanr.followmate.app.Constants;

public final class UrlConstants {

    private static int development = 0;
    private static int production = 1;

    private static int CURRENT_ENV = development;

    private static String USER_REGISTER[] = {"http://192.168.50.56:6610/user/register", "http://localhost:6610/user/register"};

    public static String getUserRegisterUrl(){
        return USER_REGISTER[CURRENT_ENV];
    }
}
