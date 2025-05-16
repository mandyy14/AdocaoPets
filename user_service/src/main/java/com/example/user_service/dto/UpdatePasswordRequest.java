package com.example.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UpdatePasswordRequest {

    @NotBlank(message = "A senha atual é obrigatória")
    private String currentPassword;

    @NotBlank(message = "A nova senha não pode estar vazia")
    @Size(min = 8, max = 50, message = "Nova senha deve conter entre {min} e {max} caracteres")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,50}$",
             message = "Nova senha deve conter pelo menos uma letra maiúscula, uma minúscula, um número e um caractere especial (@$!%*?&)")
    private String newPassword;


    public String getCurrentPassword() { return currentPassword; }
    public String getNewPassword() { return newPassword; }

    public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }

}
