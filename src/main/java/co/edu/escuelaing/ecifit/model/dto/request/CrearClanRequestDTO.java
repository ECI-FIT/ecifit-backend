package co.edu.escuelaing.ecifit.model.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CrearClanRequestDTO(
        @NotBlank String nombre,
        @NotBlank String liderEstudianteId
) {
}