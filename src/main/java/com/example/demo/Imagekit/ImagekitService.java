package com.example.demo.Imagekit;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.exceptions.BadRequestException;
import io.imagekit.sdk.exceptions.ForbiddenException;
import io.imagekit.sdk.exceptions.InternalServerException;
import io.imagekit.sdk.exceptions.TooManyRequestsException;
import io.imagekit.sdk.exceptions.UnauthorizedException;
import io.imagekit.sdk.exceptions.UnknownException;
import io.imagekit.sdk.models.FileCreateRequest;
import io.imagekit.sdk.models.results.Result;

@Service
public class ImagekitService {

	private final ImageKit imageKit;

	public ImagekitService(ImageKit imageKit) {
		this.imageKit = imageKit;
	}

	public String uploadFile(MultipartFile file) throws IOException, InternalServerException, BadRequestException,
			UnknownException, ForbiddenException, TooManyRequestsException, UnauthorizedException {

		// Create a FileCreateRequest for the uploaded file (no compression)
		FileCreateRequest fileCreateRequest = new FileCreateRequest(file.getBytes(), file.getOriginalFilename());
		fileCreateRequest.setFolder("/categories");

		// Upload to ImageKit
		Result result = imageKit.upload(fileCreateRequest);

		if (result != null && result.getUrl() != null) {
			return result.getUrl(); // Return the full URL provided by ImageKit
		} else {
			throw new IOException("Failed to upload image to ImageKit");
		}
	}

	public String uploadFileToProduct(MultipartFile file) throws IOException, InternalServerException,
			BadRequestException, UnknownException, ForbiddenException, TooManyRequestsException, UnauthorizedException {

// Upload the original image without compression
		byte[] imageBytes = file.getBytes(); // Get the image byte array

// Create a FileCreateRequest for the byte array upload
		FileCreateRequest fileCreateRequest = new FileCreateRequest(imageBytes, file.getOriginalFilename());
		fileCreateRequest.setFolder("/Products");

// Upload to ImageKit
		Result result = imageKit.upload(fileCreateRequest);

		if (result != null && result.getUrl() != null) {
			return result.getUrl(); // Return the fileId instead of URL for future deletion
		} else {
			throw new IOException("Failed to upload image to ImageKit");
		}
	}

	public String uploadSpecificationImageFile(MultipartFile file) throws IOException, InternalServerException,
			BadRequestException, UnknownException, ForbiddenException, TooManyRequestsException, UnauthorizedException {

// Upload the original image without compression
		byte[] imageBytes = file.getBytes(); // Get the image byte array

// Create a FileCreateRequest for the byte array upload
		FileCreateRequest fileCreateRequest = new FileCreateRequest(imageBytes, file.getOriginalFilename());
		fileCreateRequest.setFolder("/SpecificationImages");

// Upload to ImageKit
		Result result = imageKit.upload(fileCreateRequest);

		if (result != null && result.getUrl() != null) {
			return result.getUrl(); // Return the fileId instead of URL for future deletion
		} else {
			throw new IOException("Failed to upload image to ImageKit");
		}
	}

}
