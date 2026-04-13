package com.fooddelivery.services;

import com.fooddelivery.patterns.behavioral.CashOnDeliveryStrategy;
import com.fooddelivery.patterns.behavioral.CreditCardPaymentStrategy;
import com.fooddelivery.patterns.behavioral.PaymentStrategy;
import com.fooddelivery.patterns.behavioral.UpiPaymentStrategy;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class PaymentStrategyFactory {

    public PaymentStrategy create(String paymentMethod, String paymentReference) {
        if (paymentMethod == null || paymentMethod.isBlank()) {
            throw new IllegalArgumentException("paymentMethod is required");
        }
        String key = paymentMethod.trim().toUpperCase(Locale.ROOT).replace(' ', '_');
        return switch (key) {
            case "UPI" -> new UpiPaymentStrategy(
                    (paymentReference != null && !paymentReference.isBlank())
                            ? paymentReference.trim()
                            : "guest@upi");
            case "CREDIT_CARD", "CARD", "CREDITCARD" -> new CreditCardPaymentStrategy(
                    (paymentReference != null && !paymentReference.isBlank())
                            ? paymentReference.trim()
                            : "4111111111111234");
            case "COD", "CASH_ON_DELIVERY", "CASHONDELIVERY" -> new CashOnDeliveryStrategy();
            default -> throw new IllegalArgumentException("Unsupported paymentMethod: " + paymentMethod);
        };
    }
}
