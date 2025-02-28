-- Create billet table
CREATE TABLE IF NOT EXISTS billet (
    ID INT PRIMARY KEY AUTO_INCREMENT,
    eventID INT NOT NULL,
    dateAchat DATE NOT NULL,
    prix DECIMAL(10,2) NOT NULL,
    typeBillet VARCHAR(50) NOT NULL,
    statut VARCHAR(20) NOT NULL,
    quantite INT NOT NULL,
    CHECK (prix >= 0),
    CHECK (quantite >= 0)
);

-- Create reservation_billet table
CREATE TABLE IF NOT EXISTS reservation_billet (
    reservationID INT PRIMARY KEY AUTO_INCREMENT,
    billetID INT NOT NULL,
    nombreBillet INT NOT NULL,
    CHECK (nombreBillet > 0),
    FOREIGN KEY (billetID) REFERENCES billet(ID)
        ON DELETE CASCADE
        ON UPDATE CASCADE
); 