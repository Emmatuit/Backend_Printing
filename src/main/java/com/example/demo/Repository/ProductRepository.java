package com.example.demo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Product;
import com.example.demo.model.Subcategory;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	boolean existsByNameAndSubcategoryId(String name, Long subcategoryId);

	List<Product> findBySubcategory(Subcategory subcategory);

	List<Product> findBySubcategoryCategoryId(Long categoryId);

	@Query("SELECT p FROM Product p WHERE p.subcategory.id = :subcategoryId")
	List<Product> findBySubcategoryId(@Param("subcategoryId") Long subcategoryId);
}
