package com.example.pet_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class PetRequestDTO {

    @NotBlank(message = "Nome do pet não pode estar vazio")
    @Size(min = 2, max = 100, message = "Nome do pet deve ter entre {min} e {max} caracteres")
    private String nome;

    @NotBlank(message = "Espécie não pode estar vazia")
    @Size(max = 50, message = "Espécie pode ter no máximo {max} caracteres")
    private String especie;

    @Size(max = 100, message = "Raça pode ter no máximo {max} caracteres")
    private String raca;

    @NotNull(message = "Idade não pode ser nula")
    @PositiveOrZero(message = "Idade deve ser um número positivo ou zero")
    private Integer idade;

    @NotBlank(message = "Gênero não pode estar vazio")
    @Size(max = 10, message = "Gênero pode ter no máximo {max} caracteres")
    private String genero;

    @NotBlank(message = "Porte não pode estar vazio")
    @Size(max = 20, message = "Porte pode ter no máximo {max} caracteres")
    private String porte;

    @Size(max = 2000, message = "Descrição pode ter no máximo {max} caracteres")
    private String descricao;

    @NotBlank(message = "Cidade não pode estar vazia")
    @Size(max = 100, message = "Cidade pode ter no máximo {max} caracteres")
    private String cidade;

    @NotBlank(message = "Estado (UF) não pode estar vazio")
    @Size(min = 2, max = 2, message = "Estado (UF) deve ter {max} caracteres")
    private String estado;

    @NotBlank(message = "Identificador da mídia não pode estar vazio")
    @Size(max = 100, message = "Identificador da mídia pode ter no máximo {max} caracteres")
    private String mediaIdentifier;
    

    // Getters e Setters
    public String getNome() { return nome; }
    public String getEspecie() { return especie; }
    public String getRaca() { return raca; }
    public Integer getIdade() { return idade; }
    public String getGenero() { return genero; }
    public String getPorte() { return porte; }
    public String getDescricao() { return descricao; }
    public String getCidade() { return cidade; }
    public String getEstado() { return estado; }
    public String getMediaIdentifier() { return mediaIdentifier; }

    public void setNome(String nome) { this.nome = nome; }
    public void setEspecie(String especie) { this.especie = especie; }
    public void setRaca(String raca) { this.raca = raca; }
    public void setIdade(Integer idade) { this.idade = idade; }
    public void setGenero(String genero) { this.genero = genero; }
    public void setPorte(String porte) { this.porte = porte; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setMediaIdentifier(String mediaIdentifier) { this.mediaIdentifier = mediaIdentifier; }
}
