package com.example.chandramohanr.followmate.app.Constants;

public final class UrlConstants {

    public static int development = 1;
    public static int production = 2;

    public static int CURRENT_ENV = development;

    public static String USER_REGISTER[] = {"http://localhost:6610/register-user","http://localhost:6610/register-user"};

    public static String getUserRegisterUrl(){
        return USER_REGISTER[CURRENT_ENV];
    }

}
