package co.edu.eci.dosw.ecifit.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegistrarActividadRequestDTO(
        @NotBlank(message = "El ID de estudiante es obligatorio")
        String estudianteId,

        @NotBlank(message = "El tipo de actividad es obligatorio")
        String tipo,

        @NotNull(message = "La duración es obligatoria")
        @Min(value = 10, message = "La duración mínima es de 10 minutos")
        Integer duracionMinutos,

        @NotNull(message = "La intensidad es obligatoria")
        @Min(value = 1, message = "Intensidad mínima 1")
        @Max(value = 10, message = "Intensidad máxima 10")
        Integer intensidad
) {}
