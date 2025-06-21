package com.workxlife.recommendation_service.controller;

import com.workxlife.recommendation_service.dto.EmployeeProfileDto;
import com.workxlife.recommendation_service.dto.JobDto;
import com.workxlife.recommendation_service.service.OpenAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final OpenAiService openAiService;
    private final WebClient.Builder webClientBuilder;

    @Autowired
    public RecommendationController(OpenAiService openAiService,
                                    @Qualifier("loadBalancedWebClientBuilder") WebClient.Builder webClientBuilder) {
        this.openAiService = openAiService;
        this.webClientBuilder = webClientBuilder;
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<List<JobDto>> getRecommendations(@PathVariable Long employeeId,
                                                           @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");

            // Fetch employee profile
            EmployeeProfileDto employee = webClientBuilder.build()
                    .get()
                    .uri("http://employee-service/api/employees/" + employeeId)
                    .header("Authorization", authHeader)
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

            // Get recommended job IDs using AI
            List<Long> recommendedIds = openAiService.getRecommendedJobIds(employee, jobs, token);

            // Filter jobs based on AI recommendation
            List<JobDto> recommendedJobs = jobs.stream()
                    .filter(job -> recommendedIds.contains(job.getId()))
                    .collect(Collectors.toList());

            // Send email notifications
            openAiService.sendRecommendationsAsNotifications(employee, recommendedJobs);


            return ResponseEntity.ok(recommendedJobs);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
