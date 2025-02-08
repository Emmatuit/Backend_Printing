package com.example.demo.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Dto.DesignRequestDto;
import com.example.demo.Enum.DesignStatus;
import com.example.demo.Imagekit.ImagekitService;
import com.example.demo.Repository.DesignRequestRepository;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.model.DesignRequest;
import com.example.demo.model.Product;

import io.imagekit.sdk.exceptions.BadRequestException;
import io.imagekit.sdk.exceptions.ForbiddenException;
import io.imagekit.sdk.exceptions.InternalServerException;
import io.imagekit.sdk.exceptions.TooManyRequestsException;
import io.imagekit.sdk.exceptions.UnauthorizedException;
import io.imagekit.sdk.exceptions.UnknownException;

@Service
public class DesignRequestService {

	@Autowired
	private ImagekitService imagekitService;
	
	private DesignRequestRepository designRequestRepository;
	
	public DesignRequestDto uploadDesign(MultipartFile file, String description) throws IOException, InternalServerException, BadRequestException, UnknownException, ForbiddenException, TooManyRequestsException, UnauthorizedException {
        // Validate file size
        if (file.getSize() > 10 * 1024 * 1024) {  // 10MB limit
            throw new IOException("File size exceeds 10MB limit");
        }

        // Upload the file and get the URL
        String imageUrl = imagekitService.uploadDesignRequest(file);

        // Save design request to DB
        DesignRequest designRequest = new DesignRequest();
        designRequest.setFileName(file.getOriginalFilename());
        designRequest.setFileType(file.getContentType());
        designRequest.setFileUrl(imageUrl);
        designRequest.setDescription(description);

        designRequestRepository.save(designRequest);

        return convertToDto(designRequest);
    }

	private DesignRequestDto convertToDto(DesignRequest designRequest) {
        return new DesignRequestDto(
                designRequest.getFileName(),
                designRequest.getFileType(),
                designRequest.getFileUrl(),
                designRequest.getDescription()
        );
    }

}
