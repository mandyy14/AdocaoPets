package com.example.user_service.service;

import com.example.user_service.dao.UsuarioDao;
import com.example.user_service.exceptions.CargoInvalidoException;
import com.example.user_service.exceptions.UsuarioJaExistenteException;
import com.example.user_service.exceptions.UsuarioNaoEncontradoException;
import com.example.user_service.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service  // Anotação para o Spring saber que essa é uma classe de Service
class UsuarioService implements IUsuarioService {

    @Autowired
    private UsuarioDao usuarioDao;


    // Métodos

    // Regra de Negócio: salvar um novo usuário com validações

    @Override
    public void salvarUsuario(Usuario usuario) {
        // Verificar se o login já está em uso
        if (usuarioDao.buscarPorLogin(usuario.getLogin()) != null) {
            throw new UsuarioJaExistenteException("Login já está em uso");
        }

        // Verificar se o e-mail é válido
        if (usuarioDao.buscarPorLogin(usuario.getEmail()) != null) {
            throw new UsuarioJaExistenteException("E-mail já está em uso");
        }

        // Verificar se o cargo é válido
        if (!"admin".equals(usuario.getCargo()) && !"user".equals(usuario.getCargo())) {
            throw new CargoInvalidoException("Cargo inválido. Os valores permitidos são 'admin' e 'user'.");
        }

        usuarioDao.salvarLogin(usuario);
    }


    // Regra de Negócio: busca por ID de usuário

    @Override
    public Usuario buscarPorId(Long id) {
        Usuario usuario = usuarioDao.buscarPorId(id);
        if (usuario == null) {
            throw new UsuarioNaoEncontradoException("Usuário não encontrado");
        }
        return usuario;
    }


    // Regra de Negócio: busca por login de usuário

    @Override
    public Usuario buscarPorLogin(String login) {
        Usuario usuario = usuarioDao.buscarPorLogin(login);
        if (usuario == null) {
            throw new UsuarioNaoEncontradoException("Usuário não encontrado com o login fornecido");
        }
        return usuario;
    }


    // Listar todos os usuários

    @Override
    public List<Usuario> listarTodos() {
        return usuarioDao.listarTodos();
    }


    // Regra de Negócio: atualização de usuário

    @Override
    public void atualizarUsuario(Usuario usuario) {
        // Verificar se o usuário existe no banco de dados
        Usuario usuarioExistente = usuarioDao.buscarPorId(usuario.getId());
        if (usuarioExistente == null) {
            throw new UsuarioNaoEncontradoException("Usuário não encontrado para atualização");
        }

        // Verificar se o novo login já está em uso (se o login for diferente do original)
        if (!usuario.getLogin().equals(usuarioExistente.getLogin()) && usuarioDao.buscarPorLogin(usuario.getLogin()) != null) {
            throw new UsuarioJaExistenteException("Login já está em uso");
        }

        // Verificar se o novo e-mail já está em uso (se o e-mail for diferente do original)
        if (!usuario.getEmail().equals(usuarioExistente.getEmail()) && usuarioDao.buscarPorLogin(usuario.getEmail()) != null) {
            throw new UsuarioJaExistenteException("E-mail já está em uso");
        }

        usuarioDao.atualizarLogin(usuario);
    }


    // Regra de Negócio: exclusão de usuário

    @Override
    public void deletarUsuario(Long id, boolean confirmarExclusao) {
        // Verificar se o usuário existe antes de tentar deletat
        Usuario usuarioExistente = usuarioDao.buscarPorId(id);
        if (usuarioExistente == null) {
            throw new UsuarioNaoEncontradoException("Usuário não encontrado para deleção");
        }
    
        // Regra de Negócio: Confirmar a exclusão
        if (!confirmarExclusao) {
            throw new IllegalArgumentException("A exclusão do usuário precisa ser confirmada.");
        }
    
        usuarioDao.deletarUsuario(id);
    }
    

}
