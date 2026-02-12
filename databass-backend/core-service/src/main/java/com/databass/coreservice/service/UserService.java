package com.databass.coreservice.service;

import com.databass.coreservice.model.User;
import com.databass.coreservice.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public void deductCredits(String username, int amount) {
        User user = getUserByUsername(username);

        if (user.getCredits() < amount) {
            throw new RuntimeException("Insufficient funds! You have " + user.getCredits() + " credits, but need " + amount + ".");
        }

        user.setCredits(user.getCredits() - amount);
        userRepository.save(user);

        System.out.println("Transaction: -" + amount + " credits for " + username + ". New Balance: " + user.getCredits());
    }
}