package co.edu.eci.dosw.ecifit.security.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank(message = "El correo electrónico es obligatorio")
        @Email(message = "El formato del correo electrónico es inválido")
        @JsonAlias({"email", "correo", "correoInstitucional", "username"})
        @Schema(description = "Correo electrónico institucional", example = "admin@mail.escuelaing.edu.co")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @JsonAlias({"password", "contrasena", "clave"})
        @Schema(description = "Contraseña de acceso", example = "Admin123*")
        String password
) {}
