package co.edu.eci.dosw.ecifit.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegistrarActividadRequestDTO(
        @NotBlank(message = "El ID de estudiante es obligatorio")
        @JsonAlias({"estudianteId", "idEstudiante", "estudiante_id"})
        @Schema(description = "Identificador del estudiante", example = "EST-1")
        String estudianteId,

        @NotBlank(message = "El tipo de actividad es obligatorio")
        @JsonAlias({"tipo", "tipoActividad", "deporte"})
        @Schema(description = "Tipo de actividad física (CARDIO, FUERZA, FLEXIBILIDAD)", example = "CARDIO")
        String tipo,

        @NotNull(message = "La duración es obligatoria")
        @Min(value = 10, message = "La duración mínima es de 10 minutos")
        @JsonAlias({"duracionMinutos", "duracion", "minutos"})
        @Schema(description = "Duración de la actividad en minutos", example = "30")
        Integer duracionMinutos,

        @NotNull(message = "La intensidad es obligatoria")
        @Min(value = 1, message = "Intensidad mínima 1")
        @Max(value = 10, message = "Intensidad máxima 10")
        @JsonAlias({"intensidad", "nivelIntensidad"})
        @Schema(description = "Nivel de intensidad de 1 a 10", example = "8")
        Integer intensidad
) {}
