package com.team18.FleetForge.dto.driver;

import com.team18.FleetForge.dto.vehicle.VehicleCreateRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverCreateRequestDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    VehicleCreateRequestDTO vehicle;
}
