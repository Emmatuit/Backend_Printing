package com.example.demo.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Repository.OrderRepository;
import com.example.demo.Service.CheckoutService;
import com.example.demo.model.Order;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/webhook")
public class WebhookController {

    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);

    private final CheckoutService checkoutService;
    private final OrderRepository orderRepository;

    public WebhookController(CheckoutService checkoutService, OrderRepository orderRepository) {
        this.checkoutService = checkoutService;
        this.orderRepository = orderRepository;
    }

    @PostMapping("/flutterwave")
    @Transactional
    public ResponseEntity<String> handleFlutterwaveWebhook(@RequestBody TransactionVerificationResponse webhookPayload) {

        logger.info("Received Flutterwave webhook: {}", webhookPayload);

        try {
            // Extract tx_ref (orderNumber) and flw_ref (Flutterwave transaction ID)
            String txRef = webhookPayload.getData().getTx_ref();
            String flwRef = webhookPayload.getData().getFlw_ref();
            String paymentStatus = webhookPayload.getData().getStatus();

            Order order = checkoutService.getOrderByTxRef(txRef);

            // Only mark as paid if payment was successful and order is not already paid
            if ("successful".equalsIgnoreCase(paymentStatus) &&
                order.getPaymentStatus() != Order.PaymentStatus.COMPLETED) {

            	checkoutService.markOrderAsPaid(order.getOrderNumber(), flwRef);
                logger.info("Order {} marked as PAID", order.getOrderNumber());
            } else {
                logger.warn("Payment not successful or order already paid for orderNumber: {}", txRef);
            }

            // Respond with 200 OK to Flutterwave
            return ResponseEntity.ok("Webhook received");

        } catch (Exception e) {
            logger.error("Error processing Flutterwave webhook", e);
            return ResponseEntity.status(500).body("Error processing webhook");
        }
    }
}
