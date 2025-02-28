package tn.esprit.jappa;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application {
    private static Main instance;
    private Stage primaryStage;
    private TabPane tabPane;
    private Scene scene;

    // Public no-args constructor required by JavaFX
    public Main() {
        tabPane = new TabPane();
        instance = this;
    }

    public static Main getInstance() {
        if (instance == null) {
            instance = new Main();
        }
        return instance;
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            this.primaryStage = primaryStage;
            
            // Add tabs for different views
            addTab("Tickets", "/fxml/billet.fxml");
            addTab("Reservations", "/fxml/reservation_billet.fxml");
            
            scene = new Scene(tabPane);
            String cssPath = Main.class.getResource("/styles/style.css").toExternalForm();
            scene.getStylesheets().add(cssPath);

            // Configure and show stage
            primaryStage.setTitle("Ticket Management System");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading resources: " + e.getMessage());
        }
    }

    private void addTab(String title, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Tab tab = new Tab(title, loader.load());
            tab.setClosable(false);
            tabPane.getTabs().add(tab);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load " + title + " view: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @Override
    public void stop() {
        // Application cleanup code if needed
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Getters
    public Stage getPrimaryStage() { return primaryStage; }
    public TabPane getTabPane() { return tabPane; }
    public Scene getScene() { return scene; }
} 