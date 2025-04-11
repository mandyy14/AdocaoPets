package com.example.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CadastroRequest {

    @NotBlank(message = "Nome não pode estar vazio")
    @Size(min = 2, max = 100, message = "Nome deve conter entre {min} e {max} caracteres")
    private String nome;

    @NotBlank(message = "CPF não pode estar vazio")
    @Pattern(regexp = "\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}", message = "CPF deve conter 11 dígitos ou estar no formato 000.000.000-00")
    // Nota: Esta regex é básica, validação de CPF real envolve cálculo de dígitos verificadores (geralmente feito no Service ou com lib específica)
    private String cpf;

    // Celular pode ser opcional ou ter validação de formato mais complexa
    @Pattern(regexp = "^$|\\(?[1-9]{2}\\)? ?9?\\d{4}-?\\d{4}", message = "Formato de celular inválido") // Permite vazio ou formato BR
    private String celular;

    @NotBlank(message = "Endereço não pode estar vazio")
    @Size(min = 5, max = 200, message = "Endereço deve conter entre {min} e {max} caracteres")
    private String endereco;

    @NotBlank(message = "Email não pode estar vazio")
    @Email(message = "Insira um e-mail válido")
    @Size(max = 100, message = "Email pode ter no máximo {max} caracteres")
    private String email;

    @NotBlank(message = "Senha não pode estar vazio")
    @Size(min = 8, max = 50, message = "Senha deve conter entre {min} e {max} caracteres") // Ajustar max se usar hash
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,50}$",
             message = "Senha deve conter pelo menos uma letra maiúscula, uma minúscula, um número e um caractere especial (@$!%*?&)")
    private String senha;  

    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public String getCelular() { return celular; }
    public String getEndereco() { return endereco; }
    public String getEmail() { return email; }
    public String getSenha() { return senha; }


    public void setNome(String nome) { this.nome = nome; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public void setCelular(String celular) { this.celular = celular; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public void setEmail(String email) { this.email = email; }
    public void setSenha(String senha) { this.senha = senha; }

}