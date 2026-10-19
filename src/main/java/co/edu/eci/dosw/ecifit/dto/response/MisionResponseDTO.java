package co.edu.eci.dosw.ecifit.dto.response;

public record MisionResponseDTO(
        String id,
        String descripcion,
        Integer recompensa,
        boolean completada,
        String estudianteId
) {
}
