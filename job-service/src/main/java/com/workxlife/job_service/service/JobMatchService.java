package com.workxlife.job_service.service;

import com.workxlife.job_service.dto.EmployeeProfileDTO;
import com.workxlife.job_service.dto.JobMatchBreakdown;
import com.workxlife.job_service.entity.Job;
import com.workxlife.job_service.entity.WorkMode;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class JobMatchService {

    private static final double SKILLS_WEIGHT = 0.5;
    private static final double LOCATION_WEIGHT = 0.2;
    private static final double EXPERIENCE_WEIGHT = 0.3;

    public JobMatchBreakdown calculate(Job job, EmployeeProfileDTO employeeProfile) {
        if (job == null || employeeProfile == null) {
            return null;
        }

        double skillsScore = computeSkillsScore(job, employeeProfile);
        double locationScore = computeLocationScore(job, employeeProfile);
        double experienceScore = computeExperienceScore(job, employeeProfile);

        double weightedTotal = (skillsScore * SKILLS_WEIGHT)
                + (locationScore * LOCATION_WEIGHT)
                + (experienceScore * EXPERIENCE_WEIGHT);

        JobMatchBreakdown breakdown = new JobMatchBreakdown();
        breakdown.setSkillsMatchPercentage(roundToOneDecimal(skillsScore * 100));
        breakdown.setLocationMatchPercentage(roundToOneDecimal(locationScore * 100));
        breakdown.setExperienceMatchPercentage(roundToOneDecimal(experienceScore * 100));
        breakdown.setOverallMatchPercentage(roundToOneDecimal(weightedTotal * 100));
        breakdown.setSkillsWeightPercentage(SKILLS_WEIGHT * 100);
        breakdown.setLocationWeightPercentage(LOCATION_WEIGHT * 100);
        breakdown.setExperienceWeightPercentage(EXPERIENCE_WEIGHT * 100);

        populateSkillBreakdown(job, employeeProfile, breakdown);
        breakdown.setSummary(buildSummary(breakdown));

        return breakdown;
    }

    private void populateSkillBreakdown(Job job, EmployeeProfileDTO employeeProfile, JobMatchBreakdown breakdown) {
        List<String> jobSkills = Optional.ofNullable(job.getKeySkills()).orElseGet(Collections::emptyList);
        List<String> employeeSkills = Optional.ofNullable(employeeProfile.getSkills()).orElseGet(Collections::emptyList);

        Map<String, String> normalizedJobSkillToOriginal = jobSkills.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toMap(
                        skill -> skill.toLowerCase(Locale.ENGLISH),
                        skill -> skill,
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));

        Set<String> normalizedEmployeeSkills = employeeSkills.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(skill -> skill.toLowerCase(Locale.ENGLISH))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        List<String> matchedSkills = normalizedJobSkillToOriginal.entrySet().stream()
                .filter(entry -> normalizedEmployeeSkills.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        List<String> missingSkills = normalizedJobSkillToOriginal.entrySet().stream()
                .filter(entry -> !normalizedEmployeeSkills.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        breakdown.setMatchedSkills(matchedSkills);
        breakdown.setMissingSkills(missingSkills);
    }

    private double computeSkillsScore(Job job, EmployeeProfileDTO employeeProfile) {
        List<String> jobSkills = Optional.ofNullable(job.getKeySkills()).orElseGet(Collections::emptyList);
        if (jobSkills.isEmpty()) {
            return 1.0;
        }

        List<String> employeeSkills = Optional.ofNullable(employeeProfile.getSkills()).orElseGet(Collections::emptyList);
        if (employeeSkills.isEmpty()) {
            return 0.0;
        }

        Set<String> normalizedJobSkills = jobSkills.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(skill -> skill.toLowerCase(Locale.ENGLISH))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if (normalizedJobSkills.isEmpty()) {
            return 1.0;
        }

        Set<String> normalizedEmployeeSkills = employeeSkills.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(skill -> skill.toLowerCase(Locale.ENGLISH))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        long matched = normalizedJobSkills.stream()
                .filter(normalizedEmployeeSkills::contains)
                .count();

        return Math.min(1.0, matched / (double) normalizedJobSkills.size());
    }

    private double computeLocationScore(Job job, EmployeeProfileDTO employeeProfile) {
        if (job.getWorkMode() == WorkMode.REMOTE) {
            return 1.0;
        }

        String jobLocation = normalizeLocation(job.getLocation());
        String employeeLocation = normalizeLocation(employeeProfile.getLocation());

        if (jobLocation.isEmpty() || employeeLocation.isEmpty()) {
            return 0.5;
        }

        if (jobLocation.equals(employeeLocation)) {
            return 1.0;
        }

        if (jobLocation.contains(employeeLocation) || employeeLocation.contains(jobLocation)) {
            return job.getWorkMode() == WorkMode.HYBRID ? 0.7 : 0.6;
        }

        return job.getWorkMode() == WorkMode.HYBRID ? 0.4 : 0.2;
    }

    private double computeExperienceScore(Job job, EmployeeProfileDTO employeeProfile) {
        Integer employeeExperience = employeeProfile.getExperienceYears();
        if (employeeExperience == null) {
            return 0.0;
        }

        Integer minExperience = job.getMinExperienceYears();
        Integer maxExperience = job.getMaxExperienceYears();

        double score = 1.0;
        if (minExperience != null && minExperience > 0 && employeeExperience < minExperience) {
            score = Math.max(0.0, employeeExperience / (double) minExperience);
        }

        if (maxExperience != null && maxExperience > 0 && employeeExperience > maxExperience) {
            double overQualificationPenalty = maxExperience / (double) employeeExperience;
            score = Math.min(score, Math.max(0.0, overQualificationPenalty));
        }

        return Math.max(0.0, Math.min(1.0, score));
    }

    private String normalizeLocation(String location) {
        if (location == null) {
            return "";
        }
        return location.trim().toLowerCase(Locale.ENGLISH);
    }

    private double roundToOneDecimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private String buildSummary(JobMatchBreakdown breakdown) {
        return String.format(
                Locale.ENGLISH,
                "Overall match %.1f%% (skills %.1f%%, location %.1f%%, experience %.1f%%)",
                breakdown.getOverallMatchPercentage(),
                breakdown.getSkillsMatchPercentage(),
                breakdown.getLocationMatchPercentage(),
                breakdown.getExperienceMatchPercentage()
        );
    }
}
