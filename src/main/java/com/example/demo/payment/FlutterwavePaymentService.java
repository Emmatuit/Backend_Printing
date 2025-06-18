package com.example.demo.payment;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.Repository.OrderRepository;
import com.example.demo.model.Order;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FlutterwavePaymentService {

    private static final Logger logger = LoggerFactory.getLogger(FlutterwavePaymentService.class);

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private OrderRepository orderRepository;

    @Value("${flutterwave.secret.key}")
    private String FLW_SECRET_KEY;

    @Value("${flutterwave.base.url}")
    private String FLW_BASE_URL;

    @Value("${flutterwave.redirect.url}")
    private String FLW_REDIRECT_URL;

    // Initialize Payment - returns payment link
    public PaymentResponse initializePayment(Order order) {

        String url = FLW_BASE_URL + "/payments";

        String txRef = order.getOrderNumber() + "-" + UUID.randomUUID();
        order.setTxRef(txRef);
        orderRepository.save(order);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("tx_ref", txRef);
        requestBody.put("amount", order.getGrandTotal().toPlainString());
        requestBody.put("currency", "NGN");
        requestBody.put("redirect_url", FLW_REDIRECT_URL);

        Map<String, String> customer = new HashMap<>();
        customer.put("email", order.getEmail());
        customer.put("name", order.getFullName());

        requestBody.put("customer", customer);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(FLW_SECRET_KEY);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        try {
            Map<String, Object> responseMap = objectMapper.readValue(response.getBody(), Map.class);
            Map<String, Object> data = (Map<String, Object>) responseMap.get("data");

            String paymentLink = (String) data.get("link");

            PaymentResponse paymentResponse = new PaymentResponse();
            PaymentResponse.DataPayload dataPayload = new PaymentResponse.DataPayload();
            dataPayload.setPaymentlink(paymentLink);
            paymentResponse.setData(dataPayload);

            logger.info("Generated payment link: {}", paymentLink);

            return paymentResponse;

        } catch (Exception e) {
            logger.error("Error parsing Flutterwave initialize payment response", e);
            throw new RuntimeException("Failed to initialize Flutterwave payment", e);
        }
    }

    // Verify transaction from Flutterwave webhook
    public TransactionVerificationResponse verifyTransaction(String transactionId) {

        String url = FLW_BASE_URL + "/transactions/" + transactionId + "/verify";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(FLW_SECRET_KEY);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<TransactionVerificationResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                TransactionVerificationResponse.class
        );

        return response.getBody();
    }
}
