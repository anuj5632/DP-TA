package com.fooddelivery.models;

import com.fooddelivery.patterns.structural.FoodItem;

public class OrderItem {
    private final FoodItem foodItem;
    private final int quantity;

    public OrderItem(FoodItem foodItem, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        this.foodItem = foodItem;
        this.quantity = quantity;
    }

    public FoodItem getFoodItem() {
        return foodItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getSubTotal() {
        return foodItem.getCost() * quantity;
    }
}
