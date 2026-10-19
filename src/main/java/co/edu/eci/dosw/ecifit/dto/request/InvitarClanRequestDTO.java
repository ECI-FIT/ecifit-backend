package co.edu.eci.dosw.ecifit.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record InvitarClanRequestDTO(
        @NotBlank(message = "El ID del clan es obligatorio")
        @JsonAlias({"clanId", "idClan", "clan_id"})
        @Schema(description = "Clan al que se invita", example = "CLAN-1")
        String clanId,

        @NotBlank(message = "El ID del estudiante invitado es obligatorio")
        @JsonAlias({"estudianteId", "idEstudiante", "estudiante_id"})
        @Schema(description = "Estudiante que recibe la invitación", example = "EST-2")
        String estudianteId,

        @NotBlank(message = "El ID de quien invita es obligatorio")
        @JsonAlias({"invitadoPorId", "invitadorId", "invitado_por_id"})
        @Schema(description = "Miembro del clan que envía la invitación", example = "EST-1")
        String invitadoPorId
) {}
