package com.workxlife.job_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.workxlife.job_service.dto.ApplicationDetailsDto;
import com.workxlife.job_service.dto.EmployeeDto;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import org.springframework.web.reactive.function.client.WebClient;
import com.workxlife.job_service.entity.ApplicationStatus;


import java.time.LocalDateTime;

public class ApplicationDetailsDto {

    private Long applicantId;
    private String applicantName;

    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime appliedAt;

    private String jobTitle;
    private String companyName;
    private ApplicationStatus status;


    public ApplicationDetailsDto(Long applicantId, String applicantName,
                                 LocalDateTime appliedAt, String jobTitle,
                                 String companyName, ApplicationStatus status, Long id) {
        this.applicantId = applicantId;
        this.applicantName = applicantName;
        this.appliedAt = appliedAt;
        this.jobTitle = jobTitle;
        this.companyName = companyName;
        this.status = status;
        this.id = id;
    }


    public Long getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(Long applicantId) {
        this.applicantId = applicantId;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(LocalDateTime appliedAt) {
        this.appliedAt = appliedAt;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
