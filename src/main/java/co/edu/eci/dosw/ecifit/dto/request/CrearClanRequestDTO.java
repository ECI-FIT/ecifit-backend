package co.edu.eci.dosw.ecifit.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CrearClanRequestDTO(
        @NotBlank(message = "El nombre del clan es obligatorio")
        @JsonAlias({"nombre", "nombreClan"})
        @Schema(description = "Nombre único del clan", example = "Espartanos ECI")
        String nombre,

        @NotBlank(message = "El ID del líder es obligatorio")
        @JsonAlias({"liderEstudianteId", "liderId", "estudianteId", "idLider"})
        @Schema(description = "Identificador del estudiante líder que funda el clan", example = "EST-1")
        String liderEstudianteId
) {}