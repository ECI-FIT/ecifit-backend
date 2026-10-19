package co.edu.eci.dosw.ecifit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PublicarRetoClanRequestDTO(
        @NotBlank(message = "El ID del clan es obligatorio")
        @Schema(description = "Identificador del clan al que se asigna el reto", example = "CLAN-01")
        String clanId,

        @NotBlank(message = "La descripción del reto es obligatoria")
        @Size(min = 5, max = 255, message = "La descripción debe tener entre 5 y 255 caracteres")
        @Schema(description = "Objetivo y reglas del reto colectivo", example = "Acumular 500 puntos colectivos en actividades de fuerza")
        String descripcion,

        @NotNull(message = "El objetivo de puntos es obligatorio")
        @Min(value = 1, message = "El objetivo de puntos debe ser mayor a 0")
        @Schema(description = "Puntaje meta colectivo que debe acumular el clan", example = "500")
        Integer objetivoPuntos,

        @NotNull(message = "La recompensa es obligatoria")
        @Min(value = 1, message = "La recompensa debe ser mayor a 0")
        @Schema(description = "Puntos de recompensa abonados al clan si cumple el reto", example = "150")
        Integer recompensa,

        @NotNull(message = "La fecha de inicio es obligatoria")
        @Schema(description = "Fecha de inicio de vigencia del reto", example = "2026-10-18")
        LocalDate fechaInicio,

        @NotNull(message = "La fecha de fin es obligatoria")
        @Schema(description = "Fecha de cierre del reto semanal", example = "2026-10-25")
        LocalDate fechaFin
) {}
