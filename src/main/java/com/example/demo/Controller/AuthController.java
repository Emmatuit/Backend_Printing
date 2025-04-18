package com.example.demo.Controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Dto.RegisterRequest;
import com.example.demo.Enum.Role;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Security.JwtService;
import com.example.demo.Service.CartService;
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



    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return "Username already exists!";
        }

        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Hash password
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(Role.USER); // Default role

        userRepository.save(user);
        return "User registered successfully!";
    }
    
    @PostMapping("/login1")
    public ResponseEntity<?> login(@RequestParam("username") String username, 
                                   @RequestParam("password") String password, 
                                   @RequestParam("sessionId") String sessionId, 
                                   HttpSession session) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            String token = jwtService.generateToken(userDetails.getUsername());

            // ✅ Debugging logs
            System.out.println("🔹 User logged in: " + username);
            System.out.println("🔹 Received sessionId from frontend: " + sessionId);

            // Store sessionId before merging
            session.setAttribute("sessionId", sessionId);

            // ✅ Call merge function and add debug logs
            cartService.mergeSessionCartWithUser(username, session);
            System.out.println("✅ mergeSessionCartWithUser() has been called!");

            // ✅ Fetch user details from the database
            UserEntity user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // ✅ Build the response as a JSON object (Map)
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful"); // Optional success message
            response.put("token", token);
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("phoneNumber", user.getPhoneNumber());

            return ResponseEntity.ok(response); // ✅ Corrected to return the full response map
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("error", "Invalid username or password"));
        }
    }

}


 
