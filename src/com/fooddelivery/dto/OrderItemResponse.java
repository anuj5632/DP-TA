package com.fooddelivery.dto;

public class OrderItemResponse {
    private String description;
    private int quantity;
    private double lineTotal;

    public OrderItemResponse() {
    }

    public OrderItemResponse(String description, int quantity, double lineTotal) {
        this.description = description;
        this.quantity = quantity;
        this.lineTotal = lineTotal;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(double lineTotal) {
        this.lineTotal = lineTotal;
    }
}
