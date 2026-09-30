package co.edu.eci.dosw.ecifit.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CrearClanRequestDTO(
        @NotBlank String nombre,
        @NotBlank String liderEstudianteId
) {
}