# Terrain Booking System

A JavaFX application for managing terrain and seat reservations.

## Features

- Interactive seat selection interface
- Real-time seat availability tracking
- Visual seat status indicators
- Price calculation based on seat selection
- Transaction management for reservations
- Automatic terrain status updates

## Prerequisites

- Java 17 or later
- Maven 3.6 or later
- MySQL 8.0 or later

## Database Setup

1. Create a MySQL database named `jawher_db`
2. Execute the SQL script located at `src/main/resources/database/init.sql`

## Configuration

Update the database connection settings in `src/main/java/com/jawher/util/DatabaseConnection.java` if needed:

```java
private static final String URL = "jdbc:mysql://localhost:3306/jawher_db";
private static final String USER = "root";
private static final String PASSWORD = "";
```

## Building the Application

```bash
mvn clean package
```

## Running the Application

```bash
mvn javafx:run
```

Or run the generated JAR file:

```bash
java -jar target/terrain-booking-1.0-SNAPSHOT.jar
```

## Usage

1. Select a terrain from the available options
2. Choose a date and time for the reservation
3. Click on seats to select/deselect them
4. Review the total price
5. Click "Confirm Reservation" to complete the booking

## Seat Status Colors

- Green: Available
- Blue: Selected
- Red: Reserved

## Development

### Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── jawher/
│   │           ├── controller/
│   │           ├── model/
│   │           ├── service/
│   │           └── util/
│   └── resources/
│       ├── fxml/
│       ├── database/
│       └── styles.css
```

### Key Components

- `TerrainSiege`: Model class for seat data
- `TerrainSiegeService`: Service class for database operations
- `TerrainSeatingController`: Controller for the seating interface
- `DatabaseConnection`: Utility class for database connectivity

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details. 