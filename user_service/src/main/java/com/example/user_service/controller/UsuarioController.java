package com.example.user_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.user_service.dto.LoginRequest;
import com.example.user_service.exceptions.CredenciaisInvalidasException;
import com.example.user_service.model.Usuario;
import com.example.user_service.service.IUsuarioService;

@RestController
@RequestMapping("/api/users")
// @CrossOrigin(origins = "http://localhost:3000") // Alternativa para CORS (ver WebConfig)
public class UsuarioController {

    private final IUsuarioService usuarioService;

    public UsuarioController(IUsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<Usuario> cadastrarUsuario(@RequestBody Usuario usuario) {
        try {
            usuarioService.salvarUsuario(usuario);
            usuario.setSenha(null);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
       } catch (Exception e) {
            throw e;
       }
   }

    @PostMapping("/login")
    public ResponseEntity<?> loginUsuario(@RequestBody LoginRequest loginRequest) {
        try {
            Usuario usuarioAutenticado = usuarioService.autenticarUsuario(loginRequest.getEmail(), loginRequest.getSenha());
            usuarioAutenticado.setSenha(null);
            // TODO: retornar um Token JWT
            return ResponseEntity.ok(usuarioAutenticado);
        } catch (CredenciaisInvalidasException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/listar")
    public List<Usuario> listarUsuarios() {
        List<Usuario> usuarios = usuarioService.listarTodos();
        usuarios.forEach(u -> u.setSenha(null));
        return usuarios;
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<Usuario> buscarUsuario(@PathVariable Long id) {
        Usuario usuario = usuarioService.buscarPorId(id);
        usuario.setSenha(null);
        return ResponseEntity.ok(usuario);
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<Usuario> atualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
         usuario.setId(id);

         // TODO: Melhorar a lógica de atualização de senha
         // Se a senha no request for nula/vazia, talvez não deva atualizar?
         // Ou um endpoint separado para mudança de senha.

        usuarioService.atualizarUsuario(usuario);
        usuario.setSenha(null);
        return ResponseEntity.ok(usuario);
    }

    @DeleteMapping("/deletar/{id}")
     public ResponseEntity<Void> deletarUsuario(@PathVariable Long id, @RequestParam(defaultValue = "false") boolean confirmacao) {
        if (!confirmacao) {
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).header("X-Error-Message", "Confirmacao necessaria para deletar").build();
         }
        usuarioService.deletarUsuario(id, confirmacao);
        return ResponseEntity.noContent().build();
    }

}
