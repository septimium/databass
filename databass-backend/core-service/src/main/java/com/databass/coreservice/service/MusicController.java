package com.databass.coreservice.service;

import com.databass.coreservice.dto.GenerateRequest;
import com.databass.coreservice.model.Song;
import com.databass.coreservice.repository.SongRepository;
import com.databass.coreservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/music")
@RequiredArgsConstructor
public class MusicController {

    private final MusicProducer musicProducer;
    private final UserService userService;
    private final GeminiClient geminiClient;
    private final SongRepository songRepository;
    private final UserRepository userRepository;

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

        Song song = musicProducer.startGeneration(request, username);
        System.out.println("🎵 User " + username + " is requesting a song (ID: " + song.getId() + ")!");

        return ResponseEntity.accepted().body(song);
    }

    @GetMapping("/history")
    public ResponseEntity<?> getMyHistory() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(songRepository.findByUserUsernameOrderByCreatedAtDesc(username));
    }

    @GetMapping("/profile/{username}")
    public ResponseEntity<?> getUserProfile(@PathVariable("username") String username) {
        if (!userRepository.existsByUsername(username)) {
            return ResponseEntity.status(404).body(Map.of("error", "Producer not found in Databass."));
        }

        List<Song> publicSongs = songRepository.findByUserUsernameAndIsPublicTrueOrderByCreatedAtDesc(username);
        return ResponseEntity.ok(publicSongs);
    }

    @PutMapping("/{id}/visibility")
    public ResponseEntity<?> toggleVisibility(@PathVariable("id") Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Song song = songRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        if (!song.getUser().getUsername().equals(username)) {
            return ResponseEntity.status(403).body(Map.of("error", "UNAUTHORIZED ACTION."));
        }

        song.setPublic(!song.isPublic());
        songRepository.save(song);

        return ResponseEntity.ok(Map.of(
                "message", "Visibility updated.",
                "isPublic", song.isPublic()
        ));
    }
}