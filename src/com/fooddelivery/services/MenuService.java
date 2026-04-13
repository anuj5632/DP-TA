package com.fooddelivery.services;

import com.fooddelivery.models.MenuItemDefinition;
import com.fooddelivery.models.MenuItemType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class MenuService {
    private final List<MenuItemDefinition> menu;

    public MenuService() {
        List<MenuItemDefinition> menuData = new ArrayList<>();
        // Legacy codes preserved for backward compatibility
        menuData.add(new MenuItemDefinition("M1", "Margherita Pizza", MenuItemType.PIZZA, 199));
        menuData.add(new MenuItemDefinition("M2", "Crispy Veg Burger", MenuItemType.BURGER, 149));
        menuData.add(new MenuItemDefinition("M3", "Grilled Paneer Sandwich", MenuItemType.SANDWICH, 129));

        // Example: Farmhouse + Corn + Olives = 230 + 25 + 35 = 290
        menuData.add(new MenuItemDefinition("P1", "Farmhouse Pizza", MenuItemType.PIZZA, 230));
        menuData.add(new MenuItemDefinition("P2", "Pepperoni Pizza", MenuItemType.PIZZA, 279));
        menuData.add(new MenuItemDefinition("P3", "Veggie Supreme Pizza", MenuItemType.PIZZA, 259));

        menuData.add(new MenuItemDefinition("B1", "Cheese Burger", MenuItemType.BURGER, 139));
        menuData.add(new MenuItemDefinition("B2", "Double Stack Burger", MenuItemType.BURGER, 189));

        menuData.add(new MenuItemDefinition("S1", "Club Sandwich", MenuItemType.SANDWICH, 159));

        this.menu = Collections.unmodifiableList(menuData);
    }

    public List<MenuItemDefinition> getMenu() {
        return menu;
    }

    public Optional<MenuItemDefinition> findByCode(String code) {
        return menu.stream()
                .filter(item -> item.getCode().equalsIgnoreCase(code))
                .findFirst();
    }

    /**
     * Resolves a menu row by type and a human-readable name (e.g. {@code Farmhouse} or {@code Farmhouse Pizza}).
     */
    public Optional<MenuItemDefinition> findByTypeAndName(MenuItemType type, String name) {
        if (name == null || name.isBlank()) {
            return Optional.empty();
        }
        String needle = normalize(name);
        return menu.stream()
                .filter(item -> item.getType() == type)
                .filter(item -> matchesName(item.getDisplayName(), needle))
                .findFirst();
    }

    private static boolean matchesName(String displayName, String normalizedNeedle) {
        String displayNorm = normalize(displayName);
        if (displayNorm.equals(normalizedNeedle)) {
            return true;
        }
        // "Farmhouse" matches "Farmhouse Pizza"
        return displayNorm.startsWith(normalizedNeedle)
                || displayNorm.contains(" " + normalizedNeedle)
                || displayNorm.endsWith(" " + normalizedNeedle);
    }

    private static String normalize(String value) {
        return value.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }
}
