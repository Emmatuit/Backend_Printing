package com.example.demo.payment;

import lombok.Data;

@Data
public class WebhookPayload {

    @Data
    public static class DataPayload {
        private Long id;
        private String txRef;
        private String status;
        private Double amount; // Or BigDecimal
        private String createdAt; // ISO 8601
        private String paymentType; // ✅ Add this!

        private Card card; // ✅ If paymentType == "card"

        // --- Card inner class ---
        @Data
        public static class Card {
            private String type;
            private String last4digits;
            private String issuer;

            public String getType() { return type; }
            public void setType(String type) { this.type = type; }

            public String getLast4digits() { return last4digits; }
            public void setLast4digits(String last4digits) { this.last4digits = last4digits; }

            public String getIssuer() { return issuer; }
            public void setIssuer(String issuer) { this.issuer = issuer; }
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getTxRef() { return txRef; }
        public void setTxRef(String txRef) { this.txRef = txRef; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }

        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

        public String getPaymentType() { return paymentType; }
        public void setPaymentType(String paymentType) { this.paymentType = paymentType; }

        public Card getCard() { return card; }
        public void setCard(Card card) { this.card = card; }
    }

    private DataPayload data;

    public WebhookPayload() {}

    public WebhookPayload(DataPayload data) {
        this.data = data;
    }

    public DataPayload getData() { return data; }
    public void setData(DataPayload data) { this.data = data; }
}
