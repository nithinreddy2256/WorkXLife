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


import java.util.List;

@Service
public class OpenAiService {

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.url}")
    private String openAiUrl;

    private final WebClient webClient;

    public OpenAiService(@Qualifier("openAiWebClient") WebClient webClient) {
        this.webClient = webClient;
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
            return response.getChoices().get(0).getMessage().getContent();
        } else {
            return "No recommendation generated.";
        }
    }
}
