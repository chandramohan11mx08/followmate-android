package com.example.chandramohanr.followmate.app.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chandramohanr.followmate.R;
import com.example.chandramohanr.followmate.app.Constants.AppConstants;
import com.example.chandramohanr.followmate.app.SocketController;
import com.example.chandramohanr.followmate.app.helpers.AppUtil;
import com.example.chandramohanr.followmate.app.helpers.SharedPreferenceHelper;
import com.example.chandramohanr.followmate.app.models.ParticipantInfo;
import com.example.chandramohanr.followmate.app.models.UserLocation;
import com.example.chandramohanr.followmate.app.models.events.ShareLocationInfo;
import com.example.chandramohanr.followmate.app.models.events.StartSessionRequest;
import com.example.chandramohanr.followmate.app.models.events.request.JoinSessionRequest;
import com.example.chandramohanr.followmate.app.models.events.response.DropUserFromSessionResponse;
import com.example.chandramohanr.followmate.app.models.events.response.JoinRoomResponse;
import com.example.chandramohanr.followmate.app.models.events.response.NewUserJoinedEvent;
import com.example.chandramohanr.followmate.app.models.events.response.ReconnectToPreviousLostSession;
import com.example.chandramohanr.followmate.app.models.events.response.ReconnectedToSession;
import com.example.chandramohanr.followmate.app.models.events.response.SessionStartedEvent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.noveogroup.android.log.Log;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

