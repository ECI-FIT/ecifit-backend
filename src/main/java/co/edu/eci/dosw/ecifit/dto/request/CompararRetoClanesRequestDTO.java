package co.edu.eci.dosw.ecifit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CompararRetoClanesRequestDTO(
        @NotBlank(message = "El ID del primer clan es obligatorio")
        @Schema(description = "Identificador del primer clan retador", example = "CLAN-01")
        String clanId1,

        @NotBlank(message = "El ID del segundo clan es obligatorio")
        @Schema(description = "Identificador del segundo clan rival", example = "CLAN-02")
        String clanId2,

        @NotNull(message = "Los puntos de bonificación por victoria son obligatorios")
        @Min(value = 1, message = "La bonificación por victoria debe ser mayor a 0")
        @Schema(description = "Puntos de bonificación para el clan ganador del duelo", example = "200")
        Integer bonificacionVictoria
) {}
