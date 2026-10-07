package co.edu.eci.dosw.ecifit.security.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank(message = "El correo electrónico es obligatorio")
        @Email(message = "El formato del correo electrónico es inválido")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        String password
) {}
