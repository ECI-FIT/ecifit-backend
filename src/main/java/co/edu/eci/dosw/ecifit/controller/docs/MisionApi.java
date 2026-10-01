package co.edu.eci.dosw.ecifit.controller.docs;

import co.edu.eci.dosw.ecifit.dto.request.CrearMisionRequestDTO;
import co.edu.eci.dosw.ecifit.dto.response.MisionResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Misiones", description = "Generación y consulta de misiones diarias y semanales")
public interface MisionApi {

    @Operation(summary = "Generar una misión diaria para un estudiante")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Misión diaria generada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "422", description = "El estudiante ya tiene el máximo de misiones activas")
    })
    ResponseEntity<MisionResponseDTO> generarDiaria(@Valid CrearMisionRequestDTO dto);

    @Operation(summary = "Generar una misión semanal para un estudiante")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Misión semanal generada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "422", description = "El estudiante ya tiene el máximo de misiones activas")
    })
    ResponseEntity<MisionResponseDTO> generarSemanal(@Valid CrearMisionRequestDTO dto);

    @Operation(summary = "Listar las misiones de un estudiante")
    @ApiResponse(responseCode = "200", description = "Listado de misiones del estudiante")
    ResponseEntity<List<MisionResponseDTO>> obtenerPorEstudiante(
            @Parameter(description = "Id del estudiante") String estudianteId);
}