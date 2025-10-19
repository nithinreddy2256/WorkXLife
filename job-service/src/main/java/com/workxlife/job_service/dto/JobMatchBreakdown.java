package com.workxlife.job_service.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the calculated compatibility between an employee and a job posting.
 */
public class JobMatchBreakdown {

    private double overallMatchPercentage;
    private double skillsMatchPercentage;
    private double locationMatchPercentage;
    private double experienceMatchPercentage;
    private double skillsWeightPercentage;
    private double locationWeightPercentage;
    private double experienceWeightPercentage;
    private List<String> matchedSkills = new ArrayList<>();
    private List<String> missingSkills = new ArrayList<>();
    private String summary;

    public double getOverallMatchPercentage() {
        return overallMatchPercentage;
    }

    public void setOverallMatchPercentage(double overallMatchPercentage) {
        this.overallMatchPercentage = overallMatchPercentage;
    }

    public double getSkillsMatchPercentage() {
        return skillsMatchPercentage;
    }

    public void setSkillsMatchPercentage(double skillsMatchPercentage) {
        this.skillsMatchPercentage = skillsMatchPercentage;
    }

    public double getLocationMatchPercentage() {
        return locationMatchPercentage;
    }

    public void setLocationMatchPercentage(double locationMatchPercentage) {
        this.locationMatchPercentage = locationMatchPercentage;
    }

    public double getExperienceMatchPercentage() {
        return experienceMatchPercentage;
    }

    public void setExperienceMatchPercentage(double experienceMatchPercentage) {
        this.experienceMatchPercentage = experienceMatchPercentage;
    }

    public double getSkillsWeightPercentage() {
        return skillsWeightPercentage;
    }

    public void setSkillsWeightPercentage(double skillsWeightPercentage) {
        this.skillsWeightPercentage = skillsWeightPercentage;
    }

    public double getLocationWeightPercentage() {
        return locationWeightPercentage;
    }

    public void setLocationWeightPercentage(double locationWeightPercentage) {
        this.locationWeightPercentage = locationWeightPercentage;
    }

    public double getExperienceWeightPercentage() {
        return experienceWeightPercentage;
    }

    public void setExperienceWeightPercentage(double experienceWeightPercentage) {
        this.experienceWeightPercentage = experienceWeightPercentage;
    }

    public List<String> getMatchedSkills() {
        return matchedSkills;
    }

    public void setMatchedSkills(List<String> matchedSkills) {
        this.matchedSkills = matchedSkills != null ? new ArrayList<>(matchedSkills) : new ArrayList<>();
    }

    public List<String> getMissingSkills() {
        return missingSkills;
    }

    public void setMissingSkills(List<String> missingSkills) {
        this.missingSkills = missingSkills != null ? new ArrayList<>(missingSkills) : new ArrayList<>();
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
