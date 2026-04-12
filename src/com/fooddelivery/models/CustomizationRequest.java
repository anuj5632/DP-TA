package com.fooddelivery.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomizationRequest {
    private boolean extraCheese;
    private boolean extraSauce;
    private final List<NamedPriceOption> toppings;
    private final List<NamedPriceOption> drinks;

    public CustomizationRequest(boolean extraCheese, boolean extraSauce,
                                List<NamedPriceOption> toppings,
                                List<NamedPriceOption> drinks) {
        this.extraCheese = extraCheese;
        this.extraSauce = extraSauce;
        this.toppings = new ArrayList<>(toppings);
        this.drinks = new ArrayList<>(drinks);
    }

    public static CustomizationRequest empty() {
        return new CustomizationRequest(false, false, List.of(), List.of());
    }

    public boolean isExtraCheese() {
        return extraCheese;
    }

    public boolean isExtraSauce() {
        return extraSauce;
    }

    public List<NamedPriceOption> getToppings() {
        return Collections.unmodifiableList(toppings);
    }

    public List<NamedPriceOption> getDrinks() {
        return Collections.unmodifiableList(drinks);
    }

    public static class NamedPriceOption {
        private final String name;
        private final double price;

        public NamedPriceOption(String name, double price) {
            this.name = name;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }
    }
}
