package co.edu.eci.dosw.ecifit.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

public record RetoClanResponseDTO(
        @Schema(description = "Identificador único del reto", example = "RETO-01")
        String id,

        @Schema(description = "Identificador del clan asignado", example = "CLAN-01")
        String clanId,

        @Schema(description = "Descripción del reto semanal", example = "Acumular 500 puntos colectivos")
        String descripcion,

        @Schema(description = "Meta de puntos requerida", example = "500")
        Integer objetivoPuntos,

        @Schema(description = "Progreso acumulado de puntos al momento del cálculo", example = "520")
        Integer progresoActual,

        @Schema(description = "Puntos de recompensa por cumplir la meta", example = "150")
        Integer recompensa,

        @Schema(description = "Fecha de inicio del reto", example = "2026-10-18")
        LocalDate fechaInicio,

        @Schema(description = "Fecha de finalización del reto", example = "2026-10-25")
        LocalDate fechaFin,

        @Schema(description = "Estado de superación del reto", example = "true")
        Boolean completado,

        @Schema(description = "Descripción textual del estado del resultado", example = "SUPERADO: +150 pts asignados al clan")
        String estadoResultado
) {}
