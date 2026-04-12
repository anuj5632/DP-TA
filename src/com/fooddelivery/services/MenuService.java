package com.fooddelivery.services;

import com.fooddelivery.models.MenuItemDefinition;
import com.fooddelivery.models.MenuItemType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MenuService {
    private final List<MenuItemDefinition> menu;

    public MenuService() {
        List<MenuItemDefinition> menuData = new ArrayList<>();
        menuData.add(new MenuItemDefinition("M1", "Margherita Pizza", MenuItemType.PIZZA, 8.00));
        menuData.add(new MenuItemDefinition("M2", "Crispy Veg Burger", MenuItemType.BURGER, 6.50));
        menuData.add(new MenuItemDefinition("M3", "Grilled Paneer Sandwich", MenuItemType.SANDWICH, 5.75));
        this.menu = Collections.unmodifiableList(menuData);
    }

    public List<MenuItemDefinition> getMenu() {
        return menu;
    }

    public Optional<MenuItemDefinition> findByCode(String code) {
        return menu.stream().filter(item -> item.getCode().equalsIgnoreCase(code)).findFirst();
    }
}
