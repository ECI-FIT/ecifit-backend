package co.edu.eci.dosw.ecifit.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record ResultadoRetoClanesResponseDTO(
        @Schema(description = "ID del primer clan participante", example = "CLAN-01")
        String clanId1,

        @Schema(description = "Nombre del primer clan", example = "Guerreros ECI")
        String nombreClan1,

        @Schema(description = "Puntaje total alcanzado por el primer clan", example = "850")
        Integer puntosClan1,

        @Schema(description = "ID del segundo clan participante", example = "CLAN-02")
        String clanId2,

        @Schema(description = "Nombre del segundo clan", example = "Titanes de Sistemas")
        String nombreClan2,

        @Schema(description = "Puntaje total alcanzado por el segundo clan", example = "620")
        Integer puntosClan2,

        @Schema(description = "ID del clan ganador del duelo (null en caso de empate)", example = "CLAN-01")
        String clanGanadorId,

        @Schema(description = "Puntos de bonificación acreditados al clan ganador", example = "200")
        Integer bonificacionOtorgada,

        @Schema(description = "Indica si el duelo terminó en empate", example = "false")
        Boolean esEmpate,

        @Schema(description = "Mensaje descriptivo del resultado del duelo", example = "¡Victoria para Guerreros ECI! +200 pts otorgados.")
        String mensajeResultado
) {}
