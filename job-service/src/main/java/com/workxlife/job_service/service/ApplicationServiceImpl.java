package com.workxlife.job_service.service;

import com.workxlife.job_service.entity.Application;
import com.workxlife.job_service.exception.ResourceNotFoundException;
import com.workxlife.job_service.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.workxlife.job_service.dto.Notification;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import com.workxlife.job_service.util.JwtEmailExtractor;
import com.workxlife.job_service.entity.Job;
import com.workxlife.job_service.repository.JobRepository;

import java.time.LocalDateTime;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class ApplicationServiceImpl {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private HttpServletRequest request;

    public List<Application> getApplicationsByJobId(Long jobId) {
        return applicationRepository.findByJobId(jobId);
    }

    public List<Application> getApplicationsByApplicantId(Long applicantId) {
        return applicationRepository.findByApplicantId(applicantId);
    }

    public Application applyForJob(Application application) {
        if (application.getJob() == null || application.getJob().getId() == null) {
            throw new IllegalArgumentException("Missing job ID in request.");
        }

        Long jobId = application.getJob().getId();

        // Validate that job exists
        jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found with ID: " + jobId));

        Application savedApplication = applicationRepository.save(application);

        try {
            String email = JwtEmailExtractor.extractEmail(request);
            System.out.println("Application received from: " + email);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return savedApplication;
    }


    public void deleteApplication(Long id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found with ID: " + id));
        applicationRepository.delete(application);
    }
}

