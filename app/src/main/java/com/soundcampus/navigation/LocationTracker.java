package com.soundcampus.navigation;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import androidx.core.app.ActivityCompat;

public class LocationTracker {
    private static final String TAG = "LocationTracker";
    private Context context;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location currentLocation;
    private LocationUpdateCallback callback;

    public interface LocationUpdateCallback {
        void onLocationUpdate(Location location);
        void onLocationError(String error);
    }

    public LocationTracker(Context context) {
        this.context = context;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public void startTracking(LocationUpdateCallback callback) {
        this.callback = callback;

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (callback != null) {
                callback.onLocationError("位置权限未授予");
            }
            return;
        }

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                currentLocation = location;
                if (callback != null) {
                    callback.onLocationUpdate(location);
                }
                Log.d(TAG, "Location updated: " + location.getLatitude() + ", " + location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d(TAG, "Provider enabled: " + provider);
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d(TAG, "Provider disabled: " + provider);
                if (callback != null) {
                    callback.onLocationError("GPS已关闭");
                }
            }
        };

        try {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    2000,
                    5,
                    locationListener
            );

            Location lastKnown = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnown != null) {
                currentLocation = lastKnown;
                if (callback != null) {
                    callback.onLocationUpdate(lastKnown);
                }
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Security exception: " + e.getMessage());
            if (callback != null) {
                callback.onLocationError("无法获取位置信息");
            }
        }
    }

    public void stopTracking() {
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public boolean hasLocation() {
        return currentLocation != null;
    }
}
