package com.fooddelivery.models;

public enum OrderStatus {
    CREATED,
    PAYMENT_PENDING,
    /** Successfully paid / confirmed (exposed as PLACED in API responses). */
    PLACED,
    FAILED
}
