package com.example.user_service.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.example.user_service.dto.CadastroRequest;
import com.example.user_service.dto.LoginRequest;
import com.example.user_service.dto.UpdateEmailRequest;
import com.example.user_service.dto.UpdatePasswordRequest;
import com.example.user_service.model.Usuario;
import com.example.user_service.service.IUsuarioService;
import com.example.user_service.service.JwtService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Usuários", description = "Endpoints para gerenciamento, cadastro e login de usuários")
public class UsuarioController {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    private final IUsuarioService usuarioService;
    private final JwtService jwtService;

    @Autowired
    public UsuarioController(IUsuarioService usuarioService, JwtService jwtService) {
        this.usuarioService = usuarioService;
        this.jwtService = jwtService;
    }

    @PostMapping("/cadastrar")
    @Operation(summary = "Cadastrar Novo Usuário",
               description = "Cria um novo usuário no sistema. O login será o mesmo que o email e o cargo é 'user' por padrão.")
    public ResponseEntity<Usuario> cadastrarUsuario(@Valid @RequestBody CadastroRequest dto) {
        Usuario usuarioSalvo = usuarioService.salvarUsuario(dto);
        usuarioSalvo.setSenha(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioSalvo);
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticar Usuário",
               description = "Autentica um usuário com email e senha. Em caso de sucesso, retorna os dados do usuário e JWT.")
    public ResponseEntity<?> loginUsuario(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("Tentativa de login para email: {}", loginRequest.getEmail());

        Usuario usuarioAutenticado = usuarioService.autenticarUsuario(loginRequest.getEmail(), loginRequest.getSenha());

        String token = jwtService.generateToken(usuarioAutenticado);
        logger.info("Token JWT gerado para usuário ID: {}", usuarioAutenticado.getId());

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("token", token);

       Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", usuarioAutenticado.getId());
        userMap.put("nome", usuarioAutenticado.getNome());
        userMap.put("email", usuarioAutenticado.getEmail());
        userMap.put("cargo", usuarioAutenticado.getCargo());
        userMap.put("cpf", usuarioAutenticado.getCpf());
        userMap.put("celular", usuarioAutenticado.getCelular());
        userMap.put("endereco", usuarioAutenticado.getEndereco());
        userMap.put("login", usuarioAutenticado.getLogin());

        responseBody.put("user", userMap);

        return ResponseEntity.ok(responseBody);
    }

    @GetMapping("/listar")
    @Operation(summary = "Listar Todos os Usuários",
               description = "Retorna uma lista de todos os usuários cadastrados. (Endpoint a ser protegido futuramente).")
    public List<Usuario> listarUsuarios() {
        List<Usuario> usuarios = usuarioService.listarTodos();
        usuarios.forEach(u -> u.setSenha(null));
        return usuarios;
    }

    @GetMapping("/{id}/buscar")
    @Operation(summary = "Buscar Usuário por ID",
               description = "Retorna os detalhes de um usuário específico baseado no seu ID.")
    public ResponseEntity<Usuario> buscarUsuario(@PathVariable Long id) {
        Usuario usuario = usuarioService.buscarPorId(id);
        usuario.setSenha(null);
        return ResponseEntity.ok(usuario);
    }

    @DeleteMapping("/{id}/deletar")
    @Operation(summary = "Deletar Usuário por ID",
               description = "Remove um usuário do sistema baseado no seu ID. Requer confirmação.")
     public ResponseEntity<Void> deletarUsuario(@PathVariable Long id, @RequestParam(defaultValue = "false") boolean confirmacao) {
        usuarioService.deletarUsuario(id, confirmacao);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/email")
    @Operation(summary = "Atualizar Email do Usuário",
               description = "Permite que um usuário (autenticado) atualize seu endereço de email. Requer a senha atual para confirmação.")
    public ResponseEntity<Usuario> updateEmail(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEmailRequest dto) {

        Usuario usuarioAtualizado = usuarioService.updateEmail(id, dto);
        usuarioAtualizado.setSenha(null);
        return ResponseEntity.ok(usuarioAtualizado);
    }

    @PatchMapping("/{id}/password")
    @Operation(summary = "Atualizar Senha do Usuário",
               description = "Permite que um usuário (autenticado) atualize sua senha. Requer a senha atual e a nova senha.")
    public ResponseEntity<Void> updatePassword(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePasswordRequest dto) {

        usuarioService.updatePassword(id, dto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/profile-picture-url")
    @Operation(summary = "Buscar URL da Foto de Perfil do Usuário",
               description = "Endpoint proxy que busca a URL da foto de perfil do usuário no media_service.")
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
