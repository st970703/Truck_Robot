package com.example.truckrobot.commands;

import com.example.truckrobot.model.Direction;
import com.example.truckrobot.model.Robot;

import java.util.Optional;

/**
 * Simple parser+executor for textual commands. It mutates the provided Robot instance.
 * Invalid commands are ignored. REPORT returns a non-empty Optional with the output.
 */
public final class CommandParser {
    public static Optional<String> execute(String line, Robot robot) {
        if (line == null) return Optional.empty();
        String trimmed = line.trim();
        if (trimmed.isEmpty()) return Optional.empty();

        String upper = trimmed.toUpperCase();
        if (upper.startsWith("PLACE")) {
            String arg = trimmed.substring(5).trim();
            if (arg.startsWith(" ")) arg = arg.trim();
            String[] parts = arg.split(",");
            if (parts.length != 3) return Optional.empty();
            try {
                int x = Integer.parseInt(parts[0].trim());
                int y = Integer.parseInt(parts[1].trim());
                Direction dir = Direction.valueOf(parts[2].trim().toUpperCase());
                robot.place(x, y, dir);
            } catch (Exception ignored) {
                // ignore malformed PLACE
            }
            return Optional.empty();
        }

        switch (upper) {
            case "MOVE": robot.move(); break;
            case "LEFT": robot.left(); break;
            case "RIGHT": robot.right(); break;
            case "REPORT": return Optional.of(robot.report());
            default: break; // ignore unknown
        }
        return Optional.empty();
    }
}
