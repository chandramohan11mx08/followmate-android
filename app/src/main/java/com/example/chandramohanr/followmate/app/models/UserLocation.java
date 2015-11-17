package com.example.chandramohanr.followmate.app.models;

public class UserLocation {
    public double lat;
    public double lng;
    public float bearingTo;

    public UserLocation(double lat, double lng, float bearingTo) {
        this.lat = lat;
        this.lng = lng;
        this.bearingTo = bearingTo;
    }
}
