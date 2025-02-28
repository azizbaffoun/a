-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Feb 27, 2025 at 03:15 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.1.25

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `chleghem`
--

-- --------------------------------------------------------

--
-- Table structure for table `billet`
--

CREATE TABLE `billet` (
  `ID` int(11) NOT NULL,
  `eventID` int(11) NOT NULL,
  `dateAchat` datetime DEFAULT NULL,
  `prix` float DEFAULT NULL,
  `typeBillet` varchar(255) DEFAULT NULL,
  `statut` enum('Valide','Annulé','Non valide') DEFAULT 'Valide',
  `quantite` int(11) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `billet`
--

INSERT INTO `billet` (`ID`, `eventID`, `dateAchat`, `prix`, `typeBillet`, `statut`, `quantite`) VALUES
(1, 2, '2025-02-12 00:00:00', 1500, 'teskra', 'Valide', 12);

-- --------------------------------------------------------

--
-- Table structure for table `emprunt`
--

CREATE TABLE `emprunt` (
  `empruntID` int(11) NOT NULL,
  `userID` int(11) DEFAULT NULL,
  `materielID` int(11) DEFAULT NULL,
  `dateEmprunt` date NOT NULL,
  `dateRetour` date NOT NULL,
  `statutEmprunt` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `emprunt`
--

INSERT INTO `emprunt` (`empruntID`, `userID`, `materielID`, `dateEmprunt`, `dateRetour`, `statutEmprunt`) VALUES
(2, 13, 1, '2025-02-11', '2025-02-20', 'Active');

-- --------------------------------------------------------

--
-- Table structure for table `evenement`
--

CREATE TABLE `evenement` (
  `ID` int(11) NOT NULL,
  `nom` varchar(255) NOT NULL,
  `details` text DEFAULT NULL,
  `dateDebut` date DEFAULT NULL,
  `dateFin` date DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `recompense` varchar(255) DEFAULT NULL,
  `statut` enum('En cours','Terminé','Annulé') DEFAULT NULL,
  `participantsMax` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `evenement`
--

INSERT INTO `evenement` (`ID`, `nom`, `details`, `dateDebut`, `dateFin`, `type`, `recompense`, `statut`, `participantsMax`) VALUES
(1, 'Hackathon 2025', 'Compétition de programmation', '2025-03-01', '2025-03-03', 'Technologie', '5000$', '', 100),
(2, 'Conférence IA', 'Discussion sur l\'intelligence artificielle', '2025-04-15', '2025-04-17', 'Conférence', 'Certificat', '', 200),
(3, 'Startup Challenge', 'Concours de startups innovantes', '2025-05-10', '2025-05-12', 'Business', '10000$', '', 50),
(4, 'Marathon Code', 'Concours de développement rapide', '2025-06-20', '2025-06-22', 'Compétition', 'Ordinateur portable', '', 150),
(5, 'Journée des Sciences', 'Événement scientifique pour étudiants', '2025-07-05', '2025-07-05', 'Éducation', 'Trophée', 'Annulé', 300),
(6, 'Tennis Tournament 2024', 'Annual tennis tournament', '2025-02-19', '2025-02-20', 'Sport', 'Trophy + Prize Money', 'En cours', 32),
(7, 'takwira', 'takwira sobheya', '2002-12-15', '2002-12-16', 'takwira', '100', 'En cours', 10),
(8, 'TAKWIRA', 'ZEZEZEZE', '2025-02-21', '2025-02-22', 'EZEZ', '1500', 'Terminé', 150);

-- --------------------------------------------------------

--
-- Table structure for table `jointable`
--

CREATE TABLE `jointable` (
  `userID` int(11) NOT NULL,
  `eventID` int(11) NOT NULL,
  `userRoleInEvent` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `maintenance`
--

CREATE TABLE `maintenance` (
  `maintenanceID` int(11) NOT NULL,
  `materielID` int(11) DEFAULT NULL,
  `dateMaintenance` date NOT NULL,
  `description` text NOT NULL,
  `statutMaintenance` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `maintenance`
--

INSERT INTO `maintenance` (`maintenanceID`, `materielID`, `dateMaintenance`, `description`, `statutMaintenance`) VALUES
(1, 1, '2025-02-12', 'dsqddqd', 'Scheduled');

-- --------------------------------------------------------

--
-- Table structure for table `materiel`
--

CREATE TABLE `materiel` (
  `ID` int(11) NOT NULL,
  `type` varchar(255) DEFAULT NULL,
  `typeSport` varchar(255) DEFAULT NULL,
  `prix` float DEFAULT NULL,
  `dateReservation` date DEFAULT NULL,
  `statut` varchar(50) DEFAULT NULL,
  `ownerType` enum('Club','Fédération','Privé') DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `materiel`
--

INSERT INTO `materiel` (`ID`, `type`, `typeSport`, `prix`, `dateReservation`, `statut`, `ownerType`) VALUES
(1, 'dgfdgfdg', 'dfgdfgfdg', 1500, '2025-02-19', 'Available', 'Fédération');

-- --------------------------------------------------------

--
-- Table structure for table `reservation`
--

CREATE TABLE `reservation` (
  `ID` int(11) NOT NULL,
  `utilisateurID` int(11) NOT NULL,
  `dateReservation` datetime DEFAULT current_timestamp(),
  `statut` enum('Confirmée','Annulée') DEFAULT 'Confirmée',
  `type` enum('TERRAIN','BILLET') DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `reservation`
--

INSERT INTO `reservation` (`ID`, `utilisateurID`, `dateReservation`, `statut`, `type`) VALUES
(17, 12, '2025-02-20 20:05:16', 'Confirmée', 'BILLET'),
(18, 5, '2025-02-20 12:00:00', 'Confirmée', 'TERRAIN'),
(20, 16, '2025-02-20 20:28:50', 'Confirmée', 'BILLET'),
(21, 5, '2025-02-20 12:00:00', 'Confirmée', 'TERRAIN'),
(22, 15, '2025-02-22 12:00:00', 'Confirmée', 'TERRAIN'),
(23, 17, '2025-02-21 12:00:00', 'Confirmée', 'TERRAIN'),
(24, 15, '2025-02-21 00:34:45', 'Confirmée', 'BILLET'),
(25, 17, '2025-02-21 12:00:00', 'Confirmée', 'TERRAIN'),
(26, 5, '2025-02-21 12:00:00', 'Confirmée', 'TERRAIN');

-- --------------------------------------------------------

--
-- Table structure for table `reservationmateriel`
--

CREATE TABLE `reservationmateriel` (
  `ID` int(11) NOT NULL,
  `materielID` int(11) DEFAULT NULL,
  `dateReservation` date DEFAULT NULL,
  `statut` enum('Confirmée','Annulée') DEFAULT NULL,
  `reservationID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `reservationterrain`
--

CREATE TABLE `reservationterrain` (
  `ID` int(11) NOT NULL,
  `userID` int(11) DEFAULT NULL,
  `terrainID` int(11) DEFAULT NULL,
  `dateReservation` date DEFAULT NULL,
  `heureReservation` time DEFAULT NULL,
  `statut` enum('Confirmée','Annulée') DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `reservationterrain`
--

INSERT INTO `reservationterrain` (`ID`, `userID`, `terrainID`, `dateReservation`, `heureReservation`, `statut`) VALUES
(19, 5, 2, '2025-02-20', '12:00:00', 'Confirmée'),
(21, 5, 2, '2025-02-20', '12:00:00', 'Confirmée'),
(22, 15, 2, '2025-02-22', '12:00:00', 'Confirmée'),
(23, 17, 2, '2025-02-21', '12:00:00', 'Confirmée'),
(25, 17, 5, '2025-02-21', '12:00:00', 'Confirmée'),
(26, 5, 5, '2025-02-21', '12:00:00', 'Confirmée');

-- --------------------------------------------------------

--
-- Table structure for table `reservation_billet`
--

CREATE TABLE `reservation_billet` (
  `reservationID` int(11) NOT NULL,
  `billetID` int(11) NOT NULL,
  `nombreBillet` int(11) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `reservation_billet`
--

INSERT INTO `reservation_billet` (`reservationID`, `billetID`, `nombreBillet`) VALUES
(17, 1, 6),
(20, 1, 1),
(24, 1, 1);

-- --------------------------------------------------------

--
-- Table structure for table `role`
--

CREATE TABLE `role` (
  `roleID` int(11) NOT NULL,
  `roleNom` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `role`
--

INSERT INTO `role` (`roleID`, `roleNom`) VALUES
(1, 'ADMIN'),
(2, 'USER'),
(3, 'USER');

-- --------------------------------------------------------

--
-- Table structure for table `terrain`
--

CREATE TABLE `terrain` (
  `courtID` int(11) NOT NULL,
  `type` varchar(255) DEFAULT NULL,
  `localisation` text DEFAULT NULL,
  `capacite` int(11) DEFAULT NULL,
  `statut` enum('Disponible','Réservé','Maintenance') DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `terrain`
--

INSERT INTO `terrain` (`courtID`, `type`, `localisation`, `capacite`, `statut`) VALUES
(2, 'STADE', 'GAFSA', 1500, 'Réservé'),
(5, 'houma', 'dqsdsqd', 1500, 'Disponible');

-- --------------------------------------------------------

--
-- Table structure for table `utilisateur`
--

CREATE TABLE `utilisateur` (
  `ID` int(11) NOT NULL,
  `email` varchar(255) NOT NULL,
  `motdepasse` varchar(255) NOT NULL,
  `genre` varchar(50) DEFAULT NULL,
  `prenom` varchar(255) DEFAULT NULL,
  `nom` varchar(255) DEFAULT NULL,
  `numeroTelephone` varchar(50) DEFAULT NULL,
  `adresse` text DEFAULT NULL,
  `photoProfil` varchar(255) DEFAULT NULL,
  `roleID` int(11) DEFAULT NULL,
  `nomOrganisation` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `utilisateur`
--

INSERT INTO `utilisateur` (`ID`, `email`, `motdepasse`, `genre`, `prenom`, `nom`, `numeroTelephone`, `adresse`, `photoProfil`, `roleID`, `nomOrganisation`) VALUES
(5, 'aziz@aziz.com', '123456789', 'aziz', 'aziz', 'baffoun', '98765432', 'atete', 'erer', 1, '150'),
(11, 'AZIZ@TEST.COM', '123456789', 'M', 'Ali', 'Ben Salem', '123456789', 'Tunis, Tunisie', 'photo1.jpg', 2, 'Company A'),
(12, 'user2@example.com', 'password123', 'F', 'Sara', 'Mahmoud', '987654321', 'Sfax, Tunisie', 'photo2.jpg', 1, 'Company B'),
(13, 'user3@example.com', 'password123', 'M', 'Omar', 'Trabelsi', '1122334455', 'Sousse, Tunisie', 'photo3.jpg', 1, 'Company C'),
(14, 'user4@example.com', 'password123', 'F', 'Nour', 'Jaziri', '2233445566', 'Nabeul, Tunisie', 'photo4.jpg', 1, 'Company D'),
(15, 'user5@example.com', 'password123', 'M', 'Karim', 'Dridi', '3344556677', 'Monastir, Tunisie', 'photo5.jpg', 1, 'Company E'),
(16, 'user6@example.com', 'password123', 'F', 'Mouna', 'Gharbi', '4455667788', 'Bizerte, Tunisie', 'photo6.jpg', 1, 'Company F'),
(17, 'user7@example.com', 'password123', 'M', 'Ahmed', 'Saidi', '5566778899', 'Gabès, Tunisie', 'photo7.jpg', 1, 'Company G'),
(18, 'user8@example.com', 'password123', 'F', 'Rania', 'Khelifi', '6677889900', 'Djerba, Tunisie', 'photo8.jpg', 1, 'Company H'),
(19, 'user9@example.com', 'password123', 'M', 'Walid', 'Boussetta', '7788990011', 'Kairouan, Tunisie', 'photo9.jpg', 1, 'Company I'),
(20, 'user10@example.com', 'password123', 'F', 'Ines', 'Zouari', '8899001122', 'Gafsa, Tunisie', 'photo10.jpg', 1, 'Company J');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `billet`
--
ALTER TABLE `billet`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `eventID` (`eventID`);

--
-- Indexes for table `emprunt`
--
ALTER TABLE `emprunt`
  ADD PRIMARY KEY (`empruntID`),
  ADD KEY `userID` (`userID`),
  ADD KEY `materielID` (`materielID`);

--
-- Indexes for table `evenement`
--
ALTER TABLE `evenement`
  ADD PRIMARY KEY (`ID`);

--
-- Indexes for table `jointable`
--
ALTER TABLE `jointable`
  ADD PRIMARY KEY (`userID`,`eventID`),
  ADD KEY `eventID` (`eventID`);

--
-- Indexes for table `maintenance`
--
ALTER TABLE `maintenance`
  ADD PRIMARY KEY (`maintenanceID`),
  ADD KEY `materielID` (`materielID`);

--
-- Indexes for table `materiel`
--
ALTER TABLE `materiel`
  ADD PRIMARY KEY (`ID`);

--
-- Indexes for table `reservation`
--
ALTER TABLE `reservation`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `utilisateurID` (`utilisateurID`);

--
-- Indexes for table `reservationmateriel`
--
ALTER TABLE `reservationmateriel`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `materielID` (`materielID`),
  ADD KEY `reservationID` (`reservationID`);

--
-- Indexes for table `reservationterrain`
--
ALTER TABLE `reservationterrain`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `userID` (`userID`),
  ADD KEY `terrainID` (`terrainID`);

--
-- Indexes for table `reservation_billet`
--
ALTER TABLE `reservation_billet`
  ADD PRIMARY KEY (`reservationID`,`billetID`),
  ADD KEY `billetID` (`billetID`);

--
-- Indexes for table `role`
--
ALTER TABLE `role`
  ADD PRIMARY KEY (`roleID`);

--
-- Indexes for table `terrain`
--
ALTER TABLE `terrain`
  ADD PRIMARY KEY (`courtID`);

--
-- Indexes for table `utilisateur`
--
ALTER TABLE `utilisateur`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `roleID` (`roleID`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `billet`
--
ALTER TABLE `billet`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `emprunt`
--
ALTER TABLE `emprunt`
  MODIFY `empruntID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `evenement`
--
ALTER TABLE `evenement`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `maintenance`
--
ALTER TABLE `maintenance`
  MODIFY `maintenanceID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `materiel`
--
ALTER TABLE `materiel`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `reservation`
--
ALTER TABLE `reservation`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=27;

--
-- AUTO_INCREMENT for table `reservationmateriel`
--
ALTER TABLE `reservationmateriel`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `reservationterrain`
--
ALTER TABLE `reservationterrain`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=27;

--
-- AUTO_INCREMENT for table `role`
--
ALTER TABLE `role`
  MODIFY `roleID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `terrain`
--
ALTER TABLE `terrain`
  MODIFY `courtID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `utilisateur`
--
ALTER TABLE `utilisateur`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `billet`
--
ALTER TABLE `billet`
  ADD CONSTRAINT `billet_ibfk_2` FOREIGN KEY (`eventID`) REFERENCES `evenement` (`ID`);

--
-- Constraints for table `emprunt`
--
ALTER TABLE `emprunt`
  ADD CONSTRAINT `emprunt_ibfk_1` FOREIGN KEY (`userID`) REFERENCES `utilisateur` (`ID`),
  ADD CONSTRAINT `emprunt_ibfk_2` FOREIGN KEY (`materielID`) REFERENCES `materiel` (`ID`);

--
-- Constraints for table `jointable`
--
ALTER TABLE `jointable`
  ADD CONSTRAINT `jointable_ibfk_1` FOREIGN KEY (`userID`) REFERENCES `utilisateur` (`ID`),
  ADD CONSTRAINT `jointable_ibfk_2` FOREIGN KEY (`eventID`) REFERENCES `evenement` (`ID`);

--
-- Constraints for table `maintenance`
--
ALTER TABLE `maintenance`
  ADD CONSTRAINT `maintenance_ibfk_1` FOREIGN KEY (`materielID`) REFERENCES `materiel` (`ID`);

--
-- Constraints for table `reservation`
--
ALTER TABLE `reservation`
  ADD CONSTRAINT `reservation_ibfk_1` FOREIGN KEY (`utilisateurID`) REFERENCES `utilisateur` (`ID`);

--
-- Constraints for table `reservationmateriel`
--
ALTER TABLE `reservationmateriel`
  ADD CONSTRAINT `reservationmateriel_ibfk_2` FOREIGN KEY (`materielID`) REFERENCES `materiel` (`ID`),
  ADD CONSTRAINT `reservationmateriel_ibfk_3` FOREIGN KEY (`reservationID`) REFERENCES `reservation` (`ID`);

--
-- Constraints for table `reservationterrain`
--
ALTER TABLE `reservationterrain`
  ADD CONSTRAINT `reservationterrain_ibfk_1` FOREIGN KEY (`userID`) REFERENCES `utilisateur` (`ID`),
  ADD CONSTRAINT `reservationterrain_ibfk_2` FOREIGN KEY (`terrainID`) REFERENCES `terrain` (`courtID`);

--
-- Constraints for table `reservation_billet`
--
ALTER TABLE `reservation_billet`
  ADD CONSTRAINT `reservation_billet_ibfk_1` FOREIGN KEY (`reservationID`) REFERENCES `reservation` (`ID`),
  ADD CONSTRAINT `reservation_billet_ibfk_2` FOREIGN KEY (`billetID`) REFERENCES `billet` (`ID`);

--
-- Constraints for table `utilisateur`
--
ALTER TABLE `utilisateur`
  ADD CONSTRAINT `utilisateur_ibfk_1` FOREIGN KEY (`roleID`) REFERENCES `role` (`roleID`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
