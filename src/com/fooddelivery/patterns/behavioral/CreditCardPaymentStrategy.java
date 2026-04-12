package com.fooddelivery.patterns.behavioral;

public class CreditCardPaymentStrategy implements PaymentStrategy {
    private final String maskedCardNumber;

    public CreditCardPaymentStrategy(String cardNumber) {
        String normalized = cardNumber.replaceAll("\\s+", "");
        if (normalized.length() >= 4) {
            this.maskedCardNumber = "****-****-****-" + normalized.substring(normalized.length() - 4);
        } else {
            this.maskedCardNumber = "****";
        }
    }

    @Override
    public String getPaymentMethod() {
        return "Credit Card";
    }

    @Override
    public PaymentResult pay(double amount) {
        return new PaymentResult(true,
                String.format("Card payment of Rs. %.2f successful using card %s", amount, maskedCardNumber));
    }
}
