package com.team18.FleetForge.model;

import com.team18.FleetForge.model.enums.InformationChangeRequestStatus;
import com.team18.FleetForge.model.enums.VehicleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="vehicle_information_change_request")
public class VehicleInformationChangeRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false, foreignKey = @ForeignKey(name = "fk_vehicle_information_change_request"))
    private Vehicle vehicle;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InformationChangeRequestStatus status;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "new_model", nullable = false)
    private String newModel;
    @Enumerated(EnumType.STRING)
    @Column(name = "new_type", nullable = false)
    private VehicleType newType;
    @Column(name = "new_registration_number", nullable = false)
    private String newRegistrationNumber;
    @Column(name = "new_space", nullable = false)
    private int newSpace;
    @Column(name = "new_baby_seat", nullable = false)
    private boolean newBabySeat;
    @Column(name = "new_pet_friendly", nullable = false)
    private boolean newPetFriendly;
}
