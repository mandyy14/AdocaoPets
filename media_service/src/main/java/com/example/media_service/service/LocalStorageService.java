package com.example.media_service.service;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.nio.file.InvalidPathException;

import com.example.media_service.exceptions.StorageException;
import com.example.media_service.exceptions.StorageFileNotFoundException;

@Service
public class LocalStorageService implements IStorageService {

    private static final Logger logger = LoggerFactory.getLogger(LocalStorageService.class);

    @Value("${app.storage.profile-pictures.local-dir}")
    private String uploadDir;

    private Path rootLocation;

    @Override
    @PostConstruct
    public void init() {
        try {
            this.rootLocation = Paths.get(uploadDir);
            if (!Files.exists(rootLocation)) {
                Files.createDirectories(rootLocation);
                logger.info("Diretório de upload criado: {}", rootLocation.toAbsolutePath());
            } else {
                logger.info("Diretório de upload já existe: {}", rootLocation.toAbsolutePath());
            }
        } catch (IOException e) {
            logger.error("Não foi possível inicializar o diretório de upload!", e);
            throw new StorageException("Não foi possível inicializar o diretório de upload!", e);
        }
    }

    @Override
    public String store(MultipartFile file, String specificSubFolder) {
        String originalFilenameNullable = file.getOriginalFilename();
        String originalFilename;
        if (originalFilenameNullable == null) {
             throw new StorageException("Nome do arquivo original não pode ser nulo.");
        } else {
             originalFilename = originalFilenameNullable;
        }
        String cleanedFilename = StringUtils.cleanPath(originalFilename);

        String fileExtension = "";
        int extensionIndex = cleanedFilename.lastIndexOf(".");
        if (extensionIndex > 0) {
             fileExtension = cleanedFilename.substring(extensionIndex);
        }
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

        try {
            if (file.isEmpty()) {
                throw new StorageException("Falha ao armazenar arquivo vazio " + cleanedFilename);
            }
            if (uniqueFilename.contains("..")) {
                 throw new StorageException("Path inválido fora do diretório: " + cleanedFilename);
            }
            
            Path destinationFile = this.rootLocation.resolve(uniqueFilename).normalize().toAbsolutePath();
            Path rootAbsoluteNormalized = this.rootLocation.toAbsolutePath().normalize();
            Path destinationAbsoluteNormalized = destinationFile.toAbsolutePath().normalize();
    
            if (!destinationAbsoluteNormalized.startsWith(rootAbsoluteNormalized)) {
                throw new StorageException("Não é possível armazenar arquivo fora do diretório raiz (check startsWith): " + cleanedFilename);
            }
            System.out.println("[DEBUG] 7. Checagem startsWith passou."); 

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
                logger.info("Arquivo salvo com sucesso: {}", destinationFile);
            }
            return uniqueFilename;

        } catch (IOException e) {
            logger.error("Falha ao armazenar arquivo {}: {}", cleanedFilename, e.getMessage());
            throw new StorageException("Falha ao armazenar arquivo " + cleanedFilename, e);        
        } catch (StorageException e) {
            logger.error("StorageException durante store: {}", e.getMessage());
            throw e;
        }
    }

    private Path loadPath(String subfolder, String filename) {
        try {
            if (filename == null || filename.trim().isEmpty()) {
                 throw new StorageException("Nome do arquivo não pode ser nulo ou vazio.");
            }
            if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
                 throw new StorageException("Nome de arquivo contém caracteres inválidos/inseguros: " + filename);
            }

            Path targetDir = this.rootLocation;

            if (subfolder != null && !subfolder.trim().isEmpty()) {
                 if (subfolder.contains("..") || subfolder.contains("/") || subfolder.contains("\\")) {
                    throw new StorageException("Subdiretório contém caracteres inválidos/inseguros: " + subfolder);
                 }
                 targetDir = this.rootLocation.resolve(subfolder).normalize();
                 if (!targetDir.startsWith(this.rootLocation)) {
                      throw new StorageException("Tentativa de acesso a subdiretório inválido (fora da raiz): " + subfolder);
                 }
            }

            Path resolvedPath = targetDir.resolve(filename).normalize();

             if (!resolvedPath.toAbsolutePath().startsWith(this.rootLocation.toAbsolutePath())) {
                 throw new StorageException("Tentativa de acesso a arquivo fora do diretório de armazenamento permitido: " + filename);
             }

            return resolvedPath;

        } catch (InvalidPathException ex) {
             throw new StorageException("Nome de arquivo ou subdiretório contém caracteres inválidos: " + filename, ex);
        }
    }

    @Override
    public ResponseEntity<Resource> loadAndServe(String subfolder, String filename, HttpServletRequest request) {
        try {
            Path file = loadPath(subfolder, filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                String contentType = null;
                try {
                    contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
                } catch (IOException ex) {
                    logger.info("Não foi possível determinar o tipo do arquivo via ServletContext: {}", filename);
                }
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }
                MediaType mediaType = MediaType.parseMediaType(contentType);

                return ResponseEntity.ok()
                        .contentType(mediaType)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                 logger.warn("Recurso não encontrado ou ilegível: {}", filename);
                throw new StorageFileNotFoundException("Não foi possível ler o arquivo: " + filename);
            }
        } catch (MalformedURLException e) {
            logger.error("Erro ao formar URL para o arquivo {}: {}", filename, e.getMessage());
             throw new StorageFileNotFoundException("Não foi possível ler o arquivo: " + filename, e);
        } catch (StorageFileNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao carregar/servir arquivo {}: {}", filename, e.getMessage());
             throw new StorageException("Erro inesperado ao servir o arquivo " + filename, e);
        }
    }
}
