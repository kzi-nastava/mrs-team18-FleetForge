package com.team18.FleetForge.repository.users;

import com.team18.FleetForge.model.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query(value = "SELECT u FROM User u WHERE (TYPE(u) = com.team18.FleetForge.model.users.Passenger OR TYPE(u) = com.team18.FleetForge.model.users.Driver) " +
            "AND u.email LIKE CONCAT(:prefix, '%')")
    List<User> findTop5ByEmailPrefix(@Param("prefix") String prefix);
}
