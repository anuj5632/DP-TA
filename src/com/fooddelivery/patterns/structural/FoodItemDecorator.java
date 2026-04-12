package com.fooddelivery.patterns.structural;

public abstract class FoodItemDecorator implements FoodItem {
    protected final FoodItem foodItem;

    protected FoodItemDecorator(FoodItem foodItem) {
        this.foodItem = foodItem;
    }

    @Override
    public double getCost() {
        return foodItem.getCost();
    }

    @Override
    public String getDescription() {
        return foodItem.getDescription();
    }
}
