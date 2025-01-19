package com.example.demo.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.SpecificationOption;

@Repository
public interface SpecificationOptionRepository extends JpaRepository<SpecificationOption, Long> {
	
	 Optional<SpecificationOption> findById(Long id);
}