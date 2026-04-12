package com.fooddelivery.patterns.behavioral;

public class UpiPaymentStrategy implements PaymentStrategy {
    private final String upiId;

    public UpiPaymentStrategy(String upiId) {
        this.upiId = upiId;
    }

    @Override
    public String getPaymentMethod() {
        return "UPI";
    }

    @Override
    public PaymentResult pay(double amount) {
        return new PaymentResult(true,
                String.format("UPI payment of Rs. %.2f successful using UPI ID: %s", amount, upiId));
    }
}
