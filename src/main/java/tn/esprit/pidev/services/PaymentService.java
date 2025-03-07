package tn.esprit.pidev.services;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import java.util.HashMap;
import java.util.Map;

public class PaymentService {
    private static final String STRIPE_SECRET_KEY = "YOUR_STRIPE_SECRET_KEY"; // Replace with your Stripe secret key

    public PaymentService() {
        Stripe.apiKey = STRIPE_SECRET_KEY;
    }

    public PaymentIntent createPaymentIntent(long amount, String currency, String description) {
        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount) // amount in cents
                .setCurrency(currency)
                .setDescription(description)
                .setAutomaticPaymentMethods(
                    PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                        .setEnabled(true)
                        .build()
                )
                .build();

            return PaymentIntent.create(params);
        } catch (Exception e) {
            throw new RuntimeException("Error creating payment intent: " + e.getMessage());
        }
    }

    public PaymentIntent retrievePaymentIntent(String paymentIntentId) {
        try {
            return PaymentIntent.retrieve(paymentIntentId);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving payment intent: " + e.getMessage());
        }
    }

    public Refund createRefund(String paymentIntentId, Long amount) {
        try {
            RefundCreateParams params = RefundCreateParams.builder()
                .setPaymentIntent(paymentIntentId)
                .setAmount(amount) // Optional: partial refund if specified
                .build();

            return Refund.create(params);
        } catch (Exception e) {
            throw new RuntimeException("Error creating refund: " + e.getMessage());
        }
    }

    public Map<String, Object> createSubscription(String customerId, String priceId) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("customer", customerId);
            params.put("items", new Object[]{
                new HashMap<String, Object>() {{
                    put("price", priceId);
                }}
            });
            params.put("payment_behavior", "default_incomplete");
            params.put("expand", new String[]{"latest_invoice.payment_intent"});

            return new HashMap<String, Object>() {{
                put("subscription", com.stripe.model.Subscription.create(params));
            }};
        } catch (Exception e) {
            throw new RuntimeException("Error creating subscription: " + e.getMessage());
        }
    }

    public boolean validatePayment(String paymentIntentId) {
        try {
            PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);
            return "succeeded".equals(intent.getStatus());
        } catch (Exception e) {
            throw new RuntimeException("Error validating payment: " + e.getMessage());
        }
    }

    // Calculate prices for different membership tiers
    public Map<String, Long> calculateMembershipPrices() {
        return new HashMap<String, Long>() {{
            put("bronze", 2000L);   // $20.00
            put("silver", 4000L);   // $40.00
            put("gold", 6000L);     // $60.00
            put("platinum", 8000L); // $80.00
        }};
    }

    // Calculate dynamic pricing based on time and demand
    public long calculateDynamicPrice(long basePrice, int hour, double demandMultiplier) {
        // Peak hours (17:00-22:00) get a premium
        double timeMultiplier = (hour >= 17 && hour < 22) ? 1.3 : 1.0;
        
        // Early bird discount (06:00-08:00)
        if (hour >= 6 && hour < 8) {
            timeMultiplier = 0.7;
        }

        // Apply multipliers
        return Math.round(basePrice * timeMultiplier * demandMultiplier);
    }

    // Handle package deals
    public long calculatePackagePrice(String packageType, int sessions) {
        long basePrice = 2000L; // $20.00 per session
        double discount;
        
        switch (packageType) {
            case "10_sessions":
                discount = 0.15; // 15% off
                break;
            case "monthly":
                discount = 0.20; // 20% off
                break;
            case "quarterly":
                discount = 0.25; // 25% off
                break;
            default:
                discount = 0.0;
                break;
        }

        return Math.round(basePrice * sessions * (1 - discount));
    }
} 