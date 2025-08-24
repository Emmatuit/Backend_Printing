package com.example.demo.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

	// Check if a category with the given name already exists
	boolean existsByName(String name);

	@Query("SELECT c FROM Category c LEFT JOIN FETCH c.subcategories s LEFT JOIN FETCH s.products WHERE c.id = :categoryId")
	Category findByIdWithSubcategoriesAndProducts(@Param("categoryId") Long categoryId);

	Optional<Category> findByNameIgnoreCase(String name);

	boolean existsByNameIgnoreCase(String name);

}
