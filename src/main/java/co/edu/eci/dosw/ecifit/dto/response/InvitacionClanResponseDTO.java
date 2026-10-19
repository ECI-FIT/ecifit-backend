package co.edu.eci.dosw.ecifit.dto.response;

import co.edu.eci.dosw.ecifit.model.EstadoInvitacion;

import java.time.LocalDateTime;

public record InvitacionClanResponseDTO(
        String id,
        String clanId,
        String estudianteId,
        String invitadoPorId,
        EstadoInvitacion estado,
        LocalDateTime fecha
) {
}
