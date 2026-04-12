package com.fooddelivery.controllers;

import com.fooddelivery.models.CustomizationRequest;
import com.fooddelivery.models.MenuItemDefinition;
import com.fooddelivery.models.Order;
import com.fooddelivery.models.OrderItem;
import com.fooddelivery.patterns.behavioral.PaymentResult;
import com.fooddelivery.patterns.behavioral.PaymentStrategy;
import com.fooddelivery.patterns.creational.OrderManager;
import com.fooddelivery.patterns.structural.FoodItem;
import com.fooddelivery.services.BillService;
import com.fooddelivery.services.CustomizationService;
import com.fooddelivery.services.FoodFactoryService;
import com.fooddelivery.services.LoggingService;
import com.fooddelivery.services.MenuService;
import com.fooddelivery.services.PaymentService;

import java.util.List;

public class OrderController {
    private final MenuService menuService;
    private final FoodFactoryService foodFactoryService;
    private final CustomizationService customizationService;
    private final PaymentService paymentService;
    private final BillService billService;
    private final OrderManager orderManager;
    private final LoggingService logger;

    public OrderController(MenuService menuService,
                           FoodFactoryService foodFactoryService,
                           CustomizationService customizationService,
                           PaymentService paymentService,
                           BillService billService) {
        this.menuService = menuService;
        this.foodFactoryService = foodFactoryService;
        this.customizationService = customizationService;
        this.paymentService = paymentService;
        this.billService = billService;
        this.orderManager = OrderManager.getInstance();
        this.logger = LoggingService.getInstance();
    }

    public List<MenuItemDefinition> browseMenu() {
        logger.info("User is browsing menu");
        return menuService.getMenu();
    }

    public Order createOrder(String customerName) {
        Order order = new Order(customerName);
        orderManager.addOrder(order);
        logger.info("Created new order for customer: " + customerName + " (" + order.getOrderId() + ")");
        return order;
    }

    public void addItemToOrder(Order order, MenuItemDefinition menuItem,
                               CustomizationRequest customizationRequest, int quantity) {
        logger.info("Item selected: " + menuItem.getDisplayName() + " (Base price Rs. "
                + String.format("%.2f", menuItem.getBasePrice()) + ")");

        FoodItem baseItem = foodFactoryService.createBaseFood(menuItem);
        FoodItem customized = customizationService.applyCustomizations(baseItem, customizationRequest);
        logger.info("Decorators applied: " + customized.getDescription());

        order.addItem(new OrderItem(customized, quantity));
        logger.info("Added item to order. Quantity: " + quantity + ", Current total: Rs. "
                + String.format("%.2f", order.getTotalCost()));
    }

    public boolean removeItemFromOrder(Order order, int itemIndex) {
        boolean removed = order.removeItem(itemIndex);
        if (removed) {
            logger.warn("Removed item at index " + itemIndex + " from order " + order.getOrderId());
        } else {
            logger.warn("Unable to remove item at index " + itemIndex + " from order " + order.getOrderId());
        }
        return removed;
    }

    public PaymentResult checkout(Order order, PaymentStrategy paymentStrategy) {
        logger.info("Strategy chosen: " + paymentStrategy.getPaymentMethod());
        PaymentResult result = paymentService.processPayment(order, paymentStrategy);
        logger.info(result.getMessage());
        return result;
    }

    public String generateBill(Order order) {
        return billService.generateBill(order);
    }
}
