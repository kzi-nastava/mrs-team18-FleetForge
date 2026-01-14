package com.team18.FleetForge.repository;

import com.team18.FleetForge.model.ActivationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ActivationTokenRepository extends JpaRepository<ActivationToken, Long> {

    Optional<ActivationToken> findByToken(String token);
}