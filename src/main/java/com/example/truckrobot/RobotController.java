package com.example.truckrobot;

import com.example.truckrobot.commands.CommandParser;
import com.example.truckrobot.model.Robot;
import com.example.truckrobot.model.TableTop;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/robot")
public class RobotController {
    private final Robot robot = new Robot(new TableTop(5, 5));

    @PostMapping("/command")
    public ResponseEntity<String> command(@RequestBody(required = false) String line) {
        Optional<String> output = CommandParser.execute(line, robot);
        return output.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.ok().build());
    }
}
