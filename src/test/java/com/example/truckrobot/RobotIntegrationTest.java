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
    private static final String PLACE_ENDPOINT = "/api/v1/place";
    private static final String MOVE_ENDPOINT = "/api/v1/move";
    private static final String LEFT_ENDPOINT = "/api/v1/left";
    private static final String RIGHT_ENDPOINT = "/api/v1/right";
    private static final String RESET_ENDPOINT = "/api/v1/reset";
    private static final String REPORT_ENDPOINT = "/api/v1/report";

    @Autowired
    private TestRestTemplate rest;


    /**
     * Test a robot can be placed on the table top.
     */
    @Test
    void testPlace() {
        rest.postForEntity(PLACE_ENDPOINT, new RobotController.PlaceRequest(0, 0, "NORTH"), Void.class);

        ResponseEntity<String> response =
                rest.getForEntity(REPORT_ENDPOINT, String.class);

        assertEquals("0,0,NORTH", response.getBody());
    }

    /**
     * Test moving once.
     */
    @Test
    void testMovingOnce() {
        rest.postForEntity(PLACE_ENDPOINT, new RobotController.PlaceRequest(0, 0, "NORTH"), Void.class);
        rest.postForEntity(MOVE_ENDPOINT, null, Void.class);

        ResponseEntity<String> response =
                rest.getForEntity(REPORT_ENDPOINT, String.class);

        assertEquals("0,1,NORTH", response.getBody());
    }


    /**
     * Test a left turn.
     */
    @Test
    void testLeftTurn() {
        rest.postForEntity(PLACE_ENDPOINT, new RobotController.PlaceRequest(0, 0, "NORTH"), Void.class);
        rest.postForEntity(LEFT_ENDPOINT, null, Void.class);

        ResponseEntity<String> response =
                rest.getForEntity(REPORT_ENDPOINT, String.class);

        assertEquals("0,0,WEST", response.getBody());
    }

    /**
     * Test a right turn.
     */
    @Test
    void testRightTurn() {
        rest.postForEntity(PLACE_ENDPOINT, new RobotController.PlaceRequest(0, 0, "NORTH"), Void.class);
        rest.postForEntity(RIGHT_ENDPOINT, null, Void.class);

        ResponseEntity<String> response =
                rest.getForEntity(REPORT_ENDPOINT, String.class);

        assertEquals("0,0,EAST", response.getBody());
    }

    /**
     * Test two moves, left turn, and a move.
     */
    @Test
    void testMultipleMoves() {
        rest.postForEntity(PLACE_ENDPOINT, new RobotController.PlaceRequest(1, 2, "EAST"), Void.class);
        rest.postForEntity(MOVE_ENDPOINT, null, Void.class);
        rest.postForEntity(MOVE_ENDPOINT, null, Void.class);
        rest.postForEntity(LEFT_ENDPOINT, null, Void.class);
        rest.postForEntity(MOVE_ENDPOINT, null, Void.class);

        ResponseEntity<String> response =
                rest.getForEntity(REPORT_ENDPOINT, String.class);

        assertEquals("3,3,NORTH", response.getBody());
    }

    /**
     * Test when the robot is never placed on the table top.
     */
    @Test
    void testRobotIsNeverPlaced() {
        ResponseEntity<String> response =
                rest.getForEntity(REPORT_ENDPOINT, String.class);

        assertEquals("ROBOT MISSING", response.getBody());
    }

    /**
     * Test when the robot state is reset.
     */
    @Test
    void testResetRemovesRobotPlacement() {
        rest.postForEntity(PLACE_ENDPOINT, new RobotController.PlaceRequest(0, 0, "NORTH"), Void.class);
        rest.postForEntity(RESET_ENDPOINT, null, Void.class);

        ResponseEntity<String> response =
                rest.getForEntity(REPORT_ENDPOINT, String.class);

        assertEquals("ROBOT MISSING", response.getBody());
    }

    /**
     * The robot cannot go outside the tabletop.
     */
    @ParameterizedTest
    @CsvSource(
        {
            "0, 4, NORTH",
            "0, 0, SOUTH",
            "4, 0, EAST",
            "0, 0, WEST",
        }
    )
    void testMustStayOnTheTableTop(int x, int y, String direction) {
        rest.postForEntity(PLACE_ENDPOINT, new RobotController.PlaceRequest(x, y, direction), Void.class);

        rest.postForEntity(MOVE_ENDPOINT, null, Void.class);

        ResponseEntity<String> response =
                rest.getForEntity(REPORT_ENDPOINT, String.class);
        // Note the invalid moves are ignored. Robot must stay within the table top
        String expected = String.format("%d,%d,%s", x, y, direction);
        assertEquals(expected, response.getBody());
    }

    /**
     * The robot cannot be placed beyond the table top.
     */
    @ParameterizedTest
    @CsvSource(
        {
            "0, 5, NORTH",
            "5, 0, NORTH",
            "5, 5, NORTH",
            "-1, -1, NORTH",
            "0, -1, NORTH",
            "-1, 0, NORTH",
        }
    )
    void testCanNotBePlacedBeyondTableTop(int x, int y, String direction) {
        rest.postForEntity(PLACE_ENDPOINT, new RobotController.PlaceRequest(x, y, direction), Void.class);

        // Report position
        ResponseEntity<String> response =
                rest.getForEntity(REPORT_ENDPOINT, String.class);

        assertEquals("ROBOT MISSING", response.getBody());
    }
}
