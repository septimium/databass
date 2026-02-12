package com.databass.coreservice.service;

import com.databass.coreservice.config.RabbitConfig;
import com.databass.coreservice.model.Song;
import com.databass.coreservice.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MusicConsumer {

    private final SongRepository songRepository;
    private final PythonClient pythonClient;
    private final StorageService storageService;

    @RabbitListener(queues = RabbitConfig.MUSIC_QUEUE)
    public void processMusicJob(Long songId) {
        System.out.println("New Job ID: " + songId);

        Song song = songRepository.findById(songId).orElseThrow();
        song.setStatus("PROCESSING");
        songRepository.save(song);

        try {
            System.out.println("Generating song for: " + song.getPrompt());
            byte[] audioBytes = pythonClient.generateSong(
                    song.getPrompt(),
                    song.getDuration(),
                    song.getTemperature(),
                    song.getTopK(),
                    song.getGuidanceScale()
            );

            System.out.println("💾 Uploading " + audioBytes.length + " bytes to Storage...");
            String url = storageService.uploadSong(audioBytes, songId);

            song.setStatus("COMPLETED");
            song.setS3Url(url);
            songRepository.save(song);

            System.out.println("Generation Finished! URL: " + url);

        } catch (Exception e) {
            System.err.println("Generation Failed: " + e.getMessage());
            song.setStatus("FAILED");
            songRepository.save(song);
        }
    }
}