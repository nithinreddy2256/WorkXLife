package com.workxlife.recommendation_service.dto;

import java.util.List;

public class EmployeeProfileDto {
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String department;
    private int experienceYears;
    private String professionalSummary;
    private String location;
    private List<String> skills;
    private List<String> certifications;
    private List<String> preferredJobTitles;

    // Getters
    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getDepartment() {
        return department;
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    public String getProfessionalSummary() {
        return professionalSummary;
    }

    public String getLocation() {
        return location;
    }

    public List<String> getSkills() {
        return skills;
    }

    public List<String> getCertifications() {
        return certifications;
    }

    public List<String> getPreferredJobTitles() {
        return preferredJobTitles;
    }

    // Setters
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }

    public void setProfessionalSummary(String professionalSummary) {
        this.professionalSummary = professionalSummary;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public void setCertifications(List<String> certifications) {
        this.certifications = certifications;
    }

    public void setPreferredJobTitles(List<String> preferredJobTitles) {
        this.preferredJobTitles = preferredJobTitles;
    }

    // Optional: convenience method
    public String getFullName() {
        return (firstName != null ? firstName : "") +
                (middleName != null ? " " + middleName : "") +
                (lastName != null ? " " + lastName : "");
    }
}
