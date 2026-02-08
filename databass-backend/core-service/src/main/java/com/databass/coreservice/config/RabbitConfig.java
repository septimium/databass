package com.databass.coreservice.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String MUSIC_QUEUE = "music-generation-queue";

    @Bean
    public Queue musicQueue() {
        return new Queue(MUSIC_QUEUE, true);
    }
}