package com.soundcampus.data;

public class CampusLocation {
    private String id;
    private String name;
    private double latitude;
    private double longitude;
    private String description;
    private String category;

    public CampusLocation(String id, String name, double latitude, double longitude, String description, String category) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return name;
    }
}
