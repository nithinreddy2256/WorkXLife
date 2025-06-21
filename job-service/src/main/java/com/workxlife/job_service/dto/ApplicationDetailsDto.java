package com.workxlife.job_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.workxlife.job_service.dto.ApplicationDetailsDto;
import com.workxlife.job_service.dto.EmployeeDto;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;

public class ApplicationDetailsDto {

    private Long applicantId;
    private String applicantName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime appliedAt;

    private String jobTitle;
    private String companyName;

    public ApplicationDetailsDto(Long applicantId, String applicantName,
                                 LocalDateTime appliedAt, String jobTitle, String companyName) {
        this.applicantId = applicantId;
        this.applicantName = applicantName;
        this.appliedAt = appliedAt;
        this.jobTitle = jobTitle;
        this.companyName = companyName;
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
}
