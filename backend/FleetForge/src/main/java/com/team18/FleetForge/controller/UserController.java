package com.team18.FleetForge.controller;


import com.team18.FleetForge.model.users.User;
import com.team18.FleetForge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;

    @PostMapping(
            value = "/profile-picture",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Void> uploadProfileImage(
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) throws java.io.IOException {

        User user = (User) authentication.getPrincipal();

        String extension = Objects.requireNonNull(file.getOriginalFilename())
                .substring(file.getOriginalFilename().lastIndexOf("."));

        String filename = user.getUsername() + extension;

        Path path = Paths.get("src/main/resources/static/uploads/pfp/" + filename);

        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());

        user.setProfilePicture("/uploads/pfp/" + filename);
        userRepository.save(user);

        return ResponseEntity.noContent().build();
    }

}
