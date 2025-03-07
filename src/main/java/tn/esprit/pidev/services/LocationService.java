package tn.esprit.pidev.services;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class LocationService {
    private static final String NOMINATIM_BASE_URL = "https://nominatim.openstreetmap.org";
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public static class Location {
        private final double latitude;
        private final double longitude;
        private final String displayName;

        public Location(double latitude, double longitude, String displayName) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.displayName = displayName;
        }

        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }
        public String getDisplayName() { return displayName; }
    }

    public Location getCoordinates(String address) throws IOException, InterruptedException {
        String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);
        String url = String.format("%s/search?q=%s&format=json", NOMINATIM_BASE_URL, encodedAddress);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "PaddlePro Application")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JSONArray results = new JSONArray(response.body());
            if (results.length() > 0) {
                JSONObject result = results.getJSONObject(0);
                return new Location(
                    result.getDouble("lat"),
                    result.getDouble("lon"),
                    result.getString("display_name")
                );
            }
        }
        throw new IOException("Failed to get coordinates for address: " + address);
    }

    public String reverseGeocode(double lat, double lon) throws IOException, InterruptedException {
        String url = String.format("%s/reverse?lat=%f&lon=%f&format=json", 
            NOMINATIM_BASE_URL, lat, lon);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "PaddlePro Application")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JSONObject result = new JSONObject(response.body());
            return result.getString("display_name");
        }
        throw new IOException("Failed to reverse geocode coordinates");
    }

    public double calculateDistance(Location loc1, Location loc2) {
        final int R = 6371; // Earth's radius in kilometers

        double lat1 = Math.toRadians(loc1.getLatitude());
        double lon1 = Math.toRadians(loc1.getLongitude());
        double lat2 = Math.toRadians(loc2.getLatitude());
        double lon2 = Math.toRadians(loc2.getLongitude());

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                   Math.cos(lat1) * Math.cos(lat2) *
                   Math.sin(dLon/2) * Math.sin(dLon/2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return R * c; // Distance in kilometers
    }

    public String getStaticMapUrl(Location location, int zoom) {
        // Note: Replace with your actual MapBox API key
        String MAPBOX_API_KEY = "YOUR_MAPBOX_API_KEY";
        return String.format(
            "https://api.mapbox.com/styles/v1/mapbox/streets-v11/static/%f,%f,%d,0/%dx%d?access_token=%s",
            location.getLongitude(),
            location.getLatitude(),
            zoom,
            600, // width
            400, // height
            MAPBOX_API_KEY
        );
    }
} 