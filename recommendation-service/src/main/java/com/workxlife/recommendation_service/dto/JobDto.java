package com.workxlife.recommendation_service.dto;

import java.util.List;

public class JobDto {
    private Long id;
    private String title;
    private String description;
    private List<String> requiredSkills;
    private String companyName;
    private String location;
    private String employmentType;  // FULL_TIME, PART_TIME, CONTRACT
    private double salary;          // or use String if ranges like "70k-90k"
    private Long postedBy;          // Employer ID

    // Getters
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getRequiredSkills() {
        return requiredSkills;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getLocation() {
        return location;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public double getSalary() {
        return salary;
    }

    public Long getPostedBy() {
        return postedBy;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRequiredSkills(List<String> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public void setPostedBy(Long postedBy) {
        this.postedBy = postedBy;
    }
}
