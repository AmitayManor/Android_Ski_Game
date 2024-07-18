//package com.example.skigame.Utils;
//
//import android.Manifest;
//import android.content.Context;
//import android.content.pm.PackageManager;
//import android.location.Location;
//import android.location.LocationManager;
//import android.os.Looper;
//import android.util.Log;
//
//import androidx.core.content.ContextCompat;
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationListener;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationResult;
//import com.google.android.gms.location.LocationServices;
//
//import org.jetbrains.annotations.NotNull;
//
//public class LocationHandler {
//
//    private static final String TAG = "LocationHandler";
//    private final Context context;
//    private FusedLocationProviderClient fusedLocationClient;
//    private LocationCallback locationCallback;
//    private LocationRequest locationRequest;
//    private double lastKnownLatitude;
//    private double lastKnownLongitude;
//    private LocationUpdateListener listener;
//
//
//    public LocationHandler(Context context) {
//        this.context = context;
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
//    }
//
//    public interface LocationCallback {
//        void onLocationResult(double latitude, double longitude);
//
//        void onLocationResult(LocationResult locationResult);
//    }
//
//    public interface LocationUpdateListener {
//        void onLocationUpdated(double latitude, double longitude);
//        void onLocationPermissionRequired();
//    }
//
//    public void requestSingleUpdate(LocationCallback callback) {
//        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            if (listener != null) {
//                listener.onLocationPermissionRequired();
//            }
//            return;
//        }
//
//        LocationRequest locationRequest = LocationRequest.create();
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        locationRequest.setNumUpdates(1);
//
//        fusedLocationClient.requestLocationUpdates(locationRequest, new com.google.android.gms.location.LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                if (locationResult == null) {
//                    return;
//                }
//                Location location = locationResult.getLastLocation();
//                if (location != null) {
//                    callback.onLocationResult(location.getLatitude(), location.getLongitude());
//                }
//            }
//        }, Looper.getMainLooper());
//    }
//
//    public void setLocationUpdateListener(LocationUpdateListener listener) {
//        this.listener = listener;
//    }
//
//    public void startLocationUpdates() {
//        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            if (listener != null) {
//                listener.onLocationPermissionRequired();
//            }
//            return;
//        }
//        createLocationRequest();
//        createLocationCallback();
//        fusedLocationClient.requestLocationUpdates(locationRequest, (LocationListener) locationCallback, Looper.getMainLooper());
//    }
//
//    public void stopLocationUpdates() {
//        if (locationCallback != null) {
//            fusedLocationClient.removeLocationUpdates((LocationListener) locationCallback);
//        }
//    }
//
//    private void createLocationCallback() {
//        locationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(@NotNull LocationResult locationResult) {
//                if (locationResult == null) {
//                    return;
//                }
//                for (Location location : locationResult.getLocations()) {
//                    if (location != null) {
//                        double latitude = location.getLatitude();
//                        double longitude = location.getLongitude();
//
//                        lastKnownLatitude = latitude;
//                        lastKnownLongitude = longitude;
//
//                        if (listener != null) {
//                            listener.onLocationUpdated(latitude, longitude);
//                        }
//
//                        Log.d(TAG, "Location updated: " + latitude + ", " + longitude);
//                    }
//                }
//            }
//        };
//    }
//
//
//    private void createLocationRequest() {
//        locationRequest = LocationRequest.create();
//        locationRequest.setInterval(10000);
//        locationRequest.setFastestInterval(5000);
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//    }
//
//
//    public boolean areLocationServicesEnabled(Context context) {
//        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//        boolean gpsEnabled = false;
//        boolean networkEnabled = false;
//
//        try {
//            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        } catch(Exception ex) {}
//
//        try {
//            networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//        } catch(Exception ex) {}
//
//        return gpsEnabled || networkEnabled;
//    }
//
//    public double[] getLastKnownLocation() {
//        return new double[]{lastKnownLatitude, lastKnownLongitude};
//    }
//
//}

package com.example.skigame.Utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class LocationHandler {

    private static final String TAG = "LocationHandler";
    private final Context context;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private double lastKnownLatitude;
    private double lastKnownLongitude;
    private LocationUpdateListener listener;

    public LocationHandler(Context context) {
        this.context = context;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public interface SingleUpdateCallback {
        void onLocationResult(double latitude, double longitude);
    }

    public interface LocationUpdateListener {
        void onLocationUpdated(double latitude, double longitude);
        void onLocationPermissionRequired();
    }

    public void requestSingleUpdate(SingleUpdateCallback callback) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (listener != null) {
                listener.onLocationPermissionRequired();
            }
            return;
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setNumUpdates(1);

        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    callback.onLocationResult(location.getLatitude(), location.getLongitude());
                }
                fusedLocationClient.removeLocationUpdates(this);
            }
        }, Looper.getMainLooper());
    }

    public void setLocationUpdateListener(LocationUpdateListener listener) {
        this.listener = listener;
    }

    public void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (listener != null) {
                listener.onLocationPermissionRequired();
            }
            return;
        }
        createLocationRequest();
        createLocationCallback();
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    public void stopLocationUpdates() {
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        lastKnownLatitude = latitude;
                        lastKnownLongitude = longitude;

                        if (listener != null) {
                            listener.onLocationUpdated(latitude, longitude);
                        }

                        Log.d(TAG, "Location updated: " + latitude + ", " + longitude);
                    }
                }
            }
        };
    }

    private void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public boolean areLocationServicesEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = false;
        boolean networkEnabled = false;

        try {
            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        return gpsEnabled || networkEnabled;
    }

    public double[] getLastKnownLocation() {
        return new double[]{lastKnownLatitude, lastKnownLongitude};
    }
}
