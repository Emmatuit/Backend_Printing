package com.example.demo.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Repository.SpecificationOptionRepository;
import com.example.demo.model.Specification;
import com.example.demo.model.SpecificationOption;

@Service
public class SpecificationOptionService {

    @Autowired
    private SpecificationOptionRepository specificationOptionRepository;


    public List<SpecificationOption> getSpecificationOptionsBySpecification(Specification specification) {
        return specificationOptionRepository.findBySpecification(specification);
    }

    public SpecificationOption saveSpecificationOption(SpecificationOption option) {
        if (option == null) {
            throw new IllegalArgumentException("Specification option cannot be null");
        }
        return specificationOptionRepository.save(option);
    }
}
