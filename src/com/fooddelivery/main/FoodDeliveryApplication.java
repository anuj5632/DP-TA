package com.fooddelivery.main;

import com.fooddelivery.controllers.OrderController;
import com.fooddelivery.models.CustomizationRequest;
import com.fooddelivery.models.MenuItemDefinition;
import com.fooddelivery.models.Order;
import com.fooddelivery.patterns.behavioral.CashOnDeliveryStrategy;
import com.fooddelivery.patterns.behavioral.CreditCardPaymentStrategy;
import com.fooddelivery.patterns.behavioral.PaymentResult;
import com.fooddelivery.patterns.behavioral.UpiPaymentStrategy;
import com.fooddelivery.patterns.creational.OrderManager;
import com.fooddelivery.services.BillService;
import com.fooddelivery.services.CustomizationService;
import com.fooddelivery.services.FoodFactoryService;
import com.fooddelivery.services.LoggingService;
import com.fooddelivery.services.MenuService;
import com.fooddelivery.services.PaymentService;

import java.util.List;

public class FoodDeliveryApplication {
    public static void main(String[] args) {
        LoggingService logger = LoggingService.getInstance();

        MenuService menuService = new MenuService();
        FoodFactoryService foodFactoryService = new FoodFactoryService();
        CustomizationService customizationService = new CustomizationService();
        PaymentService paymentService = new PaymentService();
        BillService billService = new BillService();

        OrderController orderController = new OrderController(
                menuService,
                foodFactoryService,
                customizationService,
                paymentService,
                billService
        );

        logger.info("========== ONLINE FOOD DELIVERY SYSTEM STARTED ==========");
        printMenu(orderController.browseMenu());

        // Simulate order 1
        Order order1 = orderController.createOrder("Rahul Sharma");

        MenuItemDefinition pizza = menuService.findByCode("M1").orElseThrow();
        CustomizationRequest pizzaCustomization = new CustomizationRequest(
                true,
                true,
                List.of(
                        new CustomizationRequest.NamedPriceOption("Olives", 1.20),
                        new CustomizationRequest.NamedPriceOption("Jalapenos", 1.10)
                ),
                List.of(new CustomizationRequest.NamedPriceOption("Coke", 1.50))
        );
        orderController.addItemToOrder(order1, pizza, pizzaCustomization, 1);

        MenuItemDefinition burger = menuService.findByCode("M2").orElseThrow();
        CustomizationRequest burgerCustomization = new CustomizationRequest(
                false,
                true,
                List.of(new CustomizationRequest.NamedPriceOption("Caramelized Onion", 0.80)),
                List.of()
        );
        orderController.addItemToOrder(order1, burger, burgerCustomization, 2);

        PaymentResult order1Payment = orderController.checkout(order1, new UpiPaymentStrategy("rahul@upi"));
        if (order1Payment.isSuccessful()) {
            System.out.println(orderController.generateBill(order1));
        }

        // Simulate order 2 with add/remove flow and different strategy
        Order order2 = orderController.createOrder("Anita Verma");
        MenuItemDefinition sandwich = menuService.findByCode("M3").orElseThrow();

        orderController.addItemToOrder(order2, sandwich, CustomizationRequest.empty(), 1);
        orderController.removeItemFromOrder(order2, 0);

        CustomizationRequest sandwichCustomization = new CustomizationRequest(
                true,
                false,
                List.of(new CustomizationRequest.NamedPriceOption("Sweet Corn", 0.90)),
                List.of(new CustomizationRequest.NamedPriceOption("Lemon Soda", 1.30))
        );
        orderController.addItemToOrder(order2, sandwich, sandwichCustomization, 2);

        orderController.checkout(order2, new CreditCardPaymentStrategy("4111 1111 1111 1234"));
        System.out.println(orderController.generateBill(order2));

        // Simulate order 3 with COD strategy
        Order order3 = orderController.createOrder("Vikram Singh");
        orderController.addItemToOrder(order3, burger, CustomizationRequest.empty(), 1);
        orderController.checkout(order3, new CashOnDeliveryStrategy());
        System.out.println(orderController.generateBill(order3));

        logger.info("Active orders managed by Singleton OrderManager: " + OrderManager.getInstance().getActiveOrderCount());
        logger.info("========== SYSTEM EXECUTION COMPLETED ==========");
    }

    private static void printMenu(List<MenuItemDefinition> menu) {
        System.out.println("\n=============== MENU ===============");
        for (MenuItemDefinition item : menu) {
            System.out.printf("%s | %-25s | Rs. %.2f%n",
                    item.getCode(), item.getDisplayName(), item.getBasePrice());
        }
        System.out.println("====================================\n");
    }
}
