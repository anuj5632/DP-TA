package com.fooddelivery.dto;

import java.util.List;

public class OrderResponse {
    private String orderId;
    private List<OrderItemResponse> items;
    private double totalAmount;
    private String status;

    public OrderResponse() {
    }

    public OrderResponse(String orderId, List<OrderItemResponse> items, double totalAmount, String status) {
        this.orderId = orderId;
        this.items = items;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public List<OrderItemResponse> getItems() {
        return items;
    }

    public void setItems(List<OrderItemResponse> items) {
        this.items = items;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
