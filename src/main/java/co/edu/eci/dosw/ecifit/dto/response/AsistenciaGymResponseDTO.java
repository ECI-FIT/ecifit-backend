package co.edu.eci.dosw.ecifit.dto.response;

import java.time.LocalDateTime;

public record AsistenciaGymResponseDTO(
        String id,
        String estudianteId,
        String entrenadorId,
        LocalDateTime fechaHora,
        Integer puntosOtorgados
) {
}
