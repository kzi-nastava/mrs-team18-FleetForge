package com.team18.FleetForge.controller.user;


import com.team18.FleetForge.dto.CurrentUserDTO;
import com.team18.FleetForge.model.users.User;
import com.team18.FleetForge.service.ProfilePictureService;
import com.team18.FleetForge.service.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final ProfilePictureService profilePictureService;
    private final UserService userService;

    @GetMapping("/current")
    public ResponseEntity<CurrentUserDTO> getCurrentUser(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if(user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        CurrentUserDTO dto = new CurrentUserDTO();
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setProfileImage(user.getProfilePicture());
        dto.setRole(user.getRole().name());

        return ResponseEntity.ok(dto);
    }

    /**
     * POST /api/users/profile-picture
     * Request:
     *  - file
     *  - authentication
     * Response:
     *  - 204 NO CONTENT on successful registration
     *  - 409 CONFLICT on already taken email
     *  - 500 INTERNAL SERVER ERROR if errors while saving
     */
    @PostMapping(
            value = "/profile-picture",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> uploadProfileImage(
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) {
        try {
            User user = (User) authentication.getPrincipal();
            profilePictureService.uploadProfilePicture(user, file);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload profile picture"));
        }
    }
    @PostMapping("upload-profile-picture/{id}")
    public ResponseEntity<?> uploadProfilePictureById(@RequestParam("file") MultipartFile file,@PathVariable Long id){
        try {
            User user = (User) userService.getUserById(id);
            profilePictureService.uploadProfilePicture(user, file);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload profile picture"));
        }
    }
    @PostMapping("upload-profile-picture")
    public ResponseEntity<?> uploadProfilePictureCurrentUser(@RequestParam("file") MultipartFile file){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) authentication.getPrincipal();
            profilePictureService.uploadProfilePicture(user, file);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload profile picture"));
        }
    }
}
