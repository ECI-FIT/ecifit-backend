package co.edu.eci.dosw.ecifit.dto.request;

import co.edu.eci.dosw.ecifit.model.TipoComida;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record RegistrarComidaRequestDTO(
        @NotBlank(message = "El ID del estudiante es obligatorio")
        String estudianteId,

        @NotNull(message = "El tipo de comida es obligatorio")
        TipoComida tipo,

        @NotBlank(message = "La descripción de la comida es obligatoria")
        @Size(min = 3, max = 500, message = "La descripción debe tener entre 3 y 500 caracteres")
        String descripcion,

        LocalDateTime fechaHora
) {
}
