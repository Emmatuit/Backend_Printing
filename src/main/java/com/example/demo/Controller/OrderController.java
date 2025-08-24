package com.example.demo.Controller;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Dto.CheckoutRequest;
import com.example.demo.Dto.OrderDto;
import com.example.demo.Dto.OrderItemDto;
import com.example.demo.Enum.OrderStatus;
import com.example.demo.Repository.OrderRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.CheckoutService;
import com.example.demo.model.Order;
import com.example.demo.model.UserEntity;

@RestController
@RequestMapping("/api")
public class OrderController {

	private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
	private final CheckoutService checkoutService;

	private final UserRepository userRepository;

	private final OrderRepository orderRepository;

	public OrderController(CheckoutService checkoutService, UserRepository userRepository,
			OrderRepository orderRepository) {
		this.checkoutService = checkoutService;
		this.userRepository = userRepository;
		this.orderRepository = orderRepository;
	}

	@PostMapping("/checkout")
	public ResponseEntity<?> checkout(@AuthenticationPrincipal UserDetails userDetails,
			@RequestBody CheckoutRequest request) {

		if (userDetails == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not authenticated"));
		}

		String email = userDetails.getUsername();

		Optional<UserEntity> optionalUser = userRepository.findByEmail(email);

		if (optionalUser.isEmpty()) {
			return ResponseEntity.badRequest().body(Map.of("error", "Email not found"));
		}

		// Pass UserEntity to checkout method
		OrderDto order = checkoutService.checkout(request, optionalUser.get());
		return ResponseEntity.ok(order);
	}

	@DeleteMapping("/ordersDelete/{orderNumber}")
	public ResponseEntity<?> deleteOrder(@AuthenticationPrincipal UserDetails userDetails,
			@PathVariable("orderNumber") String orderNumber) {

		if (userDetails == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not authenticated"));
		}

		String email = userDetails.getUsername();

		Optional<UserEntity> optionalUser = userRepository.findByEmail(email);

		if (optionalUser.isEmpty()) {
			return ResponseEntity.badRequest().body(Map.of("error", "Email not found"));
		}

		try {
			// 1️⃣ Find the order
			Order order = checkoutService.getOrderByOrderNumber(orderNumber);

			// 2️⃣ Check ownership
			if (!order.getEmail().equalsIgnoreCase(optionalUser.get().getEmail())) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "You do not own this order"));
			}

			// 4️⃣ Delete order
			checkoutService.deleteOrder(order);

			return ResponseEntity.ok(Map.of("message", "Order deleted successfully"));
		} catch (Exception ex) {
			logger.error("Error deleting order", ex);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Error deleting order"));
		}
	}

	@GetMapping("/orders/{orderId}")
	public ResponseEntity<?> getOrderSummary(@PathVariable("orderId") Long orderId,
			@AuthenticationPrincipal UserDetails userDetails) {
		if (userDetails == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not authenticated"));
		}

		String email = userDetails.getUsername();
		Optional<UserEntity> optionalUser = userRepository.findByEmail(email);

		if (optionalUser.isEmpty()) {
			return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
		}

		try {
			OrderDto orderDto = checkoutService.getOrderSummary(orderId, optionalUser.get());
			return ResponseEntity.ok(orderDto);
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("error", "Order not found or access denied"));
		}
	}

	@GetMapping("/orders")
	public ResponseEntity<?> getUserOrders(@AuthenticationPrincipal UserDetails userDetails) {
		if (userDetails == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not authenticated"));
		}

		String email = userDetails.getUsername();
		Optional<UserEntity> optionalUser = userRepository.findByEmail(email);

		if (optionalUser.isEmpty()) {
			return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
		}

		List<Order> orders = orderRepository.findByUserOrderByCreatedAtDesc(optionalUser.get());
		List<OrderDto> orderDtos = orders.stream().map(this::mapToDto).toList();

		return ResponseEntity.ok(orderDtos);
	}

	private OrderDto mapToDto(Order order) {
		OrderDto dto = new OrderDto();
		dto.setOrderId(order.getId());
		dto.setOrderNumber(order.getOrderNumber());
		dto.setTotalAmount(order.getTotalAmount());
		dto.setEmail(order.getEmail());
		dto.setFullName(order.getFullName());
		dto.setPhoneNumber(order.getPhoneNumber());
		dto.setAddress1(order.getAddress1());
		dto.setAddress2(order.getAddress2());
		dto.setState(order.getState());
		dto.setPostalCode(order.getPostalCode());
		dto.setPaymentMethod(order.getPaymentMethod());
		dto.setPaymentStatus(order.getPaymentStatus());
		dto.setStatus(order.getStatus());
		dto.setCreatedAt(order.getCreatedAt());
		dto.setShippingMethod(order.getShippingMethod());
		dto.setShippingAddress(order.getShippingAddress());
		dto.setShippingFee(order.getShippingFee());
		dto.setTaxAmount(order.getTaxAmount());
		dto.setGrandTotal(order.getGrandTotal());
		dto.setCouponCode(order.getCouponCode());
		dto.setDiscountAmount(order.getDiscountAmount());

		List<OrderItemDto> items = order.getItems().stream().map(item -> {
			OrderItemDto itemDto = new OrderItemDto();
			itemDto.setProductId(item.getProduct().getId());
			itemDto.setProductName(item.getProduct().getName());
			itemDto.setQuantity(item.getQuantity());
			itemDto.setPrice(item.getPrice());
			itemDto.setSubTotal(item.getSubTotal());
			itemDto.setCreatedAt(item.getCreatedAt());
			return itemDto;
		}).collect(Collectors.toList());

		dto.setItems(items);
		return dto;
	}

	@GetMapping("/orders/status")
	public ResponseEntity<?> getOrdersByOptionalStatus(@RequestParam(value = "status", required = false) String status,
			@AuthenticationPrincipal UserDetails userDetails) {

		if (userDetails == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not authenticated"));
		}

		String email = userDetails.getUsername();
		Optional<UserEntity> optionalUser = userRepository.findByEmail(email);

		if (optionalUser.isEmpty()) {
			return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
		}

		List<Order> orders;

		if (status != null && !status.isBlank()) {
			try {
				OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
				orders = orderRepository.findByUserAndStatusOrderByCreatedAtDesc(optionalUser.get(), orderStatus);
			} catch (IllegalArgumentException e) {
				return ResponseEntity.badRequest().body(Map.of("error", "Invalid order status: " + status));
			}
		} else {
			orders = orderRepository.findByUserOrderByCreatedAtDesc(optionalUser.get());
		}

		List<OrderDto> orderDtos = orders.stream().map(this::mapToDto).toList();
		return ResponseEntity.ok(orderDtos);
	}

}