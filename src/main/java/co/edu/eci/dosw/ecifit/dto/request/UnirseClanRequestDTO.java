package co.edu.eci.dosw.ecifit.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record UnirseClanRequestDTO(
        @NotBlank(message = "El ID de estudiante es obligatorio")
        @JsonAlias({"estudianteId", "idEstudiante", "estudiante_id"})
        @Schema(description = "Identificador del estudiante que se une al clan", example = "EST-1")
        String estudianteId,

        @NotBlank(message = "El ID del clan es obligatorio")
        @JsonAlias({"clanId", "idClan", "clan_id"})
        @Schema(description = "Identificador del clan destino", example = "C1")
        String clanId
) {}