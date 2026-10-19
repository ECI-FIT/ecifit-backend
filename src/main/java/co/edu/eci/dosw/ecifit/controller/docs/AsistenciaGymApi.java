package co.edu.eci.dosw.ecifit.controller.docs;

import co.edu.eci.dosw.ecifit.dto.request.AsistenciaGymRequestDTO;
import co.edu.eci.dosw.ecifit.dto.response.AsistenciaGymResponseDTO;
import co.edu.eci.dosw.ecifit.dto.response.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Gimnasio", description = "Gestión de asistencias y ocupación del gimnasio")
public interface AsistenciaGymApi {

    @Operation(
            summary = "Confirmar asistencia al gimnasio",
            description = "Registra la asistencia presencial de un estudiante, validada por un entrenador, y otorga 20 puntos."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Asistencia confirmada y puntos otorgados",
                    content = @Content(schema = @Schema(implementation = AsistenciaGymResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Estudiante no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "El estudiante ya tiene asistencia registrada hoy",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "422", description = "La solicitud no cumple una regla de negocio",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<AsistenciaGymResponseDTO> registrarAsistencia(
            @Valid @RequestBody AsistenciaGymRequestDTO dto);
}
