package com.team18.FleetForge.service;

import com.team18.FleetForge.model.users.User;
import com.team18.FleetForge.repository.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfilePictureService {

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/pfp/";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final List<String> ALLOWED_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/jpg", "image/webp"
    );

    private final UserRepository userRepository;

    /**
     * Validates and uploads profile picture for a user
     * @param user The user to upload the picture for
     * @param file The image file
     * @throws IOException if file operations fail
     * @throws IllegalArgumentException if validation fails
     */
    public void uploadProfilePicture(User user, MultipartFile file) throws IOException {
        validateProfilePicture(file);

        String extension = getFileExtension(file.getOriginalFilename());
        String filename = generateFilename(user, extension);
        Path filePath = saveFile(file, filename);

        updateUserProfilePicture(user, "/uploads/pfp/" + filename);
    }

    private void validateProfilePicture(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        // Check file type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Only JPEG, PNG and WebP images are allowed");
        }

        // Check file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size must be less than 5MB");
        }

        // Check file extension
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.matches(".*\\.(jpg|jpeg|png|webp)$")) {
            throw new IllegalArgumentException("Invalid file extension");
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null");
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    private String generateFilename(User user, String extension) {
        // Use username or ID - username is more readable
        String base = user.getUsername() != null ? user.getUsername() : String.valueOf(user.getId());
        return base + extension;
    }

    private Path saveFile(MultipartFile file, String filename) throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR);
        Path filePath = uploadPath.resolve(filename);

        // Create directory if it doesn't exist
        Files.createDirectories(uploadPath);

        // Write file
        Files.write(filePath, file.getBytes());

        return filePath;
    }

    private void updateUserProfilePicture(User user, String picturePath) {
        user.setProfilePicture(picturePath);
        userRepository.save(user);
    }
}