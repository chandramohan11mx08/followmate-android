package com.example.chandramohanr.followmate.app.Constants;

public final class UrlConstants {

    private static int development = 0;
    private static int production = 1;

    private static int CURRENT_ENV = production;
    private static String url[] = {"http://192.168.1.105:6610", "http://128.199.224.226::6610/"};

    private static String USER_REGISTER[] = {"http://192.168.50.108:6610/user/register", "http://128.199.224.226:6610/user/register"};

    private static String SERVER_SOCKET[] = {"http://192.168.1.105:6610", "http://128.199.224.226:6610"};

    public static String getUserRegisterUrl(){
        return USER_REGISTER[CURRENT_ENV];
    }

    public static String getUrl(){
        return url[CURRENT_ENV];
    }

    public static String getServerSocketUrl(){
        return SERVER_SOCKET[CURRENT_ENV];
    }
}
