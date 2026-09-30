package co.edu.eci.dosw.ecifit.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CrearMisionRequestDTO(
        @NotBlank String estudianteId
) {
}
  