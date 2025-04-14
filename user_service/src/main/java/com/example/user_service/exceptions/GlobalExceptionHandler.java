package com.example.user_service.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.context.support.DefaultMessageSourceResolvable;

// import com.example.user_service.exceptions.UsuarioNaoEncontradoException;
// import com.example.user_service.exceptions.UsuarioJaExistenteException;
// import com.example.user_service.exceptions.CredenciaisInvalidasException;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UsuarioNaoEncontradoException.class)
    public ResponseEntity<String> handleUsuarioNaoEncontrado(UsuarioNaoEncontradoException ex) {
        logger.warn("Tentativa de acesso a usuário não encontrado: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND); // 404
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
     public ResponseEntity<String> handleCredenciaisInvalidas(CredenciaisInvalidasException ex) {
         logger.warn("Tentativa de login com credenciais inválidas: {}", ex.getMessage());
         return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED); // 401
     }

    @ExceptionHandler(UsuarioJaExistenteException.class)
    public ResponseEntity<String> handleUsuarioJaExistente(UsuarioJaExistenteException ex) {
        logger.warn("Tentativa de cadastro duplicado: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT); // 409
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        logger.error("Erro de integridade de dados no banco: {}", ex.getMostSpecificCause().getMessage());
        String mensagemCliente = "Erro ao salvar os dados. Verifique se o email, login ou CPF já estão cadastrados.";
        return new ResponseEntity<>(mensagemCliente, HttpStatus.CONFLICT); // 409
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String erros = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
        logger.warn("Erro de validação nos dados de entrada: {}", erros);
        return new ResponseEntity<>(erros, HttpStatus.BAD_REQUEST); // 400
    }

     @ExceptionHandler(IllegalArgumentException.class)
     public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        logger.warn("Argumento inválido recebido ou regra de negócio violada: {}", ex.getMessage());
         return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST); // 400
     }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
         logger.error("Erro inesperado na aplicação: ", ex);

        String mensagemCliente = "Ocorreu um erro inesperado no servidor. Tente novamente mais tarde.";
        return new ResponseEntity<>(mensagemCliente, HttpStatus.INTERNAL_SERVER_ERROR); // 500
    }

}
