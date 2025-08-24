package com.example.demo;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.demo.Enum.Role;
import com.example.demo.Repository.UserRepository;
import com.example.demo.model.UserEntity;

@Component
public class AdminSeeder implements CommandLineRunner {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) throws Exception {
		String email = "admin@yourapp.com";
		if (userRepository.findByEmail(email).isEmpty()) {
			UserEntity admin = new UserEntity();
			admin.setUsername("Admin");
			admin.setEmail(email);
			admin.setPhoneNumber("08000000000");
			admin.setPassword(passwordEncoder.encode("admin123")); // must be BCrypt!
			admin.setRole(Role.ADMIN);
			admin.setEmailVerified(true);
			admin.setCreatedAt(LocalDateTime.now());

			userRepository.save(admin);
			System.out.println("✅ Admin user created");
		}
	}
}
