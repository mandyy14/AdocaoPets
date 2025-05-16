package com.example.user_service.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import br.com.caelum.stella.validation.CPFValidator;
import br.com.caelum.stella.validation.InvalidStateException;

import com.example.user_service.dto.CadastroRequest;
import com.example.user_service.dto.UpdateEmailRequest;
import com.example.user_service.dto.UpdatePasswordRequest;
import com.example.user_service.exceptions.*;
import com.example.user_service.model.Usuario;
import com.example.user_service.repository.UsuarioRepository;

@Service
@Transactional
class UsuarioService implements IUsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;
    private final RestTemplate restTemplate;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.services.media.url}")
    private String mediaServiceBaseUrl;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, RestTemplate restTemplate, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.restTemplate = restTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Usuario salvarUsuario(CadastroRequest dto) {

        // Regra de negócio (duplicidade dos dados)
        if (usuarioRepository.findByLogin(dto.getEmail()).isPresent()) { throw new UsuarioJaExistenteException("Login (baseado no email) '" + dto.getEmail() + "' já está em uso"); }
        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) { throw new UsuarioJaExistenteException("E-mail '" + dto.getEmail() + "' já está em uso"); }
        if (usuarioRepository.findByCpf(dto.getCpf()).isPresent()) { throw new UsuarioJaExistenteException("CPF '" + dto.getCpf() + "' já está em uso"); }

        // Regra de negócio (validação de cpf)
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

        // Regra de negócio (hashing da senha)
        String senhaComHash = passwordEncoder.encode(dto.getSenha());
        novoUsuario.setSenha(senhaComHash);

        Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);

        return usuarioSalvo;
    }


    @Override
    @Transactional(readOnly = true)
    public Usuario autenticarUsuario(String email, String senha) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado com o email fornecido: " + email));

        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
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

        if (!passwordEncoder.matches(dto.getCurrentPassword(), usuario.getSenha())) {
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
 
        if (!passwordEncoder.matches(dto.getCurrentPassword(), usuario.getSenha())) {
            throw new CredenciaisInvalidasException("Senha atual incorreta.");
        }
 
        String newPassword = dto.getNewPassword();
 
        if (passwordEncoder.matches(newPassword, usuario.getSenha())) {
            throw new IllegalArgumentException("A nova senha não pode ser igual à senha atual.");
        }
 
        String novaSenhaComHash = passwordEncoder.encode(newPassword);
        usuario.setSenha(novaSenhaComHash);
 
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


    @Override
    @Transactional(readOnly = true)
    public Optional<String> getProfileImageUrlFromMediaService(Long userId) {
        if (!usuarioRepository.existsById(userId)) {
            logger.warn("Tentativa de buscar URL de foto para usuário inexistente: {}", userId);
            return Optional.empty();
        }

        String url = mediaServiceBaseUrl + "/api/media/profile-picture-info/" + userId;
        logger.info("Chamando media-service para obter URL da foto: {}", url);

        try {
            ParameterizedTypeReference<Map<String, Object>> responseType = new ParameterizedTypeReference<>() {};

            ResponseEntity<Map<String, Object>> response =
                restTemplate.exchange(url, HttpMethod.GET, null, responseType);

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();

                if (responseBody != null) {
                    Object imageUrlObj = responseBody.get("imageUrl");
                    if (imageUrlObj instanceof String && !((String) imageUrlObj).isEmpty()) {
                        logger.info("URL da foto encontrada para userId {}: {}", userId, imageUrlObj);
                        return Optional.of((String) imageUrlObj);
                    } else {
                        logger.warn("Resposta OK do media-service mas 'imageUrl' inválida ou ausente no corpo para userId {}", userId);
                        return Optional.empty();
                    }
                } else {
                    logger.warn("Resposta OK do media-service mas corpo era nulo para userId {}", userId);
                    return Optional.empty();
                }
            } else {
                logger.warn("Media-service retornou status {} para busca de foto do userId {}", response.getStatusCode(), userId);
                return Optional.empty();
            }

        } catch (HttpClientErrorException.NotFound ex) {
            logger.info("Media-service retornou 404 (sem foto) para userId {}", userId);
            return Optional.empty();
        } catch (RestClientException ex) {
            logger.error("Erro de comunicação ao chamar media-service ({}) para obter URL da foto do userId {}: {}", url, userId, ex.getMessage());
            return Optional.empty();
        } catch (Exception ex) {
             logger.error("Erro inesperado em getProfileImageUrlFromMediaService para userId {}: {}", userId, ex.getMessage(), ex);
             return Optional.empty();
        }
    }

 }
