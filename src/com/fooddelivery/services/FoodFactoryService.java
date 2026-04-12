package com.fooddelivery.services;

import com.fooddelivery.models.MenuItemDefinition;
import com.fooddelivery.models.MenuItemType;
import com.fooddelivery.patterns.structural.Burger;
import com.fooddelivery.patterns.structural.FoodItem;
import com.fooddelivery.patterns.structural.Pizza;
import com.fooddelivery.patterns.structural.Sandwich;

public class FoodFactoryService {
    public FoodItem createBaseFood(MenuItemDefinition definition) {
        MenuItemType type = definition.getType();
        switch (type) {
            case PIZZA:
                return new Pizza(definition.getBasePrice());
            case BURGER:
                return new Burger(definition.getBasePrice());
            case SANDWICH:
                return new Sandwich(definition.getBasePrice());
            default:
                throw new IllegalArgumentException("Unsupported menu item type: " + type);
        }
    }
}
