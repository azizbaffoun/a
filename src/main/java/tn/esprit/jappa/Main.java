package tn.esprit.jappa;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.jappa.controllers.MaterielController;
import tn.esprit.jappa.controllers.MaintenanceController;
import tn.esprit.jappa.controllers.EmpruntController;
import javafx.fxml.FXMLLoader;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Load FXML files
        FXMLLoader materielLoader = new FXMLLoader(getClass().getResource("/fxml/materiel.fxml"));
        FXMLLoader maintenanceLoader = new FXMLLoader(getClass().getResource("/fxml/maintenance.fxml"));
        FXMLLoader empruntLoader = new FXMLLoader(getClass().getResource("/fxml/emprunt.fxml"));

        // Create tabs
        Tab materielTab = new Tab("Gestion Materiel", materielLoader.load());
        Tab maintenanceTab = new Tab("Gestion Maintenance", maintenanceLoader.load());
        Tab empruntTab = new Tab("Gestion Emprunt", empruntLoader.load());

        tabPane.getTabs().addAll(materielTab, maintenanceTab, empruntTab);

        // Apply styles
        Scene scene = new Scene(tabPane, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
        
        primaryStage.setTitle("Sport Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}