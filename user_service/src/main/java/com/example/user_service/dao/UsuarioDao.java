package com.example.user_service.dao;

import com.example.user_service.model.Usuario;
import org.springframework.jdbc.core.JdbcTemplate;
// Importa a classe JdbcTemplate do Spring, ela facilita ler e colocar coisas no banco
import org.springframework.jdbc.core.RowMapper;
// Interface do jdbc pra mapear pra model de Usuario uma linha do objeto retornado como resultado da consulta sql
import org.springframework.stereotype.Repository;
// Importa a anotação @Repository
import org.springframework.lang.NonNull;
// Importa a anotaçao @NonNull, que indica que um campo não pode ser nulo

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository // marca a classe como um compenente de repositório (camada de persistência) gerenciado pelo Spring
// Repository Pattern: não precisa instanciar a classe manualmente, o Spring injeta a dependência automaticamente
public class UsuarioDao {

    private final JdbcTemplate bancoMock; 


    //Construtores

    public UsuarioDao(JdbcTemplate bancoMock) {
        this.bancoMock = bancoMock;
    }
    // construtor que vai receber o objeto java JdbcTemplate como parametro (o mapping do RowMapper) e salvar na variável declarada acima
    // sempre que a UsuarioDao for instanciada o Spring vai fornecer uma instância do JdbcTemplate
    // nesse caso não é necessário @Autowired pq a injeção de dependência é feita automaticamente pelo Spring no construtor (jdbcTemplate)


    // Métodos

    public void salvarLogin(Usuario usuario) {
        // salva um novo usuário no banco
        String sql = "INSERT INTO usuario (login, senha, email, cargo) VALUES (?, ?, ?, ?)";
        bancoMock.update(sql, usuario.getLogin(), usuario.getSenha(), usuario.getEmail(), usuario.getCargo());
        // update preenche as "?" da query com parametros (usuario.getLogin(), getSenha(), etc.) e retorna o numero de linhas alteradas
    }

    public Usuario buscarPorId(Long id) {
        // busca um usuario no banco pelo id
        String sql = "SELECT * FROM usuario WHERE id = ?";
        return bancoMock.queryForObject(sql, usuarioMapper, id);
        // usa o método de queryForObject que faz a consulta no banco por id e devolve o objeto mapeado (ResultSet)
        // ai usa o método usuarioMapper pra iterar e converter o ResultSet em um objeto do tipo "Usuario"
    }

    public Usuario buscarPorLogin(String login) {
        // busca um usuario no banco pelo login
        String sql = "SELECT * FROM usuario WHERE usuario = ?";
        return bancoMock.queryForObject(sql, usuarioMapper, login);
    }

    public List<Usuario> listarTodos() {
        // busca todos os usuarios do banco
        String sql = "SELECT * FROM usuario";
        return bancoMock.query(sql, usuarioMapper);
        // método query retorna um array de objetos, aqui ele usa o usuarioMapper pra mapear cada linha do ResultSet pra um objeto
    }

    public void atualizarLogin(Usuario usuario) {
        String sql = "UPDATE usuario SET usuario = ?, senha = ?, email = ?, cargo = ? WHERE id = ?";
        bancoMock.update(sql, usuario.getLogin(), usuario.getSenha(), usuario.getEmail(), usuario.getCargo(), usuario.getId());
    }

    public void deletarUsuario(Long id) {
        String sql = "DELETE FROM usuario WHERE id = ?";
        bancoMock.update(sql, id);
    }


    // Interface
    // implementação abstrata (incompleta nesse caso)
    private final RowMapper<Usuario> usuarioMapper = new RowMapper<Usuario>() {
        @Override        
        public Usuario mapRow(@NonNull ResultSet rs, int numLinha) throws SQLException { // mapRow transforma o ResultSet em um Usuario
            return new Usuario(
                rs.getString("login"),
                rs.getString("senha"),
                rs.getString("email"),
                rs.getString("cargo")
            );
        }
    };

}
