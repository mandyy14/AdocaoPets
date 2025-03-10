package com.example.user_service.service;

import com.example.user_service.model.Usuario;
import java.util.List;

public interface IUsuarioService {
    void salvarUsuario(Usuario usuario);
    Usuario buscarPorId(Long id);
    Usuario buscarPorLogin(String login);
    List<Usuario> listarTodos();
    void atualizarUsuario(Usuario usuario);
    void deletarUsuario(Long id, boolean confirmarExclusao);
}

