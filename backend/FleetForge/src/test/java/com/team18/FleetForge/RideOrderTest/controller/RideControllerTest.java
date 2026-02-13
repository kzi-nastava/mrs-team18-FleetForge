package com.team18.FleetForge.RideOrderTest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team18.FleetForge.dto.auth.LoginRequestDTO;
import com.team18.FleetForge.dto.auth.LoginResponseDTO;
import com.team18.FleetForge.dto.ride.lifecycle.RideCreateRequestDTO;
import com.team18.FleetForge.dto.ride.lifecycle.RideCreateResponseDTO;
import com.team18.FleetForge.dto.ride.routes.WayPointDTO;
import com.team18.FleetForge.model.enums.VehicleType;
import com.team18.FleetForge.model.ride.GeoPoint;
import com.team18.FleetForge.model.ride.Ride;
import com.team18.FleetForge.repository.rides.RideRepository;
import com.team18.FleetForge.repository.users.UserRepository;
import com.team18.FleetForge.service.impl.RideServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RideControllerTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private TestRestTemplate restTemplate;

    private String token;

    public void logIn(String email, String password){
        LoginRequestDTO loginRequestDTO= new LoginRequestDTO();
        loginRequestDTO.setEmail(email);
        loginRequestDTO.setPassword(password);

        ResponseEntity<LoginResponseDTO> response=restTemplate.exchange("/api/auth/login", HttpMethod.POST,new HttpEntity<>(loginRequestDTO), new ParameterizedTypeReference<>(){});
        token=response.getBody().getToken();
    }



    private RideCreateRequestDTO createRequestDTO(){
        logIn("passenger1@test.com","123");
        RideCreateRequestDTO requestDTO= new RideCreateRequestDTO();
        ArrayList<WayPointDTO> coordinates= new ArrayList<>();
        WayPointDTO startLoc= new WayPointDTO(new GeoPoint(45.0,19.0),"Start Address",0);
        WayPointDTO waypoint= new WayPointDTO(new GeoPoint(45.2,19.33),"Waypoint",1);
        WayPointDTO endLoc= new WayPointDTO(new GeoPoint(45.65,19.5),"End Address",2);
        coordinates.add(startLoc);
        coordinates.add(waypoint);
        coordinates.add(endLoc);

        requestDTO.setCoordinates(coordinates);
        requestDTO.setPassengerNumber(2);
        requestDTO.setRideTime(LocalDateTime.now());
        requestDTO.setRideNow(true);
        requestDTO.setPassengerEmails(new ArrayList<>());
        requestDTO.setVehicleType(VehicleType.STANDARD);
        requestDTO.setBabySeat(requestDTO.isBabySeat());
        requestDTO.setPetFriendly(requestDTO.isPetFriendly());
        requestDTO.setStartAddress(startLoc.getAddress());
        requestDTO.setEndAddress(endLoc.getAddress());
        requestDTO.setTotalDistance(30.2);
        requestDTO.setEstimatedDuration(22.0);
        return requestDTO;
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void createRideNowSuccessfull(){
        logIn("passenger1@test.com","123");
        RideCreateRequestDTO requestDTO= createRequestDTO();

        HttpHeaders headers= new HttpHeaders();
        headers.setBearerAuth(this.token);

        HttpEntity<RideCreateRequestDTO> entity= new HttpEntity<>(requestDTO,headers);

        ResponseEntity<RideCreateResponseDTO> response=restTemplate.exchange("/api/rides/create"
        ,HttpMethod.POST,entity, new ParameterizedTypeReference<>(){});

        Assert.assertNotNull(response.getBody());
        assertTrue(response.getBody().isCreated());

    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void createRideScheduledSuccessfull(){
        logIn("passenger1@test.com","123");
        RideCreateRequestDTO requestDTO= createRequestDTO();
        requestDTO.setRideNow(false);
        requestDTO.setRideTime(LocalDateTime.now().plusHours(2));
        HttpHeaders headers= new HttpHeaders();
        headers.setBearerAuth(this.token);

        HttpEntity<RideCreateRequestDTO> entity= new HttpEntity<>(requestDTO,headers);

        ResponseEntity<RideCreateResponseDTO> response=restTemplate.exchange("/api/rides/create"
                ,HttpMethod.POST,entity, new ParameterizedTypeReference<>(){});

        Assert.assertNotNull(response.getBody());
        assertTrue(response.getBody().isCreated());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void createRideNowNoDriverCriteriaFail(){
        logIn("passenger1@test.com","123");
        RideCreateRequestDTO requestDTO= createRequestDTO();
        requestDTO.setBabySeat(true);
        HttpHeaders headers= new HttpHeaders();
        headers.setBearerAuth(this.token);

        HttpEntity<RideCreateRequestDTO> entity= new HttpEntity<>(requestDTO,headers);

        ResponseEntity<RideCreateResponseDTO> response=restTemplate.exchange("/api/rides/create"
                ,HttpMethod.POST,entity, RideCreateResponseDTO.class);

        Assert.assertNotNull(response.getBody());
        assertFalse(response.getBody().isCreated());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void createRideNow_NoDriver_Available(){
        logIn("passenger1@test.com","123");
        RideCreateRequestDTO requestDTO= createRequestDTO();

        HttpHeaders headers= new HttpHeaders();
        headers.setBearerAuth(this.token);

        HttpEntity<RideCreateRequestDTO> entity= new HttpEntity<>(requestDTO,headers);

        ResponseEntity<RideCreateResponseDTO> response=restTemplate.exchange("/api/rides/create"
                ,HttpMethod.POST,entity, new ParameterizedTypeReference<>(){});

        Assert.assertNotNull(response.getBody());
        assertTrue(response.getBody().isCreated());

        logIn("passenger2@test.com","123");
        RideCreateRequestDTO requestDTO2= createRequestDTO();
        HttpHeaders headers2= new HttpHeaders();
        headers.setBearerAuth(this.token);

        HttpEntity<RideCreateRequestDTO> entity2= new HttpEntity<>(requestDTO,headers);

        ResponseEntity<RideCreateResponseDTO> response2=restTemplate.exchange("/api/rides/create"
                ,HttpMethod.POST,entity2, new ParameterizedTypeReference<>(){});

        Assert.assertNotNull(response2.getBody());
        assertFalse(response2.getBody().isCreated());
    }
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void createRideScheduled_NoDriver_Available(){
        logIn("passenger1@test.com","123");
        RideCreateRequestDTO requestDTO= createRequestDTO();
        requestDTO.setRideNow(false);
        requestDTO.setRideTime(LocalDateTime.now().plusHours(2));
        HttpHeaders headers= new HttpHeaders();
        headers.setBearerAuth(this.token);

        HttpEntity<RideCreateRequestDTO> entity= new HttpEntity<>(requestDTO,headers);

        ResponseEntity<RideCreateResponseDTO> response=restTemplate.exchange("/api/rides/create"
                ,HttpMethod.POST,entity, new ParameterizedTypeReference<>(){});

        Assert.assertNotNull(response.getBody());
        assertTrue(response.getBody().isCreated());

        RideCreateRequestDTO requestDTO2= createRequestDTO();
        requestDTO2.setRideNow(false);
        requestDTO2.setRideTime(LocalDateTime.now().plusHours(2));
        HttpEntity<RideCreateRequestDTO> entity2= new HttpEntity<>(requestDTO2,headers);
        ResponseEntity<RideCreateResponseDTO> response2=restTemplate.exchange("/api/rides/create",HttpMethod.POST,entity2, new ParameterizedTypeReference<>(){});

        Assert.assertNotNull(response.getBody());
        assertFalse(response2.getBody().isCreated());
    }

    @Test
    public void createRide_InvalidDto_NoCoordinates_Returns400BadRequest() {
        logIn("passenger1@test.com","123");
        RideCreateRequestDTO invalidDto = new RideCreateRequestDTO();
        invalidDto.setCoordinates(new ArrayList<>());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(this.token);
        HttpEntity<RideCreateRequestDTO> entity = new HttpEntity<>(invalidDto, headers);

        ResponseEntity<String> response = restTemplate.exchange("/api/rides/create", HttpMethod.POST, entity, String.class);

        Assert.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void createRide_LoggedInAsDriver_Returns403Forbidden() {
        logIn("driver1@test.com","123");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(this.token);
        HttpEntity<RideCreateRequestDTO> entity = new HttpEntity<>(createRequestDTO(), headers);

        ResponseEntity<String> response = restTemplate.exchange("/api/rides/create", HttpMethod.POST, entity, String.class);

        Assert.assertEquals(response.getStatusCode(), HttpStatus.FORBIDDEN);
    }
}
