package com.example.demo.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long>{

}
