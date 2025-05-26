package com.example.demo.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.Dto.OrderItemDto;
import com.example.demo.Dto.OrderRequestDTO;
import com.example.demo.Dto.OrderResponseDto;
import com.example.demo.Dto.ShippingdetailsDto;
import com.example.demo.Enum.OrderStatus;
import com.example.demo.Repository.CartItemRepository;
import com.example.demo.Repository.CartRepository;
import com.example.demo.Repository.OrderRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.model.Cart;
import com.example.demo.model.CartItem;
import com.example.demo.model.Order;
import com.example.demo.model.OrderItem;
import com.example.demo.model.UserEntity;

@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private CartItemRepository cartItemRepository;

	@Autowired
	private UserRepository userRepository;

	private OrderResponseDto convertToOrderResponseDTO(Order order) {
		OrderResponseDto responseDTO = new OrderResponseDto();
		responseDTO.setId(order.getId());
		responseDTO.setUsername(order.getUser().getUsername());
		responseDTO.setTotalAmount(order.getTotalAmount());
		responseDTO.setShippingAddress(order.getAddress1() + ", " + order.getState());
		responseDTO.setStatus(order.getStatus().name());
		responseDTO.setCreatedAt(order.getCreatedAt());

		List<OrderItemDto> itemDTOs = order.getItems().stream().map(item -> {
			OrderItemDto itemDTO = new OrderItemDto();
			itemDTO.setProductId(item.getProduct().getId());
			itemDTO.setProductName(item.getProduct().getName());
			itemDTO.setQuantity(item.getQuantity());
			itemDTO.setPrice(item.getPrice());
			return itemDTO;
		}).collect(Collectors.toList());

		responseDTO.setItems(itemDTOs);
		return responseDTO;
	}

	@Transactional
	public OrderResponseDto placeOrder(String username, OrderRequestDTO orderRequest) {
	    // Fetch user
	    UserEntity user = userRepository.findByUsername(username)
	            .orElseThrow(() -> new RuntimeException("User not found"));

	    // Get user's cart
	    Cart cart = cartRepository.findByUser(user)
	            .orElseThrow(() -> new RuntimeException("Cart is empty"));

	    if (cart.getItems().isEmpty()) {
	        throw new RuntimeException("Cart has no items to order");
	    }

	    // Get shipping details
	    ShippingdetailsDto shipping = orderRequest.getShippingDetails();

	    // Create order
	    Order order = new Order();
	    order.setUser(user);
	    order.setFullName(shipping.getFullName());
	    order.setEmail(shipping.getEmail());
	    order.setPhoneNumber(shipping.getPhoneNumber());
	    order.setAddress1(shipping.getAddress1());
	    order.setAddress2(shipping.getAddress2());
	    order.setState(shipping.getState());
	    order.setPostalCode(shipping.getPostalCode());
	    order.setStatus(OrderStatus.PENDING);

	    // Convert cart items to order items with BigDecimal
	    List<OrderItem> orderItems = cart.getItems().stream().map(cartItem -> {
	        OrderItem orderItem = new OrderItem();
	        orderItem.setOrder(order);
	        orderItem.setProduct(cartItem.getProduct());
	        orderItem.setQuantity(cartItem.getSelectedQuantity());
	        
	        // Convert to BigDecimal if cart uses double
	        BigDecimal itemPrice = cartItem.getTotalPrice() != null ?
	            cartItem.getTotalPrice() : // Assuming CartItem now uses BigDecimal
	            BigDecimal.valueOf(cartItem.getTotalPriceAsDouble()); // Fallback for legacy
	        
	        orderItem.setPrice(itemPrice);
	        return orderItem;
	    }).collect(Collectors.toList());

	    order.setItems(orderItems);
	    
	    // Calculate total amount
	 // Calculate total amount properly
	    BigDecimal total = order.getItems().stream()
	            .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
	            .reduce(BigDecimal.ZERO, BigDecimal::add)
	            .setScale(2, RoundingMode.HALF_UP);
	    
	    order.setTotalAmount(total);

	    // Save the order
	    orderRepository.save(order);

	    // Clear the cart
	    cartItemRepository.deleteAll(cart.getItems());
	    cartRepository.delete(cart);

	    return convertToOrderResponseDTO(order);
	}
}
