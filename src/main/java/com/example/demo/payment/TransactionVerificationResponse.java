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
        private String created_at; // <-- add this

        // existing getters and setters...

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

		public Double getAmount() {
			return amount;
		}
		public String getCurrency() {
			return currency;
		}
		public String getFlw_ref() {
			return flw_ref;
		}
		public Long getId() {
			return id;
		}
		public String getStatus() {
			return status;
		}
		public String getTx_ref() {
			return tx_ref;
		}
		public void setAmount(Double amount) {
			this.amount = amount;
		}
		public void setCurrency(String currency) {
			this.currency = currency;
		}
		public void setFlw_ref(String flw_ref) {
			this.flw_ref = flw_ref;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public void setTx_ref(String tx_ref) {
			this.tx_ref = tx_ref;
		}


    }
    private String status;
    private String message;



    private DataPayload data;



	public DataPayload getData() {
		return data;
	}



	public String getMessage() {
		return message;
	}



	public String getStatus() {
		return status;
	}



	public void setData(DataPayload data) {
		this.data = data;
	}



	public void setMessage(String message) {
		this.message = message;
	}



	public void setStatus(String status) {
		this.status = status;
	}
}
