package com.databass.coreservice.service;

import com.databass.coreservice.dto.GenerateRequest;
import com.databass.coreservice.model.Song;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/music")
@RequiredArgsConstructor
public class MusicController {

    private final MusicProducer musicProducer;
    private final UserService userService;
    private final GeminiClient geminiClient;

    @PostMapping("/generate")
    public ResponseEntity<?> generateMusic(@RequestBody GenerateRequest request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        int cost = request.getDuration();

        if (request.isUseAdvancedParams()) {
            cost += 20;
        }

        if (request.isEnhancePrompt()) {
            cost += 15;
        }

        try {
            userService.deductCredits(username, cost);
        } catch (RuntimeException e) {
            System.out.println("User " + username + " attempted to generate a song but had insufficient credits. Required: " + cost);
            return ResponseEntity.status(402).body(e.getMessage());
        }

        String finalPrompt = request.getPrompt();

        if (request.isEnhancePrompt()) {
            finalPrompt = geminiClient.enhancePrompt(request.getPrompt());
            request.setPrompt(finalPrompt);
        }

        Song song = musicProducer.startGeneration(request);
        System.out.println("🎵 User " + username + " is requesting a song (ID: " + song.getId() + ")!");

        return ResponseEntity.accepted().body(song);
    }
}