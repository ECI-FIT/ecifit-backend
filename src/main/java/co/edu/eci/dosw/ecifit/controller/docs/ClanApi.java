package co.edu.eci.dosw.ecifit.controller.docs;

import co.edu.eci.dosw.ecifit.dto.request.AbandonarClanRequestDTO;
import co.edu.eci.dosw.ecifit.dto.request.CompararRetoClanesRequestDTO;
import co.edu.eci.dosw.ecifit.dto.request.CrearClanRequestDTO;
import co.edu.eci.dosw.ecifit.dto.request.PublicarRetoClanRequestDTO;
import co.edu.eci.dosw.ecifit.dto.request.UnirseClanRequestDTO;
import co.edu.eci.dosw.ecifit.dto.response.ClanResponseDTO;
import co.edu.eci.dosw.ecifit.dto.response.ResultadoRetoClanesResponseDTO;
import co.edu.eci.dosw.ecifit.dto.response.RetoClanResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Clanes", description = "Creación y gestión de clanes, retos colectivos semanales y guerras de clanes")
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
            @ApiResponse(responseCode = "422", description = "El clan ya alcanzó el límite de miembros")
    })
    ResponseEntity<ClanResponseDTO> unirse(@Valid UnirseClanRequestDTO dto);

    @Operation(summary = "Abandonar un clan")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estudiante retirado del clan; si era el líder, el siguiente miembro toma el liderazgo"),
            @ApiResponse(responseCode = "404", description = "Clan no encontrado"),
            @ApiResponse(responseCode = "422", description = "El estudiante no pertenece al clan")
    })
    ResponseEntity<ClanResponseDTO> abandonar(@Valid AbandonarClanRequestDTO dto);

    @Operation(summary = "Obtener un clan por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Clan encontrado"),
            @ApiResponse(responseCode = "404", description = "Clan no encontrado")
    })
    ResponseEntity<ClanResponseDTO> obtenerPorId(@Parameter(description = "Id del clan") String id);

    @Operation(summary = "Listar todos los clanes")
    @ApiResponse(responseCode = "200", description = "Listado de clanes")
    ResponseEntity<List<ClanResponseDTO>> obtenerTodos();

    @Operation(summary = "Publicar un reto colectivo semanal para un clan (RF-34)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Reto colectivo publicado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Clan no encontrado"),
            @ApiResponse(responseCode = "422", description = "Infracción de reglas de negocio en fechas o metas")
    })
    ResponseEntity<RetoClanResponseDTO> publicarReto(@Valid PublicarRetoClanRequestDTO dto);

    @Operation(summary = "Calcular y evaluar el resultado de un reto colectivo para un clan (RF-36)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resultado del reto evaluado y recompensa asignada"),
            @ApiResponse(responseCode = "404", description = "Clan o reto no encontrado"),
            @ApiResponse(responseCode = "422", description = "El reto no pertenece al clan o ya fue evaluado previamente")
    })
    ResponseEntity<RetoClanResponseDTO> calcularResultadoReto(
            @Parameter(description = "Id del clan") String clanId,
            @Parameter(description = "Id del reto a evaluar") String retoId);

    @Operation(summary = "Calcular resultado del duelo de reto entre dos clanes (RF-36)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Duelo calculado y bonificación asignada al clan vencedor"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Uno de los clanes no fue encontrado"),
            @ApiResponse(responseCode = "422", description = "Regla de negocio infringida (ej. mismo clan en duelo)")
    })
    ResponseEntity<ResultadoRetoClanesResponseDTO> compararRetoEntreClanes(@Valid CompararRetoClanesRequestDTO dto);

    @Operation(summary = "Listar todos los retos asignados a un clan")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de retos del clan"),
            @ApiResponse(responseCode = "404", description = "Clan no encontrado")
    })
    ResponseEntity<List<RetoClanResponseDTO>> obtenerRetosPorClan(@Parameter(description = "Id del clan") String clanId);
}