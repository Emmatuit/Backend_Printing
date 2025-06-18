package com.example.demo.payment;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "flutterwave")

public class FlutterwaveConfig {
    private String publicKey;
    private String secretKey;
    private String encryptionKey;
    private String baseUrl;
	public String getBaseUrl() {
		return baseUrl;
	}
	public String getEncryptionKey() {
		return encryptionKey;
	}
	public String getPublicKey() {
		return publicKey;
	}
	public String getSecretKey() {
		return secretKey;
	}
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	public void setEncryptionKey(String encryptionKey) {
		this.encryptionKey = encryptionKey;
	}
	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}



}



