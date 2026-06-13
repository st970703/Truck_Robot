package com.example.truckrobot.commands;
import com.example.truckrobot.model.Direction;

public class Command {
    public enum Type { PLACE, MOVE, LEFT, RIGHT, REPORT }

    private final Type type;
    private final Integer x;
    private final Integer y;
    private final Direction dir;

    private Command(Type type, Integer x, Integer y, Direction dir) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.dir = dir;
    }

    public static Command place(int x, int y, Direction dir) { return new Command(Type.PLACE, x, y, dir); }

    public Type getType() { return type; }
    public Integer getX() { return x; }
    public Integer getY() { return y; }
    public Direction getDir() { return dir; }
}
