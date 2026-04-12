package com.fooddelivery.patterns.structural;

public class ExtraCheeseDecorator extends FoodItemDecorator {
    private static final double EXTRA_CHEESE_COST = 1.50;

    public ExtraCheeseDecorator(FoodItem foodItem) {
        super(foodItem);
    }

    @Override
    public double getCost() {
        return super.getCost() + EXTRA_CHEESE_COST;
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + Extra Cheese";
    }
}
