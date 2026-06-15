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
    private static final String RESET_ENDPOINT = "/api/v1/reset";
    private static final String REPORT_ENDPOINT = "/api/v1/report";

    @Autowired
    private TestRestTemplate rest;

    @Test
    void testMovingOnce() {
        rest.postForEntity(PLACE_ENDPOINT, new RobotController.PlaceRequest(0, 0, "NORTH"), Void.class);
        rest.postForEntity(MOVE_ENDPOINT, null, Void.class);

        ResponseEntity<String> response =
                rest.getForEntity(REPORT_ENDPOINT, String.class);

        assertEquals("0,1,NORTH", response.getBody());
    }

    @Test
    void testCanNotLeaveTabletop() {
        rest.postForEntity(PLACE_ENDPOINT, new RobotController.PlaceRequest(0, 0, "NORTH"), Void.class);
        rest.postForEntity(LEFT_ENDPOINT, null, Void.class);

        ResponseEntity<String> response =
                rest.getForEntity(REPORT_ENDPOINT, String.class);

        assertEquals("0,0,WEST", response.getBody());
    }

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

    @Test
    void testRobotIsNeverPlaced() {
        ResponseEntity<String> response =
                rest.getForEntity(REPORT_ENDPOINT, String.class);

        assertEquals("ROBOT MISSING", response.getBody());
    }

    @Test
    void testResetRemovesRobotPlacement() {
        rest.postForEntity(PLACE_ENDPOINT, new RobotController.PlaceRequest(0, 0, "NORTH"), Void.class);
        rest.postForEntity(RESET_ENDPOINT, null, Void.class);

        ResponseEntity<String> response =
                rest.getForEntity(REPORT_ENDPOINT, String.class);

        assertEquals("ROBOT MISSING", response.getBody());
    }

    @Test
    void testCanNotGoBelowZeroY() {
        // Place robot at the southern edge facing SOUTH
        rest.postForEntity(PLACE_ENDPOINT, new RobotController.PlaceRequest(0, 0, "SOUTH"), Void.class);

        // Attempt to move off the table (should be ignored)
        rest.postForEntity(MOVE_ENDPOINT, null, Void.class);

        // Report position
        ResponseEntity<String> response =
                rest.getForEntity(REPORT_ENDPOINT, String.class);

        // Robot must stay at y = 0
        assertEquals("0,0,SOUTH", response.getBody());
    }

    /**
     * The robot cannot go outside the tabletop on the x-axis.
     */
    @Test
    void testCanNotGoBeyondMaxX() {
        rest.postForEntity(PLACE_ENDPOINT, new RobotController.PlaceRequest(4, 0, "EAST"), Void.class);

        rest.postForEntity(MOVE_ENDPOINT, null, Void.class);

        ResponseEntity<String> response =
                rest.getForEntity(REPORT_ENDPOINT, String.class);

        // Robot must stay within the table top
        assertEquals("4,0,EAST", response.getBody());
    }

    /**
     * The robot cannot go outside the tabletop on the y-axis.
     */
    @Test
    void testCanNotGoBeyondMaxY() {
        rest.postForEntity(PLACE_ENDPOINT, new RobotController.PlaceRequest(0, 4, "NORTH"), Void.class);

        rest.postForEntity(MOVE_ENDPOINT, null, Void.class);

        ResponseEntity<String> response =
                rest.getForEntity(REPORT_ENDPOINT, String.class);

        // Robot must stay within the table top
        assertEquals("0,4,NORTH", response.getBody());
    }

    /**
     * The robot cannot be placed beyond the tabletop.
     */
    @ParameterizedTest
    @CsvSource(
        {
            "0, 5, NORTH",
            "5, 0, NORTH",
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
