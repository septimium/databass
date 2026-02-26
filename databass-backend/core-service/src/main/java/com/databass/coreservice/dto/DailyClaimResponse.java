package com.databass.coreservice.dto;

import lombok.Data;

@Data
public class DailyClaimResponse {
    private int creditsRewarded;
    private int totalCredits;
    private int currentStreak;
    private String message;

    public DailyClaimResponse(int creditsRewarded, int totalCredits, int currentStreak, String message) {
        this.creditsRewarded = creditsRewarded;
        this.totalCredits = totalCredits;
        this.currentStreak = currentStreak;
        this.message = message;
    }
}