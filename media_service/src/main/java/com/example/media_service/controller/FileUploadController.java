package com.example.mediaservice.controller;

import com.example.mediaservice.service.IStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/media")
public class FileUploadController {

    private final IStorageService storageService;

    @Autowired
    public FileUploadController(IStorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/upload/profile-picture")
    public ResponseEntity<?> uploadProfilePicture(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Arquivo está vazio.");
        }
        String contentType = file.getContentType();
         if (contentType == null || !(contentType.equals("image/jpeg") || contentType.equals("image/png") || contentType.equals("image/webp"))) {
             return ResponseEntity.badRequest().body("Tipo de arquivo inválido. Use JPG, PNG ou WEBP.");
         }

        try {
            String generatedFilename = storageService.store(file, null);

            Map<String, String> response = Map.of(
                 "fileName", generatedFilename,
                 "relativeUrl", "/media/profile-pictures/" + generatedFilename
             );
             return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            System.err.println("!!! ERRO Upload: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Falha ao fazer upload: " + e.getMessage());
        }
    }

    // TODO: Adicionar um endpoint GET para servir os arquivos salvos localmente
}
