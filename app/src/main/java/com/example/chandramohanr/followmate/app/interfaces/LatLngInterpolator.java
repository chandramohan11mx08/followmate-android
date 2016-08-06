package com.example.chandramohanr.followmate.app.interfaces;

import com.google.android.gms.maps.model.LatLng;

public interface LatLngInterpolator {
    LatLng interpolate(float fraction, LatLng a, LatLng b);

    public class LinearFixed implements LatLngInterpolator {
        @Override
        public LatLng interpolate(float fraction, LatLng a, LatLng b) {
            double lat = (b.latitude - a.latitude) * fraction + a.latitude;
            double lngDelta = b.longitude - a.longitude; // Take the shortest path across the 180th meridian. if (Math.abs(lngDelta) > 180) { lngDelta -= Math.signum(lngDelta) * 360; } double lng = lngDelta * fraction + a.longitude; return new LatLng(lat, lng); - See more at: http://www.simosh.com/article/bdihciad-how-to-animate-marker-in-android-map-api-v2.html#sthash.xCNvNMXk.dpuf
            if (Math.abs(lngDelta) > 180) {
                lngDelta -= Math.signum(lngDelta) * 360;
            }
            double lng = lngDelta * fraction + a.longitude;
            return new LatLng(lat, lng);
        }
    }
}