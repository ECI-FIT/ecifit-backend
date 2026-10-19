package co.edu.eci.dosw.ecifit.controller.docs;

import co.edu.eci.dosw.ecifit.dto.request.RegistrarActividadRequestDTO;
import co.edu.eci.dosw.ecifit.dto.response.ActividadResponseDTO;
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

@Tag(name = "Actividades", description = "Registro y consulta de actividades físicas")
public interface ActividadApi {

    @Operation(summary = "Registrar una nueva actividad física", description = "Registra y valida una sesión deportiva para un estudiante, calculando y otorgando puntos según su rol de temporada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Actividad registrada y puntos otorgados exitosamente",
                    content = @Content(schema = @Schema(implementation = ActividadResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Estudiante no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "422", description = "Actividad no válida según reglas de salud/anti-cheat",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<ActividadResponseDTO> registrarActividad(@Valid @RequestBody RegistrarActividadRequestDTO dto);

    @Operation(summary = "Consultar historial cronológico de actividades de un estudiante",
               description = "Retorna el listado de actividades físicas completadas por el estudiante ordenadas de forma cronológica descendente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial retornado exitosamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ActividadResponseDTO.class)))),
            @ApiResponse(responseCode = "404", description = "Estudiante no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<List<ActividadResponseDTO>> obtenerHistorial(@PathVariable String estudianteId);
}
