package com.databass.coreservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class MusicGenClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${python.api.token}")
    private String hfToken;

    @Value("${python.api.trigger.url}")
    private String triggerUrl;

    public MusicGenClient(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    public byte[] generateSong(String prompt, int duration, double temp, int topK, double guidance) {
        try {
            String cleanTriggerUrl = triggerUrl.endsWith("/") ?
                    triggerUrl.substring(0, triggerUrl.length() - 1) : triggerUrl;

            Object[] dataPayload = new Object[]{ prompt, duration, guidance, temp, topK };
            Map<String, Object> requestBody = Map.of("data", dataPayload);

            String triggerResponse = webClient.post()
                    .uri(cleanTriggerUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + hfToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode triggerJson = objectMapper.readTree(triggerResponse);
            if (!triggerJson.has("event_id")) {
                throw new RuntimeException("No event_id in response");
            }
            String eventId = triggerJson.get("event_id").asText();
            System.out.println("🆔 Event ID: " + eventId);

            Thread.sleep(5000); // safety

            String statusUrl = cleanTriggerUrl + "/" + eventId;

            String fullResponse = webClient.get()
                    .uri(statusUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + hfToken)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            String audioUrl = extractUrlSimple(fullResponse);
            if (audioUrl == null) {
                throw new RuntimeException("Could not find http link in response.");
            }
            System.out.println("Found Audio URL: " + audioUrl);

            System.out.println("Downloading bytes...");

            WebClient largeFileClient = WebClient.builder()
                    .exchangeStrategies(ExchangeStrategies.builder()
                            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                            .build())
                    .build();

            return largeFileClient.get()
                    .uri(audioUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + hfToken)
                    .accept(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();

        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            throw new RuntimeException("Generation Failed: " + e.getMessage(), e);
        }
    }

    private String extractUrlSimple(String text) {
        if (text == null) return null;
        int httpIndex = text.indexOf("http");
        if (httpIndex == -1) return null;
        int endIndex = text.indexOf("\"", httpIndex);
        if (endIndex == -1) return null;
        return text.substring(httpIndex, endIndex);
    }
}