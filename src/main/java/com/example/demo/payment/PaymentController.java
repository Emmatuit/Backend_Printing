package com.example.demo.payment;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Repository.OrderRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.CheckoutService;
import com.example.demo.model.Order;
import com.example.demo.model.UserEntity;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private final FlutterwavePaymentService paymentService;
    private final CheckoutService checkoutService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    public PaymentController(FlutterwavePaymentService paymentService, CheckoutService checkoutService) {
        this.paymentService = paymentService;
        this.checkoutService = checkoutService;
    }

    @PostMapping("/initiate/{orderNumber}")
    public ResponseEntity<?> initiatePayment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("orderNumber") String orderNumber) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not authenticated"));
        }

        String email = userDetails.getUsername();
        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Email not found"));
        }

        Order order = checkoutService.getOrderByOrderNumber(orderNumber);

        if (!order.getEmail().equalsIgnoreCase(optionalUser.get().getEmail())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You do not own this order"));
        }

        PaymentResponse paymentResponse = paymentService.initializePayment(order);

        return ResponseEntity.ok(paymentResponse);
    }

    @GetMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyPayment(
            @RequestParam("tx_ref") String txRef,
            @RequestParam("transaction_id") String transactionId,
            @RequestParam("status") String status
    ) {
        logger.info("Verifying payment for tx_ref: {}, transactionId: {}, status: {}", txRef, transactionId, status);
        
        

        try {
            if (txRef == null || !txRef.contains("-")) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "ERROR",
                        "message", "Invalid tx_ref format"
                ));
            }

            String[] parts = txRef.split("-");
            if (parts.length < 2) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "ERROR",
                        "message", "Invalid tx_ref parts"
                ));
            }

            String orderNumber = parts[0] + "-" + parts[1];

            Order order = checkoutService.getOrderByOrderNumber(orderNumber);

            if (order == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "ERROR",
                        "message", "Order not found for tx_ref: " + txRef
                ));
            }

            if ("failed".equalsIgnoreCase(status)) {
                checkoutService.markOrderAsFailed(orderNumber);
                return ResponseEntity.ok(Map.of(
                        "status", "FAILED",
                        "message", "Payment FAILED from redirect param"
                ));
            }

            TransactionVerificationResponse verifyResponse = paymentService.verifyTransaction(transactionId);
            logger.info("Full Flutterwave verifyResponse JSON: {}", new ObjectMapper().writeValueAsString(verifyResponse));


            if (verifyResponse == null || verifyResponse.getData() == null) {
                logger.error("Invalid verify response or data is null");
                return ResponseEntity.status(500).body(Map.of(
                        "status", "ERROR",
                        "message", "Invalid verification response from Flutterwave"
                ));
            }

            TransactionVerificationResponse.DataPayload data = verifyResponse.getData();

            logger.info("Flutterwave verify response: id={}, status={}, amount={}, created_at={}, paymentType={}",
                    data.getId(), data.getStatus(), data.getAmount(), data.getCreated_at(), data.getPayment_type());

            if ("successful".equalsIgnoreCase(data.getStatus())) {
                String paymentType = data.getPayment_type() != null ? data.getPayment_type() : "Unknown";
                String last4 = (data.getCard() != null && data.getCard().getLast4digits() != null)
                	    ? data.getCard().getLast4digits()
                	    : "****";


                order.setPaymentMethod(paymentType);
                order.setCardLast4(last4);
                orderRepository.save(order);

                checkoutService.markOrderAsPaid(orderNumber, transactionId);

                return ResponseEntity.ok(Map.of(
                        "status", "SUCCESS",
                        "message", "Payment Verified & Order marked as PAID",
                        "paymentId", data.getId(),
                        "tx_ref", txRef,
                        "amount", data.getAmount(),
                        "currency", data.getCurrency(),
                        "created_at", data.getCreated_at(),
                        "payment_type", paymentType,
                        "card_last4", last4
                ));

            } else if ("failed".equalsIgnoreCase(data.getStatus())) {
                checkoutService.markOrderAsFailed(orderNumber);
                return ResponseEntity.ok(Map.of(
                        "status", "FAILED",
                        "message", "Payment Verified as FAILED",
                        "paymentId", data.getId(),
                        "tx_ref", txRef
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                        "status", "PENDING",
                        "message", "Payment not successful yet. Current status: " + data.getStatus(),
                        "paymentId", data.getId(),
                        "tx_ref", txRef
                ));
            }

        } catch (Exception e) {
            logger.error("Error verifying payment", e);
            return ResponseEntity.status(500).body(Map.of(
                    "status", "ERROR",
                    "message", "Error verifying payment: " + e.getMessage()
            ));
        }
    }

}
