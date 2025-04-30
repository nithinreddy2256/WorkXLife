package com.workxlife.job_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workxlife.job_service.dto.Notification;
import com.workxlife.job_service.entity.Job;
import com.workxlife.job_service.exception.ResourceNotFoundException;
import com.workxlife.job_service.repository.JobRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.workxlife.job_service.util.JwtEmailExtractor;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class JobService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private HttpServletRequest request;

    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    public Job getJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with ID: " + id));
    }

    public List<Job> searchJobs(String title, String location, String companyName) {
        title = title != null ? title : "";
        location = location != null ? location : "";
        companyName = companyName != null ? companyName : "";

        return jobRepository
                .findByTitleContainingIgnoreCaseAndLocationContainingIgnoreCaseAndCompanyNameContainingIgnoreCase(
                        title, location, companyName
                );
    }

    public Job createJob(Job job) {
        Job savedJob = jobRepository.save(job);

        try {
            String email = JwtEmailExtractor.extractEmail(request);

            Notification notification = new Notification();
            notification.setRecipientId(101L); // (optional, if known)
            notification.setRecipientEmail(email); // ✅ dynamic
            notification.setMessage("New job posted: " + savedJob.getTitle());
            notification.setType("EMAIL");

            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(notification);
            rabbitTemplate.convertAndSend("notification.queue", json);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return savedJob;
    }

    public Job updateJob(Long id, Job jobDetails) {
        Job job = getJobById(id);
        job.setTitle(jobDetails.getTitle());
        job.setDescription(jobDetails.getDescription());
        job.setCompanyName(jobDetails.getCompanyName());
        job.setLocation(jobDetails.getLocation());
        job.setSalary(jobDetails.getSalary());
        job.setEmploymentType(jobDetails.getEmploymentType());
        return jobRepository.save(job);
    }

    public void deleteJob(Long id) {
        Job job = getJobById(id);
        jobRepository.delete(job);
    }
}
