package com.example.pet_service.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pets")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 50)
    private String especie;

    @Column(length = 100)
    private String raca;

    @Column
    private Integer idade;

    @Column(length = 10)
    private String genero;

    @Column(length = 20)
    private String porte;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(length = 100)
    private String cidade;

    @Column(length = 2) // Sigla
    private String estado;

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @Column(nullable = false)
    private boolean adotado = false; // Inicia como não adotado

    // Identificador da foto principal no media-service
    @Column(name = "media_identifier", length = 100)
    private String mediaIdentifier;


    public Pet() {}


    @PrePersist // Define a data de cadastro automaticamente antes de salvar
    protected void onCreate() {
        dataCadastro = LocalDateTime.now();
    }

    
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEspecie() { return especie; }
    public String getRaca() { return raca; }
    public Integer getIdade() { return idade; }
    public String getGenero() { return genero; }
    public String getPorte() { return porte; }
    public String getDescricao() { return descricao; }
    public String getCidade() { return cidade; }
    public String getEstado() { return estado; }
    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public boolean isAdotado() { return adotado; }
    public String getMediaIdentifier() { return mediaIdentifier; }
    
    public void setId(Long id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setEspecie(String especie) { this.especie = especie; }
    public void setRaca(String raca) { this.raca = raca; }
    public void setIdade(Integer idade) { this.idade = idade; }
    public void setGenero(String genero) { this.genero = genero; }
    public void setPorte(String porte) { this.porte = porte; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }
    public void setAdotado(boolean adotado) { this.adotado = adotado; }
    public void setMediaIdentifier(String mediaIdentifier) { this.mediaIdentifier = mediaIdentifier; }

}
