package com.example.user_service.controller;

import com.example.user_service.model.Usuario;
import com.example.user_service.service.IUsuarioService;
import com.example.user_service.service.UsuarioServiceFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UsuarioController {
    
    private final IUsuarioService usuarioService;

    public UsuarioController(UsuarioServiceFactory usuarioServiceFactory) {
        this.usuarioService = usuarioServiceFactory.create();
    }


    // Listar todos os usuários
    @GetMapping("/listar")
    public List<Usuario> listarUsuarios() {
        return usuarioService.listarTodos();
    }


    // Buscar usuário por id
    @GetMapping("/buscar/{id}")
    public ResponseEntity<Usuario> buscarUsuario(@PathVariable Long id) {
        Usuario usuario = usuarioService.buscarPorId(id);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(200).body(usuario); // 200 Ok
    }


    // Cadastrar novo usuário
    @PostMapping("/cadastrar")
    public ResponseEntity<Usuario> cadastrarUsuario(@RequestBody Usuario usuario) {
        usuarioService.salvarUsuario(usuario);
        return ResponseEntity.status(201).body(usuario);  // 201 Created
    }


    // Atualizar usuário exsitente por id
    @PutMapping("/atualizar/{id}")
    public ResponseEntity<Usuario> atualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
        
        // usuário com o id informado existe?
        Usuario usuarioExistente = usuarioService.buscarPorId(id);
        
        if (usuarioExistente == null) {
            return ResponseEntity.notFound().build();  // 404 Not found
        }
        
        usuarioService.atualizarUsuario(usuario);
        
        return ResponseEntity.status(200).body(usuario);  // 200 Ok
    }
        

    // Deletar usuário
    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id, @RequestParam boolean confirmacao) {

        // confirmação existe?
        if (!confirmacao) {
            return ResponseEntity.status(400).build();  // 400 Bad Request
        }
    
        // usuário existe?
        Usuario usuarioExistente = usuarioService.buscarPorId(id);

        if (usuarioExistente == null || confirmacao) {
            return ResponseEntity.notFound().build();  // 404 Not found
        }
    
        usuarioService.deletarUsuario(id, confirmacao);
    
        return ResponseEntity.status(204).build(); // 204 No content
    }
    
}
