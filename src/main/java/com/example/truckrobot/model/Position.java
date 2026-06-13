package com.example.truckrobot.model;

/**
 * Immutable coordinate pair. Using a record keeps equals/hashCode/toString correct and concise.
 */
public record Position(int x, int y) {
    public Position {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("Coordinates must be non-negative");
        }
    }
}
