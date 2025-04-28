package com.workxlife.recommendation_service.dto;

import lombok.Data;
import java.util.List;

@Data
public class JobDto {
    private Long id;
    private String title;
    private List<String> requiredSkills;

    // Getters
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
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

    public void setRequiredSkills(List<String> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

}

