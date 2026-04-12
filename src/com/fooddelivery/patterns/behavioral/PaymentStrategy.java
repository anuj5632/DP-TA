package com.fooddelivery.patterns.behavioral;

public interface PaymentStrategy {
    String getPaymentMethod();
    PaymentResult pay(double amount);
}
