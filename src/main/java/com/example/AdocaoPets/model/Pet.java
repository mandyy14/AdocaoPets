package com.example.AdocaoPets.model;

public class Pet {

    private Long id;
    private String nome;
    private String tipo;
    private int idade;
    private String sexo;

   
    public Pet() {}
    
    public Pet(Long id, String nome, String tipo, int idade, String sexo) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.idade = idade;
        this.sexo = sexo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }
    
}
