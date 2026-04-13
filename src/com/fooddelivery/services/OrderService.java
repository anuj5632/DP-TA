package com.fooddelivery.services;

import com.fooddelivery.controllers.OrderController;
import com.fooddelivery.dto.CreateOrderRequest;
import com.fooddelivery.dto.OrderItemResponse;
import com.fooddelivery.dto.OrderResponse;
import com.fooddelivery.dto.OrderLineRequest;
import com.fooddelivery.models.CustomizationRequest;
import com.fooddelivery.models.MenuItemDefinition;
import com.fooddelivery.models.MenuItemType;
import com.fooddelivery.models.Order;
import com.fooddelivery.models.OrderItem;
import com.fooddelivery.models.OrderStatus;
import com.fooddelivery.patterns.behavioral.PaymentResult;
import com.fooddelivery.patterns.behavioral.PaymentStrategy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Application-level order flow for REST: resolve menu lines, apply decorators via existing services,
 * calculate totals, and process payment using the Strategy pattern.
 */
@Service
public class OrderService {
    private final OrderController orderController;
    private final MenuService menuService;
    private final CustomizationRequestFactory customizationRequestFactory;
    private final PaymentStrategyFactory paymentStrategyFactory;

    public OrderService(OrderController orderController,
                        MenuService menuService,
                        CustomizationRequestFactory customizationRequestFactory,
                        PaymentStrategyFactory paymentStrategyFactory) {
        this.orderController = orderController;
        this.menuService = menuService;
        this.customizationRequestFactory = customizationRequestFactory;
        this.paymentStrategyFactory = paymentStrategyFactory;
    }

    public OrderResponse placeOrder(CreateOrderRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("At least one item is required");
        }

        String customer = request.getCustomerName() != null && !request.getCustomerName().isBlank()
                ? request.getCustomerName().trim()
                : "Guest";

        Order order = orderController.createOrder(customer);

        for (OrderLineRequest line : request.getItems()) {
            MenuItemType type = parseType(line.getType());
            MenuItemDefinition definition = menuService
                    .findByTypeAndName(type, line.getName())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Unknown menu item: " + line.getType() + " / " + line.getName()));

            CustomizationRequest customization = customizationRequestFactory.fromExtras(line.getExtras());
            orderController.addItemToOrder(order, definition, customization, 1);
        }

        PaymentStrategy strategy = paymentStrategyFactory.create(
                request.getPaymentMethod(),
                request.getPaymentReference());
        PaymentResult paymentResult = orderController.checkout(order, strategy);

        if (!paymentResult.isSuccessful()) {
            throw new IllegalStateException(paymentResult.getMessage());
        }

        return toResponse(order);
    }

    private static MenuItemType parseType(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Item type is required");
        }
        try {
            return MenuItemType.valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unsupported item type: " + raw);
        }
    }

    private static OrderResponse toResponse(Order order) {
        List<OrderItemResponse> lines = new ArrayList<>();
        for (OrderItem item : order.getItems()) {
            lines.add(new OrderItemResponse(
                    item.getFoodItem().getDescription(),
                    item.getQuantity(),
                    roundMoney(item.getSubTotal())));
        }
        return new OrderResponse(
                order.getOrderId(),
                lines,
                roundMoney(order.getTotalCost()),
                order.getStatus() == OrderStatus.PLACED ? "PLACED" : order.getStatus().name());
    }

    private static double roundMoney(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
