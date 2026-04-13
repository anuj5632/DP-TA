package com.fooddelivery.services;

import com.fooddelivery.models.CustomizationRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CustomizationRequestFactory {
    private final ToppingCatalogService toppingCatalogService;

    public CustomizationRequestFactory(ToppingCatalogService toppingCatalogService) {
        this.toppingCatalogService = toppingCatalogService;
    }

    /**
     * Maps API {@code extras} strings to decorator-driving {@link CustomizationRequest}.
     * Recognizes extra cheese / extra sauce synonyms; other strings become priced toppings.
     */
    public CustomizationRequest fromExtras(List<String> extras) {
        if (extras == null || extras.isEmpty()) {
            return CustomizationRequest.empty();
        }

        boolean extraCheese = false;
        boolean extraSauce = false;
        List<CustomizationRequest.NamedPriceOption> toppings = new ArrayList<>();

        for (String raw : extras) {
            if (raw == null || raw.isBlank()) {
                continue;
            }
            String label = raw.trim();
            String norm = ToppingCatalogService.normalize(label);

            if (isExtraCheese(norm)) {
                extraCheese = true;
                continue;
            }
            if (isExtraSauce(norm)) {
                extraSauce = true;
                continue;
            }

            double price = toppingCatalogService
                    .priceFor(label)
                    .orElseThrow(() -> new IllegalArgumentException("Unknown extra: " + label));
            toppings.add(new CustomizationRequest.NamedPriceOption(label, price));
        }

        return new CustomizationRequest(extraCheese, extraSauce, toppings, Collections.emptyList());
    }

    private static boolean isExtraCheese(String normalized) {
        return normalized.equals("extra cheese")
                || normalized.equals("cheese")
                || normalized.equals("double cheese");
    }

    private static boolean isExtraSauce(String normalized) {
        return normalized.equals("extra sauce")
                || normalized.equals("sauce")
                || normalized.equals("extra sauces");
    }
}
