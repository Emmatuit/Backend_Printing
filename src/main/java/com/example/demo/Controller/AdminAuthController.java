package com.example.demo.Controller;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Dto.AdminLoginRequest;
import com.example.demo.Dto.AdminLoginResponse;
import com.example.demo.Dto.OrderDto;
import com.example.demo.Dto.UserDto;
import com.example.demo.Emails.EmailService;
import com.example.demo.Repository.OrderRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Security.CustomUserDetailsService;
import com.example.demo.Security.JwtService;
import com.example.demo.Service.CheckoutService;
import com.example.demo.Service.UserService;
import com.example.demo.model.Order;
import com.example.demo.model.UserEntity;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAuthController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EmailService emailService;

	@Autowired
	private CheckoutService checkoutService;

	@Autowired
	private OrderRepository orderRepository;

	@PostMapping("/login")
	public ResponseEntity<?> loginAdmin(@RequestBody AdminLoginRequest request) {
		try {
			// Authenticate email & password
			authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

			// Load user
			UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

			// Check user in DB
			UserEntity user = userRepository.findByEmail(request.getEmail())
					.orElseThrow(() -> new UsernameNotFoundException("User not found"));

			// Reject non-admins
			if (!user.getRole().name().equals("ADMIN")) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN)
						.body(Collections.singletonMap("error", "Access denied. Not an admin."));
			}

			// Generate token
			String token = jwtService.generateToken(userDetails.getUsername());

			// Respond with token and redirect path
			AdminLoginResponse response = new AdminLoginResponse("Admin login successful", token, "/dashboard");

			return ResponseEntity.ok(response);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Collections.singletonMap("error", "Invalid admin credentials"));
		}
	}

	// ✅ DELETE USER
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteUser(@PathVariable Long id) {
		return userRepository.findById(id).map(user -> {
			userRepository.delete(user);
			return ResponseEntity.ok("User deleted successfully.");
		}).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found."));
	}

	// ✅ SUSPEND USER
	@PutMapping("/{id}/suspend")
	public ResponseEntity<?> suspendUser(@PathVariable Long id,
			@RequestParam("until") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime suspendedUntil) {

		return userRepository.findById(id).map(user -> {
			user.setSuspendedUntil(suspendedUntil);
			userRepository.save(user);

			// ✅ Send email notification
			emailService.sendSuspensionEmail(user.getEmail(), user.getUsername(), suspendedUntil);

			return ResponseEntity.ok("User suspended and notified via email.");
		}).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found."));
	}

	@Autowired
	private UserService userService;

	// ✅ 1. Get total user count
	@GetMapping("/count")
	public ResponseEntity<?> getUserCount() {
		try {
			long count = userService.getUserCount();
			return ResponseEntity.ok().body(count); // 200 OK
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to fetch user count. Error: " + e.getMessage()); // 500 Error
		}
	}

	// ✅ 2. Get all user details
	@GetMapping("/all")
	public ResponseEntity<?> getAllUsers() {
		try {
			List<UserDto> users = userService.getAllUsers();
			if (users.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No users found."); // 204 No Content
			}
			return ResponseEntity.ok(users); // 200 OK
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to fetch users. Error: " + e.getMessage()); // 500 Error
		}
	}

	@PostMapping("/unsuspend/{userId}")
	public ResponseEntity<?> unsuspendUser(@PathVariable Long userId) {
		Optional<UserEntity> optionalUser = userRepository.findById(userId);

		if (optionalUser.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Collections.singletonMap("error", "User not found"));
		}

		UserEntity user = optionalUser.get();
		user.setSuspendedUntil(null); // Remove suspension
		userRepository.save(user);

		// ✅ Send unsuspension email
		emailService.sendUnsuspensionEmail(user.getEmail(), user.getUsername());

		return ResponseEntity.ok(Collections.singletonMap("message", "User unsuspended and notified via email"));
	}

	// ✅ Runs every day at 2am (you can change this)
	@Scheduled(cron = "0 0 2 * * *")
	public void liftExpiredSuspensions() {
		List<UserEntity> suspendedUsers = userRepository.findBySuspendedUntilBefore(LocalDateTime.now());

		for (UserEntity user : suspendedUsers) {
			user.setSuspendedUntil(null); // unsuspend
			userRepository.save(user);

			emailService.sendUnsuspensionEmail(user.getEmail(), user.getUsername());
			System.out.println("✅ User unsuspended: " + user.getEmail());
		}
	}

	@GetMapping("/payment-history")
//	@PreAuthorize("hasRole('ADMIN')") // Only admins allowed
	public Page<OrderDto> getPaymentHistory(Pageable pageable) {

		Page<Order> ordersPage = orderRepository.findAll(pageable);

		return ordersPage.map(order -> {
			OrderDto dto = new OrderDto();
			dto.setOrderNumber(order.getOrderNumber());
			dto.setGrandTotal(order.getGrandTotal());
			dto.setPaymentMethod(order.getPaymentMethod());
			dto.setCardLast4(order.getCardLast4());
			dto.setTxRef(order.getTxRef());
			dto.setPaymentId(order.getPaymentId());
			dto.setPaymentStatus(order.getPaymentStatus());
			dto.setStatus(order.getStatus());
			dto.setCreatedAt(order.getCreatedAt());
			dto.setUpdatedAt(order.getUpdatedAt());
			return dto;
		});
	}

}
