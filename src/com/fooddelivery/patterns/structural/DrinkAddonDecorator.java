package com.fooddelivery.patterns.structural;

public class DrinkAddonDecorator extends FoodItemDecorator {
    private final String drinkName;
    private final double drinkCost;

    public DrinkAddonDecorator(FoodItem foodItem, String drinkName, double drinkCost) {
        super(foodItem);
        this.drinkName = drinkName;
        this.drinkCost = drinkCost;
    }

    @Override
    public double getCost() {
        return super.getCost() + drinkCost;
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + Drink(" + drinkName + ")";
    }
}
