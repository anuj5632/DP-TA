package com.fooddelivery.patterns.structural;

public class ToppingDecorator extends FoodItemDecorator {
    private final String toppingName;
    private final double toppingCost;

    public ToppingDecorator(FoodItem foodItem, String toppingName, double toppingCost) {
        super(foodItem);
        this.toppingName = toppingName;
        this.toppingCost = toppingCost;
    }

    @Override
    public double getCost() {
        return super.getCost() + toppingCost;
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + " + toppingName;
    }
}
