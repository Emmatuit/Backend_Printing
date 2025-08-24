package com.example.demo.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Dto.UserDto;
import com.example.demo.Repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	public long getUserCount() {
		return userRepository.count();
	}

	public List<UserDto> getAllUsers() {
		return userRepository
				.findAll().stream().map(user -> new UserDto(user.getId(), user.getUsername(), user.getEmail(),
						user.getPhoneNumber(), user.getRole(), user.isEmailVerified(), user.getCreatedAt()))
				.collect(Collectors.toList());
	}

}
