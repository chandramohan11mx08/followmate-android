package com.example.chandramohanr.followmate.app.activities;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chandramohanr.followmate.R;
import com.example.chandramohanr.followmate.app.Constants.AppConstants;
import com.example.chandramohanr.followmate.app.SocketController;
import com.example.chandramohanr.followmate.app.helpers.AppUtil;
import com.example.chandramohanr.followmate.app.helpers.SharedPreferenceHelper;
import com.example.chandramohanr.followmate.app.interfaces.LatLngInterpolator;
import com.example.chandramohanr.followmate.app.models.ParticipantInfo;
import com.example.chandramohanr.followmate.app.models.UserLocation;
import com.example.chandramohanr.followmate.app.models.events.ChangeMarkerVisibility;
import com.example.chandramohanr.followmate.app.models.events.SessionConnectionSocketFailure;
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
import org.androidannotations.annotations.CheckedChange;
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

    @ViewById(R.id.share_my_location)
    SwitchCompat shareMyLocationSwitch;

    Activity activity;
    GoogleApiClient googleApiClient = null;
    private LocationRequest mLocationRequest;
    MapFragment mapFragment;
    GoogleMap map;
    Location lastKnownLocation;

    List<UserMarkerInfo> markers = new ArrayList<>();

    EventBus eventBus = EventBus.getDefault();

    private final static int JOIN_ACTIVITY_REQUEST_CODE = 1;

    SocketController socketController = new SocketController(this);
    boolean shareMyLocation = true;
    boolean isSessionOwner;
    Marker myMarker;
    String loggedInUserId = AppUtil.getLoggedInUserId();

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        eventBus.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        socketController.disconnect();
        eventBus.unregister(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!googleApiClient.isConnected()) {
            googleApiClient.connect();
        }
    }

    private void removeLocationUpdate() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    private void requestLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
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
                .setFastestInterval(5 * 1000);
        requestGPSAccess();
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

        Intent intent = getIntent();
        String action = intent.getAction();

        if (Intent.ACTION_VIEW.equals(action)) {
            final List<String> segments = intent.getData().getPathSegments();
            if (segments.size() > 1) {
                String sessionId = segments.get(1);
                requestToJoinSession(sessionId, false);
            }
        }
    }

    @CheckedChange(R.id.share_my_location)
    public void toggleShareMyLocationSwitch() {
        shareMyLocation = shareMyLocationSwitch.isChecked();
        ChangeMarkerVisibility changeMarkerVisibility = new ChangeMarkerVisibility(AppUtil.getSessionId(), loggedInUserId, shareMyLocation);
        String json = new Gson().toJson(changeMarkerVisibility);
        try {
            socketController.changeVisibility(new JSONObject(json));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        updateMapZoom();
    }

    public void setLocation(Location location) {

        float bearingTo = lastKnownLocation != null ? lastKnownLocation.bearingTo(location) : 0;
        UserLocation userLocation = new UserLocation(location.getLatitude(), location.getLongitude(), bearingTo);
        lastKnownLocation = location;

        boolean anySessionActive = AppUtil.isAnySessionActive();

        if ((anySessionActive && shareMyLocation)) {
            shareMyLocation(userLocation);
        }
        if (myMarker == null) {
            myMarker = getMarker(loggedInUserId, userLocation, true);
        } else {
            myMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
            myMarker.setRotation(bearingTo);
        }
        updateMapZoom();
    }

    private void shareMyLocation(UserLocation userLocation) {
        ShareLocationInfo shareLocationInfo = new ShareLocationInfo(loggedInUserId, AppUtil.getSessionId(), userLocation);
        String json = new Gson().toJson(shareLocationInfo);
        try {
            socketController.shareLocation(new JSONObject(json));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setMarkerVisibility(String userId, boolean markerVisible) {
        for (int i = 0; i < markers.size(); i++) {
            UserMarkerInfo userMarkerInfo = markers.get(i);
            if (userMarkerInfo.userId.equalsIgnoreCase(userId)) {
                userMarkerInfo.isMarkerVisible = markerVisible;
                userMarkerInfo.marker.setVisible(markerVisible);
                break;
            }
        }
        updateMapZoom();
    }

    public void setMarker(String userId, UserLocation userLocation) {
        boolean markerSet = false;
        for (int i = 0; i < markers.size(); i++) {
            final UserMarkerInfo userMarkerInfo = markers.get(i);
            if (userMarkerInfo.userId.equalsIgnoreCase(userId)) {
                animateMarker(userLocation, userMarkerInfo.marker);
                //                userMarkerInfo.marker.setPosition(latlng);
//                    userMarkerInfo.marker.setRotation(userLocation.bearingTo);
                markerSet = true;
                break;
            }
        }
        if (!markerSet) {
            Marker marker = getMarker(userId, userLocation, false);
            markers.add(new UserMarkerInfo(userId, marker));
        }
        updateMapZoom();
    }

    public static void animateMarker(final UserLocation userLocation, final Marker marker) {
        if (marker != null) {
            final LatLng startPosition = marker.getPosition();
            final LatLng endPosition = new LatLng(userLocation.lat, userLocation.lng);
            final float startRotation = marker.getRotation();
            final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.LinearFixed();
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(1000);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
                        marker.setPosition(newPosition);
                        marker.setRotation(computeRotation(v, startRotation, userLocation.bearingTo));
                    } catch (Exception ex) {

                    }
                }
            });
            valueAnimator.start();
        }
    }

    private static float computeRotation(float fraction, float start, float end) {
        float normalizeEnd = end - start;
        float normalizedEndAbs = (normalizeEnd + 360) % 360;
        float direction = (normalizedEndAbs > 180) ? -1 : 1;
        float rotation;
        if (direction > 0) {
            rotation = normalizedEndAbs;
        } else {
            rotation = normalizedEndAbs - 360;
        }
        float result = fraction * rotation + start;
        return (result + 360) % 360;
    }

    @NonNull
    private Marker getMarker(String userId, UserLocation userLocation, boolean isOwner) {
        Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(userLocation.lat, userLocation.lng)));
        marker.setTitle(isOwner ? "You" : userId);
        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_navigation_black_24dp));
        marker.setFlat(true);
        marker.setRotation(userLocation.bearingTo);
        return marker;
    }

    public void updateMapZoom() {
        CameraUpdate cu = null;
        if (markers.size() == 0) {
            myMarker.setVisible(true);
            cu = CameraUpdateFactory.newLatLngZoom(myMarker.getPosition(), 18f);
            map.animateCamera(cu);
        } else {

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (UserMarkerInfo userMarkerInfo : markers) {
                if (userMarkerInfo.isMarkerVisible) {
                    Marker marker = userMarkerInfo.marker;
                    builder.include(marker.getPosition());
                }
            }
            if (shareMyLocation) {
                builder.include(myMarker.getPosition());
            }
            myMarker.setVisible(shareMyLocation);
            LatLngBounds bounds = builder.build();
            cu = CameraUpdateFactory.newLatLngBounds(bounds, 300);
            map.moveCamera(cu);
        }
    }

    private void requestGPSAccess() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(
                                    activity, 1000);
                        } catch (IntentSender.SendIntentException e) {
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.a("Google Api connection");
        mapFragment.getMapAsync(this);
        requestLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.a("Google Api connection suspended");
        removeLocationUpdate();
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
        if (!AppUtil.isAnySessionActive()) {
            removeLocationUpdate();
        }
    }

    @Click(R.id.start_session)
    public void startNewSession() {
        socketController.initSession();
        resetPreviousSession();
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
        for (UserMarkerInfo userMarkerInfo : markers) {
            userMarkerInfo.marker.remove();
        }
        markers = new ArrayList<>();
        shareMyLocationSwitch.setVisibility(View.GONE);
        AppUtil.resetSessionInfo();
    }

    @Click(R.id.join_session)
    public void joinSession() {
        Intent intent = new Intent(this, AuthenticateJoinActivity_.class);
        startActivityForResult(intent, JOIN_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case JOIN_ACTIVITY_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    String sessionId = data.getStringExtra(AppConstants.SESSION_ID);
                    requestToJoinSession(sessionId, false);
                }
                break;
            case 1000:
                if (resultCode != Activity.RESULT_OK) {
                    finish();
                }
        }
    }

    public UserLocation getLastKnownUserLocation() {
        Double latitude = null;
        Double longitude = null;
        if (lastKnownLocation != null) {
            latitude = lastKnownLocation.getLatitude();
            longitude = lastKnownLocation.getLongitude();
        }
        return new UserLocation(latitude, longitude, 0);
    }

    private void requestToJoinSession(String sessionId, boolean isRejoin) {
        socketController.initSession();
        JoinSessionRequest joinSessionRequest = new JoinSessionRequest(sessionId, loggedInUserId, getLastKnownUserLocation());
        SharedPreferenceHelper.set(SharedPreferenceHelper.KEY_SESSION_TO_JOIN, sessionId);
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

    private void setParticipantInfo(List<ParticipantInfo> participantInfoList) {
        for (ParticipantInfo participantInfo : participantInfoList) {
            if (!participantInfo.user_id.equals(loggedInUserId)) {
                setMarker(participantInfo.user_id, participantInfo.latest_location);
                Log.a("user info " + participantInfo.user_id + " location " + participantInfo.latest_location.lat + " " + participantInfo.latest_location.lng);
            }
        }
    }

    public void onEventMainThread(SessionStartedEvent sessionStartedEvent) {
        Log.a("on SessionStartedEvent " + sessionStartedEvent.is_session_created);
        if (sessionStartedEvent.is_session_created) {
            AppUtil.setNewSessionInfo(sessionStartedEvent.session_id, true);
            vSessionInfo.setText("Started session " + sessionStartedEvent.session_id);
            vSessionInfo.setVisibility(View.VISIBLE);
            String loggedInUserMobileNumber = AppUtil.getLoggedInUserMobileNumber();
            String message = AppConstants.DOMAIN + (loggedInUserMobileNumber + "_" + sessionStartedEvent.session_id);
            AppUtil.openChooseDialogToSendTextMessage(this, message);
            isSessionOwner = true;
        } else {
            vSessionInfo.setVisibility(View.GONE);
            Toast.makeText(this, sessionStartedEvent.msg, Toast.LENGTH_SHORT).show();
        }
    }


    public void onEventMainThread(JoinRoomResponse joinRoomResponse) {
        resetPreviousSession();
        boolean joined = joinRoomResponse.joined;
        Toast.makeText(this, "Joined new session " + joined, Toast.LENGTH_SHORT).show();
        if (joined) {
            AppUtil.setNewSessionInfo(joinRoomResponse.session_id, false);
            vSessionInfo.setText("Joined session " + joinRoomResponse.session_id);
            vSessionInfo.setVisibility(View.VISIBLE);
            shareMyLocationSwitch.setVisibility(View.VISIBLE);
            myMarker.setVisible(false);
            shareMyLocation = false;
            setParticipantInfo(joinRoomResponse.participants);
        }
    }

    public void onEventMainThread(NewUserJoinedEvent newUserJoinedEvent) {
        String user_id = newUserJoinedEvent.user_id;
        if (newUserJoinedEvent.visibility) {
            setMarker(user_id, newUserJoinedEvent.userLocation);
        }
        Toast.makeText(this, "New user joined " + user_id, Toast.LENGTH_SHORT).show();
    }

    public void onEventMainThread(ReconnectToPreviousLostSession reconnectToPreviousLostSession) {
        requestToJoinSession(reconnectToPreviousLostSession.sessionId, true);
    }

    public void onEventMainThread(ReconnectedToSession reconnectedToSession) {
        Log.a("Reconnected to previous session " + reconnectedToSession.joined);
        if (reconnectedToSession.joined) {
            setParticipantInfo(reconnectedToSession.participantInfoList);
        }
    }

    public void onEventMainThread(DropUserFromSessionResponse dropUserFromSessionResponse) {
        Log.a("Drop notified to server" + dropUserFromSessionResponse.updated);
    }

    public void onEventMainThread(ShareLocationInfo shareLocationInfo) {
        setMarker(shareLocationInfo.user_id, shareLocationInfo.userLocation);
    }

    public void onEventMainThread(SessionConnectionSocketFailure sessionConnectionSocketFailure) {
        requestToJoinSession(sessionConnectionSocketFailure.sessionId, false);
    }

    public void onEventMainThread(ChangeMarkerVisibility changeMarkerVisibility) {
        Log.a("visibility changed by user " + changeMarkerVisibility.user_id + " visible " + changeMarkerVisibility.visibility);
        setMarkerVisibility(changeMarkerVisibility.user_id, changeMarkerVisibility.visibility);
    }

    class UserMarkerInfo {
        public String userId;
        public Marker marker;
        public boolean isMarkerVisible = true;

        public UserMarkerInfo(String userId, Marker marker) {
            this.userId = userId;
            this.marker = marker;
        }
    }
}
