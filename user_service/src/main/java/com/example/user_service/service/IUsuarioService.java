package com.example.user_service.service;

import java.util.List;

import com.example.user_service.model.Usuario;

public interface IUsuarioService {
    void salvarUsuario(Usuario usuario);
    Usuario buscarPorId(Long id);
    Usuario buscarPorLogin(String login);
    List<Usuario> listarTodos();
    void atualizarUsuario(Usuario usuario);
    void deletarUsuario(Long id, boolean confirmarExclusao);
    Usuario autenticarUsuario(String email, String senha);
}

