package com.example.truckrobot.model;

public class TableTop {
    private final int width;
    private final int height;

    public TableTop(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Dimensions must be positive");
        }
        this.width = width;
        this.height = height;
    }

    public boolean isValid(Position p) {
        if (p == null) return false;
        boolean validX = p.x() >= 0 && p.x() < this.width;
        boolean validY = p.y() >= 0 && p.y() < this.height;

        return validX && validY;
    }
}
