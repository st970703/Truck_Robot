package com.example.truckrobot;

import com.example.truckrobot.model.Direction;
import com.example.truckrobot.model.Robot;
import com.example.truckrobot.model.TableTop;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class RobotController {
    private final Robot robot = new Robot(new TableTop(5, 5));

    @PostMapping("/place")
    public ResponseEntity<Void> place(@RequestBody PlaceRequest request) {
        if (request == null || request.f() == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Direction direction = Direction.valueOf(request.f().trim().toUpperCase());
            robot.place(request.x(), request.y(), direction);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/move")
    public ResponseEntity<Void> move() {
        robot.move();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/left")
    public ResponseEntity<Void> left() {
        robot.left();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/right")
    public ResponseEntity<Void> right() {
        robot.right();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset")
    public ResponseEntity<Void> reset() {
        robot.reset();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/report")
    public ResponseEntity<String> report() {
        return ResponseEntity.ok(robot.report());
    }

    public record PlaceRequest(int x, int y, String f) {
    }
}
