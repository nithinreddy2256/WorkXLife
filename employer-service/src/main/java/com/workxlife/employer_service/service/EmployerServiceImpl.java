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
    public List<Employer> getAllEmployers() {
        return employerRepository.findAll();
    }

    @Override
    public Employer updateEmployer(Long id, Employer employer) {
        Employer existing = getEmployerById(id);

        existing.setCompanyName(employer.getCompanyName());
        existing.setIndustry(employer.getIndustry());
        existing.setWebsite(employer.getWebsite());
        existing.setLocation(employer.getLocation());
        existing.setSize(employer.getSize());
        existing.setEstablishedYear(employer.getEstablishedYear());
        existing.setAbout(employer.getAbout());

        existing.setContactName(employer.getContactName());
        existing.setContactEmail(employer.getContactEmail());
        existing.setContactPhone(employer.getContactPhone());

        existing.setRecruiterName(employer.getRecruiterName());
        existing.setRecruiterRole(employer.getRecruiterRole());
        existing.setRecruiterBio(employer.getRecruiterBio());

        return employerRepository.save(existing);
    }


    @Override
    public void deleteEmployer(Long id) {
        Employer employer = getEmployerById(id);
        employerRepository.delete(employer);
    }

    @Override
    public Employer saveEmployer(Employer employer) {
        return employerRepository.save(employer);
    }

    @Override
    public Employer getEmployerById(Long id) {
        return employerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employer not found"));
    }
}
