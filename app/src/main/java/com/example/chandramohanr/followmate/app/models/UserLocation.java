package com.example.chandramohanr.followmate.app.models;

public class UserLocation {
    public Double lat;
    public Double lng;
    public float bearingTo;

    public UserLocation(Double lat, Double lng, float bearingTo) {
        this.lat = lat;
        this.lng = lng;
        this.bearingTo = bearingTo;
    }
}
