package com.example.user_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsuarioNaoEncontradoException.class)
    public ResponseEntity<String> handleUsuarioNaoEncontrado(UsuarioNaoEncontradoException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UsuarioJaExistenteException.class)
    public ResponseEntity<String> handleUsuarioJaExistente(UsuarioJaExistenteException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CargoInvalidoException.class)
    public ResponseEntity<String> handleCargoInvalido(CargoInvalidoException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
