package com.example.demo.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Subcategory;

@Repository
public interface SubcategoryRepository extends JpaRepository<Subcategory, Long> {
	boolean existsByNameAndCategoryId(String name, Long categoryId);

	List<Subcategory> findByCategoryId(Long categoryId);

	boolean existsByNameIgnoreCaseAndCategoryId(String name, Long categoryId);

	@Query("SELECT s FROM Subcategory s WHERE LOWER(s.name) = LOWER(:name) AND s.category.id = :categoryId")
	Optional<Subcategory> findByCategoryIdAndName(@Param("categoryId") Long categoryId, @Param("name") String name);

	Optional<Subcategory> findByNameIgnoreCaseAndCategoryId(String name, Long categoryId);

	 Optional<Subcategory> findByName(String name);

}
