package com.team18.FleetForge.model;

import com.team18.FleetForge.model.enums.InformationChangeRequestStatus;
import com.team18.FleetForge.model.users.Driver;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="driver_profile_change_request")
public class DriverProfileChangeRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false, foreignKey = @ForeignKey(name = "fk_driver_request"))
    private Driver driver;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InformationChangeRequestStatus status;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "new_first_name", nullable = false)
    private String newFirstName;
    @Column(name = "new_last_name", nullable = false)
    private String newLastName;
    @Column(name = "new_email", nullable = false)
    private String newEmail;
    @Column(name = "new_phone_number", nullable = false)
    private String newPhoneNumber;
    @Column(name = "new_address", nullable = false)
    private String newAddress;
    @Column(name = "new_profile_picture")
    private String newProfilePicture;
}
