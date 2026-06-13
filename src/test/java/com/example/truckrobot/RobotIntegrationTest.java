package com.example.truckrobot;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import static org.junit.jupiter.api.Assertions.*;



@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class RobotIntegrationTest {
    @Autowired
    private TestRestTemplate rest;

    @Test
    void testMovingOnce() {
        rest.postForEntity("/robot/command", "PLACE 0,0,NORTH", Void.class);
        rest.postForEntity("/robot/command", "MOVE", Void.class);

        ResponseEntity<String> response =
                rest.postForEntity("/robot/command", "REPORT", String.class);

        assertEquals("0,1,NORTH", response.getBody());
    }

    @Test
    void testCanNotLeaveTabletop() {
        rest.postForEntity("/robot/command", "PLACE 0,0,NORTH", Void.class);
        rest.postForEntity("/robot/command", "LEFT", Void.class);

        ResponseEntity<String> response =
                rest.postForEntity("/robot/command", "REPORT", String.class);

        assertEquals("0,0,WEST", response.getBody());
    }

    @Test
    void testMultipleMoves() {
        rest.postForEntity("/robot/command", "PLACE 1,2,EAST", Void.class);
        rest.postForEntity("/robot/command", "MOVE", Void.class);
        rest.postForEntity("/robot/command", "MOVE", Void.class);
        rest.postForEntity("/robot/command", "LEFT", Void.class);
        rest.postForEntity("/robot/command", "MOVE", Void.class);

        ResponseEntity<String> response =
                rest.postForEntity("/robot/command", "REPORT", String.class);

        assertEquals("3,3,NORTH", response.getBody());
    }

    @Test
    void testRobotIsNeverPlaced() {
        ResponseEntity<String> response =
                rest.postForEntity("/robot/command", "REPORT", String.class);

        assertEquals("ROBOT MISSING", response.getBody());
    }

    @Test
    void testCanNotGoBelowZeroY() {
        // Place robot at the southern edge facing SOUTH
        rest.postForEntity("/robot/command", "PLACE 0,0,SOUTH", Void.class);

        // Attempt to move off the table (should be ignored)
        rest.postForEntity("/robot/command", "MOVE", Void.class);

        // Report position
        ResponseEntity<String> response =
                rest.postForEntity("/robot/command", "REPORT", String.class);

        // Robot must stay at y = 0
        assertEquals("0,0,SOUTH", response.getBody());
    }

    /**
     * The robot cannot go outside the tabletop on the x-axis.
     */
    @Test
    void testCanNotGoBeyondMaxX() {
        rest.postForEntity("/robot/command", "PLACE 4,0,EAST", Void.class);

        rest.postForEntity("/robot/command", "MOVE", Void.class);

        ResponseEntity<String> response =
                rest.postForEntity("/robot/command", "REPORT", String.class);

        // Robot must stay within the table top
        assertEquals("4,0,EAST", response.getBody());
    }

    /**
     * The robot cannot go outside the tabletop on the y-axis.
     */
    @Test
    void testCanNotGoBeyondMaxY() {
        rest.postForEntity("/robot/command", "PLACE 0,4,NORTH", Void.class);

        rest.postForEntity("/robot/command", "MOVE", Void.class);

        ResponseEntity<String> response =
                rest.postForEntity("/robot/command", "REPORT", String.class);

        // Robot must stay within the table top
        assertEquals("0,4,NORTH", response.getBody());
    }

    /**
     * The robot cannot be placed beyond the table top.
     */
    @ParameterizedTest
    @CsvSource(
        {
            "PLACE 0,5,NORTH",
            "PLACE 5,0,NORTH",
        }
    )
    void testCanNotBePlacedBeyondTableTop(String command) {
        rest.postForEntity("/robot/command", command, Void.class);

        // Report position
        ResponseEntity<String> response =
                rest.postForEntity("/robot/command", "REPORT", String.class);

        assertEquals("ROBOT MISSING", response.getBody());
    }
}
