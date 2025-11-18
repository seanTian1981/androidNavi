package com.soundcampus.utils;

import android.location.Location;

public class LocationHelper {
    
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS = 6371000;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    public static double calculateBearing(double lat1, double lon1, double lat2, double lon2) {
        double dLon = Math.toRadians(lon2 - lon1);
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);

        double y = Math.sin(dLon) * Math.cos(lat2Rad);
        double x = Math.cos(lat1Rad) * Math.sin(lat2Rad) -
                Math.sin(lat1Rad) * Math.cos(lat2Rad) * Math.cos(dLon);

        double bearing = Math.toDegrees(Math.atan2(y, x));
        return (bearing + 360) % 360;
    }

    public static String getDirectionFromBearing(double bearing) {
        if (bearing >= 315 || bearing < 45) {
            return "北";
        } else if (bearing >= 45 && bearing < 135) {
            return "东";
        } else if (bearing >= 135 && bearing < 225) {
            return "南";
        } else {
            return "西";
        }
    }

    public static Location createLocation(double latitude, double longitude) {
        Location location = new Location("manual");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return location;
    }
}
