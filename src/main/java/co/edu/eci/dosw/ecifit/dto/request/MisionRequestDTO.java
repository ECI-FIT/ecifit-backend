package co.edu.eci.dosw.ecifit.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record MisionRequestDTO(
        @NotBlank(message = "El ID del estudiante es obligatorio")
        @NotNull(message = "El ID del estudiante es obligatorio")
        @JsonAlias({"estudianteId", "idEstudiante", "estudiante_id"})
        @Schema(description = "Identificador del estudiante para asignarle la misión", example = "EST-1")
        String estudianteId,

        @NotBlank(message = "La descripción no puede estar vacía")
        @Size(min = 5, max = 255, message = "La descripción debe tener entre 5 y 255 caracteres")
        @JsonAlias({"descripcion", "description", "detalle"})
        @Schema(description = "Descripción detallada del objetivo de la misión", example = "Activación ECI Diaria: Completa al menos 30 minutos de actividad física hoy")
        String descripcion,

        @NotNull(message = "La recompensa es obligatoria")
        @Positive(message = "La recompensa debe ser mayor a 0")
        @Min(value = 1, message = "La recompensa mínima es 1")
        @JsonAlias({"recompensa", "puntos", "points", "reward"})
        @Schema(description = "Puntos de recompensa por cumplir la misión", example = "50")
        Integer recompensa
) {
    public MisionRequestDTO(String estudianteId) {
        this(estudianteId, "Activación ECI Diaria: Completa al menos 30 minutos de actividad física hoy", 50);
    }

    public CrearMisionRequestDTO toCrearMisionRequestDTO() {
        return new CrearMisionRequestDTO(estudianteId, descripcion, recompensa);
    }
}
