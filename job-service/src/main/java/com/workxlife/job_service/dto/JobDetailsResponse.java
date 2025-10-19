package com.workxlife.job_service.dto;

import com.workxlife.job_service.entity.EmploymentType;
import com.workxlife.job_service.entity.WorkMode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JobDetailsResponse {
    private Long id;
    private String title;
    private String description;
    private String companyName;
    private String location;
    private BigDecimal salary;
    private EmploymentType employmentType;
    private WorkMode workMode;
    private String experienceLevel;
    private Integer minExperienceYears;
    private Integer maxExperienceYears;
    private String roleSummary;
    private List<String> keySkills = new ArrayList<>();
    private List<String> responsibilities = new ArrayList<>();
    private List<String> benefits = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private EmployerSummaryDTO employer;
    private JobMatchBreakdown matchBreakdown;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public EmploymentType getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(EmploymentType employmentType) {
        this.employmentType = employmentType;
    }

    public WorkMode getWorkMode() {
        return workMode;
    }

    public void setWorkMode(WorkMode workMode) {
        this.workMode = workMode;
    }

    public String getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(String experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    public Integer getMinExperienceYears() {
        return minExperienceYears;
    }

    public void setMinExperienceYears(Integer minExperienceYears) {
        this.minExperienceYears = minExperienceYears;
    }

    public Integer getMaxExperienceYears() {
        return maxExperienceYears;
    }

    public void setMaxExperienceYears(Integer maxExperienceYears) {
        this.maxExperienceYears = maxExperienceYears;
    }

    public String getRoleSummary() {
        return roleSummary;
    }

    public void setRoleSummary(String roleSummary) {
        this.roleSummary = roleSummary;
    }

    public List<String> getKeySkills() {
        return keySkills;
    }

    public void setKeySkills(List<String> keySkills) {
        this.keySkills = keySkills != null ? new ArrayList<>(keySkills) : new ArrayList<>();
    }

    public List<String> getResponsibilities() {
        return responsibilities;
    }

    public void setResponsibilities(List<String> responsibilities) {
        this.responsibilities = responsibilities != null ? new ArrayList<>(responsibilities) : new ArrayList<>();
    }

    public List<String> getBenefits() {
        return benefits;
    }

    public void setBenefits(List<String> benefits) {
        this.benefits = benefits != null ? new ArrayList<>(benefits) : new ArrayList<>();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public EmployerSummaryDTO getEmployer() {
        return employer;
    }

    public void setEmployer(EmployerSummaryDTO employer) {
        this.employer = employer;
    }

    public JobMatchBreakdown getMatchBreakdown() {
        return matchBreakdown;
    }

    public void setMatchBreakdown(JobMatchBreakdown matchBreakdown) {
        this.matchBreakdown = matchBreakdown;
    }
}

