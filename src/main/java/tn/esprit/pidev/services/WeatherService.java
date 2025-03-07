package tn.esprit.pidev.services;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import java.io.IOException;

public class WeatherService {
    private static final String WEATHER_API_KEY = "YOUR_API_KEY"; // Replace with your OpenWeatherMap API key
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5";
    private final OkHttpClient client = new OkHttpClient();

    public JSONObject getCurrentWeather(double lat, double lon) throws IOException {
        String url = String.format("%s/weather?lat=%f&lon=%f&appid=%s&units=metric",
                BASE_URL, lat, lon, WEATHER_API_KEY);
        
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected response " + response);
            
            String responseData = response.body().string();
            return new JSONObject(responseData);
        }
    }

    public JSONObject getForecast(double lat, double lon) throws IOException {
        String url = String.format("%s/forecast?lat=%f&lon=%f&appid=%s&units=metric",
                BASE_URL, lat, lon, WEATHER_API_KEY);
        
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected response " + response);
            
            String responseData = response.body().string();
            return new JSONObject(responseData);
        }
    }

    public boolean isOutdoorPlaySafe(double lat, double lon) throws IOException {
        JSONObject weather = getCurrentWeather(lat, lon);
        
        // Get main weather parameters
        JSONObject main = weather.getJSONObject("main");
        double temp = main.getDouble("temp");
        int humidity = main.getInt("humidity");
        
        // Get weather conditions
        String condition = weather.getJSONArray("weather")
                .getJSONObject(0)
                .getString("main");

        // Define safe playing conditions
        boolean tempSafe = temp >= 10 && temp <= 35; // Safe between 10°C and 35°C
        boolean humiditySafe = humidity <= 85; // Safe below 85% humidity
        boolean weatherSafe = !condition.equals("Rain") && 
                            !condition.equals("Snow") && 
                            !condition.equals("Thunderstorm");

        return tempSafe && humiditySafe && weatherSafe;
    }

    public String getMaintenanceRecommendation(double lat, double lon) throws IOException {
        JSONObject weather = getCurrentWeather(lat, lon);
        JSONObject forecast = getForecast(lat, lon);
        
        // Analyze current and forecasted conditions
        String currentCondition = weather.getJSONArray("weather")
                .getJSONObject(0)
                .getString("main");
        
        StringBuilder recommendation = new StringBuilder();
        
        // Check current conditions
        if (currentCondition.equals("Rain")) {
            recommendation.append("Court requires immediate inspection after rain. ");
        } else if (currentCondition.equals("Snow")) {
            recommendation.append("Court needs snow removal and surface check. ");
        }
        
        // Check temperature for surface maintenance
        double temp = weather.getJSONObject("main").getDouble("temp");
        if (temp > 30) {
            recommendation.append("High temperature - check court surface for expansion. ");
        } else if (temp < 5) {
            recommendation.append("Low temperature - monitor surface for contraction. ");
        }
        
        return recommendation.length() > 0 ? recommendation.toString() : "No immediate maintenance required.";
    }
} 