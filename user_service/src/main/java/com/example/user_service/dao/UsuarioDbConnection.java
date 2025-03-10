package com.example.user_service.dao;

import com.example.user_service.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioDbConnection extends JpaRepository<Usuario, Long> {
    Usuario buscarPorUsuario(String usuario);    
}
