package com.example.user_service.service;

import com.example.user_service.dto.CadastroRequest;
import com.example.user_service.model.Usuario;
import com.example.user_service.dto.UpdateEmailRequest;
import com.example.user_service.dto.UpdatePasswordRequest;
import java.util.List;
import java.util.Optional;

public interface IUsuarioService {
    Usuario salvarUsuario(CadastroRequest dto);
    Usuario buscarPorId(Long id);
    Usuario buscarPorLogin(String login);
    Usuario buscarPorCpf(String cpf);
    List<Usuario> listarTodos();
    void deletarUsuario(Long id, boolean confirmarExclusao);
    Usuario autenticarUsuario(String email, String senha);
    Usuario updateEmail(Long userId, UpdateEmailRequest dto);
    void updatePassword(Long userId, UpdatePasswordRequest dto);
    Optional<String> getProfileImageUrlFromMediaService(Long userId);
}
