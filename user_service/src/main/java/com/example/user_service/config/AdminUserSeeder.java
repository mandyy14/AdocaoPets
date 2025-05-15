package com.example.user_service.config;

import com.example.user_service.model.Usuario;
import com.example.user_service.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AdminUserSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserSeeder.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String ADMIN_EMAIL = "admin@pet.com";
    private static final String ADMIN_SENHA_PLANA = "AdminSenhaForte!123";
    private static final String ADMIN_NOME = "Administrador do Sistema";
    private static final String ADMIN_CPF = "42154221832";
    private static final String ADMIN_CELULAR = "(11) 95846-5073";
    private static final String ADMIN_ENDERECO = "Sede da PetShop";


    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (usuarioRepository.findByEmail(ADMIN_EMAIL).isEmpty()) {
            logger.info("Criando usuário administrador padrão...");

            Usuario adminUser = new Usuario();
            adminUser.setNome(ADMIN_NOME);
            adminUser.setCpf(ADMIN_CPF);
            adminUser.setCelular(ADMIN_CELULAR);
            adminUser.setEndereco(ADMIN_ENDERECO);
            adminUser.setEmail(ADMIN_EMAIL);
            adminUser.setLogin(ADMIN_EMAIL);
            adminUser.setCargo("admin");

            adminUser.setSenha(passwordEncoder.encode(ADMIN_SENHA_PLANA));

            try {
                usuarioRepository.save(adminUser);
                logger.info("Usuário administrador padrão: {}", ADMIN_EMAIL);
                logger.warn("Senha padrão do admin: {}", ADMIN_SENHA_PLANA);
            } catch (Exception e) {
                logger.error("Erro ao criar usuário administrador padrão: {}", e.getMessage(), e);
            }
        } else {
            logger.info("Usuário administrador padrão já existe (Email: {}). Nenhum novo usuário admin foi criado.", ADMIN_EMAIL);
            logger.warn("Lembrete da senha padrão do admin (para desenvolvimento): {}", ADMIN_SENHA_PLANA);
        }
    }
}
