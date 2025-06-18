package com.example.demo.payment;


import lombok.Data;

@Data
public class PaymentResponse {
    @Data
    public static class DataPayload {
        private String paymentlink;

		public String getPaymentlink() {
			return paymentlink;
		}

		public void setPaymentlink(String paymentlink) {
			this.paymentlink = paymentlink;
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
