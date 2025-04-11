package com.example.mediaservice.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class LocalStorageService implements IStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    private Path rootLocation;

    @Override
    @PostConstruct
    public void init() {
        try {
            this.rootLocation = Paths.get(uploadDir);
            if (!Files.exists(rootLocation)) {
                Files.createDirectories(rootLocation);
                System.out.println("Diretório de upload criado: " + rootLocation.toAbsolutePath());
            } else {
                 System.out.println("Diretório de upload já existe: " + rootLocation.toAbsolutePath());
            }
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível inicializar o diretório de upload!", e);
        }
    }

    @Override
    public String store(MultipartFile file, String specificSubFolder) {
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "file");
        String fileExtension = "";
        int extensionIndex = originalFilename.lastIndexOf(".");
        if (extensionIndex > 0) {
             fileExtension = originalFilename.substring(extensionIndex);
        }

        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Falha ao armazenar arquivo vazio.");
            }

            Path subDirLocation = this.rootLocation;

            Path destinationFile = subDirLocation.resolve(Paths.get(uniqueFilename))
                    .normalize().toAbsolutePath();

            if (!destinationFile.getParent().equals(subDirLocation.toAbsolutePath())) {
                throw new RuntimeException("Não é possível armazenar arquivo fora do diretório raiz.");
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            System.out.println(">>> DEBUG Storage: Arquivo salvo em: " + destinationFile);

            return uniqueFilename;

        } catch (IOException e) {
            throw new RuntimeException("Falha ao armazenar arquivo.", e);
        }
    }
}
