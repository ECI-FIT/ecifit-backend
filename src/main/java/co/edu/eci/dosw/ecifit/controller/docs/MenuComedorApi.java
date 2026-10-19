package co.edu.eci.dosw.ecifit.controller.docs;

import co.edu.eci.dosw.ecifit.dto.request.CrearMenuComedorRequestDTO;
import co.edu.eci.dosw.ecifit.dto.response.ErrorResponseDTO;
import co.edu.eci.dosw.ecifit.dto.response.MenuComedorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Comedor", description = "Menú del comedor de la Escuela")
public interface MenuComedorApi {

    @Operation(
            summary = "Publicar el menú del comedor",
            description = "Publica el menú de una fecha y un tipo de comida. Solo puede existir un menú por fecha y tipo."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Menú publicado",
                    content = @Content(schema = @Schema(implementation = MenuComedorResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Ya existe un menú para esa fecha y tipo",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "422", description = "La solicitud no cumple una regla de negocio",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<MenuComedorResponseDTO> publicar(@Valid @RequestBody CrearMenuComedorRequestDTO dto);

    @Operation(summary = "Consultar el menú del comedor de hoy")
    @ApiResponse(responseCode = "200", description = "Menús del día (lista vacía si no se ha publicado)")
    ResponseEntity<List<MenuComedorResponseDTO>> obtenerHoy();

    @Operation(summary = "Consultar el menú del comedor de una fecha")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Menús de la fecha (lista vacía si no hay)"),
            @ApiResponse(responseCode = "400", description = "Fecha con formato inválido",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "422", description = "No se envió la fecha",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<List<MenuComedorResponseDTO>> obtenerPorFecha(
            @Parameter(description = "Fecha en formato AAAA-MM-DD", example = "2026-10-20") LocalDate fecha);
}
