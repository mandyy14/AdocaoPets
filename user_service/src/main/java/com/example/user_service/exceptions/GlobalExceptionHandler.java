package com.example.user_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsuarioNaoEncontradoException.class)
    public ResponseEntity<String> handleUsuarioNaoEncontrado(UsuarioNaoEncontradoException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND); // 404
    }

    @ExceptionHandler(UsuarioJaExistenteException.class)
    public ResponseEntity<String> handleUsuarioJaExistente(UsuarioJaExistenteException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT); // 409
    }

     @ExceptionHandler(CredenciaisInvalidasException.class)
     public ResponseEntity<String> handleCredenciaisInvalidas(CredenciaisInvalidasException ex) {
         return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED); // 401
     }

     @ExceptionHandler(IllegalArgumentException.class)
     public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
         return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST); // 400
     }


    // Handler genérico
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return new ResponseEntity<>("Ocorreu um erro interno no servidor.", HttpStatus.INTERNAL_SERVER_ERROR); // 500
    }

}
