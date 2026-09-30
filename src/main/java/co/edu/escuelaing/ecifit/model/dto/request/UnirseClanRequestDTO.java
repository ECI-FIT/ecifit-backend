package co.edu.escuelaing.ecifit.model.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UnirseClanRequestDTO(
        @NotBlank String estudianteId,
        @NotBlank String clanId
) {
}