package com.example.AdocaoPets.model;

public class Pet {

    private Long id;
    private String nome;
    private String tipo;
    private int idade;

   
    public Pet() {}
    
    public Pet(Long id, String nome, String tipo, int idade) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.idade = idade;
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
    
}
