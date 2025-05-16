package com.example.pet_service.exceptions;

public class PetNaoEncontradoException extends RuntimeException {
    public PetNaoEncontradoException(String message) {
        super(message);
    }
}
