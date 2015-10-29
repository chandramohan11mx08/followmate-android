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
import android.widget.TextView;
import android.widget.Toast;

import com.example.chandramohanr.followmate.R;
import com.example.chandramohanr.followmate.app.Constants.AppConstants;
import com.example.chandramohanr.followmate.app.SocketController;
import com.example.chandramohanr.followmate.app.helpers.AppUtil;
import com.example.chandramohanr.followmate.app.models.events.StartSessionRequest;
import com.example.chandramohanr.followmate.app.models.events.request.JoinSessionRequest;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
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
    Marker marker;

    EventBus eventBus = EventBus.getDefault();

    private static int JOIN_ACTIVITY_REQUEST_CODE = 1;

    SocketController socketController = new SocketController();

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
                .setInterval(1 * 1000)
                .setFastestInterval(1 * 1000);

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
            setLocation(location.getLatitude(), location.getLongitude());
        }
        socketController.initSession();
    }

    public void setLocation(double lat, double lng) {
        if (marker != null) {
            marker.remove();
        }
        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(lat, lng)).zoom(15).build();
        marker = map.addMarker(new MarkerOptions().position(new LatLng(lat, lng)));
        map.animateCamera(CameraUpdateFactory.newCameraPosition(position));
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
            setLocation(location.getLatitude(), location.getLongitude());
        }
    }

    @Click(R.id.start_session)
    public void startNewSession() {
        StartSessionRequest startSessionRequest = new StartSessionRequest();
        startSessionRequest.userId = AppUtil.getLoggedInUserId();
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
                requestToJoinSession(sessionId);
            }
        }
    }

    private void requestToJoinSession(String sessionId) {
        JoinSessionRequest joinSessionRequest = new JoinSessionRequest();
        joinSessionRequest.user_id = AppUtil.getLoggedInUserId();
        joinSessionRequest.session_id = sessionId;
        String json = new Gson().toJson(joinSessionRequest);
        try {
            socketController.joinSession(new JSONObject(json));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onEventMainThread(SessionStartedEvent sessionStartedEvent) {
        Log.a("on SessionStartedEvent " + sessionStartedEvent.is_session_created);
        if (sessionStartedEvent.is_session_created) {
            vSessionInfo.setText("code = " + sessionStartedEvent.session_id);
        } else {
            Toast.makeText(this, sessionStartedEvent.msg, Toast.LENGTH_SHORT).show();
        }
    }

    public void onEventMainThread(JoinRoomResponse joinRoomResponse) {
        boolean joined = joinRoomResponse.joined;
        Log.a("joined room" + joined);
        Toast.makeText(this, "Joined new session " + joined, Toast.LENGTH_SHORT).show();
    }

    public void onEventMainThread(NewUserJoinedEvent newUserJoinedEvent) {
        Toast.makeText(this, "New user joined " + newUserJoinedEvent.user_id, Toast.LENGTH_SHORT).show();
    }

    public void onEventMainThread(ReconnectToPreviousLostSession reconnectToPreviousLostSession) {
        Toast.makeText(this, "Trying to reconnect to session " + reconnectToPreviousLostSession.sessionId, Toast.LENGTH_SHORT).show();
        requestToJoinSession(reconnectToPreviousLostSession.sessionId);
    }

    public void onEventMainThread(ReconnectedToSession reconnectedToSession) {
        Toast.makeText(this, "Reconnected to previous session " + reconnectedToSession.joined, Toast.LENGTH_SHORT).show();
    }
}
