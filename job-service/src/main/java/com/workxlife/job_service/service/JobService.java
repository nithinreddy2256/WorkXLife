package com.workxlife.job_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workxlife.job_service.client.EmployeeProfileClient;
import com.workxlife.job_service.client.EmployerClient;
import com.workxlife.job_service.dto.EmployeeProfileDTO;
import com.workxlife.job_service.dto.EmployerSummaryDTO;
import com.workxlife.job_service.dto.JobDTO;
import com.workxlife.job_service.dto.JobDetailsResponse;
import com.workxlife.job_service.dto.JobMatchBreakdown;
import com.workxlife.job_service.dto.Notification;
import com.workxlife.job_service.entity.Job;
import com.workxlife.job_service.exception.ResourceNotFoundException;
import com.workxlife.job_service.repository.JobRepository;
import com.workxlife.job_service.util.JwtEmailExtractor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class JobService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private EmployerClient employerClient;

    @Autowired
    private EmployeeProfileClient employeeProfileClient;

    @Autowired
    private JobMatchService jobMatchService;

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

    public Job createJob(JobDTO jobDTO) {

        Job job = new Job();
        job.setTitle(jobDTO.getTitle());
        job.setDescription(jobDTO.getDescription());
        job.setCompanyName(jobDTO.getCompanyName());
        job.setLocation(jobDTO.getLocation());
        job.setSalary(jobDTO.getSalary());
        job.setEmploymentType(jobDTO.getEmploymentType());
        job.setWorkMode(jobDTO.getWorkMode());
        job.setExperienceLevel(jobDTO.getExperienceLevel());
        job.setMinExperienceYears(jobDTO.getMinExperienceYears());
        job.setMaxExperienceYears(jobDTO.getMaxExperienceYears());
        job.setRoleSummary(jobDTO.getRoleSummary());
        job.setKeySkills(jobDTO.getKeySkills());
        job.setResponsibilities(jobDTO.getResponsibilities());
        job.setBenefits(jobDTO.getBenefits());
        job.setPostedBy(jobDTO.getPostedBy());
        job.setCreatedAt(LocalDateTime.now());
        job.setUpdatedAt(LocalDateTime.now());

        Job savedJob = jobRepository.save(job);

        try {
            String email = JwtEmailExtractor.extractEmail(request);
            Notification notification = new Notification();
            notification.setRecipientId(101L); // optional
            notification.setRecipientEmail(email);
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

    public List<Job> getJobsByEmployer(Long employerId) {
        return jobRepository.findByPostedBy(employerId);
    }




    public Job updateJob(Long id, Job jobDetails) {
        Job job = getJobById(id);
        job.setTitle(jobDetails.getTitle());
        job.setDescription(jobDetails.getDescription());
        job.setCompanyName(jobDetails.getCompanyName());
        job.setLocation(jobDetails.getLocation());
        job.setSalary(jobDetails.getSalary());
        job.setEmploymentType(jobDetails.getEmploymentType());
        job.setWorkMode(jobDetails.getWorkMode());
        job.setExperienceLevel(jobDetails.getExperienceLevel());
        job.setMinExperienceYears(jobDetails.getMinExperienceYears());
        job.setMaxExperienceYears(jobDetails.getMaxExperienceYears());
        job.setRoleSummary(jobDetails.getRoleSummary());
        job.setKeySkills(jobDetails.getKeySkills());
        job.setResponsibilities(jobDetails.getResponsibilities());
        job.setBenefits(jobDetails.getBenefits());
        return jobRepository.save(job);
    }

    public void deleteJob(Long id) {
        Job job = getJobById(id);
        jobRepository.delete(job);
    }

    public JobDetailsResponse getJobDetails(Long id, Long employeeId, String authorizationHeader) {
        Job job = getJobById(id);

        JobDetailsResponse response = new JobDetailsResponse();
        response.setId(job.getId());
        response.setTitle(job.getTitle());
        response.setDescription(job.getDescription());
        response.setCompanyName(job.getCompanyName());
        response.setLocation(job.getLocation());
        response.setSalary(job.getSalary());
        response.setEmploymentType(job.getEmploymentType());
        response.setWorkMode(job.getWorkMode());
        response.setExperienceLevel(job.getExperienceLevel());
        response.setMinExperienceYears(job.getMinExperienceYears());
        response.setMaxExperienceYears(job.getMaxExperienceYears());
        response.setRoleSummary(job.getRoleSummary());
        response.setKeySkills(new ArrayList<>(
                job.getKeySkills() != null ? job.getKeySkills() : Collections.emptyList()
        ));
        response.setResponsibilities(new ArrayList<>(
                job.getResponsibilities() != null ? job.getResponsibilities() : Collections.emptyList()
        ));
        response.setBenefits(new ArrayList<>(
                job.getBenefits() != null ? job.getBenefits() : Collections.emptyList()
        ));
        response.setCreatedAt(job.getCreatedAt());
        response.setUpdatedAt(job.getUpdatedAt());

        EmployerSummaryDTO employer = employerClient.fetchEmployerDetails(job.getPostedBy());
        response.setEmployer(employer);

        if (employeeId != null) {
            EmployeeProfileDTO employeeProfile = employeeProfileClient.fetchEmployee(employeeId, authorizationHeader);
            if (employeeProfile != null) {
                JobMatchBreakdown matchBreakdown = jobMatchService.calculate(job, employeeProfile);
                response.setMatchBreakdown(matchBreakdown);
            }
        }

        return response;
    }
}
