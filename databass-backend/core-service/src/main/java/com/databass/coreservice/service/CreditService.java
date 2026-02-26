package com.databass.coreservice.service;

import com.databass.coreservice.dto.DailyClaimResponse;
import com.databass.coreservice.model.User;
import com.databass.coreservice.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class CreditService {

    private final UserRepository userRepository;

    public CreditService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public DailyClaimResponse claimDailyCredits(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDate today = LocalDate.now();
        LocalDate lastClaim = user.getLastClaimDate();

        if (lastClaim != null && lastClaim.isEqual(today)) {
            throw new RuntimeException("You have already claimed your daily credits today!");
        }

        if (lastClaim != null && lastClaim.isEqual(today.minusDays(1))) {
            int newStreak = Math.min(user.getCurrentStreak() + 1, 30);
            user.setCurrentStreak(newStreak);
        } else {
            user.setCurrentStreak(1);
        }

        int baseCredits = 50;
        int streakBonus = user.getCurrentStreak() * 2;
        int totalReward = baseCredits + streakBonus;

        user.setCredits(user.getCredits() + totalReward);
        user.setLastClaimDate(today);
        userRepository.save(user);

        return new DailyClaimResponse(
                totalReward,
                user.getCredits(),
                user.getCurrentStreak(),
                "Successfully claimed " + totalReward + " credits!"
        );
    }
}