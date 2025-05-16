package com.example.pet_service.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.context.support.DefaultMessageSourceResolvable;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(PetNaoEncontradoException.class)
    public ResponseEntity<String> handlePetNaoEncontrado(PetNaoEncontradoException ex) {
        logger.warn("Tentativa de acesso a pet não encontrado: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND); // 404
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String erros = ex.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
        logger.warn("Erro de validação nos dados de entrada do pet: {}", erros);
        return new ResponseEntity<>(erros, HttpStatus.BAD_REQUEST); // 400
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        logger.error("Erro de integridade de dados no banco (pet): {}", ex.getMostSpecificCause().getMessage());
        return new ResponseEntity<>("Erro ao processar dados do pet. Verifique se há informações duplicadas.", HttpStatus.CONFLICT); // 409
    }

     @ExceptionHandler(IllegalArgumentException.class)
     public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        logger.warn("Argumento inválido recebido (pet): {}", ex.getMessage());
         return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST); // 400
     }

    // Handler Genérico
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
         logger.error("Erro inesperado no PetService: ", ex);
        return new ResponseEntity<>("Ocorreu um erro inesperado no servidor de pets.", HttpStatus.INTERNAL_SERVER_ERROR); // 500
    }
    
}
