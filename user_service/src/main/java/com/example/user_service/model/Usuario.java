package com.example.user_service.model;

import jakarta.persistence.*;

@Entity
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(unique = true, nullable = false)
    private String cpf;

    @Column(unique = true, nullable = false)
    private String login;

    @Column(nullable = false)
    private String senha; // TODO: Armazenará o HASH no futuro

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String cargo;

    private String celular;
    private String endereco;

    public Usuario() {}

    public Usuario(String nome, String cpf, String celular, String endereco, String login, String senha, String email, String cargo) {
        this.nome = nome;
        this.cpf = cpf;
        this.celular = celular;
        this.endereco = endereco;
        this.login = login;
        this.senha = senha;
        this.email = email;
        this.cargo = cargo;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public String getCelular() { return celular; }
    public String getEndereco() { return endereco; }
    public String getLogin() { return login; }
    public String getSenha() { return senha; }
    public String getEmail() { return email; }
    public String getCargo() { return cargo; }

    public void setId(Long id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public void setCelular(String celular) { this.celular = celular; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public void setLogin(String login) { this.login = login; }
    public void setSenha(String senha) { this.senha = senha; }
    public void setEmail(String email) { this.email = email; }
    public void setCargo(String cargo) {
        if (cargo == null || (!"admin".equalsIgnoreCase(cargo) && !"user".equalsIgnoreCase(cargo))) {
            throw new IllegalArgumentException("Cargo inválido. Use 'admin' ou 'user'.");
        }
        this.cargo = cargo.toLowerCase();
    }
}
