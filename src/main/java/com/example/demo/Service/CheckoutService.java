package com.example.demo.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.Dto.CheckoutRequest;
import com.example.demo.Dto.OrderDto;
import com.example.demo.Dto.OrderItemDto;
import com.example.demo.Enum.OrderStatus;
import com.example.demo.Repository.OrderRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.model.Cart;
import com.example.demo.model.CartItem;
import com.example.demo.model.Coupon;
import com.example.demo.model.Order;
import com.example.demo.model.Order.ShippingMethod;
import com.example.demo.model.OrderItem;
import com.example.demo.model.UserEntity;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class CheckoutService {

    private final CouponService couponService;
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final ProductService productService;
    private final UserRepository userRepository;

    public CheckoutService(CouponService couponService, OrderRepository orderRepository, CartService cartService,
                           ProductService productService, UserRepository userRepository) {
        this.couponService = couponService;
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.productService = productService;
        this.userRepository = userRepository;
    }

    public OrderDto checkout(CheckoutRequest request, UserEntity user) {
        // 1. Get user's cart
        Cart cart = cartService.getCartByUser(user);
        if (cart == null || cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty.");
        }

        // 2. Calculate subtotal from cart items and prepare order items
        BigDecimal subtotal = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cart.getItems()) {
            var product = cartItem.getProduct();
            int quantity = cartItem.getSelectedQuantity();

            List<Long> selectedOptionIds = cartItem.getSelectedOptions().stream()
                    .map(option -> option.getId())
                    .collect(Collectors.toList());

            BigDecimal itemSubTotal = productService.calculateTotalPrice(product.getId(), quantity, selectedOptionIds);
            subtotal = subtotal.add(itemSubTotal);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(null); // will set after order creation
            orderItem.setProduct(product);
            orderItem.setQuantity(quantity);
            orderItem.setPrice(product.getBaseprice());
            orderItem.setSubTotal(itemSubTotal);

            orderItems.add(orderItem);
        }

        // 3. Coupon validation & discount calculation
        BigDecimal discountAmount = BigDecimal.ZERO;
        String couponCode = request.getCouponCode();
        Optional<Coupon> couponOpt = Optional.empty();

        if (couponCode != null && !couponCode.trim().isEmpty()) {
            couponOpt = couponService.validateCoupon(couponCode.trim(), subtotal);

            if (couponOpt.isPresent()) {
                Coupon coupon = couponOpt.get();

                // Check first-time user: user must have no previous orders
                boolean isFirstTimeUser = orderRepository.countByUser(user) == 0;

                if (isFirstTimeUser && subtotal.compareTo(new BigDecimal("20000")) >= 0) {
                    // Apply discount according to coupon type
                    if (coupon.getDiscountType() == Coupon.DiscountType.PERCENTAGE) {
                        discountAmount = subtotal.multiply(coupon.getDiscountValue().divide(BigDecimal.valueOf(100)));
                    } else if (coupon.getDiscountType() == Coupon.DiscountType.FIXED_AMOUNT) {
                        discountAmount = coupon.getDiscountValue();
                    }

                    discountAmount = discountAmount.setScale(2, RoundingMode.HALF_UP);
                }
            }
        }

        // 4. Delivery fee calculation
        BigDecimal deliveryFee = BigDecimal.ZERO;
        if (request.getShippingMethod() == ShippingMethod.DELIVERY) {
            deliveryFee = new BigDecimal("1500.00");
        }

        // 5. VAT calculation (7.5% on (subtotal - discount + delivery))
        BigDecimal vatRate = new BigDecimal("0.075");
        BigDecimal vatAmount = (subtotal.subtract(discountAmount).add(deliveryFee))
                .multiply(vatRate).setScale(2, RoundingMode.HALF_UP);

        // 6. Grand total
        BigDecimal grandTotal = subtotal.subtract(discountAmount).add(deliveryFee).add(vatAmount);

        // 7. Create order entity & set all fields
        Order order = new Order();

        order.setUser(user);
        order.setFullName(request.getFullName());
        order.setEmail(request.getEmail());
        order.setPhoneNumber(request.getPhoneNumber());

        // Address
        if (request.getShippingMethod() == ShippingMethod.PICKUP) {
            order.setShippingAddress("123 Company Street, Victoria Island, Lagos, Nigeria"); // Your pickup address
        } else {
            order.setShippingAddress(request.getAddress1() + ", " + request.getState() + ", " + request.getPostalCode());
        }
        order.setAddress1(request.getAddress1());
        order.setAddress2(request.getAddress2());
        order.setState(request.getState());
        order.setPostalCode(request.getPostalCode());

        // Payment & Shipping
        order.setPaymentMethod(request.getPaymentMethod());
        order.setShippingMethod(request.getShippingMethod());

        // Amounts
        order.setTotalAmount(subtotal.setScale(2, RoundingMode.HALF_UP));
        order.setDiscountAmount(discountAmount);
        order.setShippingFee(deliveryFee);
        order.setTaxAmount(vatAmount);
        order.setGrandTotal(grandTotal);
        order.setOrderNumber(generateOrderNumber());

        // Coupon info
        order.setCouponCode(couponCode);

        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        // Attach order items and set order reference in each
        orderItems.forEach(item -> item.setOrder(order));
        order.setItems(orderItems);

        // 8. Save order to DB
        Order savedOrder = orderRepository.save(order);

        // 9. Clear cart
        cartService.clearCart(user);

        // 10. Increment coupon usage if applicable
        if (discountAmount.compareTo(BigDecimal.ZERO) > 0 && couponOpt.isPresent()) {
            couponService.incrementUsage(couponOpt.get());
        }

        // 11. Map and return OrderDto (make sure to implement mapToDto yourself)
        return mapToDto(savedOrder);
    }


    //Permanently Delete Orders
    @Transactional
    public void deleteOrder(Order order) {
        // If you have cascading enabled, items will be deleted with order.
        orderRepository.delete(order);
    }

    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }


    @Transactional
    public Order getOrderByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new EntityNotFoundException("Order with orderNumber " + orderNumber + " not found"));
    }

    @Transactional
    public Order getOrderByTxRef(String txRef) {
        return orderRepository.findByTxRef(txRef)
                .orElseThrow(() -> new EntityNotFoundException("Order with txRef " + txRef + " not found"));
    }

    public OrderDto getOrderSummary(Long orderId, UserEntity user) {
        Order order = orderRepository.findByIdAndUser(orderId, user)
                .orElseThrow(() -> new NoSuchElementException("Order not found"));

        return mapToDto(order); // Reuse your existing mapper
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

    @Transactional
    public void markOrderAsFailed(String orderNumber) {
        Order order = getOrderByOrderNumber(orderNumber);
        order.setPaymentStatus(Order.PaymentStatus.FAILED);
        order.setStatus(OrderStatus.FAILED);
        orderRepository.save(order);
    }


    @Transactional
    public void markOrderAsPaid(String orderNumber, String flutterwaveTransactionId) {
        Order order = getOrderByOrderNumber(orderNumber);
        order.setPaymentStatus(Order.PaymentStatus.COMPLETED);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setPaymentId(flutterwaveTransactionId);
        order.setPaymentDate(LocalDateTime.now());
        orderRepository.save(order);
    }




}