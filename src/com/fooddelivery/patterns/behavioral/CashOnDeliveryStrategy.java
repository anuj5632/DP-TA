package com.fooddelivery.patterns.behavioral;

public class CashOnDeliveryStrategy implements PaymentStrategy {
    @Override
    public String getPaymentMethod() {
        return "Cash on Delivery";
    }

    @Override
    public PaymentResult pay(double amount) {
        return new PaymentResult(true,
                String.format("Order placed with Cash on Delivery. Amount payable at doorstep: Rs. %.2f", amount));
    }
}
