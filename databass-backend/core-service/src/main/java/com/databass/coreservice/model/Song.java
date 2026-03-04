package com.databass.coreservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @Column(nullable = false)
    private String title = "Untitled Track";

    private String prompt;

    private String status;

    private String s3Url;

    private int duration;

    private double temperature;

    private int topK;

    private double guidanceScale;

    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(nullable = false)
    private boolean isPublic = true;

    @Column(nullable = false)
    private Integer streamCount = 0;

    @JsonProperty("producer")
    public String getProducerName() {
        return user != null ? user.getUsername() : "Unknown";
    }
}