package com.soundcampus.navigation;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.soundcampus.data.CampusLocation;
import com.soundcampus.data.DatabaseHelper;
import java.util.ArrayList;
import java.util.List;

public class MapManager {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public MapManager(Context context) {
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getReadableDatabase();
    }

    public List<CampusLocation> getAllLocations() {
        List<CampusLocation> locations = new ArrayList<>();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_LOCATIONS,
                null,
                null,
                null,
                null,
                null,
                DatabaseHelper.COLUMN_NAME
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
                double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LATITUDE));
                double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LONGITUDE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY));

                locations.add(new CampusLocation(id, name, latitude, longitude, description, category));
            }
            cursor.close();
        }

        return locations;
    }

    public CampusLocation getLocationById(String id) {
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_LOCATIONS,
                null,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{id},
                null,
                null,
                null
        );

        CampusLocation location = null;
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
            double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LATITUDE));
            double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LONGITUDE));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION));
            String category = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY));

            location = new CampusLocation(id, name, latitude, longitude, description, category);
            cursor.close();
        }

        return location;
    }

    public CampusLocation findNearestLocation(double latitude, double longitude) {
        List<CampusLocation> allLocations = getAllLocations();
        CampusLocation nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (CampusLocation location : allLocations) {
            double distance = calculateDistance(latitude, longitude, location.getLatitude(), location.getLongitude());
            if (distance < minDistance) {
                minDistance = distance;
                nearest = location;
            }
        }

        return nearest;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS = 6371000;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    public void close() {
        if (db != null) {
            db.close();
        }
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
