package tn.esprit.pidev.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.json.JSONObject;
import org.json.JSONArray;
import tn.esprit.pidev.utils.FirebaseConfig;
import java.util.ResourceBundle;

public class ApiManagementController implements Initializable {

    @FXML private ComboBox<String> weatherApiSelector;
    @FXML private ComboBox<String> mapsApiSelector;
    @FXML private TextField weatherApiKey;
    @FXML private TextField weatherLocation;
    @FXML private TextField mapsApiKey;
    @FXML private TextField locationSearch;
    @FXML private Label weatherApiStatus;
    @FXML private Label mapsApiStatus;
    @FXML private Label statusLabel;
    @FXML private Label temperatureLabel;
    @FXML private Label humidityLabel;
    @FXML private Label windLabel;
    @FXML private Label conditionLabel;
    @FXML private TableView<LocationResult> locationResults;
    @FXML private TableColumn<LocationResult, String> locationNameColumn;
    @FXML private TableColumn<LocationResult, String> locationAddressColumn;
    @FXML private TableColumn<LocationResult, String> locationCoordinatesColumn;

    // Demo API Keys
    private static final String OPENWEATHER_API_KEY = "1234567890abcdef1234567890abcdef"; // Demo key
    private static final String MAPBOX_API_KEY = "pk.demo.key"; // Demo key

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize ComboBox items
        weatherApiSelector.setItems(FXCollections.observableArrayList(
            "OpenWeatherMap (Demo)",
            "Open-Meteo (Free)"
        ));
        
        mapsApiSelector.setItems(FXCollections.observableArrayList(
            "OpenStreetMap (Free)",
            "Nominatim (Free)"
        ));

        // Pre-fill API keys
        weatherApiKey.setText(OPENWEATHER_API_KEY);
        weatherApiKey.setEditable(false);
        mapsApiKey.setText("No API key needed");
        mapsApiKey.setEditable(false);
        
        // Set default location
        weatherLocation.setText("Tunis,TN");

        setupTableColumns();
        loadSavedConfigurations();

