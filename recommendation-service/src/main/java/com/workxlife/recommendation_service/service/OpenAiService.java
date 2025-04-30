package com.workxlife.recommendation_service.service;

import com.workxlife.recommendation_service.dto.OpenAiMessage;
import com.workxlife.recommendation_service.dto.OpenAiRequest;
import com.workxlife.recommendation_service.dto.OpenAiResponse;
import com.workxlife.recommendation_service.dto.Notification;
import com.workxlife.recommendation_service.util.JwtEmailExtractor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class OpenAiService {

    private final RabbitTemplate rabbitTemplate;
    private final WebClient webClient;

    @Autowired
    private HttpServletRequest request; // ✅ Inject HttpServletRequest

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.url}")
    private String openAiUrl;

    public OpenAiService(@Qualifier("openAiWebClient") WebClient webClient, RabbitTemplate rabbitTemplate) {
        this.webClient = webClient;
        this.rabbitTemplate = rabbitTemplate;
    }

    public String getJobRecommendations(String promptContent) {

        if (true) {
            String recommendations = "[\"Java Developer\", \"Spring Boot Engineer\", \"Backend Developer\"]";

            try {
                String email = JwtEmailExtractor.extractEmail(request); // ✅ Dynamically extract email

                Notification notification = new Notification();
                notification.setRecipientId(101L); // optional
                notification.setRecipientEmail(email);
                notification.setMessage("We’ve found job recommendations tailored to your profile: " + recommendations);
                notification.setType("EMAIL");

                ObjectMapper objectMapper = new ObjectMapper();
                String json = objectMapper.writeValueAsString(notification);
                rabbitTemplate.convertAndSend("notification.queue", json);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return recommendations;
        }

        // 🔥 If you disable the `if (true)` later, here’s the real OpenAI call:

        OpenAiMessage message = new OpenAiMessage("user", promptContent);
        OpenAiRequest requestObj = new OpenAiRequest();
        requestObj.setModel(model);
        requestObj.setMessages(List.of(message));

        OpenAiResponse response = webClient.post()
                .uri(openAiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .body(Mono.just(requestObj), OpenAiRequest.class)
                .retrieve()
                .onStatus(
                        HttpStatus.TOO_MANY_REQUESTS::equals,
                        clientResponse -> Mono.error(new RuntimeException("Too many requests to OpenAI. Please try again later."))
                )
                .bodyToMono(OpenAiResponse.class)
                .block();

        if (response != null && !response.getChoices().isEmpty()) {
            String recommendations = response.getChoices().get(0).getMessage().getContent();

            try {
                String email = JwtEmailExtractor.extractEmail(request); // ✅ Again dynamic here

                Notification notification = new Notification();
                notification.setRecipientId(101L); // optional
                notification.setRecipientEmail(email);
                notification.setMessage("We’ve found job recommendations tailored to your profile: " + recommendations);
                notification.setType("EMAIL");

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
