package co.edu.eci.dosw.ecifit.controller.docs;

import co.edu.eci.dosw.ecifit.dto.request.RegistrarComidaRequestDTO;
import co.edu.eci.dosw.ecifit.dto.response.ComidaResponseDTO;
import co.edu.eci.dosw.ecifit.dto.response.ErrorResponseDTO;
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

@Tag(name = "Nutrición", description = "Registro y evaluación de comidas")
public interface ComidaApi {

    @Operation(
            summary = "Registrar una comida",
            description = "Registra una comida consumida por el estudiante (desayuno, almuerzo, etc.) y la evalúa "
                    + "con IA: si es balanceada, una calificación, un consejo y recomendaciones. "
                    + "Otorga 5 puntos, más 15 si es balanceada; solo la primera comida de cada tipo en el día "
                    + "da puntos. Si la IA no responde, la comida queda PENDIENTE y se reintenta automáticamente."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Comida registrada",
                    content = @Content(schema = @Schema(implementation = ComidaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Estudiante no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "422", description = "La comida no cumple una regla de negocio "
                    + "(fecha futura o de hace más de 24 horas)",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<ComidaResponseDTO> registrar(@Valid @RequestBody RegistrarComidaRequestDTO dto);

    @Operation(
            summary = "Reintentar la evaluación nutricional de una comida",
            description = "Vuelve a pedir la evaluación a la IA para una comida pendiente o con error. "
                    + "Si la IA sigue sin responder, la comida queda pendiente."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resultado del reintento",
                    content = @Content(schema = @Schema(implementation = ComidaResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Comida no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "422", description = "La comida ya fue evaluada",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<ComidaResponseDTO> reevaluar(@Parameter(description = "Id de la comida") String id);

    @Operation(summary = "Consultar una comida")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comida encontrada",
                    content = @Content(schema = @Schema(implementation = ComidaResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Comida no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<ComidaResponseDTO> obtenerPorId(@Parameter(description = "Id de la comida") String id);

    @Operation(summary = "Consultar las comidas de un estudiante",
            description = "Devuelve las comidas de la más reciente a la más antigua. Si se envía la fecha, solo las de ese día.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial de comidas"),
            @ApiResponse(responseCode = "404", description = "Estudiante no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<List<ComidaResponseDTO>> obtenerHistorial(
            @Parameter(description = "Id del estudiante") String estudianteId,
            @Parameter(description = "Fecha opcional en formato AAAA-MM-DD", example = "2026-10-20") LocalDate fecha);
}
