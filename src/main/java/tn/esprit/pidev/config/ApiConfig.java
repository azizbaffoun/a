package tn.esprit.pidev.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:application.properties")
public class ApiConfig {
    
    @Value("${weather.api.key}")
    private String weatherApiKey;
    
    @Value("${weather.api.url}")
    private String weatherApiUrl;
    
    @Value("${mapbox.api.key}")
    private String mapboxApiKey;
    
    @Value("${mapbox.api.url}")
    private String mapboxApiUrl;
    
    @Value("${stripe.api.key}")
    private String stripeApiKey;
    
    @Value("${stripe.webhook.secret}")
    private String stripeWebhookSecret;
    
    @Value("${api.rate.limit}")
    private int apiRateLimit;
    
    @Value("${api.rate.window}")
    private int apiRateWindow;
    
    // Weather settings
    @Value("${weather.update.interval}")
    private long weatherUpdateInterval;
    
    @Value("${weather.cache.duration}")
    private long weatherCacheDuration;
    
    // Location settings
    @Value("${location.cache.size}")
    private int locationCacheSize;
    
    @Value("${location.cache.duration}")
    private long locationCacheDuration;
    
    // Analytics settings
    @Value("${analytics.report.interval}")
    private long analyticsReportInterval;
    
    @Value("${analytics.data.retention}")
    private long analyticsDataRetention;
    
    // Payment settings
    @Value("${payment.currency}")
    private String paymentCurrency;
    
    @Value("${payment.retry.max}")
    private int paymentRetryMax;
    
    @Value("${payment.retry.delay}")
    private long paymentRetryDelay;
    
    // Venue settings
    @Value("${venue.maintenance.check.interval}")
    private long maintenanceCheckInterval;
    
    @Value("${venue.capacity.buffer}")
    private int venueCapacityBuffer;
    
    @Value("${venue.booking.min.hours}")
    private int bookingMinHours;
    
    @Value("${venue.booking.max.days}")
    private int bookingMaxDays;
    
    // Dynamic pricing settings
    @Value("${pricing.peak.start}")
    private int pricingPeakStart;
    
    @Value("${pricing.peak.end}")
    private int pricingPeakEnd;
    
    @Value("${pricing.peak.multiplier}")
    private double pricingPeakMultiplier;
    
    @Value("${pricing.off.peak.multiplier}")
    private double pricingOffPeakMultiplier;
    
    @Value("${pricing.weekend.multiplier}")
    private double pricingWeekendMultiplier;

    // Getters for all properties
    public String getWeatherApiKey() { return weatherApiKey; }
    public String getWeatherApiUrl() { return weatherApiUrl; }
    public String getMapboxApiKey() { return mapboxApiKey; }
    public String getMapboxApiUrl() { return mapboxApiUrl; }
    public String getStripeApiKey() { return stripeApiKey; }
    public String getStripeWebhookSecret() { return stripeWebhookSecret; }
    public int getApiRateLimit() { return apiRateLimit; }
    public int getApiRateWindow() { return apiRateWindow; }
    public long getWeatherUpdateInterval() { return weatherUpdateInterval; }
    public long getWeatherCacheDuration() { return weatherCacheDuration; }
    public int getLocationCacheSize() { return locationCacheSize; }
    public long getLocationCacheDuration() { return locationCacheDuration; }
    public long getAnalyticsReportInterval() { return analyticsReportInterval; }
    public long getAnalyticsDataRetention() { return analyticsDataRetention; }
    public String getPaymentCurrency() { return paymentCurrency; }
    public int getPaymentRetryMax() { return paymentRetryMax; }
    public long getPaymentRetryDelay() { return paymentRetryDelay; }
    public long getMaintenanceCheckInterval() { return maintenanceCheckInterval; }
    public int getVenueCapacityBuffer() { return venueCapacityBuffer; }
    public int getBookingMinHours() { return bookingMinHours; }
    public int getBookingMaxDays() { return bookingMaxDays; }
    public int getPricingPeakStart() { return pricingPeakStart; }
    public int getPricingPeakEnd() { return pricingPeakEnd; }
    public double getPricingPeakMultiplier() { return pricingPeakMultiplier; }
    public double getPricingOffPeakMultiplier() { return pricingOffPeakMultiplier; }
    public double getPricingWeekendMultiplier() { return pricingWeekendMultiplier; }

    @Bean
    public Environment environment(Environment environment) {
        return environment;
    }
} 