        // Check if Firebase is already configured
        if (FirebaseConfig.isInitialized()) {
            statusLabel.setText("Firebase is already configured");
        }
    }

    private void setupTableColumns() {
        locationNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        locationAddressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        locationCoordinatesColumn.setCellValueFactory(new PropertyValueFactory<>("coordinates"));
    }

    private void loadSavedConfigurations() {
        weatherApiSelector.setValue("Open-Meteo (Free)");
        mapsApiSelector.setValue("OpenStreetMap (Free)");
        statusLabel.setText("Using free APIs - no configuration needed");
    }

    @FXML
    private void testWeatherApi() {
        String selectedApi = weatherApiSelector.getValue();
        String location = weatherLocation.getText();
        
        if (selectedApi == null || location.isEmpty()) {
            showError("Please select an API and enter a location");
            return;
        }

        try {
            if (selectedApi.contains("Open-Meteo")) {
                // Test Open-Meteo API (completely free, no API key needed)
                String url = String.format(
                    "https://geocoding-api.open-meteo.com/v1/search?name=%s&count=1",
                    URLEncoder.encode(location, StandardCharsets.UTF_8.toString())
                );
                
                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setRequestMethod("GET");
                
                if (conn.getResponseCode() == 200) {
                    weatherApiStatus.setText("Connection successful!");
                    weatherApiStatus.setStyle("-fx-text-fill: green;");
                } else {
                    weatherApiStatus.setText("Connection failed!");
                    weatherApiStatus.setStyle("-fx-text-fill: red;");
                }
            }
        } catch (Exception e) {
            showError("Error testing API: " + e.getMessage());
        }
    }

    @FXML
    private void testMapsApi() {
        String selectedApi = mapsApiSelector.getValue();
        
        if (selectedApi == null) {
            showError("Please select an API");
            return;
        }

        try {
            // Test OpenStreetMap Nominatim API (free)
            String url = "https://nominatim.openstreetmap.org/search?q=Tunis&format=json&limit=1";
            
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "PaddlePro/1.0");
            
            if (conn.getResponseCode() == 200) {
                mapsApiStatus.setText("Connection successful!");
                mapsApiStatus.setStyle("-fx-text-fill: green;");
            } else {
                mapsApiStatus.setText("Connection failed!");
                mapsApiStatus.setStyle("-fx-text-fill: red;");
            }
        } catch (Exception e) {
            showError("Error testing API: " + e.getMessage());
        }
    }

    @FXML
    private void refreshWeatherData() {
        String location = weatherLocation.getText();
        if (location.isEmpty()) {
            showError("Please enter a location");
            return;
        }

        try {
            // Use Open-Meteo API for weather data (free, no key needed)
            // First get coordinates
            String geoUrl = String.format(
                "https://geocoding-api.open-meteo.com/v1/search?name=%s&count=1",
                URLEncoder.encode(location, StandardCharsets.UTF_8.toString())
            );
            
            HttpURLConnection geoConn = (HttpURLConnection) new URL(geoUrl).openConnection();
            BufferedReader geoReader = new BufferedReader(new InputStreamReader(geoConn.getInputStream()));
            StringBuilder geoResponse = new StringBuilder();
            String line;
            
            while ((line = geoReader.readLine()) != null) {
                geoResponse.append(line);
            }
            
            JSONObject geoJson = new JSONObject(geoResponse.toString());
            if (geoJson.has("results") && geoJson.getJSONArray("results").length() > 0) {
                JSONObject firstResult = geoJson.getJSONArray("results").getJSONObject(0);
                double lat = firstResult.getDouble("latitude");
                double lon = firstResult.getDouble("longitude");
                
                // Now get weather data
                String weatherUrl = String.format(
                    "https://api.open-meteo.com/v1/forecast?latitude=%.6f&longitude=%.6f&current=temperature_2m,relative_humidity_2m,wind_speed_10m,weather_code",
                    lat, lon
                );
                
                HttpURLConnection weatherConn = (HttpURLConnection) new URL(weatherUrl).openConnection();
                BufferedReader weatherReader = new BufferedReader(new InputStreamReader(weatherConn.getInputStream()));
                StringBuilder weatherResponse = new StringBuilder();
                
                while ((line = weatherReader.readLine()) != null) {
                    weatherResponse.append(line);
                }
                
                JSONObject weatherJson = new JSONObject(weatherResponse.toString());
                JSONObject current = weatherJson.getJSONObject("current");
                
                temperatureLabel.setText(String.format("Temperature: %.1f°C", current.getDouble("temperature_2m")));
                humidityLabel.setText(String.format("Humidity: %d%%", current.getInt("relative_humidity_2m")));
                windLabel.setText(String.format("Wind: %.1f km/h", current.getDouble("wind_speed_10m")));
                conditionLabel.setText("Condition: " + getWeatherCondition(current.getInt("weather_code")));
                
                statusLabel.setText("Weather data updated successfully");
            }
        } catch (Exception e) {
            showError("Error refreshing weather data: " + e.getMessage());
        }
    }

    private String getWeatherCondition(int code) {
        // WMO Weather interpretation codes (https://open-meteo.com/en/docs)
        switch (code) {
            case 0: return "Clear sky";
            case 1: case 2: case 3: return "Partly cloudy";
            case 45: case 48: return "Foggy";
            case 51: case 53: case 55: return "Drizzle";
            case 61: case 63: case 65: return "Rain";
            case 71: case 73: case 75: return "Snow";
            case 77: return "Snow grains";
            case 80: case 81: case 82: return "Rain showers";
            case 85: case 86: return "Snow showers";
            case 95: return "Thunderstorm";
            case 96: case 99: return "Thunderstorm with hail";
            default: return "Unknown";
        }
    }

    @FXML
    private void searchLocation() {
        String query = locationSearch.getText();
        if (query.isEmpty()) {
            showError("Please enter a location to search");
            return;
        }

        try {
            // Use OpenStreetMap Nominatim API (free)
            String url = String.format(
                "https://nominatim.openstreetmap.org/search?q=%s&format=json&limit=5",
                URLEncoder.encode(query, StandardCharsets.UTF_8.toString())
            );
            
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "PaddlePro/1.0");
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            
            JSONArray results = new JSONArray(response.toString());
            ObservableList<LocationResult> locationData = FXCollections.observableArrayList();
            
            for (int i = 0; i < results.length(); i++) {
                JSONObject result = results.getJSONObject(i);
                locationData.add(new LocationResult(
                    result.getString("display_name").split(",")[0],
                    result.getString("display_name"),
                    String.format("%.6f, %.6f", 
                        result.getDouble("lat"), 
                        result.getDouble("lon"))
                ));
            }
            
            locationResults.setItems(locationData);
            statusLabel.setText("Found " + results.length() + " locations");
        } catch (Exception e) {
            showError("Error searching location: " + e.getMessage());
        }
    }

    @FXML
    private void configureFirebase() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Firebase Credentials JSON File");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON Files", "*.json")
            );
            
            File selectedFile = fileChooser.showOpenDialog(null);
            if (selectedFile != null) {
                // Copy the credentials file to the application directory
                Path destination = Path.of("firebase-credentials.json");
                Files.copy(selectedFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
                
                // Initialize Firebase
                FirebaseConfig.initialize();
                
                // Log a test event
                FirebaseConfig.logEvent("app_opened", "screen", "api_management");
                
                showMessage("Firebase configured successfully!");
                statusLabel.setText("Firebase is now configured and ready to use");
            }
        } catch (Exception e) {
            showError("Error configuring Firebase: " + e.getMessage());
        }
    }

    @FXML
    private void configureGoogleAnalytics() {
        if (!FirebaseConfig.isInitialized()) {
            showMessage("Please configure Firebase first to use Google Analytics");
            return;
        }
        
        showMessage("Google Analytics is configured through Firebase. You can now track events automatically.");
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Helper class for location results
    public static class LocationResult {
        private String name;
        private String address;
        private String coordinates;

        public LocationResult(String name, String address, String coordinates) {
            this.name = name;
            this.address = address;
            this.coordinates = coordinates;
        }

        public String getName() { return name; }
        public String getAddress() { return address; }
        public String getCoordinates() { return coordinates; }
    }
} 