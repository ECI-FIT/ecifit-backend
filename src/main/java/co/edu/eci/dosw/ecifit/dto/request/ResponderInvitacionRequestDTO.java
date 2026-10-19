package co.edu.eci.dosw.ecifit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ResponderInvitacionRequestDTO(
        @NotNull(message = "Debe indicar si acepta o rechaza la invitación")
        @Schema(description = "true para aceptar, false para rechazar", example = "true")
        Boolean aceptar
) {}
