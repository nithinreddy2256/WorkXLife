package com.workxlife.job_service.dto;

import com.workxlife.job_service.entity.EmploymentType;
import com.workxlife.job_service.entity.WorkMode;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobDTO {
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

    @NotNull(message = "PostedBy is required")
    private Long postedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getLocation() {
        return location;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public EmploymentType getEmploymentType() {
        return employmentType;
    }

    public WorkMode getWorkMode() {
        return workMode;
    }

    public Long getPostedBy() {
        return postedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public void setEmploymentType(EmploymentType employmentType) {
        this.employmentType = employmentType;
    }

    public void setWorkMode(WorkMode workMode) {
        this.workMode = workMode;
    }

    public void setPostedBy(Long postedBy) {
        this.postedBy = postedBy;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
