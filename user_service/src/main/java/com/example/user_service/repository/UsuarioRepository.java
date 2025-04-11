package com.example.user_service.repository;

import com.example.user_service.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca um usuário pelo login.
     * @param login O login a ser buscado.
     * @return Um Optional contendo o Usuario se encontrado, ou Optional.empty() caso contrário.
     */
    Optional<Usuario> findByLogin(String login);

    /**
     * Busca um usuário pelo email.
     * @param email O email a ser buscado.
     * @return Um Optional contendo o Usuario se encontrado, ou Optional.empty() caso contrário.
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Busca um usuário pelo CPF.
     * @param cpf O CPF a ser buscado.
     * @return Um Optional contendo o Usuario se encontrado, ou Optional.empty() caso contrário.
     */
    Optional<Usuario> findByCpf(String cpf);

}
