package tn.esprit.jappa.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TabPane;
import java.net.URL;
import java.util.ResourceBundle;

public class BilletMainController implements Initializable {
    @FXML private TabPane tabPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Add any initialization logic here if needed
    }

    public void switchToReservationTab() {
        tabPane.getSelectionModel().select(1); // Index 1 is the reservation tab
    }

    public void switchToManagementTab() {
        tabPane.getSelectionModel().select(0); // Index 0 is the management tab
    }
} 