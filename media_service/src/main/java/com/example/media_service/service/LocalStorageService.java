package com.example.media_service.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.media_service.exceptions.StorageException;
import com.example.media_service.exceptions.StorageFileNotFoundException;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class LocalStorageService implements IStorageService {

    private static final Logger logger = LoggerFactory.getLogger(LocalStorageService.class);

    @Value("${app.storage.local-dir}")
    private String uploadRootDir;

    private Path rootLocation;

    @Override
    @PostConstruct
    public void init() {
        try {
            this.rootLocation = Paths.get(uploadRootDir).toAbsolutePath().normalize();
            if (!Files.exists(rootLocation)) {
                Files.createDirectories(rootLocation);
                logger.info("Diretório raiz de upload criado: {}", rootLocation);
            } else {
                logger.info("Diretório raiz de upload já existe: {}", rootLocation);
            }
             Files.createDirectories(this.rootLocation.resolve("profile-pictures"));
             Files.createDirectories(this.rootLocation.resolve("pet-pictures"));

        } catch (IOException e) {
            logger.error("Não foi possível inicializar o diretório raiz de upload!", e);
            throw new StorageException("Não foi possível inicializar o diretório raiz de upload!", e);
        }
    }

    @Override
    public String store(MultipartFile file, String subfolder) {
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
                throw new StorageException("Nome de arquivo gerado contém '..' inválido: " + uniqueFilename);
            }

            if (subfolder == null || subfolder.isBlank()) {
                 throw new StorageException("Subpasta de destino não pode ser vazia para store.");
             }
            if (subfolder.contains("..") || subfolder.contains("/") || subfolder.contains("\\")) {
               throw new StorageException("Subpasta contém caracteres inválidos/inseguros: " + subfolder);
            }
            Path targetDirectory = this.rootLocation.resolve(subfolder).normalize();
            if (!targetDirectory.startsWith(this.rootLocation)) {
                 throw new StorageException("Tentativa de acesso a subpasta inválida (fora da raiz): " + subfolder);
            }
            Files.createDirectories(targetDirectory);

            Path destinationFile = targetDirectory.resolve(uniqueFilename).normalize().toAbsolutePath();

            if (!destinationFile.startsWith(this.rootLocation.toAbsolutePath())) {
                logger.error("!!! FALHA DE SEGURANÇA store: Destino {} fora da raiz {}", destinationFile, this.rootLocation.toAbsolutePath());
                throw new StorageException("Não é possível armazenar arquivo fora do diretório raiz permitido: " + cleanedFilename);
            }
            logger.debug("[DEBUG store] Checagem startsWith passou.");

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
                logger.info("Arquivo salvo com sucesso: {}", destinationFile);
            }
            return uniqueFilename;

        } catch (IOException e) {
            logger.error("Falha IO ao armazenar arquivo {} para subpasta {}: {}", cleanedFilename, subfolder, e.getMessage(), e);
            throw new StorageException("Falha de IO ao armazenar arquivo " + cleanedFilename, e);
        } catch (StorageException e) {
            logger.error("StorageException durante store: {}", e.getMessage());
            throw e;
         } catch (Exception e) {
             logger.error("Erro inesperado durante store para {}: {}", cleanedFilename, e.getMessage(), e);
             throw new StorageException("Erro inesperado ao armazenar arquivo " + cleanedFilename, e);
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
             if (subfolder == null || subfolder.isBlank()) {
                 throw new StorageException("Subpasta não pode ser vazia para carregar arquivo.");
             }
             if (subfolder.contains("..") || subfolder.contains("/") || subfolder.contains("\\")) {
                throw new StorageException("Subpasta contém caracteres inválidos/inseguros: " + subfolder);
             }

            Path targetDir = this.rootLocation.resolve(subfolder).normalize();
            if (!targetDir.startsWith(this.rootLocation.toAbsolutePath().normalize())) {
                throw new StorageException("Tentativa de acesso a subdiretório inválido (fora da raiz): " + subfolder);
            }

            Path resolvedPath = targetDir.resolve(filename).normalize();

            if (!resolvedPath.toAbsolutePath().startsWith(this.rootLocation.toAbsolutePath().normalize())) {
                 logger.error("!!! FALHA DE SEGURANÇA loadPath: Path final {} fora da raiz {}", resolvedPath.toAbsolutePath(), this.rootLocation.toAbsolutePath().normalize());
                 throw new StorageException("Tentativa de acesso a arquivo fora do diretório de armazenamento permitido: " + filename);
            }
            logger.debug("[DEBUG loadPath] Path final validado: {}", resolvedPath);
            return resolvedPath;

        } catch (InvalidPathException ex) {
             logger.error("Path inválido encontrado: subfolder={}, filename={}", subfolder, filename, ex);
             throw new StorageException("Nome de arquivo ou subdiretório contém caracteres inválidos: " + filename, ex);
        }
    }

    @Override
    public ResponseEntity<Resource> loadAndServe(String subfolder, String filename, HttpServletRequest request) {
        try {
            Path file = loadPath(subfolder, filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() && resource.isReadable()) {
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

                logger.info("Servindo arquivo: {}", filename);
                return ResponseEntity.ok()
                        .contentType(mediaType)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                logger.warn("Tentativa de servir arquivo não encontrado ou ilegível: type={}, filename={}", subfolder, filename);
                throw new StorageFileNotFoundException("Não foi possível ler o arquivo: " + filename);
            }
        } catch (MalformedURLException e) {
            logger.error("Erro de MalformedURLException ao servir {}: {}", filename, e.getMessage());
            throw new StorageFileNotFoundException("Não foi possível ler o arquivo (URL inválida): " + filename, e);
        } catch (StorageFileNotFoundException e) {
             logger.warn("StorageFileNotFoundException ao servir {}: {}", filename, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao carregar/servir arquivo {}: {}", filename, e.getMessage(), e);
             throw new StorageException("Erro inesperado ao servir o arquivo " + filename, e);
        }
    }
}
