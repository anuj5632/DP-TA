package com.fooddelivery.patterns.creational;

import com.fooddelivery.models.Order;
import com.fooddelivery.services.LoggingService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class OrderManager {
    private static volatile OrderManager instance;
    private static final Object LOCK = new Object();

    private final Map<String, Order> orders;
    private final LoggingService logger;

    private OrderManager() {
        this.orders = new ConcurrentHashMap<>();
        this.logger = LoggingService.getInstance();
    }

    public static OrderManager getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new OrderManager();
                }
            }
        }
        return instance;
    }

    public void addOrder(Order order) {
        orders.put(order.getOrderId(), order);
        logger.info("Order registered in OrderManager: " + order.getOrderId());
    }

    public Optional<Order> findOrder(String orderId) {
        return Optional.ofNullable(orders.get(orderId));
    }

    public List<Order> listAllOrders() {
        return new ArrayList<>(orders.values());
    }

    public boolean removeOrder(String orderId) {
        return orders.remove(orderId) != null;
    }

    public int getActiveOrderCount() {
        return orders.size();
    }
}
