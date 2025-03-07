package tn.esprit.pidev.config;

import com.google.analytics.data.v1beta.BetaAnalyticsDataClient;
import com.google.analytics.data.v1beta.BetaAnalyticsDataSettings;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.IOException;

public class AnalyticsConfig {
    private static final String PROPERTY_ID = "YOUR_GA4_PROPERTY_ID"; // Replace with your GA4 property ID
    private static BetaAnalyticsDataClient analyticsClient;

    private AnalyticsConfig() {}

    public static BetaAnalyticsDataClient getAnalyticsClient() throws IOException {
        if (analyticsClient == null) {
            GoogleCredentials credentials = GoogleCredentials.getApplicationDefault()
                .createScoped("https://www.googleapis.com/auth/analytics.readonly");

            BetaAnalyticsDataSettings analyticsSettings = BetaAnalyticsDataSettings.newBuilder()
                .setCredentialsProvider(() -> credentials)
                .build();

            analyticsClient = BetaAnalyticsDataClient.create(analyticsSettings);
        }
        return analyticsClient;
    }

    public static String getPropertyId() {
        return PROPERTY_ID;
    }
} 