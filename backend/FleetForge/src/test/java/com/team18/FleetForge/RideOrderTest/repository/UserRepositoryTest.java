package com.team18.FleetForge.RideOrderTest.repository;

import com.team18.FleetForge.model.enums.Role;
import com.team18.FleetForge.model.users.Passenger;
import com.team18.FleetForge.model.users.User;
import com.team18.FleetForge.repository.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Optional;

import static org.testng.AssertJUnit.*;

@DataJpaTest
public class UserRepositoryTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void findByEmailSuccessfull(){
        Passenger passenger= new Passenger();
        passenger.setFirstName("aa");
        passenger.setLastName("aa");
        passenger.setAddress("adresa");
        passenger.setPhoneNumber("343242432");
        passenger.setRole(Role.ROLE_PASSENGER);
        passenger.setPassword("123");
        passenger.setEmail("aaa@gmail.com");
        userRepository.save(passenger);

        Optional<User> user= userRepository.findByEmail("aaa@gmail.com");
        assertTrue(user.isPresent());
        assertEquals("aaa@gmail.com", user.get().getEmail());
    }
    @Test
    public void findByEmailFail() {
        Optional<User> result = userRepository.findByEmail("nepostoji@gmail.com");
        assertFalse(result.isPresent());
    }
}
