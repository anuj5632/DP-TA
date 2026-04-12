package com.fooddelivery.services;

import com.fooddelivery.models.Order;
import com.fooddelivery.models.OrderItem;

public class BillService {
    public String generateBill(Order order) {
        StringBuilder builder = new StringBuilder();
        builder.append("\n================ FINAL BILL ================\n");
        builder.append("Order ID : ").append(order.getOrderId()).append("\n");
        builder.append("Customer : ").append(order.getCustomerName()).append("\n");
        builder.append("Status   : ").append(order.getStatus()).append("\n");
        builder.append("--------------------------------------------\n");

        int line = 1;
        for (OrderItem item : order.getItems()) {
            builder.append(line++)
                    .append(". ")
                    .append(item.getFoodItem().getDescription())
                    .append(" x")
                    .append(item.getQuantity())
                    .append(" => Rs. ")
                    .append(String.format("%.2f", item.getSubTotal()))
                    .append("\n");
        }

        builder.append("--------------------------------------------\n");
        builder.append("TOTAL    : Rs. ").append(String.format("%.2f", order.getTotalCost())).append("\n");
        builder.append("============================================\n");
        return builder.toString();
    }
}
