package co.edu.eci.dosw.ecifit.dto.response;

public record ClanResponseDTO(
        String id,
        String nombre,
        Integer saludTorre,
        Integer puntosTotales,
        int cantidadMiembros
) {
}