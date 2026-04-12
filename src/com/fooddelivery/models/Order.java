package com.fooddelivery.models;

import com.fooddelivery.patterns.behavioral.PaymentStrategy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Order {
    private final String orderId;
    private final String customerName;
    private final LocalDateTime createdAt;
    private final List<OrderItem> items;
    private OrderStatus status;
    private PaymentStrategy paymentStrategy;

    public Order(String customerName) {
        this.orderId = UUID.randomUUID().toString();
        this.customerName = customerName;
        this.createdAt = LocalDateTime.now();
        this.items = new ArrayList<>();
        this.status = OrderStatus.CREATED;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void addItem(OrderItem item) {
        items.add(item);
    }

    public boolean removeItem(int index) {
        if (index < 0 || index >= items.size()) {
            return false;
        }
        items.remove(index);
        return true;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public double getTotalCost() {
        return items.stream().mapToDouble(OrderItem::getSubTotal).sum();
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public PaymentStrategy getPaymentStrategy() {
        return paymentStrategy;
    }

    public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }
}
