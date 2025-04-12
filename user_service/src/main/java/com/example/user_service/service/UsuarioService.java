package com.example.user_service.service;

import com.example.user_service.dto.CadastroRequest;
import com.example.user_service.dto.UpdateEmailRequest;
import com.example.user_service.dto.UpdatePasswordRequest; 
import com.example.user_service.model.Usuario;
import com.example.user_service.repository.UsuarioRepository;
import com.example.user_service.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
// import org.springframework.security.crypto.password.PasswordEncoder; // Para Hashing

import br.com.caelum.stella.validation.CPFValidator;
import br.com.caelum.stella.validation.InvalidStateException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
class UsuarioService implements IUsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Usuario salvarUsuario(CadastroRequest dto) {

        // Regra de Negócio (duplicidade dos dados)
        if (usuarioRepository.findByLogin(dto.getEmail()).isPresent()) { throw new UsuarioJaExistenteException("Login (baseado no email) '" + dto.getEmail() + "' já está em uso"); }
        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) { throw new UsuarioJaExistenteException("E-mail '" + dto.getEmail() + "' já está em uso"); }
        if (usuarioRepository.findByCpf(dto.getCpf()).isPresent()) { throw new UsuarioJaExistenteException("CPF '" + dto.getCpf() + "' já está em uso"); }

        // Regra de Negócio (duplicidade / validação de cpf)
        String cpfLimpo = validarELimparCpf(dto.getCpf());
        if (usuarioRepository.findByCpf(cpfLimpo).isPresent()) {
            throw new UsuarioJaExistenteException("CPF '" + dto.getCpf() + "' já está em uso");
       }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(dto.getNome());
        novoUsuario.setCpf(cpfLimpo);
        novoUsuario.setCelular(dto.getCelular());
        novoUsuario.setEndereco(dto.getEndereco());
        novoUsuario.setEmail(dto.getEmail());
        novoUsuario.setLogin(dto.getEmail());
        novoUsuario.setCargo("user");

        // TODO: Adicionar HASHING da senha AQUI
        novoUsuario.setSenha(dto.getSenha());

        Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);

        return usuarioSalvo;
    }


    @Override
    @Transactional(readOnly = true)
    public Usuario autenticarUsuario(String email, String senha) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado com o email fornecido: " + email));

        // TODO: Comparar senha com Hashing (passwordEncoder.matches)

        if (!senha.equals(usuario.getSenha())) {
            throw new CredenciaisInvalidasException("Senha inválida.");
        }
        return usuario;
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado com ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario buscarPorLogin(String login){
        return usuarioRepository.findByLogin(login)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado com Login: " + login));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Override
    public void deletarUsuario(Long id, boolean confirmarExclusao) {
        if (!confirmarExclusao) {
            throw new IllegalArgumentException("A exclusão do usuário precisa ser confirmada.");
        }
        if (!usuarioRepository.existsById(id)) {
            throw new UsuarioNaoEncontradoException("Usuário não encontrado para deleção com ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario buscarPorCpf(String cpf) {
        return usuarioRepository.findByCpf(cpf)
            .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado com CPF: " + cpf));
    }

    @Override
    public Usuario updateEmail(Long userId, UpdateEmailRequest dto) {
        Usuario usuario = buscarPorId(userId);

        // TODO: Usar Hashing: if (!passwordEncoder.matches(dto.getCurrentPassword(), usuario.getSenha())) {
        if (!dto.getCurrentPassword().equals(usuario.getSenha())) {
            throw new CredenciaisInvalidasException("Senha atual incorreta.");
        }

        String newEmail = dto.getNewEmail();
        Optional<Usuario> usuarioComMesmoEmail = usuarioRepository.findByEmail(newEmail);
        if (usuarioComMesmoEmail.isPresent() && !Objects.equals(usuarioComMesmoEmail.get().getId(), userId)) {
            throw new UsuarioJaExistenteException("O email '" + newEmail + "' já está em uso por outro usuário.");
        }

        usuario.setEmail(newEmail);
        usuario.setLogin(newEmail);

        return usuarioRepository.save(usuario);
    }
 
    @Override
    public void updatePassword(Long userId, UpdatePasswordRequest dto) {
        Usuario usuario = buscarPorId(userId);
 
        // TODO: Usar Hashing: if (!passwordEncoder.matches(dto.getCurrentPassword(), usuario.getSenha())) {
        if (!dto.getCurrentPassword().equals(usuario.getSenha())) {
            throw new CredenciaisInvalidasException("Senha atual incorreta.");
        }
 
        String newPassword = dto.getNewPassword();
 
        // TODO: Usar Hashing: if (passwordEncoder.matches(newPassword, usuario.getSenha())) {
        if (newPassword.equals(usuario.getSenha())) {
            throw new IllegalArgumentException("A nova senha não pode ser igual à senha atual.");
        }
 
        // 5. Define a nova senha (ainda sem hash)
        // TODO: Adicionar HASHING da senha AQUI
        // String senhaComHash = passwordEncoder.encode(newPassword);
        // usuario.setSenha(senhaComHash);
        usuario.setSenha(newPassword);
 
        usuarioRepository.save(usuario);
    }

    private String validarELimparCpf(String cpfBruto) {
        String cpfLimpo = cpfBruto.replaceAll("[^0-9]", "");

        try {
            CPFValidator cpfValidator = new CPFValidator(false);
            cpfValidator.assertValid(cpfLimpo);
            return cpfLimpo;
        } catch (InvalidStateException e) {
            String errorMessage = e.getInvalidMessages().isEmpty() ? "Dígitos verificadores ou formato inválido." : e.getInvalidMessages().get(0).getMessage();
            throw new IllegalArgumentException("CPF inválido: " + errorMessage);
        }
    }

 }
