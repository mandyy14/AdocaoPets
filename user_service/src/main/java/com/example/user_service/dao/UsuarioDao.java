package com.example.user_service.dao;

import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example.user_service.model.Usuario;

@Repository
public class UsuarioDao {

    private final JdbcTemplate jdbcTemplate;

    public UsuarioDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Usuario> usuarioMapper = (rs, rowNum) -> {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getLong("id"));
        usuario.setNome(rs.getString("nome"));
        usuario.setCpf(rs.getString("cpf"));
        usuario.setCelular(rs.getString("celular"));
        usuario.setEndereco(rs.getString("endereco"));
        usuario.setLogin(rs.getString("login"));
        usuario.setSenha(rs.getString("senha"));
        usuario.setEmail(rs.getString("email"));
        usuario.setCargo(rs.getString("cargo"));
        return usuario;
    };

    /**
     * Salva um novo usuário no banco de dados.
     * @param usuario O objeto Usuario a ser salvo.
     */
    public void salvarUsuario(Usuario usuario) {
        String sql = "INSERT INTO usuario (nome, cpf, celular, endereco, login, senha, email, cargo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                usuario.getNome(),
                usuario.getCpf(),
                usuario.getCelular(),
                usuario.getEndereco(),
                usuario.getLogin(),
                usuario.getSenha(),
                usuario.getEmail(),
                usuario.getCargo());
    }

    /**
     * Busca um usuário pelo seu ID.
     * @param id O ID do usuário.
     * @return O objeto Usuario encontrado, ou null se não existir.
     */
    public Usuario buscarPorId(Long id) {
         try {
            String sql = "SELECT * FROM usuario WHERE id = ?";
            return jdbcTemplate.queryForObject(sql, usuarioMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * Busca um usuário pelo seu login.
     * @param login O login do usuário.
     * @return O objeto Usuario encontrado, ou null se não existir.
     */
    public Usuario buscarPorLogin(String login) {
        try {
            String sql = "SELECT * FROM usuario WHERE login = ?";
            return jdbcTemplate.queryForObject(sql, usuarioMapper, login);
         } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * Busca um usuário pelo seu email.
     * @param email O email do usuário.
     * @return O objeto Usuario encontrado, ou null se não existir.
     */
    public Usuario buscarPorEmail(String email) {
        try {
            String sql = "SELECT * FROM usuario WHERE email = ?";
            return jdbcTemplate.queryForObject(sql, usuarioMapper, email);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * Busca um usuário pelo seu CPF.
     * @param cpf O CPF do usuário.
     * @return O objeto Usuario encontrado, ou null se não existir.
     */
    public Usuario buscarPorCpf(String cpf) {
        try {
            String sql = "SELECT * FROM usuario WHERE cpf = ?";
            return jdbcTemplate.queryForObject(sql, usuarioMapper, cpf);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * Retorna uma lista com todos os usuários cadastrados.
     * @return Lista de objetos Usuario.
     */
    public List<Usuario> listarTodos() {
        String sql = "SELECT * FROM usuario";
        return jdbcTemplate.query(sql, usuarioMapper);
    }

    /**
     * Atualiza os dados de um usuário existente no banco.
     * Assume que o ID do usuário está correto. A verificação se o usuário
     * existe deve ser feita na camada de serviço antes de chamar este método.
     * @param usuario O objeto Usuario com os dados atualizados (incluindo o ID).
     */
    public void atualizarUsuario(Usuario usuario) {
        String sql = "UPDATE usuario SET nome = ?, cpf = ?, celular = ?, endereco = ?, login = ?, senha = ?, email = ?, cargo = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                usuario.getNome(),
                usuario.getCpf(),
                usuario.getCelular(),
                usuario.getEndereco(),
                usuario.getLogin(),
                usuario.getSenha(),
                usuario.getEmail(),
                usuario.getCargo(),
                usuario.getId());
    }

    /**
     * Deleta um usuário do banco de dados pelo ID.
     * A verificação se o usuário existe deve ser feita na camada de serviço.
     * @param id O ID do usuário a ser deletado.
     */
    public void deletarUsuario(Long id) {
        String sql = "DELETE FROM usuario WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}