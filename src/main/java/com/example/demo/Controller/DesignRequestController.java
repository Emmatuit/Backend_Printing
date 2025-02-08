package com.example.demo.Controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Dto.DesignRequestDto;
import com.example.demo.Enum.DesignStatus;
import com.example.demo.Imagekit.ImagekitService;
import com.example.demo.Repository.DesignRequestRepository;
import com.example.demo.model.DesignRequest;

import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.exceptions.BadRequestException;
import io.imagekit.sdk.exceptions.ForbiddenException;
import io.imagekit.sdk.exceptions.InternalServerException;
import io.imagekit.sdk.exceptions.TooManyRequestsException;
import io.imagekit.sdk.exceptions.UnauthorizedException;
import io.imagekit.sdk.exceptions.UnknownException;
import io.imagekit.sdk.models.FileCreateRequest;
import io.imagekit.sdk.models.results.Result;

@RestController
@RequestMapping("/api/design-requests")
public class DesignRequestController {

	
	
	@Autowired
	private DesignRequestRepository designRequestRepository;
	
	private final ImageKit imageKit;


	public DesignRequestController(ImageKit imageKit) {
		this.imageKit = imageKit;
	}
	

	@PostMapping("/upload")
    public ResponseEntity<?> uploadDesignRequest(
            @RequestParam("file") MultipartFile file,
            @RequestParam("description") String description) {

        try {
            // Validate file size (Max 10MB)
            if (file.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity.badRequest().body("File size exceeds 10MB limit");
            }

            // Convert file to byte array
            byte[] imageBytes = file.getBytes();

            // Create a FileCreateRequest for ImageKit
            FileCreateRequest fileCreateRequest = new FileCreateRequest(imageBytes, file.getOriginalFilename());
            fileCreateRequest.setFolder("/DesignRequest");

            // Upload to ImageKit
            Result result = imageKit.upload(fileCreateRequest);

            if (result == null || result.getUrl() == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to upload image to ImageKit");
            }

            // Save the design request details in the database
            DesignRequest designRequest = new DesignRequest();
            designRequest.setFileName(result.getName());
            designRequest.setFileType(file.getContentType());
            designRequest.setFileUrl(result.getUrl());
            designRequest.setDescription(description); // Set the description

            // Save in the database
            DesignRequest savedDesign = designRequestRepository.save(designRequest);

            // Return the uploaded file's URL and ID
            Map<String, Object> response = new HashMap<>();
            response.put("id", savedDesign.getId());
            response.put("fileUrl", savedDesign.getFileUrl());
            response.put("description", savedDesign.getDescription());

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File upload failed: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }

}
