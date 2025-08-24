package com.example.demo.Controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Dto.OrderDto;
import com.example.demo.Enum.OrderStatus;
import com.example.demo.Repository.OrderRepository;
import com.example.demo.Service.CheckoutService;
import com.example.demo.model.Order;

@RestController
@RequestMapping("/api/admin/orders")
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {

	private final OrderRepository orderRepository;

	@Autowired
	private CheckoutService checkoutService;

	public AdminOrderController(OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	// ✅ Pagination + optional status
	@GetMapping("/paged-orders")
	public ResponseEntity<Page<OrderDto>> getAllOrders(@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "10") int size,
			@RequestParam(name = "status", required = false) String status) {

		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
		Page<Order> orders;

		if (status != null && !status.isBlank()) {
			try {
				OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
				orders = orderRepository.findByStatus(orderStatus, pageable);
			} catch (IllegalArgumentException e) {
				return ResponseEntity.badRequest().body(Page.empty());
			}
		} else {
			orders = orderRepository.findAll(pageable);
		}

		Page<OrderDto> dtoPage = orders.map(checkoutService::mapToDto);
		return ResponseEntity.ok(dtoPage);
	}

	// ✅ Search by email or orderNumber
	@GetMapping("/search")
	public ResponseEntity<List<OrderDto>> searchOrders(@RequestParam(value = "email", required = false) String email,
			@RequestParam(value = "orderNumber", required = false) String orderNumber) {

		List<Order> orders = new ArrayList<>();

		if (email != null && !email.isBlank() && orderNumber != null && !orderNumber.isBlank()) {
			orders = orderRepository.findByEmailContainingIgnoreCaseAndOrderNumber(email, orderNumber);
		} else if (email != null && !email.isBlank()) {
			orders = orderRepository.findByEmailContainingIgnoreCase(email);
		} else if (orderNumber != null && !orderNumber.isBlank()) {
			orders = orderRepository.findByOrderNumber(orderNumber).map(List::of).orElse(List.of());
		} else {
			orders = orderRepository.findAll(Sort.by("createdAt").descending());
		}

		List<OrderDto> dtos = orders.stream().map(checkoutService::mapToDto).collect(Collectors.toList());

		return ResponseEntity.ok(dtos);
	}

	@GetMapping("/{id}")
	public ResponseEntity<OrderDto> getOrderById(@PathVariable("id") Long id) {
		Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
		return ResponseEntity.ok(checkoutService.mapToDto(order));
	}

	@PutMapping("/{id}/status")
	public ResponseEntity<OrderDto> updateStatus(@PathVariable("id") Long id, @RequestParam("status") String status) {
		OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
		OrderDto updated = checkoutService.updateOrderStatus(id, orderStatus);
		return ResponseEntity.ok(updated);
	}

	// dISPLAY ORDER_sTATUSES
	@GetMapping("/statuses")
	public ResponseEntity<List<String>> getAllStatuses() {
		List<String> statuses = Arrays.stream(OrderStatus.values()).map(Enum::name).collect(Collectors.toList());
		return ResponseEntity.ok(statuses);
	}

	@PutMapping("/{id}/payment-status")
	public ResponseEntity<OrderDto> updatePaymentStatus(@PathVariable Long id, @RequestParam String paymentStatus) {
		Order.PaymentStatus ps = Order.PaymentStatus.valueOf(paymentStatus.toUpperCase());
		OrderDto updated = checkoutService.updatePaymentStatus(id, ps);
		return ResponseEntity.ok(updated);
	}

}
