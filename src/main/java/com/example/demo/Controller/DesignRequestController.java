package com.example.demo.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Dto.DesignRequestDto;
import com.example.demo.Enum.DesignStatus;
import com.example.demo.Service.DesignRequestService;
import com.example.demo.model.DesignRequest;

@RestController
@RequestMapping("/api/design-requests")
public class DesignRequestController {

	@Autowired
	private DesignRequestService designRequestService;

	// Endpoint to create a new design request
	@PostMapping
	public ResponseEntity<DesignRequest> createDesignRequest(@RequestBody DesignRequestDto designRequestDto) {
		DesignRequest createdRequest = designRequestService.createDesignRequest(designRequestDto);
		return ResponseEntity.ok(createdRequest);
	}

	// Endpoint to get all design requests
	@GetMapping
	public ResponseEntity<List<DesignRequest>> getAllDesignRequests() {
		List<DesignRequest> requests = designRequestService.getAllDesignRequests();
		return ResponseEntity.ok(requests);
	}

	// Endpoint to get a design request by ID
	@GetMapping("/{id}")
	public ResponseEntity<DesignRequest> getDesignRequestById(@PathVariable Long id) {
		Optional<DesignRequest> request = designRequestService.getDesignRequestById(id);
		return request.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	// Endpoint to update the status of a design request
	@PutMapping("/{id}/status")
	public ResponseEntity<DesignRequest> updateDesignStatus(@PathVariable Long id, @RequestParam DesignStatus status) {
		DesignRequest updatedRequest = designRequestService.updateDesignStatus(id, status);
		return ResponseEntity.ok(updatedRequest);
	}
}
