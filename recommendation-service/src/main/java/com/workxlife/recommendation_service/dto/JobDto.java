package com.workxlife.recommendation_service.dto;

import java.util.List;

public class JobDto {
    private Long id;
    private String title;
    private String description; // ✅ Added
    private List<String> requiredSkills;

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
}
