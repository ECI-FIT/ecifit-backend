package co.edu.eci.dosw.ecifit.controller.docs;

import co.edu.eci.dosw.ecifit.dto.request.CrearClanRequestDTO;
import co.edu.eci.dosw.ecifit.dto.request.UnirseClanRequestDTO;
import co.edu.eci.dosw.ecifit.dto.response.ClanResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Clanes", description = "Creación y gestion de clanes")
public interface ClanApi {

    @Operation(summary = "Crear un clan")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Clan creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "409", description = "Nombre de clan duplicado")
    })
    ResponseEntity<ClanResponseDTO> crear(@Valid CrearClanRequestDTO dto);

    @Operation(summary = "Unirse a un clan existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estudiante unido al clan"),
            @ApiResponse(responseCode = "404", description = "Clan no encontrado"),
            @ApiResponse(responseCode = "422", description = "El clan ya alcanzo el limite de miembros")
    })
    ResponseEntity<ClanResponseDTO> unirse(@Valid UnirseClanRequestDTO dto);

    @Operation(summary = "Obtener un clan por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Clan encontrado"),
            @ApiResponse(responseCode = "404", description = "Clan no encontrado")
    })
    ResponseEntity<ClanResponseDTO> obtenerPorId(@Parameter(description = "Id del clan") String id);

    @Operation(summary = "Listar todos los clanes")
    @ApiResponse(responseCode = "200", description = "Listado de clanes")
    ResponseEntity<List<ClanResponseDTO>> obtenerTodos();
}