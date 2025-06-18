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
        private String createdAt; // ISO 8601 format like "2025-06-12T10:20:30"

        public Long getId() {
            return id;
        }

        public String getStatus() {
            return status;
        }

        public String getTxRef() {
            return txRef;
        }

        public Double getAmount() {
            return amount;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public void setTxRef(String txRef) {
            this.txRef = txRef;
        }

        public void setAmount(Double amount) {
            this.amount = amount;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }
    }

    private DataPayload data;

    public WebhookPayload(DataPayload data) {
        this.data = data;
    }

    public DataPayload getData() {
        return data;
    }

    public void setData(DataPayload data) {
        this.data = data;
    }
}
