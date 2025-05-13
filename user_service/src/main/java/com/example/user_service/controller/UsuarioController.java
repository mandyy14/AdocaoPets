package com.example.user_service.controller;

import java.util.List;
import java.util.Optional;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.user_service.dto.CadastroRequest;
import com.example.user_service.dto.LoginRequest;
import com.example.user_service.dto.UpdateEmailRequest;
import com.example.user_service.dto.UpdatePasswordRequest;
import com.example.user_service.model.Usuario;
import com.example.user_service.service.IUsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UsuarioController {

    private final IUsuarioService usuarioService;

    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    public UsuarioController(IUsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<Usuario> cadastrarUsuario(@Valid @RequestBody CadastroRequest dto) {
        Usuario usuarioSalvo = usuarioService.salvarUsuario(dto);

        usuarioSalvo.setSenha(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioSalvo);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUsuario(@Valid @RequestBody LoginRequest loginRequest) {
        Usuario usuarioAutenticado = usuarioService.autenticarUsuario(loginRequest.getEmail(), loginRequest.getSenha());
        usuarioAutenticado.setSenha(null);
        return ResponseEntity.ok(usuarioAutenticado);
    }

    @GetMapping("/listar")
    public List<Usuario> listarUsuarios() {
        List<Usuario> usuarios = usuarioService.listarTodos();
        usuarios.forEach(u -> u.setSenha(null));
        return usuarios;
    }

    @GetMapping("/{id}/buscar")
    public ResponseEntity<Usuario> buscarUsuario(@PathVariable Long id) {
        Usuario usuario = usuarioService.buscarPorId(id);
        usuario.setSenha(null);
        return ResponseEntity.ok(usuario);
    }

    @DeleteMapping("/{id}/deletar")
     public ResponseEntity<Void> deletarUsuario(@PathVariable Long id, @RequestParam(defaultValue = "false") boolean confirmacao) {
        usuarioService.deletarUsuario(id, confirmacao);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/email")
    public ResponseEntity<Usuario> updateEmail(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEmailRequest dto) {

        Usuario usuarioAtualizado = usuarioService.updateEmail(id, dto);
        usuarioAtualizado.setSenha(null);
        return ResponseEntity.ok(usuarioAtualizado);
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> updatePassword(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePasswordRequest dto) {

        usuarioService.updatePassword(id, dto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/profile-picture-url")
     public ResponseEntity<?> getProfilePictureUrl(@PathVariable Long id) {
        logger.info("Recebida requisição para buscar URL da foto do usuário {}", id);
        Optional<String> imageUrlOpt = usuarioService.getProfileImageUrlFromMediaService(id);

        return imageUrlOpt
        .map(url -> ResponseEntity.ok().body(Map.of("imageUrl", url)))
        .orElseGet(() -> {
            logger.info("Nenhuma URL de foto encontrada para usuário {}", id);
            return ResponseEntity.notFound().build();
        });
    }

}
