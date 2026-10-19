package co.edu.eci.dosw.ecifit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record RegistrarCumplimientoMisionRequestDTO(
        @Schema(description = "Identificador opcional de la actividad física asociada al cumplimiento", example = "ACT-123")
        String actividadId,

        @Schema(description = "Descripción o evidencia textual del cumplimiento de la misión", example = "Completada sesión de cardio en la pista institucional")
        String evidencia
) {}
