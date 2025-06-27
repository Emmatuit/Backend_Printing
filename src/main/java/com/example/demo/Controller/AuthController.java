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
import com.example.demo.Service.RateLimiterService;
import com.example.demo.Service.WishlistService;
import com.example.demo.model.UserEntity;

import jakarta.servlet.http.HttpServletRequest;
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

	@Autowired
	private RateLimiterService rateLimiterService;


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

//	@PostMapping("/forgot-password")
//	public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
//	    String email = request.getEmail();
//
//	    // ✅ Basic format check
//	    if (email == null || !email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
//	        return ResponseEntity.badRequest().body(Map.of("error", "Invalid email format"));
//	    }
//
//	    Optional<UserEntity> optionalUser = userRepository.findByEmail(email);
//
//	    // ✅ Check if user exists
//	    if (optionalUser.isEmpty()) {
//	        return ResponseEntity.badRequest().body(Map.of("error", "Email not found"));
//	    }
//
//	    UserEntity user = optionalUser.get();
//
//	    // ✅ Generate code & save to DB
//	    String resetCode = generateVerificationCode(); // Use your 6-digit version
//	    LocalDateTime expiry = LocalDateTime.now().plusMinutes(10);
//
//	    user.setResetPasswordToken(resetCode);
//	    user.setResetTokenExpiry(expiry);
//	    userRepository.save(user);
//
//	    // ✅ Send email
//	    String emailBody = """
//	        <p>Hello %s,</p>
//	        <p>Your password reset code is:</p>
//	        <h2 style='color:blue;'>%s</h2>
//	        <p>This code will expire in 10 minutes.</p>
//	    """.formatted(user.getUsername(), resetCode);
//
//	    try {
//	        emailService.sendVerificationEmail(user.getEmail(), "Password Reset Code", emailBody);
//	        return ResponseEntity.ok(Map.of("message", "Password reset code sent", "expiry", expiry.toString()));
//	    } catch (Exception e) {
//	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//	            .body(Map.of("error", "Failed to send email: " + e.getMessage()));
//	    }
//	}


	@PostMapping("/forgot-password")
	public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request, HttpServletRequest servletRequest) {
	    String email = request.getEmail();

	    String clientIp = extractClientIp(servletRequest);

	    if (email == null || !email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
	        return ResponseEntity.badRequest().body(Map.of("error", "Invalid email format"));
	    }

	    Optional<UserEntity> optionalUser = userRepository.findByEmail(email);
	    if (optionalUser.isEmpty()) {
	        return ResponseEntity.badRequest().body(Map.of("error", "Email not found"));
	    }

	    UserEntity user = optionalUser.get();

	    if (!user.isEmailVerified()) {
	        return ResponseEntity.badRequest().body(Map.of("error", "Email is not verified. Cannot reset password."));
	    }

	    if (!rateLimiterService.isAllowed(user.getEmail(), clientIp)) {
	        long waitTime = rateLimiterService.getRemainingSeconds(user.getEmail(), clientIp);
	        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
	            .body(Map.of("error", "Please wait " + waitTime + " seconds before requesting a new code."));
	    }

	    // Generate and send reset code
	    String resetCode = generateVerificationCode1();
	    LocalDateTime expiry = LocalDateTime.now().plusMinutes(2);

	    user.setResetPasswordToken(resetCode);
	    user.setResetTokenExpiry(expiry);
	    userRepository.save(user);

	    String emailBody = """
	    		<!DOCTYPE html>
	    		<html>
	    		<head>
	    		  <meta charset="UTF-8">
	    		  <title>Password Reset Code</title>
	    		  <style>
	    		    body {
	    		      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
	    		      background-color: #f4f4f4;
	    		      margin: 0;
	    		      padding: 0;
	    		    }
	    		    .container {
	    		      max-width: 600px;
	    		      margin: 40px auto;
	    		      background-color: #ffffff;
	    		      border-radius: 10px;
	    		      box-shadow: 0 2px 8px rgba(0,0,0,0.1);
	    		      overflow: hidden;
	    		    }
	    		    .header {
	    		      background-color: #004aad;
	    		      color: #ffffff;
	    		      padding: 30px;
	    		      text-align: center;
	    		    }
	    		    .header h1 {
	    		      margin: 0;
	    		      font-size: 26px;
	    		      letter-spacing: 1px;
	    		    }
	    		    .content {
	    		      padding: 30px;
	    		      text-align: center;
	    		    }
	    		    .content p {
	    		      font-size: 16px;
	    		      color: #333333;
	    		      line-height: 1.6;
	    		    }
	    		    .code-box {
	    		      margin: 20px auto;
	    		      display: inline-block;
	    		      padding: 15px 30px;
	    		      background-color: #f1f1f1;
	    		      border-left: 5px solid #004aad;
	    		      font-size: 28px;
	    		      font-weight: bold;
	    		      color: #004aad;
	    		      letter-spacing: 3px;
	    		      border-radius: 5px;
	    		    }
	    		    .footer {
	    		      background-color: #fafafa;
	    		      text-align: center;
	    		      padding: 20px;
	    		      font-size: 12px;
	    		      color: #888888;
	    		    }
	    		  </style>
	    		</head>
	    		<body>
	    		  <div class="container">
	    		    <div class="header">
	    		      <h1>Password Reset Request</h1>
	    		    </div>
	    		    <div class="content">
	    		      <p>Hello %s,</p>
	    		      <p>We received a request to reset your password.</p>
	    		      <p>Use the following code to reset your password:</p>
	    		      <div class="code-box">%s</div>
	    		      <p>This code is valid for 10 minutes.</p>
	    		      <p>If you did not request this, you can safely ignore this email.</p>
	    		    </div>
	    		    <div class="footer">
	    		      &copy; 2025 Bukian Printing. All rights reserved.<br>
	    		      Powered by Bukian Technologies.
	    		    </div>
	    		  </div>
	    		</body>
	    		</html>
	    		""".formatted(user.getUsername(), resetCode);


	    try {
	        emailService.sendVerificationEmail(user.getEmail(), "Password Reset Code", emailBody);
	        return ResponseEntity.ok(Map.of("message", "Password reset code sent", "expiry", expiry.toString()));
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	            .body(Map.of("error", "Failed to send email: " + e.getMessage()));
	    }
	}

	//For 6 digit token
	public String generateVerificationCode() {
		Random random = new Random();
		int code = 100000 + random.nextInt(900000);
		return String.valueOf(code);
	}

	//For 4 digit token
	private String generateVerificationCode1() {
	    Random random = new Random();
	    int code = 1000 + random.nextInt(9000); // 1000–9999
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
		Optional<UserEntity> optionalUserByEmail = userRepository.findByEmail(request.getEmail());

		// ✅ If email exists but not verified → update data
		if (optionalUserByEmail.isPresent()) {
			UserEntity existingUser = optionalUserByEmail.get();

			if (existingUser.isEmailVerified()) {
				return ResponseEntity.badRequest().body("User with Email already Existed.");
			}

			// Override with latest user details
			existingUser.setUsername(request.getUsername());
			existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
			existingUser.setPhoneNumber(request.getPhoneNumber());
			userRepository.save(existingUser);

			return ResponseEntity.ok("Registration Sucessful !!. Proceed to login");
		}

		// ✅ Check username uniqueness separately
		if (userRepository.findByUsername(request.getUsername()).isPresent()) {
			return ResponseEntity.badRequest().body("Username already exists!");
		}

		// ✅ New user
		UserEntity user = new UserEntity();
		user.setUsername(request.getUsername());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setEmail(request.getEmail());
		user.setPhoneNumber(request.getPhoneNumber());
		user.setRole(Role.USER);
		user.setEmailVerified(false);

		userRepository.save(user);

		return ResponseEntity.ok("Registration Successful Proceed!. To verify your Email.");
	}

	@PostMapping("/resend-code")
	public ResponseEntity<Map<String, Object>> resendVerificationCode(@RequestBody ResendCodeRequest request,  HttpServletRequest servletRequest) {

		 System.out.println("✅ [RESEND] Backend hit for: " + request.getEmail()); // <--- Add this
	    String clientIp = extractClientIp(servletRequest);

	    Optional<UserEntity> optionalUser = userRepository.findByEmail(request.getEmail());
	    if (optionalUser.isEmpty()) {
	        return ResponseEntity.badRequest().body(Map.of("error", "Email not found"));
	    }

	    UserEntity user = optionalUser.get();

	    if (user.isEmailVerified()) {
	        return ResponseEntity.badRequest().body(Map.of("error", "Email is already verified."));
	    }

	    if (!rateLimiterService.isAllowed(user.getEmail(), clientIp)) {
	        long waitTime = rateLimiterService.getRemainingSeconds(user.getEmail(), clientIp);
	        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
	            .body(Map.of("error", "Please wait " + waitTime + " seconds before requesting a new code."));
	    }

	    // ✅ Generate verification code

	    String currentCode = user.getEmailVerificationCode();
	    LocalDateTime expiry = user.getVerificationCodeExpiry(); // ✅ pull from DB first

	    // 🔁 If code is expired or missing, generate a new one
	 // Check for expiry and regenerate if needed
	    if (currentCode == null || expiry == null || expiry.isBefore(LocalDateTime.now())) {
	    	System.out.println("🔁 Resend request: currentCode = " + currentCode + ", expiry = " + expiry);
	        currentCode = generateVerificationCode();
	        expiry = LocalDateTime.now().plusMinutes(2);
	        user.setEmailVerificationCode(currentCode);
	        user.setVerificationCodeExpiry(expiry);
	        userRepository.save(user);

	        System.out.println("📨 [FORCED] Code resent: " + currentCode);
	    }

	    // Email is ALWAYS sent, regardless of whether code was regenerated




	    String emailBody = """
	    		<!DOCTYPE html>
	    		<html>
	    		<head>
	    		  <style>
	    		    body {
	    		      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
	    		      background-color: #f4f6f8;
	    		      margin: 0;
	    		      padding: 0;
	    		    }
	    		    .container {
	    		      background-color: #ffffff;
	    		      margin: 40px auto;
	    		      padding: 30px;
	    		      border-radius: 8px;
	    		      box-shadow: 0 4px 12px rgba(0,0,0,0.1);
	    		      max-width: 480px;
	    		    }
	    		    .header {
	    		      text-align: center;
	    		      color: #1a73e8;
	    		    }
	    		    .code {
	    		      text-align: center;
	    		      font-size: 32px;
	    		      font-weight: bold;
	    		      color: #1a73e8;
	    		      margin: 20px 0;
	    		      letter-spacing: 4px;
	    		    }
	    		    .footer {
	    		      font-size: 12px;
	    		      color: #666666;
	    		      text-align: center;
	    		      margin-top: 30px;
	    		    }
	    		    .brand {
	    		      font-size: 18px;
	    		      font-weight: bold;
	    		      color: #333333;
	    		      text-align: center;
	    		    }
	    		  </style>
	    		</head>
	    		<body>
	    		  <div class="container">
	    		    <div class="brand">🖨️ BukianPrint</div>
	    		    <h2 class="header">Email Verification Code</h2>
	    		    <p>Hello <strong>%s</strong>,</p>
	    		    <p>Thank you for registering with us!</p>
	    		    <p>Please use the following code to verify your email address:</p>
	    		    <div class="code">%s</div>
	    		    <p>This code will expire in <strong>2 minutes</strong>.</p>
	    		    <p>If you didn't request this, please ignore this email.</p>
	    		    <div class="footer">
	    		      &copy; 2025 BukianPrint. All rights reserved.
	    		    </div>
	    		  </div>
	    		</body>
	    		</html>
	    		""".formatted(user.getUsername(), currentCode);


	    try {
	        emailService.sendVerificationEmail(user.getEmail(), "Your New Verification Code", emailBody);
	        System.out.println("✅ Email sent to " + user.getEmail() + " with code: " + currentCode);

	        Map<String, Object> response = new HashMap<>();
	        response.put("message", "Verification code resent successfully.");
	        response.put("verificationCodeExpiry", expiry.toString());

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

	private String extractClientIp(HttpServletRequest request) {
	    String xfHeader = request.getHeader("X-Forwarded-For");
	    if (xfHeader == null) {
	        return request.getRemoteAddr();
	    }
	    return xfHeader.split(",")[0]; // first IP
	}


}
