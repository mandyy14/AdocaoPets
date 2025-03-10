package com.example.user_service.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;


@Entity // Anotação da JPA para marcar a classe como uma entidade JPA (será mapeada para uma tabela no banco)
public class Usuario {
    @Id // Marca o ID como chave primária da entidade
    // TODO - trocar para implementação do banco real futuramente
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    // indica que o valor do ID será gerado automaticamente pelo banco

    private Long id;
    private String login;
    private String senha;
    private String email;
    private String cargo; // admin ou user


    //Construtores

    public Usuario(String login, String senha, String email, String cargo) {
        setLogin(login);
        setSenha(senha);
        setEmail(email);
        setCargo(cargo);
    }


    // Getters e Setters

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }
    public void setLogin(String usuario) {
        this.login = validarString(usuario, "Login", 3, 50);
    }

    public String getSenha() {
        return senha;
    }
    public void setSenha(String senha) {
        this.senha = validarString(senha, "Senha", 8, 50);
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        if (email == null || !email.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"))
            throw new IllegalArgumentException("Insira um e-mail válido");
        this.email = email;
    }

    public String getCargo() {
        return cargo;
    }
    public void setCargo(String cargo) {
        if (cargo == null) throw new IllegalArgumentException("Cargo do usuário não pode estar vazio");
        this.cargo = cargo;
    }


    // Métodos

    private String validarString(String valor, String nomeCampo, int minCaracteres, int maxCaracteres) {
        if (valor == null) throw new IllegalArgumentException(nomeCampo + " não pode estar vazio");
        if (valor.length() < minCaracteres || valor.length() > maxCaracteres) {
            throw new IllegalArgumentException(nomeCampo + " deve conter entre " + minCaracteres + " e " + maxCaracteres + " caracteres");
        }
        return valor;
    }
    
}
