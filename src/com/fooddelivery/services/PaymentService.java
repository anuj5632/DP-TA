package com.fooddelivery.services;

import com.fooddelivery.models.Order;
import com.fooddelivery.models.OrderStatus;
import com.fooddelivery.patterns.behavioral.PaymentResult;
import com.fooddelivery.patterns.behavioral.PaymentStrategy;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    private final LoggingService logger;

    public PaymentService() {
        this.logger = LoggingService.getInstance();
    }

    public PaymentResult processPayment(Order order, PaymentStrategy paymentStrategy) {
        if (order.getItems().isEmpty()) {
            order.setStatus(OrderStatus.FAILED);
            return new PaymentResult(false, "Cannot process payment for an empty order");
        }

        order.setPaymentStrategy(paymentStrategy);
        order.setStatus(OrderStatus.PAYMENT_PENDING);

        logger.info("Processing payment for order " + order.getOrderId()
                + " using strategy: " + paymentStrategy.getPaymentMethod());

        PaymentResult result = paymentStrategy.pay(order.getTotalCost());
        if (result.isSuccessful()) {
            order.setStatus(OrderStatus.PLACED);
        } else {
            order.setStatus(OrderStatus.FAILED);
        }
        return result;
    }
}
