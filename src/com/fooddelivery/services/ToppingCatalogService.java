package com.fooddelivery.services;

import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * Resolves add-on names from API requests to prices used by {@link com.fooddelivery.models.CustomizationRequest}
 * and {@link com.fooddelivery.patterns.structural.ToppingDecorator}.
 */
@Service
public class ToppingCatalogService {
    private final Map<String, Double> pricesByNormalizedName = new TreeMap<>();

    public ToppingCatalogService() {
        put("corn", 25);
        put("olives", 35);
        put("jalapenos", 30);
        put("jalapeño", 30);
        put("jalapeno", 30);
        put("caramelized onion", 40);
        put("sweet corn", 30);
        put("paneer cubes", 55);
        put("bacon", 60);
        put("pickles", 15);
        put("peri peri", 20);
        put("cheese sauce", 35);
        put("mayo", 20);
        put("mayo drizzle", 20);
    }

    private void put(String key, double price) {
        pricesByNormalizedName.put(normalize(key), price);
    }

    public Optional<Double> priceFor(String displayName) {
        return Optional.ofNullable(pricesByNormalizedName.get(normalize(displayName)));
    }

    public static String normalize(String name) {
        return name.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }
}
