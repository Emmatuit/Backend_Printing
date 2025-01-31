package com.example.demo.Imagekit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.config.Configuration;

@SpringBootConfiguration
public class ImageKitConfig {

	@Value("${imagekit.publicKey}")
	private String publicKey;

	@Value("${imagekit.privateKey}")
	private String privateKey;

	@Value("${imagekit.urlEndpoint}")
	private String urlEndpoint;

	@Bean
	public ImageKit imageKit() {
		// Create Configuration object
		Configuration config = new Configuration(publicKey, privateKey, urlEndpoint);

		// Get ImageKit instance and set the configuration
		ImageKit imageKit = ImageKit.getInstance();
		imageKit.setConfig(config);

		return imageKit;
	}
}
