package com.workxlife.recommendation_service.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workxlife.recommendation_service.dto.EmployeeProfileDto;
import com.workxlife.recommendation_service.dto.JobDto;
import com.workxlife.recommendation_service.dto.Notification;
import com.workxlife.recommendation_service.dto.OpenAiMessage;
import com.workxlife.recommendation_service.dto.OpenAiRequest;
import com.workxlife.recommendation_service.dto.OpenAiResponse;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OpenAiService {

    private final WebClient webClient;
    private final RabbitTemplate rabbitTemplate;

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.url}")
    private String openAiUrl;

    public OpenAiService(WebClient.Builder webClientBuilder, RabbitTemplate rabbitTemplate) {
        this.webClient = webClientBuilder.build();
        this.rabbitTemplate = rabbitTemplate;
    }
    public List<Long> getRecommendedJobIds(EmployeeProfileDto employee, List<JobDto> allJobs, String token) {
        //  Mock logic: Select top 3 job IDs based on list order
        return allJobs.stream()
                .limit(3)
                .map(JobDto::getId)
                .collect(Collectors.toList());
    }


//    public List<Long> getRecommendedJobIds(EmployeeProfileDto employee, List<JobDto> allJobs, String token) {
//        try {
//            //  Prepare job list for prompt
//            String jobList = allJobs.stream()
//                    .map(job -> String.format("ID: %d, Title: %s, Skills: %s",
//                            job.getId(),
//                            job.getTitle(),
//                            job.getRequiredSkills() != null ? String.join(", ", job.getRequiredSkills()) : "N/A"))
//                    .collect(Collectors.joining("\n"));
//
//            //  Create prompt for GPT
//            String prompt = """
//                Based on the following employee profile and job listings, recommend the top 3 job IDs that best match the employee’s skills and experience.
//
//                Employee:
//                - Name: %s
//                - Skills: %s
//                - Experience: %d years
//
//                Job Listings:
//                %s
//
//                Return ONLY a JSON array of the top 3 job IDs. Example: [2, 5, 8]
//                """.formatted(
//                    employee.getName(),
//                    employee.getSkills() != null ? String.join(", ", employee.getSkills()) : "N/A",
//                    employee.getExperienceYears(),
//                    jobList
//            );
//
//            OpenAiMessage message = new OpenAiMessage("user", prompt);
//            OpenAiRequest request = new OpenAiRequest();
//            request.setModel(model);
//            request.setMessages(List.of(message));
//
//            OpenAiResponse response = webClient.post()
//                    .uri(openAiUrl)
//                    .header("Authorization", "Bearer " + apiKey)
//                    .body(Mono.just(request), OpenAiRequest.class)
//                    .retrieve()
//                    .onStatus(HttpStatus.TOO_MANY_REQUESTS::equals,
//                            res -> Mono.error(new RuntimeException("Too many OpenAI requests")))
//                    .bodyToMono(OpenAiResponse.class)
//                    .block();
//
//            String content = response != null && !response.getChoices().isEmpty()
//                    ? response.getChoices().get(0).getMessage().getContent()
//                    : "[]";
//
//            return new ObjectMapper().readValue(content, new TypeReference<List<Long>>() {});
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new ArrayList<>();
//        }
//    }

    public void sendRecommendationsAsNotifications(EmployeeProfileDto employee, List<JobDto> recommendedJobs) {
        for (JobDto job : recommendedJobs) {
            try {
                Notification notification = new Notification();
                notification.setRecipientEmail(employee.getEmail());
                notification.setType("EMAIL");
                notification.setMessage("Recommended for you: " + job.getTitle() + " — " + job.getDescription());

                String json = new ObjectMapper().writeValueAsString(notification);
                rabbitTemplate.convertAndSend("notification.queue", json);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
