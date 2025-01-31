package com.example.demo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Product;
import com.example.demo.model.Specification;

@Repository
public interface SpecificationRepository extends JpaRepository<Specification, Long> {

	List<Specification> findByProduct(Product product);

	// Custom query to fetch specifications linked to a specific product
	@Query("SELECT s FROM Specification s WHERE s.product.id = :productId")
	List<Specification> findByProductId(@Param("productId") Long productId);

}
