package com.workxlife.recommendation_service.dto;

import lombok.Data;
import java.util.List;

@Data
public class EmployeeProfileDto {
    private String name;
    private String email;  // ✅ Add this
    private List<String> skills;
    private int experienceYears;

    // Getters
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getSkills() {
        return skills;
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }
}
