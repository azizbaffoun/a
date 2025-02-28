-- Drop test database if exists and create new one
DROP DATABASE IF EXISTS jawher_db_test;
CREATE DATABASE jawher_db_test;
USE jawher_db_test;

-- Create tables
CREATE TABLE utilisateur (
    ID INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL,
    motdepasse VARCHAR(255) NOT NULL,
    genre VARCHAR(50),
    prenom VARCHAR(255),
    nom VARCHAR(255),
    numeroTelephone VARCHAR(50),
    adresse TEXT,
    photoProfil VARCHAR(255),
    roleID INT,
    nomOrganisation VARCHAR(255)
);

CREATE TABLE evenement (
    ID INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(255) NOT NULL,
    details TEXT,
    dateDebut DATE,
    dateFin DATE,
    type VARCHAR(255),
    recompense VARCHAR(255),
    statut ENUM('En cours', 'Terminé', 'Annulé'),
    participantsMax INT
);

CREATE TABLE billet (
    ID INT PRIMARY KEY AUTO_INCREMENT,
    eventID INT NOT NULL,
    dateAchat DATETIME,
    prix FLOAT,
    typeBillet VARCHAR(255),
    statut ENUM('Valide', 'Annulé', 'Non valide') DEFAULT 'Valide',
    quantite INT DEFAULT 1,
    FOREIGN KEY (eventID) REFERENCES evenement(ID)
);

CREATE TABLE terrain (
    courtID INT PRIMARY KEY AUTO_INCREMENT,
    type VARCHAR(255),
    localisation TEXT,
    capacite INT,
    statut ENUM('Disponible', 'Réservé', 'Maintenance')
);

CREATE TABLE terrain_siege (
    siegeID INT PRIMARY KEY AUTO_INCREMENT,
    terrainID INT,
    rangee VARCHAR(5),
    numero VARCHAR(10),
    statut ENUM('Disponible', 'Réservé', 'Maintenance') DEFAULT 'Disponible',
    prix DECIMAL(10,2),
    FOREIGN KEY (terrainID) REFERENCES terrain(courtID)
);

CREATE TABLE reservation (
    ID INT PRIMARY KEY AUTO_INCREMENT,
    utilisateurID INT NOT NULL,
    dateReservation DATETIME DEFAULT CURRENT_TIMESTAMP,
    statut ENUM('Confirmée', 'Annulée') DEFAULT 'Confirmée',
    type ENUM('TERRAIN', 'BILLET'),
    FOREIGN KEY (utilisateurID) REFERENCES utilisateur(ID)
);

CREATE TABLE reservation_billet (
    reservationID INT,
    billetID INT,
    nombreBillet INT NOT NULL DEFAULT 1,
    PRIMARY KEY (reservationID, billetID),
    FOREIGN KEY (reservationID) REFERENCES reservation(ID),
    FOREIGN KEY (billetID) REFERENCES billet(ID)
);

CREATE TABLE reservation_siege (
    reservationID INT,
    siegeID INT,
    prix_unitaire DECIMAL(10,2),
    PRIMARY KEY (reservationID, siegeID),
    FOREIGN KEY (reservationID) REFERENCES reservation(ID),
    FOREIGN KEY (siegeID) REFERENCES terrain_siege(siegeID)
); 