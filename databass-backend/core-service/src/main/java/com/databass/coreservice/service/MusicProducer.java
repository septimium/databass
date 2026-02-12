package com.databass.coreservice.service;

import com.databass.coreservice.config.RabbitConfig;
import com.databass.coreservice.dto.GenerateRequest;
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

    public Song startGeneration(GenerateRequest request) {
        Song song = new Song();
        song.setPrompt(request.getPrompt());
        song.setStatus("PENDING");

        song.setDuration(request.getDuration());

        song.setTemperature(request.isUseAdvancedParams() ? request.getTemperature() : 0.85);
        song.setTopK(request.isUseAdvancedParams() ? request.getTopK() : 250);
        song.setGuidanceScale(request.isUseAdvancedParams() ? request.getGuidanceScale() : 3.2);

        song = songRepository.save(song);

        System.out.println("Queueing Job ID: " + song.getId());
        rabbitTemplate.convertAndSend(RabbitConfig.MUSIC_QUEUE, song.getId());

        return song;
    }
}