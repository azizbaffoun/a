package tn.esprit.pidev.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.Random;

public class WeatherDashboardController implements Initializable {
    @FXML private Circle weatherIcon;
    @FXML private Label tempLabel;
    @FXML private Label humidityLabel;
    @FXML private Label windLabel;
    @FXML private Label conditionLabel;
    @FXML private ProgressBar aqiProgress;
    @FXML private Label aqiLabel;
    @FXML private Label uvIndexLabel;
    @FXML private Label visibilityLabel;
    @FXML private Label lastUpdatedLabel;

    private final Random random = new Random();
    private final String[] conditions = {"Sunny", "Partly Cloudy", "Cloudy", "Light Rain", "Heavy Rain"};
    private final String[] aqiLevels = {"Good", "Moderate", "Unhealthy", "Very Unhealthy", "Hazardous"};
    private final Color[] weatherColors = {
        Color.YELLOW, // Sunny
        Color.LIGHTBLUE, // Partly Cloudy
        Color.GRAY, // Cloudy
        Color.DARKBLUE, // Light Rain
        Color.DARKGRAY // Heavy Rain
    };

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        refreshWeather();
    }

    @FXML
    private void refreshWeather() {
        // Generate random weather data
        int temp = 20 + random.nextInt(20); // Temperature between 20-40°C
        int humidity = 30 + random.nextInt(50); // Humidity between 30-80%
        int windSpeed = 5 + random.nextInt(25); // Wind speed between 5-30 km/h
        int conditionIndex = random.nextInt(conditions.length);
        double aqi = random.nextDouble(); // AQI progress between 0-1
        int uvIndex = random.nextInt(11); // UV index between 0-10
        int visibility = 5 + random.nextInt(15); // Visibility between 5-20 km

        // Update UI
        tempLabel.setText(temp + "°C");
        humidityLabel.setText(humidity + "%");
        windLabel.setText(windSpeed + " km/h");
        conditionLabel.setText(conditions[conditionIndex]);
        weatherIcon.setFill(weatherColors[conditionIndex]);
        
        aqiProgress.setProgress(aqi);
        aqiLabel.setText(getAqiLevel(aqi));
        uvIndexLabel.setText(String.valueOf(uvIndex));
        visibilityLabel.setText(visibility + " km");
        
        // Update last updated time
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        lastUpdatedLabel.setText("Last updated: " + now.format(formatter));
    }

    private String getAqiLevel(double aqi) {
        int index = (int) (aqi * aqiLevels.length);
        index = Math.min(index, aqiLevels.length - 1);
        return aqiLevels[index];
    }
} 