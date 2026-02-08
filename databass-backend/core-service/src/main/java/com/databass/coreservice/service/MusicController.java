package com.databass.coreservice.controller;

import com.databass.coreservice.model.Song;
import com.databass.coreservice.service.MusicProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/music")
@RequiredArgsConstructor
public class MusicController {

    private final MusicProducer musicProducer;

    @PostMapping("/generate")
    public ResponseEntity<Song> generateMusic(@RequestBody String prompt) {
        Song song = musicProducer.startGeneration(prompt);

        return ResponseEntity.accepted().body(song);
    }
}