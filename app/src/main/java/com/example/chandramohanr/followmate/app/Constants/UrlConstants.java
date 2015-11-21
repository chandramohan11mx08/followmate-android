package com.example.chandramohanr.followmate.app.Constants;

public final class UrlConstants {

    private static int development = 0;
    private static int production = 1;

    private static int CURRENT_ENV = development;

    private static String USER_REGISTER[] = {"http://192.168.1.107:6610/user/register", "http://localhost:6610/user/register"};

    private static String SERVER_SOCKET[] = {"http://192.168.1.107:6610", "http://localhost:6610/user/register"};

    public static String getUserRegisterUrl(){
        return USER_REGISTER[CURRENT_ENV];
    }

    public static String getServerSocketUrl(){
        return SERVER_SOCKET[CURRENT_ENV];
    }
}
