package com.fooddelivery.models;

public class MenuItemDefinition {
    private final String code;
    private final String displayName;
    private final MenuItemType type;
    private final double basePrice;

    public MenuItemDefinition(String code, String displayName, MenuItemType type, double basePrice) {
        this.code = code;
        this.displayName = displayName;
        this.type = type;
        this.basePrice = basePrice;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public MenuItemType getType() {
        return type;
    }

    public double getBasePrice() {
        return basePrice;
    }
}
