package com.example.demo.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Product;
import com.example.demo.model.Subcategory;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	boolean existsByNameAndSubcategoryId(String name, Long subcategoryId);

	// 1. Search by name, ignoring case, and only include non-deleted products
	List<Product> findByNameContainingIgnoreCaseAndIsDeletedFalse(String name);

	// 2. Find all products under a category ID via subcategory, and not deleted
	List<Product> findBySubcategoryCategoryIdAndIsDeletedFalse(Long categoryId);

	// 3. Find all products by subcategory ID, and not deleted
	List<Product> findBySubcategoryIdAndIsDeletedFalse(Long subcategoryId);

	Page<Product> findBySubcategoryIdAndIsDeletedFalse(Long subcategoryId, Pageable pageable);

	// 4. Find similar products within price range, not deleted, and excluding a
	// product ID
	@Query("SELECT p FROM Product p WHERE p.category.id = :categoryId "
			+ "AND p.baseprice BETWEEN :minPrice AND :maxPrice "
			+ "AND p.id <> :excludedProductId AND p.isDeleted = false")
	Page<Product> findSimilarProductsNotDeleted(@Param("categoryId") Long categoryId,
			@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice,
			@Param("excludedProductId") Long excludedProductId, Pageable pageable);

	List<Product> findTop10ByIsDeletedFalseOrderByViewsDesc();

	@Modifying
	@Query(value = "DELETE FROM specification_option WHERE specification_id IN (SELECT id FROM specification WHERE product_id = :productId)", nativeQuery = true)
	void deleteSpecOptionsByProductId(@Param("productId") Long productId);

	@Modifying
	@Query(value = "DELETE FROM specification WHERE product_id = :productId", nativeQuery = true)
	void deleteSpecificationsByProductId(@Param("productId") Long productId);

	@Modifying
	@Query(value = "DELETE FROM product_encrypted_images WHERE product_id = :productId", nativeQuery = true)
	void deleteEncryptedImagesByProductId(@Param("productId") Long productId);

	@Modifying
	@Query(value = "DELETE FROM product WHERE id = :productId", nativeQuery = true)
	void hardDeleteById(@Param("productId") Long productId);

	List<Product> findByIsDeletedFalse();

	Optional<Product> findByIdAndIsDeletedFalse(Long id);

	List<Product> findBySubcategoryAndIsDeletedFalse(Subcategory subcategory);

	long countBySubcategoryId(Long subcategoryId);

}
