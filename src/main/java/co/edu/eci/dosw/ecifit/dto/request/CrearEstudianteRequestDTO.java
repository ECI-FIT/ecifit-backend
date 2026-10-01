package co.edu.eci.dosw.ecifit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CrearEstudianteRequestDTO(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "Formato de correo inválido")
        String correoInstitucional,

        @NotBlank(message = "El rol es obligatorio")
        String rol
) {}
