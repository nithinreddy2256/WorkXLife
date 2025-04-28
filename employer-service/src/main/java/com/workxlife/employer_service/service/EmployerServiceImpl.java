package com.workxlife.employer_service.service;

import com.workxlife.employer_service.exception.ResourceNotFoundException;
import com.workxlife.employer_service.model.Employer;
import com.workxlife.employer_service.repository.EmployerRepository;
import com.workxlife.employer_service.service.EmployerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployerServiceImpl implements EmployerService {
    @Autowired
    private EmployerRepository employerRepository;

    @Override
    public Employer createEmployer(Employer employer) {
        return employerRepository.save(employer);
    }

    @Override
    public Employer getEmployerById(Long id) {
        return employerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employer not found with id: " + id));
    }

    @Override
    public List<Employer> getAllEmployers() {
        return employerRepository.findAll();
    }

    @Override
    public Employer updateEmployer(Long id, Employer employer) {
        Employer existing = getEmployerById(id);
        existing.setCompanyName(employer.getCompanyName());
        existing.setEmail(employer.getEmail());
        existing.setPhone(employer.getPhone());
        existing.setWebsite(employer.getWebsite());
        return employerRepository.save(existing);
    }

    @Override
    public void deleteEmployer(Long id) {
        Employer employer = getEmployerById(id);
        employerRepository.delete(employer);
    }
}
