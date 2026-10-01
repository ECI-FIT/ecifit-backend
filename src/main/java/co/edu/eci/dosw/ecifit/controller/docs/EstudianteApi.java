package co.edu.eci.dosw.ecifit.controller.docs;

import co.edu.eci.dosw.ecifit.dto.request.CrearEstudianteRequestDTO;
import co.edu.eci.dosw.ecifit.dto.response.EstudianteResponseDTO;
import co.edu.eci.dosw.ecifit.dto.response.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Estudiantes", description = "Gestión de estudiantes de la plataforma ECI FIT")
public interface EstudianteApi {

    @Operation(summary = "Crear un nuevo estudiante", description = "Registra un estudiante en el sistema validando correo institucional y asignando rol inicial")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Estudiante creado exitosamente",
                    content = @Content(schema = @Schema(implementation = EstudianteResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "El correo institucional ya se encuentra registrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "422", description = "Regla de negocio no satisfecha (ej. dominio de correo o rol no válido)",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<EstudianteResponseDTO> crearEstudiante(@Valid @RequestBody CrearEstudianteRequestDTO dto);

    @Operation(summary = "Obtener un estudiante por ID", description = "Consulta los detalles y puntaje acumulado de un estudiante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estudiante encontrado",
                    content = @Content(schema = @Schema(implementation = EstudianteResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Estudiante no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<EstudianteResponseDTO> obtenerPorId(@PathVariable String id);
}
