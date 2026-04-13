package com.fooddelivery.services;

import com.fooddelivery.models.CustomizationRequest;
import com.fooddelivery.patterns.structural.DrinkAddonDecorator;
import com.fooddelivery.patterns.structural.ExtraCheeseDecorator;
import com.fooddelivery.patterns.structural.ExtraSauceDecorator;
import com.fooddelivery.patterns.structural.FoodItem;
import com.fooddelivery.patterns.structural.ToppingDecorator;
import org.springframework.stereotype.Service;

@Service
public class CustomizationService {
    public FoodItem applyCustomizations(FoodItem baseFoodItem, CustomizationRequest customizationRequest) {
        FoodItem result = baseFoodItem;

        if (customizationRequest.isExtraCheese()) {
            result = new ExtraCheeseDecorator(result);
        }

        if (customizationRequest.isExtraSauce()) {
            result = new ExtraSauceDecorator(result);
        }

        for (CustomizationRequest.NamedPriceOption topping : customizationRequest.getToppings()) {
            result = new ToppingDecorator(result, topping.getName(), topping.getPrice());
        }

        for (CustomizationRequest.NamedPriceOption drink : customizationRequest.getDrinks()) {
            result = new DrinkAddonDecorator(result, drink.getName(), drink.getPrice());
        }

        return result;
    }
}
