-- Create terrain_siege table
CREATE TABLE IF NOT EXISTS `terrain_siege` (
    `siegeID` int PRIMARY KEY AUTO_INCREMENT,
    `terrainID` int,
    `rangee` varchar(5),
    `numero` varchar(10),
    `statut` enum('Disponible','Réservé','Maintenance') DEFAULT 'Disponible',
    `prix` decimal(10,2),
    FOREIGN KEY (`terrainID`) REFERENCES `terrain`(`courtID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create reservation_siege table
CREATE TABLE IF NOT EXISTS `reservation_siege` (
    `reservationID` int,
    `siegeID` int,
    `prix_unitaire` decimal(10,2),
    PRIMARY KEY (`reservationID`, `siegeID`),
    FOREIGN KEY (`reservationID`) REFERENCES `reservationterrain`(`ID`),
    FOREIGN KEY (`siegeID`) REFERENCES `terrain_siege`(`siegeID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert sample data for terrain_siege
INSERT INTO `terrain_siege` (`terrainID`, `rangee`, `numero`, `prix`) VALUES
(1, '1', '1', 25.00),
(1, '1', '2', 25.00),
(1, '1', '3', 25.00),
(1, '1', '4', 25.00),
(1, '1', '5', 25.00),
(1, '2', '1', 30.00),
(1, '2', '2', 30.00),
(1, '2', '3', 30.00),
(1, '2', '4', 30.00),
(1, '2', '5', 30.00),
(1, '3', '1', 35.00),
(1, '3', '2', 35.00),
(1, '3', '3', 35.00),
(1, '3', '4', 35.00),
(1, '3', '5', 35.00);

-- Create trigger to update terrain status when all seats are reserved
DELIMITER //
CREATE TRIGGER update_terrain_status_after_seat_reservation
AFTER UPDATE ON terrain_siege
FOR EACH ROW
BEGIN
    DECLARE total_seats INT;
    DECLARE reserved_seats INT;
    
    -- Count total and reserved seats for the terrain
    SELECT COUNT(*), COUNT(CASE WHEN statut = 'Réservé' THEN 1 END)
    INTO total_seats, reserved_seats
    FROM terrain_siege
    WHERE terrainID = NEW.terrainID;
    
    -- If all seats are reserved, update terrain status
    IF total_seats = reserved_seats THEN
        UPDATE terrain
        SET statut = 'Occupé'
        WHERE courtID = NEW.terrainID;
    END IF;
END //
DELIMITER ;

-- Create trigger to update terrain status when seats become available
DELIMITER //
CREATE TRIGGER update_terrain_status_after_seat_available
AFTER UPDATE ON terrain_siege
FOR EACH ROW
BEGIN
    DECLARE reserved_seats INT;
    
    -- Count reserved seats for the terrain
    SELECT COUNT(CASE WHEN statut = 'Réservé' THEN 1 END)
    INTO reserved_seats
    FROM terrain_siege
    WHERE terrainID = NEW.terrainID;
    
    -- If no seats are reserved, update terrain status
    IF reserved_seats = 0 THEN
        UPDATE terrain
        SET statut = 'Disponible'
        WHERE courtID = NEW.terrainID;
    END IF;
END //
DELIMITER ; 