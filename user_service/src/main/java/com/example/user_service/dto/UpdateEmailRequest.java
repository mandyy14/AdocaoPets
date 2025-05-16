package com.example.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateEmailRequest {

    @NotBlank(message = "O novo email não pode estar vazio")
    @Email(message = "Insira um e-mail válido")
    @Size(max = 100, message = "Email pode ter no máximo {max} caracteres")
    private String newEmail;

    @NotBlank(message = "A senha atual é obrigatória para alterar o email")
    private String currentPassword;


    public String getNewEmail() { return newEmail; }    
    public String getCurrentPassword() { return currentPassword; }

    public void setNewEmail(String newEmail) { this.newEmail = newEmail; }
    public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
    
}
