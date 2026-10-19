package co.edu.eci.dosw.ecifit.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CrearPersonajeRequestDTO(
        @NotBlank(message = "El nombre del personaje o estudiante es obligatorio")
        @Schema(description = "Nombre visible del personaje del estudiante", example = "Carlos Ramirez")
        String nombre,

        @NotBlank(message = "El rol de temporada es obligatorio")
        @JsonAlias({"rol", "rolTemporada", "tipoRol"})
        @Schema(description = "Arquetipo o rol deportivo del personaje (TANQUE, CORREDOR, ESTRATEGA)", example = "TANQUE")
        String rol
) {}
