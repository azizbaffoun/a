package tn.esprit.pidev.utils;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

public class ButtonUtils {
    public static void setupButton(Button button, String color, String hoverColor) {
        button.setMinWidth(120);
        button.setPrefHeight(35);
        button.setStyle(
            "-fx-background-color: " + color + "; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 20; " +
            "-fx-cursor: hand;"
        );

        // Add drop shadow effect
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.3));
        shadow.setRadius(5);
        button.setEffect(shadow);

        // Hover effects
        button.setOnMouseEntered(e -> {
            button.setStyle(
                "-fx-background-color: " + hoverColor + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 20; " +
                "-fx-cursor: hand; " +
                "-fx-scale-x: 1.05; " +
                "-fx-scale-y: 1.05;"
            );
        });

        button.setOnMouseExited(e -> {
            button.setStyle(
                "-fx-background-color: " + color + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 20; " +
                "-fx-cursor: hand;"
            );
        });

        // Click effect
        button.setOnMousePressed(e -> button.setEffect(null));
        button.setOnMouseReleased(e -> button.setEffect(shadow));
    }

    public static HBox createButtonBox(Button... buttons) {
        HBox buttonBox = new HBox(15);
        buttonBox.setPadding(new Insets(15));
        buttonBox.setAlignment(Pos.CENTER);

        // Set different colors for different actions
        setupButton(buttons[0], "#4CAF50", "#45a049"); // Add - Green
        setupButton(buttons[1], "#2196F3", "#1976D2"); // Update - Blue
        setupButton(buttons[2], "#f44336", "#d32f2f"); // Delete - Red
        setupButton(buttons[3], "#9E9E9E", "#757575"); // Clear - Gray

        buttonBox.getChildren().addAll(buttons);
        return buttonBox;
    }
} 