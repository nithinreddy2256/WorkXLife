package com.workxlife.recommendation_service.service;

import com.workxlife.recommendation_service.dto.OpenAiMessage;
import com.workxlife.recommendation_service.dto.OpenAiRequest;
import com.workxlife.recommendation_service.dto.OpenAiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workxlife.recommendation_service.dto.Notification;


import java.util.List;

@Service
public class OpenAiService {

    private final RabbitTemplate rabbitTemplate;


    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.url}")
    private String openAiUrl;

    private final WebClient webClient;

    public OpenAiService(@Qualifier("openAiWebClient") WebClient webClient, RabbitTemplate rabbitTemplate) {
        this.webClient = webClient;
        this.rabbitTemplate = rabbitTemplate;
    }

    public String getJobRecommendations(String promptContent) {

        if (true) {
            return "[\"Java Developer\", \"Spring Boot Engineer\", \"Backend Developer\"]";
        }

        OpenAiMessage message = new OpenAiMessage("user", promptContent);
        OpenAiRequest request = new OpenAiRequest();
        request.setModel(model);
        request.setMessages(List.of(message));

        OpenAiResponse response = webClient.post()
                .uri(openAiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .body(Mono.just(request), OpenAiRequest.class)
                .retrieve()
                .onStatus(
                        HttpStatus.TOO_MANY_REQUESTS::equals,
                        clientResponse -> Mono.error(new RuntimeException("Too many requests to OpenAI. Please try again later."))
                )
                .bodyToMono(OpenAiResponse.class)
                .block();

        if (response != null && !response.getChoices().isEmpty()) {
            String recommendations = response.getChoices().get(0).getMessage().getContent();

            // Send notification
            Notification notification = new Notification();
            notification.setRecipientId(101L); // Replace with actual user ID if available
            notification.setRecipientEmail("testuser@example.com");
            notification.setMessage("We’ve found job recommendations tailored to your profile: " + recommendations);
            notification.setType("EMAIL");

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                String json = objectMapper.writeValueAsString(notification);
                rabbitTemplate.convertAndSend("notification.queue", json);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return recommendations;
        } else {
            return "No recommendation generated.";
        }
    }

}
