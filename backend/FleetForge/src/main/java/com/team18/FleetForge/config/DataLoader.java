package com.team18.FleetForge.config;

import com.team18.FleetForge.model.ride.GeoPoint;
import com.team18.FleetForge.model.vecihles.Vehicle;
import com.team18.FleetForge.model.enums.RideActor;
import com.team18.FleetForge.model.enums.Role;
import com.team18.FleetForge.model.enums.RideStatus;
import com.team18.FleetForge.model.enums.VehicleType;
import com.team18.FleetForge.model.ride.FavoriteRoute;
import com.team18.FleetForge.model.ride.Ride;
import com.team18.FleetForge.model.users.Admin;
import com.team18.FleetForge.model.users.Driver;
import com.team18.FleetForge.model.users.Passenger;
import com.team18.FleetForge.model.chat.Chat;
import com.team18.FleetForge.model.chat.ChatMessage;
import com.team18.FleetForge.repository.chat.ChatMessageRepository;
import com.team18.FleetForge.repository.chat.ChatRepository;
import com.team18.FleetForge.repository.rides.FavoriteRouteRepo;
import com.team18.FleetForge.repository.rides.RideRepository;
import com.team18.FleetForge.repository.users.DriverRepository;
import com.team18.FleetForge.repository.users.PassengerRepository;
import com.team18.FleetForge.repository.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final DriverRepository driverRepository;
    private final PassengerRepository passengerRepository;
    private final UserRepository userRepository;
    private final RideRepository rideRepository;
    private final PasswordEncoder passwordEncoder;
    private final FavoriteRouteRepo favoriteRouteRepo;
    private final ChatRepository chatRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Override
    public void run(String... args) {
        System.out.println("DataLoader: Starting to load test data...");

        Vehicle vehicle1 = new Vehicle(null, "Toyota Camry", VehicleType.STANDARD,
                "NS-123-AB", 4, false, false);
        Vehicle vehicle2 = new Vehicle(null, "Mercedes S-Class", VehicleType.STANDARD,
                "NS-456-CD", 4, true, false);

        Driver driver1 = new Driver();
        driver1.setEmail("driver1@test.com");
        driver1.setPassword(passwordEncoder.encode("123"));
        driver1.setFirstName("John");
        driver1.setLastName("Doe");
        driver1.setPhoneNumber("+381641234567");
        driver1.setAddress("Bulevar oslobođenja 10, Novi Sad");
        driver1.setProfilePicture("/uploads/pfp/default.png");
        driver1.setVehicle(vehicle1);
        driver1.setActive(true);
        driver1.setAvailable(false);
        driver1.setCurrentLocation(new GeoPoint(45.2671, 19.8335));
        driver1.setRole(Role.ROLE_DRIVER);
        driver1.setActivated(true);
        driver1.setBlocked(false);

        Driver driver2 = new Driver();
        driver2.setEmail("driver2@test.com");
        driver2.setPassword(passwordEncoder.encode("123"));
        driver2.setFirstName("Jane");
        driver2.setLastName("Smith");
        driver2.setPhoneNumber("+381649876543");
        driver2.setAddress("Zmaj Jovina 5, Novi Sad");
        driver2.setProfilePicture("/uploads/pfp/default.png");
        driver2.setVehicle(vehicle2);
        driver2.setActive(true);
        driver2.setAvailable(true);
        driver2.setCurrentLocation(new GeoPoint(45.2551, 19.8451));
        driver2.setRole(Role.ROLE_DRIVER);
        driver2.setActivated(true);
        driver2.setBlocked(false);

        driver1 = driverRepository.save(driver1);
        driverRepository.save(driver2);

        Passenger passenger1 = new Passenger();
        passenger1.setEmail("passenger1@test.com");
        passenger1.setPassword(passwordEncoder.encode("123"));
        passenger1.setFirstName("Marko");
        passenger1.setLastName("Marković");
        passenger1.setPhoneNumber("+381641111111");
        passenger1.setAddress("Narodnih heroja 15, Novi Sad");
        passenger1.setProfilePicture("/uploads/pfp/default.png");
        passenger1.setRole(Role.ROLE_PASSENGER);
        passenger1.setActivated(true);
        passenger1.setBlocked(false);

        Passenger passenger2 = new Passenger();
        passenger2.setEmail("passenger2@test.com");
        passenger2.setPassword(passwordEncoder.encode("123"));
        passenger2.setFirstName("Ana");
        passenger2.setLastName("Anić");
        passenger2.setPhoneNumber("+381642222222");
        passenger2.setAddress("Dunavska 10, Novi Sad");
        passenger2.setProfilePicture("/uploads/pfp/default.png");
        passenger2.setRole(Role.ROLE_PASSENGER);
        passenger2.setActivated(true);
        passenger2.setBlocked(false);

        Passenger passenger3 = new Passenger();
        passenger3.setEmail("mdamjanovic2004@gmail.com");
        passenger3.setPassword(passwordEncoder.encode("123"));
        passenger3.setFirstName("Jovana");
        passenger3.setLastName("Jovanović");
        passenger3.setPhoneNumber("+381643333333");
        passenger3.setAddress("Tolstojeva 5, Novi Sad");
        passenger3.setProfilePicture("/uploads/pfp/default.png");
        passenger3.setRole(Role.ROLE_PASSENGER);
        passenger3.setActivated(true);
        passenger3.setBlocked(false);

        Passenger passenger4 = new Passenger();
        passenger4.setEmail("passenger4@test.com");
        passenger4.setPassword(passwordEncoder.encode("123"));
        passenger4.setFirstName("Petar");
        passenger4.setLastName("Petrović");
        passenger4.setPhoneNumber("+381644444444");
        passenger4.setAddress("Modene 20, Novi Sad");
        passenger4.setProfilePicture("/uploads/pfp/default.png");
        passenger4.setRole(Role.ROLE_PASSENGER);
        passenger4.setActivated(true);
        passenger4.setBlocked(false);

        passenger1 = passengerRepository.save(passenger1);
        passenger2 = passengerRepository.save(passenger2);
        passenger3 = passengerRepository.save(passenger3);
        passenger4 = passengerRepository.save(passenger4);

        Admin admin = new Admin();
        admin.setEmail("admin@test.com");
        admin.setPassword(passwordEncoder.encode("123"));
        admin.setFirstName("System");
        admin.setLastName("Admin");
        admin.setPhoneNumber("+381640000000");
        admin.setAddress("Admin Office");
        admin.setRole(Role.ROLE_ADMIN);
        admin.setActivated(true);
        admin.setBlocked(false);

        userRepository.save(admin);

        System.out.println("DataLoader: Users, drivers, and passengers created");

        Ride ride1 = createRide(
                driver1, passenger1,
                new GeoPoint(45.2550, 19.8450), "Bulevar oslobođenja 46, Novi Sad",
                new GeoPoint(45.2671, 19.8335), "Trg slobode 1, Novi Sad",
                LocalDateTime.now(),
                null,
                5.2, 20.0, 850.0,
                RideStatus.ACCEPTED, false, null, null
        );
        rideRepository.save(ride1);

        Ride ride2 = createRide(
                driver1, passenger2,
                new GeoPoint(45.2600, 19.8400), "Futoška 10, Novi Sad",
                new GeoPoint(45.2500, 19.8600), "Petrovaradin Tvrđava",
                LocalDateTime.now().withHour(18).withMinute(0),
                LocalDateTime.now().withHour(18).withMinute(30),
                7.5, 30.0, 1120.0,
                RideStatus.COMPLETED, false, null, null
        );
        ride2.setLinkedPassengers(new ArrayList<>(List.of(passenger3)));
        rideRepository.save(ride2);

        Ride ride3 = createRide(
                driver1, passenger4,
                new GeoPoint(45.2731, 19.8535), "Trg slobode 1, Novi Sad",
                new GeoPoint(45.2470, 19.8454), "Spens, Novi Sad",
                LocalDateTime.now().plusMinutes(7),
                null,
                5.2, 20.0, 850.0,
                RideStatus.ACCEPTED, false, null, null
        );
        rideRepository.save(ride3);

        Ride ride4 = createRide(
                driver1, passenger2,
                new GeoPoint(45.2650, 19.8300), "Cara Dušana 10, Novi Sad",
                new GeoPoint(45.2700, 19.8500), "Liman 3",
                LocalDateTime.now().minusDays(5).withHour(16).withMinute(0),
                null, // No end time because cancelled
                8.0, 25.0, 1080.0,
                RideStatus.CANCELLED, false, RideActor.PASSENGER,
                "Changed plans, no longer need ride"
        );
        ride4.setCancelledAt(LocalDateTime.now().minusDays(5).withHour(15).withMinute(55));
        rideRepository.save(ride4);

        Ride ride5 = createRide(
                driver1, passenger1,
                new GeoPoint(45.2580, 19.8420), "Modene 10, Novi Sad",
                new GeoPoint(45.2620, 19.8380), "Dnevnik, Novi Sad",
                LocalDateTime.now().withHour(9).withMinute(0),
                LocalDateTime.now().withHour(9).withMinute(20),
                4.5, 20.0, 780.0,
                RideStatus.COMPLETED, false, null, null
        );
        rideRepository.save(ride5);

        Ride ride6 = createRide(
                driver2, passenger3,
                new GeoPoint(45.2600, 19.8400), "Novi Sad Centar",
                new GeoPoint(45.2500, 19.8600), "Petrovaradin",
                LocalDateTime.now().minusDays(3).withHour(20).withMinute(0),
                LocalDateTime.now().minusDays(3).withHour(20).withMinute(25),
                6.0, 25.0, 960.0,
                RideStatus.COMPLETED, true, null, null
        );
        ride6.setPanicActivatedAt(LocalDateTime.now().minusDays(3).withHour(20).withMinute(10));
        rideRepository.save(ride6);

//        Ride ride7 = createRide(
//                driver1, passenger1,
//                new GeoPoint(45.2605, 19.8421), "Bulevar oslobođenja 12, Novi Sad",
//                new GeoPoint(45.2510, 19.8640), "Liman 4",
//                LocalDateTime.now().plusDays(2).withHour(14).withMinute(0),
//                null,
//                6.3, 22.0, 920.0,
//                RideStatus.ACCEPTED, false, null, null
//        );
//        rideRepository.save(ride7);

        Ride ride8 = createRide(
                driver2, passenger4,
                new GeoPoint(45.2670, 19.8305), "Cara Dušana 55, Novi Sad",
                new GeoPoint(45.2520, 19.8615), "Petrovaradin",
                LocalDateTime.now().plusDays(1).withHour(16).withMinute(30),
                null,
                7.1, 25.0, 1100.0,
                RideStatus.ACCEPTED, false, null, null
        );
        rideRepository.save(ride8);

        Ride ride9 = createRide(
                driver1, passenger4,
                new GeoPoint(45.2590, 19.8350), "Futoška 18, Novi Sad",
                new GeoPoint(45.2671, 19.8335), "Trg slobode",
                LocalDateTime.now().plusDays(5).withHour(9).withMinute(0),
                null,
                4.8, 18.0, 760.0,
                RideStatus.CANCELLED, false, null, null
        );
        rideRepository.save(ride9);

        Ride ride10 = createRide(
                driver2, passenger4,
                new GeoPoint(45.2623, 19.8315), "Bulevar Cara Lazara 15, Novi Sad",
                new GeoPoint(45.2480, 19.8520), "Štrand, Novi Sad",
                LocalDateTime.now().minusDays(10).withHour(12).withMinute(30),
                LocalDateTime.now().minusDays(10).withHour(12).withMinute(55),
                4.2, 18.0, 720.0,
                RideStatus.COMPLETED, false, null, null
        );
        rideRepository.save(ride10);

        Ride ride11 = createRide(
                driver1, passenger4,
                new GeoPoint(45.2720, 19.8470), "Bulevar Mihajla Pupina 10, Novi Sad",
                new GeoPoint(45.2630, 19.8410), "Delta City, Novi Sad",
                LocalDateTime.now().minusDays(8).withHour(19).withMinute(15),
                null,
                3.8, 15.0, 680.0,
                RideStatus.CANCELLED, false, RideActor.DRIVER,
                "Vehicle breakdown"
        );
        ride11.setCancelledAt(LocalDateTime.now().minusDays(8).withHour(19).withMinute(10));
        rideRepository.save(ride11);

        Ride ride12 = createRide(
                driver2, passenger4,
                new GeoPoint(45.2555, 19.8380), "Kisacka 25, Novi Sad",
                new GeoPoint(45.2680, 19.8600), "Telep, Novi Sad",
                LocalDateTime.now().minusDays(7).withHour(8).withMinute(45),
                LocalDateTime.now().minusDays(7).withHour(9).withMinute(15),
                5.7, 22.0, 890.0,
                RideStatus.COMPLETED, true, null, null
        );
        ride12.setPanicActivatedAt(LocalDateTime.now().minusDays(7).withHour(9).withMinute(5));
        rideRepository.save(ride12);

        Ride ride13 = createRide(
                driver1, passenger4,
                new GeoPoint(45.2640, 19.8550), "Liman 1, Novi Sad",
                new GeoPoint(45.2570, 19.8320), "Grbavica, Novi Sad",
                LocalDateTime.now().minusDays(6).withHour(17).withMinute(30),
                LocalDateTime.now().minusDays(6).withHour(18).withMinute(5),
                6.8, 28.0, 1050.0,
                RideStatus.COMPLETED, false, null, null
        );
        rideRepository.save(ride13);

        Ride ride14 = createRide(
                driver2, passenger4,
                new GeoPoint(45.2500, 19.8450), "Petrovaradin, Novi Sad",
                new GeoPoint(45.2700, 19.8350), "Centar, Novi Sad",
                LocalDateTime.now().minusDays(4).withHour(14).withMinute(0),
                null,
                7.2, 30.0, 1150.0,
                RideStatus.CANCELLED, false, RideActor.PASSENGER,
                "Found alternative transportation"
        );
        ride14.setCancelledAt(LocalDateTime.now().minusDays(4).withHour(13).withMinute(45));
        rideRepository.save(ride14);

        Ride ride15 = createRide(
                driver1, passenger4,
                new GeoPoint(45.2590, 19.8270), "Detelinara, Novi Sad",
                new GeoPoint(45.2730, 19.8420), "Vojvodanska 10, Novi Sad",
                LocalDateTime.now().minusDays(3).withHour(10).withMinute(0),
                LocalDateTime.now().minusDays(3).withHour(10).withMinute(35),
                8.5, 32.0, 1250.0,
                RideStatus.COMPLETED, false, null, null
        );
        rideRepository.save(ride15);

        Ride ride16 = createRide(
                driver2, passenger4,
                new GeoPoint(45.2675, 19.8370), "Trg slobode 5, Novi Sad",
                new GeoPoint(45.2530, 19.8570), "Petrovaradinska tvrdjava",
                LocalDateTime.now().minusDays(2).withHour(21).withMinute(0),
                LocalDateTime.now().minusDays(2).withHour(21).withMinute(40),
                5.5, 24.0, 950.0,
                RideStatus.COMPLETED, true, null, null
        );
        ride16.setPanicActivatedAt(LocalDateTime.now().minusDays(2).withHour(21).withMinute(25));
        rideRepository.save(ride16);

        Ride ride17 = createRide(
                driver1, passenger4,
                new GeoPoint(45.2610, 19.8430), "Futoski put 30, Novi Sad",
                new GeoPoint(45.2490, 19.8310), "Satelit, Novi Sad",
                LocalDateTime.now().plusDays(1).withHour(7).withMinute(30),
                null,
                4.0, 16.0, 650.0,
                RideStatus.ACCEPTED, false, RideActor.DRIVER,
                "Emergency situation"
        );
        ride17.setCancelledAt(LocalDateTime.now().minusDays(1).withHour(7).withMinute(20));
        rideRepository.save(ride17);

        Ride ride18 = createRide(
                driver2, passenger4,
                new GeoPoint(45.2560, 19.8520), "Klisa, Novi Sad",
                new GeoPoint(45.2690, 19.8280), "Podbara, Novi Sad",
                LocalDateTime.now().minusHours(6).withMinute(0),
                LocalDateTime.now().minusHours(5).withMinute(25),
                9.0, 35.0, 1350.0,
                RideStatus.COMPLETED, false, null, null
        );
        rideRepository.save(ride18);

        Ride ride19 = createRide(
                driver1, passenger4,
                new GeoPoint(45.2630, 19.8490), "Bulevar oslobođenja 80, Novi Sad",
                new GeoPoint(45.2540, 19.8390), "Mise Dimitrijevica 12, Novi Sad",
                LocalDateTime.now().minusHours(3).withMinute(0),
                null,
                3.5, 14.0, 580.0,
                RideStatus.CANCELLED, false, RideActor.PASSENGER,
                "Plans changed unexpectedly"
        );
        ride19.setCancelledAt(LocalDateTime.now().minusHours(3).withMinute(30));
        rideRepository.save(ride19);

        Ride ride20 = createRide(
                driver2, passenger4,
                new GeoPoint(45.2580, 19.8620), "Sajam, Novi Sad",
                new GeoPoint(45.2470, 19.8250), "Novo Naselje, Novi Sad",
                LocalDateTime.now().minusHours(12).withMinute(0),
                LocalDateTime.now().minusHours(11).withMinute(20),
                10.5, 40.0, 1550.0,
                RideStatus.COMPLETED, false, null, null
        );
        ride20.setLinkedPassengers(new ArrayList<>(List.of(passenger1, passenger2)));
        rideRepository.save(ride20);


        FavoriteRoute route=new FavoriteRoute();
        route.setRide(ride1);
        route.setPassenger(passenger1);
        route.setName("Home-work");
        favoriteRouteRepo.save(route);


        Chat chat1 = Chat.builder()
                .user(passenger1)
                .createdAt(LocalDateTime.now().minusDays(2))
                .lastMessageAt(LocalDateTime.now().minusHours(1))
                .build();
        chat1 = chatRepository.save(chat1);

        ChatMessage msg1_1 = ChatMessage.builder()
                .chat(chat1)
                .sender(passenger1)
                .content("Hello, I have a question about my recent ride.")
                .sentAt(LocalDateTime.now().minusHours(2))
                .isRead(true)
                .readAt(LocalDateTime.now().minusHours(2).plusMinutes(5))
                .build();
        chatMessageRepository.save(msg1_1);

        ChatMessage msg1_2 = ChatMessage.builder()
                .chat(chat1)
                .sender(admin)
                .content("Hello John! I'd be happy to help. What's your question?")
                .sentAt(LocalDateTime.now().minusHours(2).plusMinutes(6))
                .isRead(true)
                .readAt(LocalDateTime.now().minusHours(2).plusMinutes(10))
                .build();
        chatMessageRepository.save(msg1_2);

        ChatMessage msg1_3 = ChatMessage.builder()
                .chat(chat1)
                .sender(passenger1)
                .content("I was charged twice for the same ride yesterday.")
                .sentAt(LocalDateTime.now().minusHours(1).minusMinutes(30))
                .isRead(true)
                .readAt(LocalDateTime.now().minusHours(1).minusMinutes(25))
                .build();
        chatMessageRepository.save(msg1_3);

        ChatMessage msg1_4 = ChatMessage.builder()
                .chat(chat1)
                .sender(admin)
                .content("I apologize for that. Let me check your transaction history and I'll resolve this immediately.")
                .sentAt(LocalDateTime.now().minusHours(1))
                .isRead(false) // Unread by passenger
                .build();
        chatMessageRepository.save(msg1_4);

        Chat chat2 = Chat.builder()
                .user(driver1)
                .createdAt(LocalDateTime.now().minusDays(1))
                .lastMessageAt(LocalDateTime.now().minusMinutes(30))
                .build();
        chat2 = chatRepository.save(chat2);

        ChatMessage msg2_1 = ChatMessage.builder()
                .chat(chat2)
                .sender(driver1)
                .content("Hi, I need help updating my vehicle information.")
                .sentAt(LocalDateTime.now().minusHours(1))
                .isRead(true)
                .readAt(LocalDateTime.now().minusMinutes(55))
                .build();
        chatMessageRepository.save(msg2_1);

        ChatMessage msg2_2 = ChatMessage.builder()
                .chat(chat2)
                .sender(admin)
                .content("Sure! What would you like to update?")
                .sentAt(LocalDateTime.now().minusMinutes(50))
                .isRead(true)
                .readAt(LocalDateTime.now().minusMinutes(45))
                .build();
        chatMessageRepository.save(msg2_2);

    }

    private Ride createRide(Driver driver, Passenger passenger,
                            GeoPoint startLocation, String startAddress,
                            GeoPoint endLocation, String endAddress,
                            LocalDateTime startTime, LocalDateTime endTime,
                            Double distance, Double duration, Double cost,
                            RideStatus status, Boolean panicActivated,
                            RideActor cancelledBy, String cancellationReason) {

        Ride ride = new Ride();
        ride.setDriver(driver);
        ride.setPassenger(passenger);
        ride.setStartLocation(startLocation);
        ride.setStartAddress(startAddress);
        ride.setEndLocation(endLocation);
        ride.setEndAddress(endAddress);
        ride.setStartTime(startTime);
        ride.setEndTime(endTime);
        ride.setTotalDistance(distance);
        ride.setEstimatedDuration(duration);
        ride.setTotalCost(cost);
        ride.setStatus(status);
        ride.setPanicActivated(panicActivated);
        ride.setCancelledBy(cancelledBy);
        ride.setCancellationReason(cancellationReason);
        ride.setLinkedPassengers(new ArrayList<>());
        ride.setWayPoints(new ArrayList<>());
        ride.setVehicleType(VehicleType.STANDARD);

        return ride;
    }
}