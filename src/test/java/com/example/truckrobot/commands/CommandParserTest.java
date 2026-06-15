package com.example.truckrobot.commands;

import com.example.truckrobot.model.Robot;
import com.example.truckrobot.model.TableTop;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CommandParserTest {
    private Robot robot;

    @BeforeEach
    void setUp() {
        this.robot = new Robot(new TableTop(5, 5));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void ignoresEmptyAndBlankCommands(String command) {
        Optional<String> result = CommandParser.execute(command, robot);

        assertTrue(result.isEmpty());
        assertReport("ROBOT MISSING");
    }

    @Test
    void ignoresNullCommand() {
        Optional<String> result = CommandParser.execute(null, robot);

        assertTrue(result.isEmpty());
        assertReport("ROBOT MISSING");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "PLACE 1,2",
            "PLACE a,b,NORTH",
            "PLACE 1,2,3,4"
    })
    void ignoresMalformedPlaceCommands(String command) {
        CommandParser.execute(command, robot);

        assertReport("ROBOT MISSING");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "PLACE 0,5,NORTH",
            "PLACE 5,0,NORTH",
            "PLACE -1,0,NORTH"
    })
    void ignoresPlaceCommandsOutsideTabletop(String command) {
        CommandParser.execute(command, robot);

        assertReport("ROBOT MISSING");
    }

    @Test
    void ignoresMovementCommandsBeforeRobotIsPlaced() {
        CommandParser.execute("MOVE", robot);
        assertReport("ROBOT MISSING");
    }

    @Test
    void executesPlaceMovementAndReportCommands() {
        CommandParser.execute("  place 1,2,east  ", robot);
        CommandParser.execute("MOVE", robot);
        CommandParser.execute("MOVE", robot);
        CommandParser.execute("LEFT", robot);
        CommandParser.execute("MOVE", robot);

        Optional<String> report = CommandParser.execute("report", robot);

        assertTrue(report.isPresent());
        assertEquals("3,3,NORTH", report.get());
    }

    @Test
    void ignoresUnknownCommands() {
        CommandParser.execute("PLACE 0,0,NORTH", robot);
        CommandParser.execute("JUMP", robot);
        CommandParser.execute("SPIN", robot);

        assertReport("0,0,NORTH");
    }

    private void assertReport(String expected) {
        Optional<String> report = CommandParser.execute("REPORT", robot);

        assertTrue(report.isPresent());
        assertEquals(expected, report.get());
    }
}
