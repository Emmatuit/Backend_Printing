package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Dto.OrderRequestDTO;
import com.example.demo.Dto.OrderResponseDto;
import com.example.demo.Service.OrderService;

@RestController
@RequestMapping("/api")
public class OrderController {

	@Autowired
	private OrderService orderService;

	@PostMapping("/place")
	public OrderResponseDto placeOrder(@AuthenticationPrincipal User user, @RequestBody OrderRequestDTO orderRequest) {
		return orderService.placeOrder(user.getUsername(), orderRequest);
	}
}
