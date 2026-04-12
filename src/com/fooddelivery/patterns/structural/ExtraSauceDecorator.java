package com.fooddelivery.patterns.structural;

public class ExtraSauceDecorator extends FoodItemDecorator {
    private static final double EXTRA_SAUCE_COST = 0.75;

    public ExtraSauceDecorator(FoodItem foodItem) {
        super(foodItem);
    }

    @Override
    public double getCost() {
        return super.getCost() + EXTRA_SAUCE_COST;
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + Extra Sauce";
    }
}
