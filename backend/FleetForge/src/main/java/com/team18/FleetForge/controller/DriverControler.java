package com.team18.FleetForge.controller;

import com.team18.FleetForge.dto.driver.*;
import com.team18.FleetForge.dto.vehicle.VehicleCreateResponseDTO;
import com.team18.FleetForge.dto.vehicle.VehicleInformationChangeRequestDTO;
import com.team18.FleetForge.dto.vehicle.VehicleInformationChangeResponseDTO;
import com.team18.FleetForge.model.DriverProfileChangeRequest;
import com.team18.FleetForge.model.DriverSession;
import com.team18.FleetForge.model.users.Driver;
import com.team18.FleetForge.model.Vehicle;
import com.team18.FleetForge.model.GeoPoint;
import com.team18.FleetForge.model.VehicleInformationChangeRequest;
import com.team18.FleetForge.model.enums.InformationChangeRequestStatus;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/drivers")
public class DriverControler {

    @GetMapping("/{id}")
    public ResponseEntity<DriverGetResponseDTO> getDriver(@PathVariable Long id) {
        Driver foundDriver = new Driver();
        foundDriver.setId(id);
        foundDriver.setFirstName("Admin");
        foundDriver.setLastName("Admin");
        foundDriver.setPassword("admin");
        foundDriver.setEmail("admin");
        foundDriver.setPhoneNumber("123456789");
        foundDriver.setAddress("address");
        foundDriver.setProfilePicture("profilePicture");

        DriverGetResponseDTO driverGetResponseDTO = new DriverGetResponseDTO(foundDriver);

        return new ResponseEntity<>(driverGetResponseDTO, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<DriverCreateResponseDTO> createDriver(@RequestBody DriverCreateRequestDTO request) {
        DriverCreateResponseDTO driverCreateResponseDTO = new DriverCreateResponseDTO();
        driverCreateResponseDTO.setFirstName(request.getFirstName());
        driverCreateResponseDTO.setLastName(request.getLastName());
        driverCreateResponseDTO.setEmail(request.getEmail());
        driverCreateResponseDTO.setPhone(request.getPhone());
        driverCreateResponseDTO.setAddress(request.getAddress());

        VehicleCreateResponseDTO vehicleCreateResponseDTO = new VehicleCreateResponseDTO();
        vehicleCreateResponseDTO.setType(request.getVehicle().getType());
        vehicleCreateResponseDTO.setRegistrationNumber(request.getVehicle().getRegistrationNumber());
        vehicleCreateResponseDTO.setModel(request.getVehicle().getModel());
        vehicleCreateResponseDTO.setSpace(request.getVehicle().getSpace());
        vehicleCreateResponseDTO.setPetFriendly(request.getVehicle().isPetFriendly());
        vehicleCreateResponseDTO.setBabySeat(request.getVehicle().isBabySeat());

        driverCreateResponseDTO.setVehicle(vehicleCreateResponseDTO);
        //treba sacuvati sve posle u bazi
        return new ResponseEntity<>(driverCreateResponseDTO, HttpStatus.CREATED);
    }


    /**
     * PUT /api/drivers/{id}/availability
     * Request Param:
     *  - available (boolean)
     * Response:
     *  - 204 NO_CONTENT if changed
     *  - 409 CONFLICT if change is deferred
     */
    @PutMapping("/{id}/availability")
    public ResponseEntity<Void> changeAvailability(
            @PathVariable Long id,
            @RequestBody ChangeAvailabilityRequestDTO request
    ) {
        return ResponseEntity.noContent().build();
    }


    /**
     * POST /api/drivers/{id}/logout-requests
     * Response:
     *  - 204 NO_CONTENT if logout is allowed
     *  - 409 CONFLICT if logout conditions are not met
     */
    @PostMapping("/{id}/logout-requests")
    public ResponseEntity<Void> requestLogout(
            @PathVariable Long id
    ) {
        // Servers check if request is allowed, for now always send status 200
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @PutMapping("/{id}/set-password")
    public ResponseEntity<DriverPasswordResponseDTO> setDriverPassword(@PathVariable long id, @RequestBody DriverPasswordRequestDTO request) {
        DriverPasswordResponseDTO driverPasswordResponseDTO = new DriverPasswordResponseDTO();

        //ovde treba da se dobavlja iz baze preko id pa da se azurira i da se vrati nesto ovo sam lupio podatke samo sad jer nema baze
        driverPasswordResponseDTO.setId(id);
        driverPasswordResponseDTO.setEmail("milos.damjanovic@gmail.com");

        return new ResponseEntity<>(driverPasswordResponseDTO, HttpStatus.OK);
    }

    @PostMapping("/update-request")
    public ResponseEntity<DriverProfileChangeResponseDTO> createChangeRequest(@RequestBody DriverProfileChangeRequestDTO request) {
        //treba pronaci drivera preko maila iz baze da bi id postavio id, sad je samo privremeno ovako
        Driver foundDriver = new Driver();
        foundDriver.setId(1L);

        DriverProfileChangeRequest driverProfileChangeRequest = new DriverProfileChangeRequest();
        driverProfileChangeRequest.setDriverId(foundDriver.getId());
        driverProfileChangeRequest.setNewFirstName(request.getNewFirstName());
        driverProfileChangeRequest.setNewLastName(request.getNewLastName());
        driverProfileChangeRequest.setNewEmail(request.getNewEmail());
        driverProfileChangeRequest.setNewPhoneNumber(request.getNewPhoneNumber());
        driverProfileChangeRequest.setNewAddress(request.getNewAddress());
        driverProfileChangeRequest.setNewProfilePicture(request.getNewProfilePicture());
        driverProfileChangeRequest.setCreatedAt(LocalDateTime.now());
        driverProfileChangeRequest.setStatus(InformationChangeRequestStatus.PENDING);
        // treba sacuvati ovo posle


        DriverProfileChangeResponseDTO driverProfileChangeResponseDTO = new DriverProfileChangeResponseDTO(driverProfileChangeRequest);
        return new ResponseEntity<>(driverProfileChangeResponseDTO, HttpStatus.CREATED);
    }

    @PostMapping("/{id}/online")
    public ResponseEntity<DriverSessionResponseDTO> startSession(@PathVariable Long id) {
        DriverSession newSession = new DriverSession();
        newSession.setDriverId(id);
        newSession.setStartedAt(LocalDateTime.now());
        //sacuvaj

        DriverSessionResponseDTO driverSessionResponseDTO = new DriverSessionResponseDTO();
        // driverSessionResponseDTO.setSessionId(newSession.getId()); //moze posle sa bazom kad se kljuc generise
        driverSessionResponseDTO.setDriverId(id);
        driverSessionResponseDTO.setStartedAt(newSession.getStartedAt());
        driverSessionResponseDTO.setActive(true);
        return new ResponseEntity<>(driverSessionResponseDTO, HttpStatus.OK);


    }

    @PutMapping("/{id}/offline")
    public ResponseEntity<DriverSessionResponseDTO> stopSession(@PathVariable Long id, @RequestBody DriverSessionEndRequestDTO request) {
        //preko session id iz request.getId() nadjem session
        Long sessionId = request.getSessionId();
        DriverSession foundSession = new DriverSession();
        foundSession.setDriverId(id);
        foundSession.setStartedAt(LocalDateTime.now()); //samo za prikaz je .now inace se uzima iz ucitane sesije
        foundSession.setEndedAt(LocalDateTime.now());
        //sacuvam
        DriverSessionResponseDTO driverSessionResponseDTO = new DriverSessionResponseDTO();
        driverSessionResponseDTO.setDriverId(id);
        driverSessionResponseDTO.setStartedAt(foundSession.getStartedAt());
        driverSessionResponseDTO.setEndedAt(foundSession.getEndedAt());
        driverSessionResponseDTO.setActive(false);
        return new ResponseEntity<>(driverSessionResponseDTO, HttpStatus.OK);
    }

    @GetMapping("/{id}/active-hours")
    public ResponseEntity<DriverActivityResponseDTO> getActiveHours(@PathVariable Long id) {

        LocalDateTime last24Hours = LocalDateTime.now().minusHours(24);


        List<DriverSession> sessions = new ArrayList<>();
        DriverSession session = new DriverSession();
        session.setStartedAt(LocalDateTime.of(2025, 12, 26, 14, 30, 0));
        session.setEndedAt(LocalDateTime.of(2025, 12, 26, 19, 30, 0));
        sessions.add(session);
        // sessions = sessionRepository.findByDriverIdAndStartedAtAfter(id, last24Hours);

        Long totalHours = calculateTotalHours(sessions);

        DriverActivityResponseDTO response = new DriverActivityResponseDTO();
        response.setDriverId(id);
        response.setActiveMinutesLast24h(totalHours);

        return ResponseEntity.ok(response);
    }

    private Long calculateTotalHours(List<DriverSession> sessions) {
        return sessions.stream()
                .filter(s -> s.getEndedAt() != null)
                .mapToLong(s -> {
                    Duration duration = Duration.between(s.getStartedAt(), s.getEndedAt());
                    return duration.toMinutes();
                })
                .sum();
    }

    @PostMapping("/update-request-vehicle")
    public ResponseEntity<VehicleInformationChangeResponseDTO> createChangeRequest(@RequestBody VehicleInformationChangeRequestDTO request) {
        //treba pronaci drivera preko maila iz baze da bi id postavio id, sad je samo privremeno ovako
        Vehicle foundVehicle = new Vehicle();
        foundVehicle.setId(1L);

        VehicleInformationChangeRequest vehicleInformationChangeRequest = new VehicleInformationChangeRequest();
        vehicleInformationChangeRequest.setVehicleId(foundVehicle.getId());
        vehicleInformationChangeRequest.setNewModel(request.getNewModel());
        vehicleInformationChangeRequest.setNewRegistrationNumber(request.getNewRegistrationNumber());
        vehicleInformationChangeRequest.setNewType(request.getNewType());
        vehicleInformationChangeRequest.setNewSpace(request.getNewSpace());
        vehicleInformationChangeRequest.setNewBabySeat(request.isNewBabySeat());
        vehicleInformationChangeRequest.setNewPetFriendly(request.isNewPetFriendly());

        vehicleInformationChangeRequest.setCreatedAt(LocalDateTime.now());
        vehicleInformationChangeRequest.setStatus(InformationChangeRequestStatus.PENDING);
        // treba sacuvati ovo posle


        VehicleInformationChangeResponseDTO vehicleInformationChangeResponseDTO = new VehicleInformationChangeResponseDTO(vehicleInformationChangeRequest);
        return new ResponseEntity<>(vehicleInformationChangeResponseDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/password-change")
    public ResponseEntity<String> passwordChange(@PathVariable Long id, @RequestBody DriverPasswordChangeRequestDTO request) {
        //pronaci drivera prvo preko id-a
        Driver foundDriver = new Driver();
        foundDriver.setPassword(request.getNewPassword());

        return ResponseEntity.ok("password changed");
    }

    /**
     * GET /api/drivers/ride-history
     * Query params:
     * - startDate (LocalDate, optional) - Filter rides from this date
     * Response: List of all rides with passenger information
     * - rideId, startTime, endTime
     * - startLocation, startAddress, endLocation, endAddress
     * - totalPrice
     * - cancelled (Boolean), cancelledBy (String)
     * - panicActivated (Boolean)
     * - passengers (List with all passenger info)
     */
    @GetMapping(
            value = "/ride-history",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<DriverRideHistoryDTO>> getRideHistory(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate) {

        List<DriverRideHistoryDTO> history = new ArrayList<>();

        history.add(DriverRideHistoryDTO.builder()
                .rideId(101L)
                .startTime(LocalDateTime.of(2025, 12, 25, 14, 30))
                .endTime(LocalDateTime.of(2025, 12, 25, 14, 50))
                .startLocation(new GeoPoint(45.2550, 19.8450))
                .startAddress("Bulevar oslobođenja 46, Novi Sad")
                .endLocation(new GeoPoint(45.2671, 19.8335))
                .endAddress("Trg slobode 1, Novi Sad")
                .totalPrice(850.00)
                .cancelled(false)
                .cancelledBy(null)
                .panicActivated(false)
                .passengers(List.of(
                        DriverRideHistoryDTO.PassengerDTO.builder()
                                .id(10L)
                                .firstName("Marko")
                                .lastName("Marković")
                                .email("marko@example.com")
                                .phoneNumber("+381641234567")
                                .profileImage("passenger10.jpg")
                                .build(),
                        DriverRideHistoryDTO.PassengerDTO.builder()
                                .id(11L)
                                .firstName("Ana")
                                .lastName("Anić")
                                .email("ana@example.com")
                                .phoneNumber("+381649876543")
                                .profileImage("passenger11.jpg")
                                .build()
                ))
                .build());

        history.add(DriverRideHistoryDTO.builder()
                .rideId(103L)
                .startTime(LocalDateTime.of(2025, 12, 23, 18, 00))
                .endTime(LocalDateTime.of(2025, 12, 23, 18, 25))
                .startLocation(new GeoPoint(45.2600, 19.8400))
                .startAddress("Novi Sad Centar")
                .endLocation(new GeoPoint(45.2500, 19.8600))
                .endAddress("Petrovaradin")
                .totalPrice(1200.00)
                .cancelled(false)
                .cancelledBy(null)
                .panicActivated(true)
                .passengers(List.of(
                        DriverRideHistoryDTO.PassengerDTO.builder()
                                .id(13L)
                                .firstName("Jovana")
                                .lastName("Jovanović")
                                .email("jovana@example.com")
                                .phoneNumber("+381645556666")
                                .profileImage("passenger13.jpg")
                                .build()
                ))
                .build());

        return new ResponseEntity<>(history, HttpStatus.OK);
    }
}
