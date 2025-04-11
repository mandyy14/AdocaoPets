package com.example.user_service.controller;

import com.example.user_service.dto.CadastroRequest;
import com.example.user_service.dto.LoginRequest;
import com.example.user_service.dto.UpdateEmailRequest;
import com.example.user_service.dto.UpdatePasswordRequest;
import com.example.user_service.model.Usuario;
import com.example.user_service.service.IUsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UsuarioController {

    private final IUsuarioService usuarioService;

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
        // TODO: retornar um Token JWT em vez do objeto Usuario
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

}
