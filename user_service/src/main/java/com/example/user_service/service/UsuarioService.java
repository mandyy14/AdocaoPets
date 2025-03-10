package com.example.user_service.service;

import com.example.user_service.dao.UsuarioDbConnection;
import com.example.user_service.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioDbConnection usuarioDbConnection;

    public Usuario cadastrarUsuario(String login, String senha, String email, String cargo) {
        // Simulação de cadastro
        Usuario usuario = new Usuario(login, senha, email, cargo);
        return usuarioDbConnection.save(usuario);
    }

    public Usuario logarUsuario(String login, String senha) {
        // Simulação de login
        Usuario usuario = usuarioDbConnection.buscarPorUsuario(login);
        if (usuario != null && usuario.getSenha().equals(senha)) {
            // TODO: Gerar e retornar JWT
            return usuario;
        }
        return null;
    }

    public Optional<Usuario> getUsuario(Long id) {
        return usuarioDbConnection.findById(id);
    }

    public Usuario atualizarUsuario(Long id, String login, String email) {
        Usuario usuario = usuarioDbConnection.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        usuario.setLogin(login);
        usuario.setEmail(email);
        return usuarioDbConnection.save(usuario);
    }
}
