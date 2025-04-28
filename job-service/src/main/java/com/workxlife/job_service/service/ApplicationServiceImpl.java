package com.workxlife.job_service.service;

import com.workxlife.job_service.entity.Application;
import com.workxlife.job_service.exception.ResourceNotFoundException;
import com.workxlife.job_service.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.workxlife.job_service.dto.Notification;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;


import java.util.List;

@Service
public class ApplicationServiceImpl {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Autowired
    private ApplicationRepository applicationRepository;

    public List<Application> getApplicationsByJobId(Long jobId) {
        return applicationRepository.findByJobId(jobId);
    }

    public List<Application> getApplicationsByApplicantId(Long applicantId) {
        return applicationRepository.findByApplicantId(applicantId);
    }

    public Application applyForJob(Application application) {
        Application savedApplication = applicationRepository.save(application);

        Notification notification = new Notification();
        notification.setRecipientId(application.getApplicantId());
        notification.setRecipientEmail("user@example.com"); // replace  with actual email
        notification.setMessage("You successfully applied for Job ID: " + application.getJob().getId());
        notification.setType("EMAIL");

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(notification);
            rabbitTemplate.convertAndSend("notification.queue", json);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return savedApplication;
    }

    public void deleteApplication(Long id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with ID: " + id));
        applicationRepository.delete(application);
    }
}
