package com.example.chandramohanr.followmate.app.Constants;

public final class UrlConstants {

    private static int development = 0;
    private static int production = 1;

    private static int CURRENT_ENV = production;

    private static final String developmentUrl = "http://192.168.1.3:6610";
    private static final String productionUrl = "http://128.199.224.226:6610";

    private static String url[] = {developmentUrl, productionUrl};

    private static String USER_REGISTER[] = {developmentUrl+"/user/register", productionUrl+"/user/register"};

    private static String SERVER_SOCKET[] = {developmentUrl, productionUrl};

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
