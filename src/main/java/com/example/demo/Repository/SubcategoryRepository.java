package com.example.demo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Subcategory;

@Repository
public interface SubcategoryRepository extends JpaRepository<Subcategory, Long> {
	boolean existsByNameAndCategoryId(String name, Long categoryId);

	List<Subcategory> findByCategoryId(Long categoryId);

}
