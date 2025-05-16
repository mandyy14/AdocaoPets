package com.example.user_service.exceptions;

public class CargoInvalidoException extends RuntimeException {
    public CargoInvalidoException(String message) {
        super(message);
    }
}
