package com.example.demo.Service;


import com.example.demo.Repository.SpecificationOptionRepository;
import com.example.demo.Repository.SpecificationRepository;
import com.example.demo.model.Product;
import com.example.demo.model.Specification;
import com.example.demo.model.SpecificationOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SpecificationService {

    @Autowired
    private SpecificationRepository specificationRepository;
    
    

    @Autowired
    private SpecificationOptionRepository specificationOptionRepository;

    /**
     * Save a new specification along with its options.
     * @param specification The specification to save.
     * @return The saved specification.
     */
    public Specification saveSpecification(Specification specification) {
        // Save the specification
        Specification savedSpecification = specificationRepository.save(specification);

        // Save all specification options
        for (SpecificationOption option : specification.getOptions()) {
            option.setSpecification(savedSpecification); // Link each option to the saved specification
            specificationOptionRepository.save(option);// Save the option
        }

        return savedSpecification;
    }
    /**
     * Get a specification by its ID.
     * @param id The ID of the specification.
     * @return The specification, if found.
     */
    public Optional<Specification> getSpecificationById(Long id) {
        return specificationRepository.findById(id);
    }

    /**
     * Get all specifications.
     * @return List of all specifications.
     */
    public List<Specification> getAllSpecifications() {
        return specificationRepository.findAll();
    }

    /**
     * Delete a specification by its ID.
     * @param id The ID of the specification to delete.
     */
    public void deleteSpecification(Long id) {
        specificationRepository.deleteById(id);
    }
    
    

    /**
     * Retrieves all specifications for a given product.
     * 
     * @param product The product for which specifications are fetched.
     * @return List of specifications.
     */
    public List<Specification> getSpecificationsByProduct(Product product) {
        return specificationRepository.findByProduct(product);
    }
}
