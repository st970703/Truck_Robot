# Toy Robot Simulator — Java + Spring Boot

## Overview
Implement a REST‑based **Toy Robot Simulator** using **Java** and **Spring Boot**.  
The robot operates on a **5×5 tabletop** and must respond to a sequence of commands while preventing it from falling from the desktop.

---

## Requirements

### Core Commands
- **PLACE X,Y,F** — positions the robot on the table
- **MOVE** — moves the robot one unit forward
- **LEFT** / **RIGHT** — rotates the robot 90 degrees
- **REPORT** — returns the robot’s position and facing direction
- **RESET** - resets the robot to the unplaced state.

## Running the Application
Use Gradle:
```
./gradlew bootRun
```

## Testing
- Manual testing:\
Run the Spring server and use the Postman application to send HTTP requests.

- Fixture-base testing:\
  `src/test/java/com/example/truckrobot/RobotIntegrationTest.java`
