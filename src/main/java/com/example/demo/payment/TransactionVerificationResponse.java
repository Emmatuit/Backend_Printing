package com.example.demo.payment;

import lombok.Data;
@Data
public class TransactionVerificationResponse {

    @Data
    public static class DataPayload {
        private Long id;
        private String tx_ref; // Your orderNumber in tx_ref
        private String flw_ref; // Flutterwave ref
        private Double amount;
        private String currency;
        private String status; // "successful", "failed", etc.
        private String created_at;

        // ✅ Add payment_type
        private String payment_type;

        // ✅ Optional: Add card info if payment_type == "card"
        private Card card;

        // --- Card inner class ---
        @Data
        public static class Card {
            private String type; // e.g., Visa
            private String last4digits;
            private String issuer;

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getLast4digits() {
                return last4digits;
            }

            public void setLast4digits(String last4digits) {
                this.last4digits = last4digits;
            }

            public String getIssuer() {
                return issuer;
            }

            public void setIssuer(String issuer) {
                this.issuer = issuer;
            }
        }

        // --- Existing getters/setters ---
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getTx_ref() { return tx_ref; }
        public void setTx_ref(String tx_ref) { this.tx_ref = tx_ref; }

        public String getFlw_ref() { return flw_ref; }
        public void setFlw_ref(String flw_ref) { this.flw_ref = flw_ref; }

        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }

        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getCreated_at() { return created_at; }
        public void setCreated_at(String created_at) { this.created_at = created_at; }

        public String getPayment_type() { return payment_type; }
        public void setPayment_type(String payment_type) { this.payment_type = payment_type; }

        public Card getCard() { return card; }
        public void setCard(Card card) { this.card = card; }
    }

    private String status;
    private String message;
    private DataPayload data;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public DataPayload getData() { return data; }
    public void setData(DataPayload data) { this.data = data; }
}
