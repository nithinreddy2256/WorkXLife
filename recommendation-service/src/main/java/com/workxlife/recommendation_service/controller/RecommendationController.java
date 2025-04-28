package com.workxlife.recommendation_service.controller;

import com.workxlife.recommendation_service.dto.EmployeeProfileDto;
import com.workxlife.recommendation_service.dto.JobDto;
import com.workxlife.recommendation_service.service.OpenAiService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Autowired;



@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final OpenAiService openAiService;
    private final WebClient.Builder webClientBuilder;
    @Autowired
    public RecommendationController(OpenAiService openAiService, @Qualifier("loadBalancedWebClientBuilder")WebClient.Builder webClientBuilder) {
        this.openAiService = openAiService;
        this.webClientBuilder = webClientBuilder;
    }

    @GetMapping("/{employeeId}")
    public String getRecommendations(@PathVariable Long employeeId) {
        // Fetch employee profile
        EmployeeProfileDto employee = webClientBuilder.build()
                .get()
                .uri("http://employee-service/api/employees/" + employeeId)
                .retrieve()
                .bodyToMono(EmployeeProfileDto.class)
                .block();

        // Fetch job listings
        List<JobDto> jobs = webClientBuilder.build()
                .get()
                .uri("http://job-service/api/jobs")
                .retrieve()
                .bodyToFlux(JobDto.class)
                .collectList()
                .block();

        // Build job listings prompt
        StringBuilder jobPrompt = new StringBuilder();
        int count = 1;
        for (JobDto job : jobs) {
            String skills = job.getRequiredSkills() != null
                    ? String.join(", ", job.getRequiredSkills())
                    : "N/A";
            jobPrompt.append(count++).append(". ").append(job.getTitle())
                    .append(" – ").append(skills).append("\n");
        }

        // Handle null employee skills
        String skillsText = employee.getSkills() != null
                ? String.join(", ", employee.getSkills())
                : "N/A";

        // Create final prompt
        String prompt = """
                You are a job recommender system. Based on the candidate's profile and the list of job openings, suggest the top 3 best-fit jobs.

                Candidate:
                - Name: %s
                - Skills: %s
                - Experience: %d years

                Job Openings:
                %s

                Return the top 3 job titles as a JSON list.
                """.formatted(
                employee.getName(),
                skillsText,
                employee.getExperienceYears(),
                jobPrompt.toString()
        );

        return openAiService.getJobRecommendations(prompt);
    }
}