@EActivity(R.layout.activity_main)
public class MainActivity extends BaseActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    @ViewById(R.id.session_info)
    TextView vSessionInfo;

    Activity activity;
    GoogleApiClient googleApiClient = null;
    private LocationRequest mLocationRequest;
    MapFragment mapFragment;
    GoogleMap map;
    Location lastKnownlocation;

    List<UserMarkerInfo> markers = new ArrayList<>();

    EventBus eventBus = EventBus.getDefault();

    private static int JOIN_ACTIVITY_REQUEST_CODE = 1;

    SocketController socketController = new SocketController(this);

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        eventBus.register(this);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        socketController.disconnect();
        eventBus.unregister(this);
    }

    @AfterViews
    void afterViewInjection() {
        activity = this;
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        googleApiClient = new GoogleApiClient.Builder(activity)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        googleApiClient.connect();
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(3 * 1000)
                .setFastestInterval(3 * 1000);

        if (isGPSEnabled(activity)) {
            Log.a("GPS is on");
        } else {
            turnGPSOn();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }
        Location location = locationManager.getLastKnownLocation(provider);

        if (location != null) {
            setLocation(location);
        }
        socketController.initSession();
    }

    public void setLocation(Location location) {
        String markerTitle = AppUtil.getLoggedInUserId();
        UserLocation userLocation;
        if(lastKnownlocation != null){
             userLocation = new UserLocation(location.getLatitude(), location.getLongitude(), lastKnownlocation.bearingTo(location));
        }else{
             userLocation = new UserLocation(location.getLatitude(), location.getLongitude(), 0);
        }
        lastKnownlocation = location;
        boolean anySessionActive = AppUtil.isAnySessionActive();
        if (anySessionActive) {
            shareMyLocation(userLocation);
        }
        setMarker(markerTitle, userLocation, true);
    }

    private void shareMyLocation(UserLocation userLocation) {
        ShareLocationInfo shareLocationInfo = new ShareLocationInfo(AppUtil.getLoggedInUserId(), AppUtil.getSessionId(),userLocation);
        String json = new Gson().toJson(shareLocationInfo);
        try {
            socketController.shareLocation(new JSONObject(json));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setMarker(String userId, UserLocation userLocation, boolean isOwner) {
        Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(userLocation.lat, userLocation.lng)));
        marker.setTitle(isOwner ? "You" : userId);
        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_navigation_black_24dp));
        marker.setFlat(true);
        marker.setRotation(userLocation.bearingTo);
        for (int i = 0; i < markers.size(); i++) {
            UserMarkerInfo userMarkerInfo = markers.get(i);
            if (userMarkerInfo.userId.equalsIgnoreCase(userId)) {
                userMarkerInfo.marker.remove();
                markers.remove(i);
                break;
            }
        }
        markers.add(new UserMarkerInfo(userId, marker, isOwner));
        updateMapZoom();
    }

    public void updateMapZoom() {
        CameraUpdate cu = null;
        if (markers.size() == 1) {
            cu = CameraUpdateFactory.newLatLngZoom(markers.get(0).marker.getPosition(), 15f);
            map.animateCamera(cu);
        } else {

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (UserMarkerInfo userMarkerInfo : markers) {
                builder.include(userMarkerInfo.marker.getPosition());
            }
            LatLngBounds bounds = builder.build();
            cu = CameraUpdateFactory.newLatLngBounds(bounds, 30);
            map.moveCamera(cu);
        }
    }

    public static boolean isGPSEnabled(Context mContext) {
        LocationManager locationManager = (LocationManager)
                mContext.getSystemService(LOCATION_SERVICE);
        boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return providerEnabled;
    }


    private void turnGPSOn() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        //**************************
        builder.setAlwaysShow(true); //this is the key ingredient
        //**************************

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        Log.a("GPS: RESOLUTION_REQUIRED");
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    activity, 1000);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }


    @Override
    public void onConnected(Bundle bundle) {
        mapFragment.getMapAsync(this);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.a("Google Api connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.a("Google Api connection failed");
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            setLocation(location);
        }
    }

    @Click(R.id.start_session)
    public void startNewSession() {
        resetPreviousSession();
        String loggedInUserId = AppUtil.getLoggedInUserId();
        StartSessionRequest startSessionRequest = new StartSessionRequest(loggedInUserId, getLastKnownUserLocation());

        startSessionRequest.userId = loggedInUserId;
        if (startSessionRequest.userId != null) {
            String json = new Gson().toJson(startSessionRequest);
            try {
                socketController.connect(new JSONObject(json));
            } catch (JSONException e) {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No user name attached", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetPreviousSession() {
        if(map!=null){
            map.clear();
        }
        markers = new ArrayList<>();
        SharedPreferenceHelper.deleteSharedPreference(SharedPreferenceHelper.KEY_ACTIVE_SESSION_ID);
    }

    @Click(R.id.join_session)
    public void joinSession(){
        Intent intent = new Intent(this,AuthenticateJoinActivity_.class);
        startActivityForResult(intent, JOIN_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == JOIN_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String sessionId = data.getStringExtra(AppConstants.SESSION_ID);
                requestToJoinSession(sessionId, false);
            }
        }
    }

    public UserLocation getLastKnownUserLocation(){
        Double latitude = null;
        Double longitude = null;
        if (lastKnownlocation != null) {
            latitude = lastKnownlocation.getLatitude();
            longitude = lastKnownlocation.getLongitude();
        }
        return new UserLocation(latitude, longitude, 0);
    }

    private void requestToJoinSession(String sessionId, boolean isRejoin) {
        String loggedInUserId = AppUtil.getLoggedInUserId();
        JoinSessionRequest joinSessionRequest = new JoinSessionRequest(sessionId, loggedInUserId, getLastKnownUserLocation());
        String json = new Gson().toJson(joinSessionRequest);
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (isRejoin) {
                socketController.rejoinSession(jsonObject);
            } else {
                socketController.joinSession(jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onEventMainThread(SessionStartedEvent sessionStartedEvent) {
        Log.a("on SessionStartedEvent " + sessionStartedEvent.is_session_created);
        if (sessionStartedEvent.is_session_created) {
            SharedPreferenceHelper.set(SharedPreferenceHelper.KEY_ACTIVE_SESSION_ID, sessionStartedEvent.session_id);
            vSessionInfo.setText("code = " + sessionStartedEvent.session_id);
            vSessionInfo.setVisibility(View.VISIBLE);
        } else {
            vSessionInfo.setVisibility(View.GONE);
            Toast.makeText(this, sessionStartedEvent.msg, Toast.LENGTH_SHORT).show();
        }
    }

    public void onEventMainThread(JoinRoomResponse joinRoomResponse) {
        resetPreviousSession();
        boolean joined = joinRoomResponse.joined;
        Toast.makeText(this, "Joined new session " + joined, Toast.LENGTH_SHORT).show();
        if(joined){
            SharedPreferenceHelper.set(SharedPreferenceHelper.KEY_ACTIVE_SESSION_ID, joinRoomResponse.session_id);
            for (ParticipantInfo participantInfo : joinRoomResponse.participants) {
                setMarker(participantInfo.user_id, participantInfo.latest_location, false);
                Log.a("user info " + participantInfo.user_id + " location " + participantInfo.latest_location.lat + " " + participantInfo.latest_location.lng);
            }
        }
    }

    public void onEventMainThread(NewUserJoinedEvent newUserJoinedEvent) {
        String user_id = newUserJoinedEvent.user_id;
        setMarker(user_id, newUserJoinedEvent.userLocation, false);
        Toast.makeText(this, "New user joined " + user_id, Toast.LENGTH_SHORT).show();
    }

    public void onEventMainThread(ReconnectToPreviousLostSession reconnectToPreviousLostSession) {
        requestToJoinSession(reconnectToPreviousLostSession.sessionId, true);
    }

    public void onEventMainThread(ReconnectedToSession reconnectedToSession) {
        Log.a("Reconnected to previous session " + reconnectedToSession.joined);
    }

    public void onEventMainThread(DropUserFromSessionResponse dropUserFromSessionResponse) {
        Log.a("Drop notified to server" + dropUserFromSessionResponse.updated);
    }

    public void onEventMainThread(ShareLocationInfo shareLocationInfo){
        Log.a("User new location "+new Gson().toJson(shareLocationInfo));
        setMarker(shareLocationInfo.user_id, shareLocationInfo.userLocation, false);
    }


    class UserMarkerInfo {
        public String userId;
        public Marker marker;
        public boolean isOwner;

        public UserMarkerInfo(String userId, Marker marker, boolean isOwner) {
            this.userId = userId;
            this.marker = marker;
            this.isOwner = isOwner;
        }
    }
}
