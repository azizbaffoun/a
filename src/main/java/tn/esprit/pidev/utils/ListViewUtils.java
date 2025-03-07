package tn.esprit.pidev.utils;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;

public class ListViewUtils {
    public static <T> VBox setupListView(ListView<T> listView, String headerText, String format) {
        // Create container VBox
        VBox container = new VBox(5);
        
        // Create header label
        Label headerLabel = new Label(headerText);
        headerLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-font-family: 'Segoe UI'; -fx-font-size: 14px; " +
                "-fx-background-color: #1a237e; -fx-padding: 5 10 5 10; " +
                "-fx-background-radius: 5 5 0 0;");
        headerLabel.setMaxWidth(Double.MAX_VALUE);
        headerLabel.setAlignment(Pos.CENTER_LEFT);

        // Style the ListView
        listView.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 0 0 5 5; " +
                "-fx-border-radius: 0 0 5 5; " +
                "-fx-border-color: #1a237e; " +
                "-fx-border-width: 1;");

        // Set cell factory with consistent styling
        listView.setCellFactory(param -> new javafx.scene.control.ListCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle(null);
                } else {
                    // Just display the item's toString() with the format
                    setText(item.toString());
                    
                    // Modern styling for rows
                    setStyle("-fx-padding: 5 10 5 10; " +
                            "-fx-font-family: 'Monospace'; " +
                            "-fx-font-size: 14px; " +
                            "-fx-background-color: " + (getIndex() % 2 == 0 ? "#f5f5f5" : "#ffffff") + ";");
                    
                    // Hover effect
                    setOnMouseEntered(event -> setStyle("-fx-padding: 5 10 5 10; " +
                            "-fx-font-family: 'Monospace'; " +
                            "-fx-font-size: 14px; " +
                            "-fx-background-color: #e3f2fd;"));
                            
                    setOnMouseExited(event -> setStyle("-fx-padding: 5 10 5 10; " +
                            "-fx-font-family: 'Monospace'; " +
                            "-fx-font-size: 14px; " +
                            "-fx-background-color: " + (getIndex() % 2 == 0 ? "#f5f5f5" : "#ffffff") + ";"));
                }
            }
        });

        // Add components to container
        container.getChildren().addAll(headerLabel, listView);
        VBox.setVgrow(listView, Priority.ALWAYS);
        return container;
    }
} 