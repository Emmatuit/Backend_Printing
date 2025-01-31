package com.example.demo.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Repository.SpecificationOptionRepository;
import com.example.demo.Repository.SpecificationRepository;
import com.example.demo.model.Product;
import com.example.demo.model.Specification;
import com.example.demo.model.SpecificationOption;

import jakarta.transaction.Transactional;

@Service
public class SpecificationService {

	@Autowired
	private SpecificationRepository specificationRepository;

	@Autowired
	private SpecificationOptionRepository specificationOptionRepository;

	public void deleteSpecification(Long id) {
		specificationRepository.deleteById(id);
	}

	public List<Specification> getAllSpecifications() {
		return specificationRepository.findAll();
	}

	public Optional<Specification> getSpecificationById(Long id) {
		return specificationRepository.findById(id);
	}

	@Transactional
	public List<Specification> getSpecificationsByProduct(Product product) {
		return specificationRepository.findByProduct(product);
	}

	public Specification saveSpecification(Specification specification) {
		Specification savedSpecification = specificationRepository.save(specification);

		if (specification.getOptions() != null) { // Add null check
			for (SpecificationOption option : specification.getOptions()) {
				option.setSpecification(savedSpecification);
				specificationOptionRepository.save(option);
			}
		}

		return savedSpecification;
	}
}
