package com.japaneseLearning.repository;

import com.japaneseLearning.entity.ApplicationUser;
import com.japaneseLearning.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByUser(ApplicationUser user);
}
