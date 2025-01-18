package com.example.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Specification;

@Repository
public interface SpecificationRepository extends JpaRepository<Specification, Long> {

}
