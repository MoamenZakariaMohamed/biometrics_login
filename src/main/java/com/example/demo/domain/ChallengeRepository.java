package com.example.demo.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ChallengeRepository extends JpaRepository<Challenge, String> {
    Optional<Challenge> findByChallengeId(String challengeId);
    void deleteByExpiresAtBefore(LocalDateTime dateTime);
}
