package com.soundcampus.data;

import java.util.List;

public class Route {
    private CampusLocation start;
    private CampusLocation destination;
    private List<NavigationInstruction> instructions;
    private double totalDistance;

    public Route(CampusLocation start, CampusLocation destination, List<NavigationInstruction> instructions, double totalDistance) {
        this.start = start;
        this.destination = destination;
        this.instructions = instructions;
        this.totalDistance = totalDistance;
    }

    public CampusLocation getStart() {
        return start;
    }

    public CampusLocation getDestination() {
        return destination;
    }

    public List<NavigationInstruction> getInstructions() {
        return instructions;
    }

    public double getTotalDistance() {
        return totalDistance;
    }
}
