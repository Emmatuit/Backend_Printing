package com.example.demo.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Dto.DesignRequestDto;
import com.example.demo.Enum.DesignStatus;
import com.example.demo.Repository.DesignRequestRepository;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.model.DesignRequest;
import com.example.demo.model.Product;

@Service
public class DesignRequestService {

	@Autowired
	private DesignRequestRepository designRequestRepository;

	@Autowired
	private ProductRepository productRepository;

	// Create a new design request
	public DesignRequest createDesignRequest(DesignRequestDto designRequestDto) {
		Product product = productRepository.findById(designRequestDto.getProductId())
				.orElseThrow(() -> new RuntimeException("Product not found"));

		DesignRequest designRequest = new DesignRequest(product, designRequestDto.getDesignFileUrl(),
				designRequestDto.getCustomerNotes());
		return designRequestRepository.save(designRequest);
	}

	// Get all design requests
	public List<DesignRequest> getAllDesignRequests() {
		return designRequestRepository.findAll();
	}

	// Get a design request by ID
	public Optional<DesignRequest> getDesignRequestById(Long id) {
		return designRequestRepository.findById(id);
	}

	// Update the status of a design request
	public DesignRequest updateDesignStatus(Long id, DesignStatus status) {
		DesignRequest designRequest = designRequestRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Design Request not found"));
		designRequest.setDesignStatus(status);
		return designRequestRepository.save(designRequest);
	}
}
