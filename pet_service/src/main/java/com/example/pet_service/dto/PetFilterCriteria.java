package com.example.pet_service.dto;

public class PetFilterCriteria {

    private String nome;
    private String especie;
    private String raca;
    private Integer idadeMin;
    private Integer idadeMax;
    private String genero;
    private String porte;
    private String cidade;
    private String estado;

    // Getters e Setters
    public String getNome() { return nome; }
    public String getEspecie() { return especie; }
    public String getRaca() { return raca; }
    public Integer getIdadeMin() { return idadeMin; }
    public Integer getIdadeMax() { return idadeMax; }
    public String getGenero() { return genero; }
    public String getPorte() { return porte; }
    public String getCidade() { return cidade; }
    public String getEstado() { return estado; }

    public void setNome(String nome) { this.nome = nome; }
    public void setEspecie(String especie) { this.especie = especie; }
    public void setRaca(String raca) { this.raca = raca; }
    public void setIdadeMin(Integer idadeMin) { this.idadeMin = idadeMin; }
    public void setIdadeMax(Integer idadeMax) { this.idadeMax = idadeMax; }
    public void setGenero(String genero) { this.genero = genero; }
    public void setPorte(String porte) { this.porte = porte; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public void setEstado(String estado) { this.estado = estado; }

}
