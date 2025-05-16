package com.example.media_service.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<String> handleStorageFileNotFound(StorageFileNotFoundException ex) {
        logger.warn("Tentativa de acesso a arquivo não encontrado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage()); // 404
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<String> handleStorageException(StorageException ex) {
        logger.error("Erro de armazenamento: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro de armazenamento: " + ex.getMessage()); // 400
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleMaxSizeException(MaxUploadSizeExceededException ex) {
        logger.warn("Tentativa de upload de arquivo muito grande: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE) // 413
                             .body("Arquivo muito grande! O tamanho máximo permitido é configurado no servidor.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
         logger.error("Erro inesperado na aplicação de mídia: ", ex);
        String mensagemCliente = "Ocorreu um erro inesperado no servidor de mídia.";
        return new ResponseEntity<>(mensagemCliente, HttpStatus.INTERNAL_SERVER_ERROR); // 500
    }
}
