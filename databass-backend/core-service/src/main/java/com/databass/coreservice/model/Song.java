package com.databass.coreservice.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "songs")
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String prompt;

    private String status;

    private String s3Url;

    private int duration;

    private double temperature;

    private int topK;

    private double guidanceScale;

    private LocalDateTime createdAt = LocalDateTime.now();
}