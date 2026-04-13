package com.fooddelivery.dto;

public class MenuItemResponse {
    private String code;
    private String type;
    private String name;
    private double basePrice;

    public MenuItemResponse() {
    }

    public MenuItemResponse(String code, String type, String name, double basePrice) {
        this.code = code;
        this.type = type;
        this.name = name;
        this.basePrice = basePrice;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }
}
