package com.fooddelivery.api;

import com.fooddelivery.dto.CreateOrderRequest;
import com.fooddelivery.dto.OrderLineRequest;
import com.fooddelivery.dto.OrderResponse;
import com.fooddelivery.services.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/order")
public class OrderRestController {
    private final OrderService orderService;

    public OrderRestController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse placeOrder(@Valid @RequestBody CreateOrderRequest request) {
        System.out.println("========== NEW ORDER ==========");
        System.out.println("Received Order Request: " + formatCreateOrderRequest(request));
        OrderResponse response = orderService.placeOrder(request);
        System.out.println("Order processed successfully");
        return response;
    }

    private static String formatCreateOrderRequest(CreateOrderRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("{customerName=").append(request.getCustomerName() != null ? request.getCustomerName() : "null");
        sb.append(", paymentMethod=").append(request.getPaymentMethod());
        sb.append(", paymentReference=").append(request.getPaymentReference() != null ? request.getPaymentReference() : "null");
        sb.append(", items=[");
        if (request.getItems() != null) {
            for (int i = 0; i < request.getItems().size(); i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                OrderLineRequest line = request.getItems().get(i);
                sb.append("{type=").append(line.getType());
                sb.append(", name=").append(line.getName());
                sb.append(", extras=").append(line.getExtras() != null ? line.getExtras().toString() : "null");
                sb.append("}");
            }
        }
        sb.append("]}");
        return sb.toString();
    }
}
