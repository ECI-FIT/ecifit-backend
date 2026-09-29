package co.edu.escuelaing.ecifit.model.dto.response;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
        int status,
        String mensaje,
        String ruta,
        LocalDateTime timestamp
) {
    public ErrorResponseDTO(int status, String mensaje, String ruta) {
        this(status, mensaje, ruta, LocalDateTime.now());
    }
}