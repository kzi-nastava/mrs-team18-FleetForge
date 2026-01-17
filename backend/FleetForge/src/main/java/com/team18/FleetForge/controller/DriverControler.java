package com.team18.FleetForge.controller;

import com.team18.FleetForge.dto.auth.SetPasswordRequestDTO;
import com.team18.FleetForge.dto.auth.SetPasswordResponseDTO;
import com.team18.FleetForge.dto.auth.ValidateTokenResponseDTO;
import com.team18.FleetForge.dto.driver.*;
import com.team18.FleetForge.dto.vehicle.VehicleCreateResponseDTO;
import com.team18.FleetForge.dto.vehicle.VehicleInformationChangeRequestDTO;
import com.team18.FleetForge.dto.vehicle.VehicleInformationChangeResponseDTO;
import com.team18.FleetForge.model.*;
import com.team18.FleetForge.model.users.Driver;
import com.team18.FleetForge.model.users.User;
import com.team18.FleetForge.model.enums.InformationChangeRequestStatus;
import com.team18.FleetForge.service.*;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverControler {
    private final UserService userService;
    private final RideService rideService;
    private final DriverProfileChangeRequestService driverChangeRequestService;
    private final VehicleInfoChangeReqService vehicleChangeService;
    private final AuthService  authService;
    private final EmailService emailService;
    private final ActivationTokenService activationTokenService;
    private final ProfilePictureService profilePictureService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<DriverGetResponseDTO> getCurrentDriver(){
        Driver driver=userService.getCurrentDriver();
        if(driver==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        ResponseEntity<DriverGetResponseDTO> responseEntity = new ResponseEntity<>(new DriverGetResponseDTO(driver), HttpStatus.OK);
        return responseEntity;
    }
    @GetMapping("/{id}")
    public ResponseEntity<DriverGetResponseDTO> getDriver(@PathVariable Long id) {
        Driver foundDriver = (Driver) userService.getUserById(id);
        if(foundDriver == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        DriverGetResponseDTO driverGetResponseDTO = new DriverGetResponseDTO(foundDriver);
        return new ResponseEntity<>(driverGetResponseDTO, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<DriverCreateResponseDTO> createDriver(@RequestBody DriverCreateRequestDTO request) throws IOException {
        Driver driver=userService.createDriver(request);
       ActivationToken token=activationTokenService.createTokenPasswordSetDriver(driver);
        emailService.sendEmail("v.vitomirovic@gmail.com","Password set","http://localhost:4200/set-password?token="+token.getToken());
        activationTokenService.saveActivationToken(token);
        DriverCreateResponseDTO driverCreateResponseDTO = new DriverCreateResponseDTO(driver);
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

    @Transactional
    @PostMapping("/update-request")
    public ResponseEntity<DriverProfileChangeResponseDTO> createChangeRequest(@RequestBody DriverProfileChangeRequestDTO request) {

        Driver foundDriver =(Driver) userService.getUserById(userService.getCurrentDriver().getId());

        DriverProfileChangeRequest changeRequest=new DriverProfileChangeRequest();
        changeRequest.setDriver(foundDriver);
        changeRequest.setStatus(InformationChangeRequestStatus.PENDING);
        changeRequest.setCreatedAt(LocalDateTime.now());
        changeRequest.setNewFirstName(request.getNewFirstName());
        changeRequest.setNewLastName(request.getNewLastName());
        changeRequest.setNewAddress(request.getNewAddress());
        changeRequest.setNewEmail(request.getNewEmail());
        changeRequest.setNewProfilePicture(request.getNewProfilePicture());
        changeRequest.setNewPhoneNumber(request.getNewPhoneNumber());

        driverChangeRequestService.save(changeRequest);

        DriverProfileChangeResponseDTO driverProfileChangeResponseDTO = new DriverProfileChangeResponseDTO(changeRequest);
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

        Driver foundDriver =(Driver) userService.getUserById(userService.getCurrentDriver().getId());
        Vehicle vehicle= foundDriver.getVehicle();

        VehicleInformationChangeRequest vehicleInformationChangeRequest = new VehicleInformationChangeRequest();
        vehicleInformationChangeRequest.setVehicle(vehicle);
        vehicleInformationChangeRequest.setCreatedAt(LocalDateTime.now());
        vehicleInformationChangeRequest.setNewModel(request.getNewModel());
        vehicleInformationChangeRequest.setNewRegistrationNumber(request.getNewRegistrationNumber());
        vehicleInformationChangeRequest.setNewType(request.getNewType());
        vehicleInformationChangeRequest.setNewSpace(request.getNewSpace());
        vehicleInformationChangeRequest.setStatus(InformationChangeRequestStatus.PENDING);
        vehicleInformationChangeRequest.setNewPetFriendly(request.isNewPetFriendly());
        vehicleInformationChangeRequest.setNewBabySeat(request.isNewBabySeat());

        vehicleChangeService.save(vehicleInformationChangeRequest);

        VehicleInformationChangeResponseDTO vehicleInformationChangeResponseDTO = new VehicleInformationChangeResponseDTO(vehicleInformationChangeRequest);
        return new ResponseEntity<>(vehicleInformationChangeResponseDTO, HttpStatus.CREATED);
    }

    @PutMapping("/password")
    public ResponseEntity<String> passwordChange(@RequestBody DriverPasswordChangeRequestDTO request) {
        Driver foundDriver = userService.getCurrentDriver();

        foundDriver.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userService.save(foundDriver);

        return ResponseEntity.ok("password changed");
    }

    @PostMapping("/set-password")
    public ResponseEntity<SetPasswordResponseDTO> setPassword(@RequestBody SetPasswordRequestDTO request) {

        Optional<ActivationToken> activationToken = authService.findByToken(request.getToken());
        if(activationToken.isEmpty()){
            SetPasswordResponseDTO setPasswordResponseDTO = new SetPasswordResponseDTO();
            setPasswordResponseDTO.setSuccess(Boolean.FALSE);
            return ResponseEntity.ok(setPasswordResponseDTO);
        }
        ActivationToken at=activationToken.get();
        Driver driver = (Driver) at.getUser();

        driver.setPassword(passwordEncoder.encode(request.getPassword()));
        userService.save(driver);
        at.setUsed(true);
        activationTokenService.saveActivationToken(at);
        SetPasswordResponseDTO setPasswordResponseDTO = new SetPasswordResponseDTO();
        setPasswordResponseDTO.setSuccess(Boolean.TRUE);
        return ResponseEntity.ok(setPasswordResponseDTO);
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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Long driverId = null;

        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            driverId = user.getId();
        }

        List<DriverRideHistoryDTO> history = rideService.getDriverRideHistory(driverId, startDate);

        return new ResponseEntity<>(history, HttpStatus.OK);
    }
}
