package com.soundcampus.data;

public class NavigationInstruction {
    public enum Direction {
        STRAIGHT, LEFT, RIGHT, ARRIVED
    }

    private Direction direction;
    private int distanceMeters;
    private String description;

    public NavigationInstruction(Direction direction, int distanceMeters, String description) {
        this.direction = direction;
        this.distanceMeters = distanceMeters;
        this.description = description;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getDistanceMeters() {
        return distanceMeters;
    }

    public String getDescription() {
        return description;
    }

    public String getDirectionText() {
        switch (direction) {
            case LEFT:
                return "左转";
            case RIGHT:
                return "右转";
            case STRAIGHT:
                return "直行";
            case ARRIVED:
                return "到达";
            default:
                return "";
        }
    }
}
