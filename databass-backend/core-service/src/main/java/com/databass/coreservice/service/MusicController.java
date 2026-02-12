package com.databass.coreservice.service;

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

    @PostMapping("/generate")
    public ResponseEntity<Song> generateMusic(@RequestBody String prompt) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Song song = musicProducer.startGeneration(prompt);
        System.out.println("🎵 User " + username + " is requesting a song (ID: " + song.getId() + ")!");

        return ResponseEntity.accepted().body(song);
    }
}