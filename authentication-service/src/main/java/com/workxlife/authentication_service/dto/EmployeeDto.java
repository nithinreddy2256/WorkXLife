package com.workxlife.authentication_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;


public class EmployeeDto {

    private Long id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String department;
    private String summary;
    private List<String> skills;
    private String profileImageUrl;
    private String currentPosition;
    private String location;
    private int experienceYears;
    private List<String> education;
    private List<String> certifications;

    public Long getId() {
        return id;
    }

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

    public String getSummary() {
        return summary;
    }

    public List<String> getSkills() {
        return skills;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getCurrentPosition() {
        return currentPosition;
    }

    public String getLocation() {
        return location;
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    public List<String> getEducation() {
        return education;
    }

    public List<String> getCertifications() {
        return certifications;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void setCurrentPosition(String currentPosition) {
        this.currentPosition = currentPosition;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }

    public void setEducation(List<String> education) {
        this.education = education;
    }

    public void setCertifications(List<String> certifications) {
        this.certifications = certifications;
    }
}
