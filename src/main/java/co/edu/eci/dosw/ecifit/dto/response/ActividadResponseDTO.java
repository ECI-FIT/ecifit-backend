package co.edu.eci.dosw.ecifit.dto.response;

import java.time.LocalDateTime;

public record ActividadResponseDTO(
        String id,
        String tipo,
        Integer duracionMinutos,
        Integer intensidad,
        LocalDateTime fecha,
        String estudianteId,
        Integer puntosOtorgados
) {}
