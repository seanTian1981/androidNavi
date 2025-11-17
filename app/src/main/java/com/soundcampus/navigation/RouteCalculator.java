package com.soundcampus.navigation;

import com.soundcampus.data.CampusLocation;
import com.soundcampus.data.NavigationInstruction;
import com.soundcampus.data.Route;
import com.soundcampus.utils.LocationHelper;
import java.util.ArrayList;
import java.util.List;

public class RouteCalculator {

    public Route calculateRoute(CampusLocation start, CampusLocation destination) {
        List<NavigationInstruction> instructions = new ArrayList<>();
        
        double distance = LocationHelper.calculateDistance(
                start.getLatitude(), start.getLongitude(),
                destination.getLatitude(), destination.getLongitude()
        );

        double bearing = LocationHelper.calculateBearing(
                start.getLatitude(), start.getLongitude(),
                destination.getLatitude(), destination.getLongitude()
        );

        NavigationInstruction.Direction direction = getDirectionFromBearing(bearing);
        
        int distanceMeters = (int) distance;
        
        if (distanceMeters > 100) {
            int firstLegDistance = distanceMeters / 2;
            instructions.add(new NavigationInstruction(
                    NavigationInstruction.Direction.STRAIGHT,
                    firstLegDistance,
                    "直行 " + firstLegDistance + " 米"
            ));
            
            instructions.add(new NavigationInstruction(
                    direction,
                    distanceMeters - firstLegDistance,
                    direction.toString() + " " + (distanceMeters - firstLegDistance) + " 米"
            ));
        } else {
            instructions.add(new NavigationInstruction(
                    NavigationInstruction.Direction.STRAIGHT,
                    distanceMeters,
                    "直行 " + distanceMeters + " 米"
            ));
        }

        instructions.add(new NavigationInstruction(
                NavigationInstruction.Direction.ARRIVED,
                0,
                "已到达 " + destination.getName()
        ));

        return new Route(start, destination, instructions, distance);
    }

    private NavigationInstruction.Direction getDirectionFromBearing(double bearing) {
        if (bearing >= 315 || bearing < 45) {
            return NavigationInstruction.Direction.STRAIGHT;
        } else if (bearing >= 45 && bearing < 135) {
            return NavigationInstruction.Direction.RIGHT;
        } else if (bearing >= 135 && bearing < 225) {
            return NavigationInstruction.Direction.STRAIGHT;
        } else {
            return NavigationInstruction.Direction.LEFT;
        }
    }

    public String formatDistance(double meters) {
        if (meters < 1000) {
            return String.format("%.0f米", meters);
        } else {
            return String.format("%.1f公里", meters / 1000);
        }
    }
}
