package co.edu.eci.dosw.ecifit.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AsistenciaGymRequestDTO(
        @NotBlank(message = "El ID del estudiante es obligatorio")
        String estudianteId,

        @NotBlank(message = "El ID del entrenador es obligatorio")
        String entrenadorId
) {
}
