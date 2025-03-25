package com.workxlife.job_service.controller;

import com.workxlife.job_service.entity.Application;
import com.workxlife.job_service.service.ApplicationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    @Autowired
    private ApplicationServiceImpl applicationService;

    @GetMapping("/job/{jobId}")
    public List<Application> getApplicationsByJobId(@PathVariable Long jobId) {
        return applicationService.getApplicationsByJobId(jobId);
    }

    @GetMapping("/applicant/{applicantId}")
    public List<Application> getApplicationsByApplicantId(@PathVariable Long applicantId) {
        return applicationService.getApplicationsByApplicantId(applicantId);
    }

    @PostMapping
    public ResponseEntity<Application> applyForJob(@Valid @RequestBody Application application) {
        return ResponseEntity.ok(applicationService.applyForJob(application));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteApplication(@PathVariable Long id) {
        applicationService.deleteApplication(id);
        return ResponseEntity.ok("Application deleted successfully.");
    }
}
