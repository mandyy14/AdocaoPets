package com.example.user_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.user_service.dao.UsuarioDao;
import com.example.user_service.exceptions.CredenciaisInvalidasException;
import com.example.user_service.exceptions.UsuarioJaExistenteException;
import com.example.user_service.exceptions.UsuarioNaoEncontradoException;
import com.example.user_service.model.Usuario;

@Service
class UsuarioService implements IUsuarioService {

    @Autowired
    private UsuarioDao usuarioDao;

    @Override
    public void salvarUsuario(Usuario usuario) {
        if (usuarioDao.buscarPorLogin(usuario.getLogin()) != null) {
            throw new UsuarioJaExistenteException("Login '" + usuario.getLogin() + "' já está em uso");
        }
        if (usuarioDao.buscarPorEmail(usuario.getEmail()) != null) {
            throw new UsuarioJaExistenteException("E-mail '" + usuario.getEmail() + "' já está em uso");
        }
        if (usuarioDao.buscarPorCpf(usuario.getCpf()) != null) {
             throw new UsuarioJaExistenteException("CPF '" + usuario.getCpf() + "' já está em uso");
        }

        usuarioDao.salvarUsuario(usuario);
    }

    @Override
    public Usuario buscarPorId(Long id) {
        Usuario usuario = usuarioDao.buscarPorId(id);
        if (usuario == null) {
            throw new UsuarioNaoEncontradoException("Usuário não encontrado com ID: " + id);
        }
        return usuario;
    }

    @Override
    public Usuario buscarPorLogin(String login) {
        Usuario usuario = usuarioDao.buscarPorLogin(login);
        if (usuario == null) {
            throw new UsuarioNaoEncontradoException("Usuário não encontrado com Login: " + login);
        }
        return usuario;
    }

    @Override
    public List<Usuario> listarTodos() {
        return usuarioDao.listarTodos();
    }

    @Override
    public void atualizarUsuario(Usuario usuario) {
        Usuario usuarioExistente = usuarioDao.buscarPorId(usuario.getId());
        if (usuarioExistente == null) {
            throw new UsuarioNaoEncontradoException("Usuário não encontrado para atualização com ID: " + usuario.getId());
        }

        if (!usuario.getLogin().equalsIgnoreCase(usuarioExistente.getLogin()) && usuarioDao.buscarPorLogin(usuario.getLogin()) != null) {
             throw new UsuarioJaExistenteException("Login '" + usuario.getLogin() + "' já está em uso por outro usuário");
        }
        if (!usuario.getEmail().equalsIgnoreCase(usuarioExistente.getEmail()) && usuarioDao.buscarPorEmail(usuario.getEmail()) != null) {
             throw new UsuarioJaExistenteException("E-mail '" + usuario.getEmail() + "' já está em uso por outro usuário");
        }
         if (!usuario.getCpf().equals(usuarioExistente.getCpf()) && usuarioDao.buscarPorCpf(usuario.getCpf()) != null) {
             throw new UsuarioJaExistenteException("CPF '" + usuario.getCpf() + "' já está em uso por outro usuário");
         }

         // TODO: Tratar atualização de senha

        usuarioDao.atualizarUsuario(usuario);
    }

    @Override
    public void deletarUsuario(Long id, boolean confirmarExclusao) {
        if (!confirmarExclusao) {
            throw new IllegalArgumentException("A exclusão do usuário precisa ser confirmada.");
        }
        if (usuarioDao.buscarPorId(id) == null) {
            throw new UsuarioNaoEncontradoException("Usuário não encontrado para deleção com ID: " + id);
        }
        usuarioDao.deletarUsuario(id);
    }


    @Override
    public Usuario autenticarUsuario(String email, String senha) {
        Usuario usuario = usuarioDao.buscarPorEmail(email);

        if (usuario == null) {
            throw new UsuarioNaoEncontradoException("Usuário não encontrado com o email fornecido: " + email);
        }

        // TODO: Substituir por passwordEncoder.matches(senha, usuario.getSenha())
        if (!senha.equals(usuario.getSenha())) {
            throw new CredenciaisInvalidasException("Senha inválida.");
        }

        return usuario;
    }

}