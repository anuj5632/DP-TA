package com.fooddelivery.patterns.structural;

public abstract class BaseFoodItem implements FoodItem {
    private final String name;
    private final double basePrice;

    protected BaseFoodItem(String name, double basePrice) {
        this.name = name;
        this.basePrice = basePrice;
    }

    @Override
    public double getCost() {
        return basePrice;
    }

    @Override
    public String getDescription() {
        return name;
    }
}
