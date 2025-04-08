package com.example.user_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


@Entity // Anotação da JPA para marcar a classe como uma entidade JPA (será mapeada para uma tabela no banco)
public class Usuario {

    @Id // Marca o ID como chave primária da entidade
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(unique = true, nullable = false)
    private String cpf;

    @Column(unique = true, nullable = false)
    private String login;

    @Column(nullable = false)
    private String senha;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String cargo; // admin ou user

    private String celular;
    private String endereco;


    //Construtores

    public Usuario() {}

    public Usuario(String nome, String cpf, String celular, String endereco, String login, String senha, String email, String cargo) {
        setNome(nome);
        setCpf(cpf);
        setCelular(celular);
        setEndereco(endereco);
        setLogin(login);
        setSenha(senha);
        setEmail(email);
        setCargo(cargo);
    }


    // Getters e Setters

    public Long getId() { return id; }
    public final void setId(Long id) {
        this.id = id;
    }

    public String getNome() { return nome; }
    public final void setNome(String nome) {
        this.nome = validarString(nome, "Nome", 2, 100);
    }

    public String getCpf() { return cpf; }
    public final void setCpf(String cpf) {
        // TODO: Adicionar validação real de formato de CPF
        this.cpf = validarString(cpf, "CPF", 11, 14);
    }

    public String getCelular() { return celular; }
    public final void setCelular(String celular) {
        // TODO: Adicionar validação de formato de celular
        this.celular = celular;
    }

    public String getEndereco() { return endereco; }
    public final void setEndereco(String endereco) {
        this.endereco = validarString(endereco, "Endereço", 5, 200);
    }

    public String getLogin() { return login; }
    public final void setLogin(String login) {
        this.login = validarString(login, "Login", 3, 50);
    }

    public String getSenha() { return senha; }
    public final void setSenha(String senha) {
        this.senha = validarString(senha, "Senha", 8, 50);
    }

    public String getEmail() { return email; }
    public final void setEmail(String email) {
        if (email == null || !email.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"))
            throw new IllegalArgumentException("Insira um e-mail válido");
        this.email = email;
    }


    public String getCargo() { return cargo; }
    public final void setCargo(String cargo) {
        if (cargo == null || (!"admin".equalsIgnoreCase(cargo) && !"user".equalsIgnoreCase(cargo))) {
             throw new IllegalArgumentException("Cargo inválido. Use 'admin' ou 'user'.");
        }
        this.cargo = cargo.toLowerCase();
    }


    // Métodos

    private String validarString(String valor, String nomeCampo, int minCaracteres, int maxCaracteres) {
        if (valor == null || valor.trim().isEmpty()) throw new IllegalArgumentException(nomeCampo + " não pode estar vazio");
        if (valor.length() < minCaracteres || valor.length() > maxCaracteres) {
            throw new IllegalArgumentException(nomeCampo + " deve conter entre " + minCaracteres + " e " + maxCaracteres + " caracteres");
        }
        return valor.trim();
    }

}
