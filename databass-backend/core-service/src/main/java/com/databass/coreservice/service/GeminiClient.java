package com.databass.coreservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.Map;

@Service
public class GeminiClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String apiKey;

    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    public GeminiClient(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    public String enhancePrompt(String userPrompt) {

        if (userPrompt == null || userPrompt.trim().isEmpty()) {
            return userPrompt;
        }

        try {
            System.out.println("🧠 Enhancing prompt via Gemini: " + userPrompt);

            String systemInstruction = "You are a professional music producer. Rewrite the following description into a concise, high-quality prompt for an AI music generator (MusicGen). Include specific instruments, mood, bpm, and genre. Keep it under 25 words. Output ONLY the new prompt.";
            String fullInput = systemInstruction + "\n\nUser Input: " + userPrompt;

            Map<String, Object> body = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(
                                    Map.of("text", fullInput)
                            ))
                    )
            );

            String finalUrl = GEMINI_URL + "?key=" + apiKey;

            String response = webClient.post()
                    .uri(finalUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = objectMapper.readTree(response);

            if (root.has("candidates") && root.get("candidates").isArray()) {
                JsonNode candidate = root.get("candidates").get(0);
                if (candidate.has("content")) {
                    String enhanced = candidate.get("content").get("parts").get(0).get("text").asText().trim();
                    System.out.println("✨ Enhanced: " + enhanced);
                    return enhanced;
                }
            }

        } catch (Exception e) {
            System.err.println("⚠️ Gemini Failed (using original): " + e.getMessage());
        }

        return userPrompt;
    }
}