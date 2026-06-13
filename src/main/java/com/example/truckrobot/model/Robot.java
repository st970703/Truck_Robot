package com.example.truckrobot.model;

public class Robot {
    private Position position;
    private Direction facing;
    private boolean placed = false;
    private final TableTop tableTop;

    public Robot(TableTop tableTop) {
        this.tableTop = tableTop;
    }

    public boolean place(int x, int y, Direction dir) {
        Position p = new Position(x, y);
        if (!tableTop.isValid(p)) return false;
        this.position = p;
        this.facing = dir;
        this.placed = true;
        return true;
    }

    private Position nextPosition(int x, int y, Direction dir) {
        if (dir == Direction.NORTH) {
            return new Position(x, y + 1);
        }
        if (dir == Direction.SOUTH) {
            return new Position(x, y - 1);
        }
        if (dir == Direction.WEST) {
            return new Position(x - 1, y);
        }
        if (dir == Direction.EAST) {
            return new Position(x + 1, y);
        }
        throw new IllegalArgumentException("Unexpected direction: " + dir);
    }

    public boolean move() {
        if (!placed) return false;
        Position next = this.nextPosition(this.position.x(), this.position.y(), this.facing);
        if (!tableTop.isValid(next)) return false; // prevent falling
        this.position = next;
        return true;
    }

    public boolean left() {
        if (!placed) return false;
        this.facing = this.facing.left();
        return true;
    }

    public boolean right() {
        if (!placed) return false;
        this.facing = this.facing.right();
        return true;
    }

    public String report() {
        if (!placed) return "ROBOT MISSING";
        return position.x() + "," + position.y() + "," + facing.name();
    }
}
