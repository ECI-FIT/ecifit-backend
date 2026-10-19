package co.edu.eci.dosw.ecifit.dto.response;

public record EstudianteResponseDTO(
        String id,
        String nombre,
        String correoInstitucional,
        Integer puntosAcumulados,
        String rolActivo,
        String clanId
) {}
