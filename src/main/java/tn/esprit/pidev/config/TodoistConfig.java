package tn.esprit.pidev.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TodoistConfig {
    private static final String CONFIG_FILE = "application.properties";
    private static String apiToken;

    static {
        loadConfig();
    }

    private static void loadConfig() {
        Properties props = new Properties();
        try (InputStream input = TodoistConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                System.err.println("Unable to find " + CONFIG_FILE);
                return;
            }
            props.load(input);
            apiToken = props.getProperty("todoist.api.token");
            
            // If property is not set in file, try to get from environment variable
            if (apiToken == null || apiToken.equals("${TODOIST_API_TOKEN}")) {
                apiToken = System.getenv("TODOIST_API_TOKEN");
            }
            
            if (apiToken == null) {
                System.err.println("Todoist API token not found in configuration or environment variables");
            }
        } catch (IOException ex) {
            System.err.println("Error loading configuration: " + ex.getMessage());
        }
    }

    public static String getApiToken() {
        return apiToken;
    }
} 