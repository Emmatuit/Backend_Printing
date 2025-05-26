package com.example.demo.Repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Product;
import com.example.demo.model.Subcategory;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	boolean existsByNameAndSubcategoryId(String name, Long subcategoryId);

	List<Product> findByNameContainingIgnoreCase(String name);

	List<Product> findBySubcategory(Subcategory subcategory);

	List<Product> findBySubcategoryCategoryId(Long categoryId);

	@Query("SELECT p FROM Product p WHERE p.subcategory.id = :subcategoryId")
	List<Product> findBySubcategoryId(@Param("subcategoryId") Long subcategoryId);
	@Query("SELECT p FROM Product p WHERE p.category.id = :categoryId " +
	           "AND p.baseprice BETWEEN :minPrice AND :maxPrice " +
	           "AND p.id <> :excludedProductId")
	    Page<Product> findSimilarProducts(
	        @Param("categoryId") Long categoryId,
	        @Param("minPrice") BigDecimal minPrice,  // Changed from Double
	        @Param("maxPrice") BigDecimal maxPrice,  // Changed from Double
	        @Param("excludedProductId") Long excludedProductId,
	        Pageable pageable);

	List<Product> findTop10ByOrderByViewsDesc();

}
