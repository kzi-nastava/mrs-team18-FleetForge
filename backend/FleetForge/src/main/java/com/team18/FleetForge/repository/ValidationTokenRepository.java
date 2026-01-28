package com.team18.FleetForge.repository;

import com.team18.FleetForge.model.ValidationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ValidationTokenRepository extends JpaRepository<ValidationToken, Long> {

    Optional<ValidationToken> findByToken(String token);
}