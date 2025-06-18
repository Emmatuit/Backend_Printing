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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.CheckoutService;
import com.example.demo.model.Order;
import com.example.demo.model.UserEntity;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

	private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private final FlutterwavePaymentService paymentService;
    private final CheckoutService checkoutService;

    @Autowired
    private UserRepository userRepository;



    public PaymentController(FlutterwavePaymentService paymentService, CheckoutService checkoutService) {
		super();
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

        // Find order
        Order order = checkoutService.getOrderByOrderNumber(orderNumber);

        // OPTIONAL: Validate that this user owns this order
        if (!order.getEmail().equalsIgnoreCase(optionalUser.get().getEmail())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You do not own this order"));
        }

        // Initialize payment
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
            String[] parts = txRef.split("-");
            String orderNumber = parts[0] + "-" + parts[1];

            if ("failed".equalsIgnoreCase(status)) {
                checkoutService.markOrderAsFailed(orderNumber);
                return ResponseEntity.ok(Map.of(
                    "status", "FAILED",
                    "message", "Payment FAILED from redirect param"
                ));
            }

            TransactionVerificationResponse verifyResponse = paymentService.verifyTransaction(transactionId);
            TransactionVerificationResponse.DataPayload data = verifyResponse.getData(); // ✅ correct type

            logger.info("Flutterwave verify response: id={}, status={}, amount={}, created_at={}",
                data.getId(), data.getStatus(), data.getAmount(), data.getCreated_at());

            if ("successful".equalsIgnoreCase(data.getStatus())) {
                checkoutService.markOrderAsPaid(orderNumber, transactionId);
                return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "message", "Payment Verified & Order marked as PAID",
                    "paymentId", data.getId(),
                    "tx_ref", txRef,
                    "amount", data.getAmount(),
                    "currency", data.getCurrency(),
                    "created_at", data.getCreated_at()
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
