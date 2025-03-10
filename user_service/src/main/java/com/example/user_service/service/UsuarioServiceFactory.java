package com.example.user_service.service;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;


@Component
public class UsuarioServiceFactory {

    @Autowired
    private UsuarioService usuarioService;

    public IUsuarioService create() {
        return usuarioService;
    }
    
}