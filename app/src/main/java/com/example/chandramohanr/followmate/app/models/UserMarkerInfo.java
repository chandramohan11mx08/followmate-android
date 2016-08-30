package com.example.chandramohanr.followmate.app.models;

import com.example.chandramohanr.followmate.app.helpers.AnimateMarker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class UserMarkerInfo {
    public String userId;
    public Marker marker;
    public AnimateMarker animateMarker;
    public boolean isMarkerVisible = true;

    public UserMarkerInfo(String userId, Marker marker) {
        this.userId = userId;
        this.marker = marker;
        this.animateMarker = new AnimateMarker();
    }

    public void animateMarker(GoogleMap map, UserLocation userLocation ){
        animateMarker.animateMarker(map, userLocation, this.marker);
    }
}
