package com.example.media_service.controller;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.media_service.model.ProfilePicture;
import com.example.media_service.repository.ProfilePictureRepository;
import com.example.media_service.service.IStorageService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/media")
public class FileUploadController {

    private final IStorageService storageService;
    private final ProfilePictureRepository profilePictureRepository;
    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    @Autowired
    public FileUploadController(IStorageService storageService, ProfilePictureRepository profilePictureRepository) {
        this.storageService = storageService;
        this.profilePictureRepository = profilePictureRepository;
    }

    // Endpoint upload de foto
    @PostMapping("/upload/profile-picture/{userId}")
    public ResponseEntity<?> uploadProfilePicture(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) { return ResponseEntity.badRequest().body("Arquivo está vazio."); }

        String contentType = file.getContentType();
        if (contentType == null || !(contentType.equals("image/jpeg") || contentType.equals("image/png") || contentType.equals("image/webp"))) {
            return ResponseEntity.badRequest().body("Tipo de arquivo inválido. Use JPG, PNG ou WEBP.");
        }

        try {
            String storageIdentifier = storageService.store(file, "profile-pictures");

            ProfilePicture profilePicture = profilePictureRepository.findByUserId(userId)
                    .orElse(new ProfilePicture(userId, null));

            profilePicture.setStorageIdentifier(storageIdentifier);
            profilePictureRepository.save(profilePicture);

            String relativeUrl = "/api/media/serve/profile-pictures/" + storageIdentifier;

            Map<String, String> response = Map.of("fileName", storageIdentifier,"relativeUrl", relativeUrl);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Falha na requisição de upload para userId {}: {}", userId, e.getMessage());
            throw e;
        }
    }

    // Endpoint pra buscar info da foto
    @GetMapping("/profile-picture-info/{userId}")
    public ResponseEntity<?> getProfilePictureInfo(@PathVariable Long userId) {
        Optional<ProfilePicture> pictureOpt = profilePictureRepository.findByUserId(userId);

        if (pictureOpt.isPresent()) {
            ProfilePicture picture = pictureOpt.get();
            String storageIdentifier = picture.getStorageIdentifier();
             String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                 .path("/api/media/serve/profile-pictures/")
                 .path(storageIdentifier)
                 .toUriString();

             Map<String, String> response = Map.of("imageUrl", fileDownloadUri);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Endpoint pra servir a img
    @GetMapping("/serve/{subfolder}/{filename:.+}")
    public ResponseEntity<Resource> serveFile(
            @PathVariable String subfolder, // profile-pictures ou pet-pictures
            @PathVariable String filename,
            HttpServletRequest request) {
        logger.debug("Requisição para servir arquivo: subfolder={}, filename={}", subfolder, filename);
        return storageService.loadAndServe(subfolder, filename, request);
    }

}
