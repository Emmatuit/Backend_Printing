package com.example.demo.Controller;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Dto.ChangePasswordRequest;
import com.example.demo.Dto.EmailVerificationRequest;
import com.example.demo.Dto.ForgotPasswordRequest;
import com.example.demo.Dto.RegisterRequest;
import com.example.demo.Dto.ResendCodeRequest;
import com.example.demo.Dto.ResetPasswordRequest;
import com.example.demo.Emails.EmailService;
import com.example.demo.Enum.Role;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Security.JwtService;
import com.example.demo.Service.CartService;
import com.example.demo.Service.ProductService;
import com.example.demo.Service.WishlistService;
import com.example.demo.model.UserEntity;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private CartService cartService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private ProductService productService;

	@Autowired
	private EmailService emailService;

	@Autowired
	private WishlistService wishlistService;

	@PostMapping("/change-password1")
	public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request,
			@AuthenticationPrincipal UserDetails userDetails) {
		String email = userDetails.getUsername();
		Optional<UserEntity> optionalUser = userRepository.findByEmail(email);

		if (optionalUser.isEmpty()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not found"));
		}

		UserEntity user = optionalUser.get();

		if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
			return ResponseEntity.badRequest().body(Map.of("error", "Old password is incorrect"));
		}

		user.setPassword(passwordEncoder.encode(request.getNewPassword()));
		userRepository.save(user);

		return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
		String email = request.getEmail();
		Optional<UserEntity> optionalUser = userRepository.findByEmail(email);

		if (optionalUser.isEmpty()) {
			return ResponseEntity.badRequest().body(Map.of("error", "Email not found"));
		}

		UserEntity user = optionalUser.get();

		String resetCode = String.valueOf((int) (Math.random() * 9000) + 1000);
		LocalDateTime expiry = LocalDateTime.now().plusMinutes(10);

		user.setResetPasswordToken(resetCode);
		user.setResetTokenExpiry(expiry);
		userRepository.save(user);

		String emailBody = "<p>Hello " + user.getUsername() + ",</p>" + "<p>Your password reset code is:</p>"
				+ "<h2 style='color:blue;'>" + resetCode + "</h2>" + "<p>This code will expire in 10 minutes.</p>";

		try {
			emailService.sendVerificationEmail(user.getEmail(), "Password Reset Code", emailBody);

			return ResponseEntity.ok(Map.of("message", "Password reset code sent", "expiry", expiry.toString()));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Failed to send email: " + e.getMessage()));
		}
	}

	public String generateVerificationCode() {
		Random random = new Random();
		int code = 100000 + random.nextInt(900000);
		return String.valueOf(code);
	}

	@PostMapping("/login1")
	public ResponseEntity<?> login(@RequestParam("email") String email, @RequestParam("password") String password,
			@RequestParam("sessionId") String sessionId, HttpSession session) {
		try {
			// ✅ Authenticate using email and password
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

			// ✅ Load user details by email
			UserDetails userDetails = userDetailsService.loadUserByUsername(email);

			// ✅ Get the actual UserEntity using email
			UserEntity user = userRepository.findByEmail(email)
					.orElseThrow(() -> new UsernameNotFoundException("User not found"));

			// ✅ Check email verification status
			if (!user.isEmailVerified()) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
						Collections.singletonMap("error", "Email not verified. Please verify your email to log in."));
			}

			// ✅ Generate JWT token
			String token = jwtService.generateToken(userDetails.getUsername()); // still using email internally

			// ✅ Log session info
			System.out.println("🔹 User logged in: " + email);
			System.out.println("🔹 Received sessionId from frontend: " + sessionId);

			// ✅ Store sessionId in session
			session.setAttribute("sessionId", sessionId);

			// ✅ Merge session cart/history with logged-in user
			cartService.mergeSessionCartWithUser(email, sessionId);
			productService.mergeSessionHistoryWithUser(email, sessionId);
			wishlistService.mergeSessionWishlistWithUser(email, sessionId); // ✅ Add this line

			// ✅ Prepare response
			Map<String, Object> response = new HashMap<>();
			response.put("message", "Login successful");
			response.put("token", token);
			response.put("username", user.getUsername()); // Optional
			response.put("email", user.getEmail());
			response.put("phoneNumber", user.getPhoneNumber());

			return ResponseEntity.ok(response);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Collections.singletonMap("error", "Invalid email or password"));
		}
	}

	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
		if (userRepository.findByUsername(request.getUsername()).isPresent()) {
			return ResponseEntity.badRequest().body("Username already exists!");
		}

		if (userRepository.findByEmail(request.getEmail()).isPresent()) {
			return ResponseEntity.badRequest().body("Email already registered!");
		}

		UserEntity user = new UserEntity();
		user.setUsername(request.getUsername());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setEmail(request.getEmail());
		user.setPhoneNumber(request.getPhoneNumber());
		user.setRole(Role.USER);
		user.setEmailVerified(false);

		userRepository.save(user);

		return ResponseEntity.ok("User registered. You can now request a verification code.");
	}

	@PostMapping("/resend-code")
	public ResponseEntity<Map<String, Object>> resendVerificationCode(@RequestBody ResendCodeRequest request) {
		Optional<UserEntity> optionalUser = userRepository.findByEmail(request.getEmail());

		if (optionalUser.isEmpty()) {
			return ResponseEntity.badRequest().body(Map.of("error", "Email not found"));
		}

		UserEntity user = optionalUser.get();

		if (user.isEmailVerified()) {
			return ResponseEntity.badRequest().body(Map.of("error", "Email is already verified."));
		}

		String newCode = generateVerificationCode();
		LocalDateTime expiry = LocalDateTime.now().plusMinutes(10);

		user.setEmailVerificationCode(newCode);
		user.setVerificationCodeExpiry(expiry);
		userRepository.save(user);

		String emailBody = "<html><body>" + "<h1>Resend Verification Code</h1>" + "<p>Hi " + user.getUsername()
				+ ",</p>" + "<p>Your new verification code is:</p>" + "<h2 style='color:blue;'>" + newCode + "</h2>"
				+ "<p>This code will expire in 10 minutes.</p>" + "</body></html>";

		try {
			emailService.sendVerificationEmail(user.getEmail(), "Your New Verification Code", emailBody // this is your
																										// full HTML
			);

			// ✅ Return expiry time
			Map<String, Object> response = new HashMap<>();
			response.put("message", "Verification code resent successfully.");
			response.put("verificationCodeExpiry", expiry.toString()); // ISO format

			return ResponseEntity.ok(response);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Failed to send verification code: " + e.getMessage()));
		}
	}

	@PostMapping("/reset-password")
	public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
		String email = request.getEmail();
		String code = request.getToken();
		String newPassword = request.getNewPassword();

		Optional<UserEntity> optionalUser = userRepository.findByEmail(email);
		if (optionalUser.isEmpty()) {
			return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
		}

		UserEntity user = optionalUser.get();

		if (!code.equals(user.getResetPasswordToken())) {
			return ResponseEntity.badRequest().body(Map.of("error", "Invalid reset code"));
		}

		if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
			return ResponseEntity.badRequest().body(Map.of("error", "Reset code expired"));
		}

		user.setPassword(passwordEncoder.encode(newPassword));
		user.setResetPasswordToken(null);
		user.setResetTokenExpiry(null);

		userRepository.save(user);

		return ResponseEntity.ok(Map.of("message", "Password reset successful"));
	}

	@PostMapping("/verify-email")
	public ResponseEntity<String> verifyEmail(@RequestBody EmailVerificationRequest request) {
		Optional<UserEntity> optionalUser = userRepository.findByEmail(request.getEmail());

		if (optionalUser.isEmpty()) {
			return ResponseEntity.badRequest().body("Email not found");
		}

		UserEntity user = optionalUser.get();

		// Check if already verified
		if (user.isEmailVerified()) {
			return ResponseEntity.badRequest().body("Email already verified.");
		}

		// Check if code matches and is not expired
		if (!user.getEmailVerificationCode().equals(request.getCode())) {
			return ResponseEntity.badRequest().body("Invalid verification code.");
		}

		if (user.getVerificationCodeExpiry() == null
				|| user.getVerificationCodeExpiry().isBefore(LocalDateTime.now())) {
			return ResponseEntity.badRequest().body("Verification code has expired.");
		}

		// If valid, mark email as verified
		user.setEmailVerified(true);
		user.setEmailVerificationCode(null);
		user.setVerificationCodeExpiry(null);
		userRepository.save(user);

		return ResponseEntity.ok("Email verified successfully!");
	}

	@PostMapping("/verify-reset-code")
	public ResponseEntity<?> verifyResetCode(@RequestBody Map<String, String> request) {
		String email = request.get("email");
		String token = request.get("token");

		Optional<UserEntity> optionalUser = userRepository.findByEmail(email);
		if (optionalUser.isEmpty()) {
			return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
		}

		UserEntity user = optionalUser.get();

		if (!token.equals(user.getResetPasswordToken())) {
			return ResponseEntity.badRequest().body(Map.of("error", "Invalid reset code"));
		}

		if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
			return ResponseEntity.badRequest().body(Map.of("error", "Reset code expired"));
		}

		return ResponseEntity.ok(Map.of("message", "Code verified"));
	}

}
