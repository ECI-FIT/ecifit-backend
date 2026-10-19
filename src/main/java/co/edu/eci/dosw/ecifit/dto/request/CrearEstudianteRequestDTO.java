package co.edu.eci.dosw.ecifit.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CrearEstudianteRequestDTO(
        @NotBlank(message = "El nombre es obligatorio")
        @Schema(description = "Nombre completo del estudiante", example = "Carlos Ramirez")
        String nombre,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "Formato de correo inválido")
        @JsonAlias({"email", "correo", "correoInstitucional"})
        @Schema(description = "Correo institucional del estudiante", example = "carlos.ramirez@mail.escuelaing.edu.co")
        String correoInstitucional,

        @NotBlank(message = "El rol es obligatorio")
        @JsonAlias({"rol", "rolTemporada", "tipoRol"})
        @Schema(description = "Rol del atleta o usuario (TANQUE, CORREDOR, ESTRATEGA)", example = "TANQUE")
        String rol
) {}
