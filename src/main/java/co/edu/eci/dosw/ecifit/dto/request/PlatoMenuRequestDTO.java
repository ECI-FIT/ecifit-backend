package co.edu.eci.dosw.ecifit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record PlatoMenuRequestDTO(
        @NotBlank(message = "El nombre del plato es obligatorio")
        @Size(max = 120, message = "El nombre del plato no puede superar 120 caracteres")
        String nombre,

        @Size(max = 300, message = "La descripción del plato no puede superar 300 caracteres")
        String descripcion,

        @PositiveOrZero(message = "El precio no puede ser negativo")
        Integer precio
) {
}
