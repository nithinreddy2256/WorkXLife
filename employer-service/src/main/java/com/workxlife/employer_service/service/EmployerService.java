package com.workxlife.employer_service.service;

import com.workxlife.employer_service.model.Employer;

import java.util.List;

public interface EmployerService {
    Employer createEmployer(Employer employer);
    Employer getEmployerById(Long id);
    List<Employer> getAllEmployers();
    Employer updateEmployer(Long id, Employer employer);
    void deleteEmployer(Long id);
}

