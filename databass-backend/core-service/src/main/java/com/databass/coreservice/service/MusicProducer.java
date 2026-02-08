package com.databass.coreservice.service;

import com.databass.coreservice.config.RabbitConfig;
import com.databass.coreservice.model.Song;
import com.databass.coreservice.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MusicProducer {

    private final SongRepository songRepository;
    private final RabbitTemplate rabbitTemplate;

    public Song startGeneration(String prompt) {
        Song song = new Song();
        song.setPrompt(prompt);
        song.setStatus("PENDING");
        song = songRepository.save(song);

        System.out.println("Queueing Job ID: " + song.getId());
        rabbitTemplate.convertAndSend(RabbitConfig.MUSIC_QUEUE, song.getId());

        return song;
    }
}