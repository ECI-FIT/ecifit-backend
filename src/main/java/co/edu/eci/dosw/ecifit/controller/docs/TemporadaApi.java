package co.edu.eci.dosw.ecifit.controller.docs;

import co.edu.eci.dosw.ecifit.dto.request.TemporadaRequestDTO;
import co.edu.eci.dosw.ecifit.dto.response.RankingTemporadaResponseDTO;
import co.edu.eci.dosw.ecifit.dto.response.TemporadaResponseDTO;
import co.edu.eci.dosw.ecifit.dto.response.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@Tag(name = "Temporadas", description = "Gestión de ligas y temporadas académicas")
public interface TemporadaApi {

    @Operation(summary = "Crear una temporada académica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Temporada creada",
                    content = @Content(schema = @Schema(implementation = TemporadaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Ya existe una temporada activa",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "422", description = "Fechas inválidas según las reglas de negocio",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<TemporadaResponseDTO> crear(@Valid @RequestBody TemporadaRequestDTO request);

    @Operation(summary = "Obtener una temporada por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Temporada encontrada",
                    content = @Content(schema = @Schema(implementation = TemporadaResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Temporada no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<TemporadaResponseDTO> obtenerPorId(@PathVariable UUID id);

    @Operation(summary = "Obtener la temporada activa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Temporada activa encontrada",
                    content = @Content(schema = @Schema(implementation = TemporadaResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "No existe una temporada activa",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<TemporadaResponseDTO> obtenerActiva();

    @Operation(summary = "Obtener el ranking general de una temporada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ranking obtenido",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(implementation = RankingTemporadaResponseDTO.class)))),
            @ApiResponse(responseCode = "404", description = "Temporada no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<List<RankingTemporadaResponseDTO>> obtenerRanking(@PathVariable UUID temporadaId);
}
