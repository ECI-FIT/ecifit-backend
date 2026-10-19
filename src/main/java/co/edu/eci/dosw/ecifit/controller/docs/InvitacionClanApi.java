package co.edu.eci.dosw.ecifit.controller.docs;

import co.edu.eci.dosw.ecifit.dto.request.InvitarClanRequestDTO;
import co.edu.eci.dosw.ecifit.dto.request.ResponderInvitacionRequestDTO;
import co.edu.eci.dosw.ecifit.dto.response.InvitacionClanResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Invitaciones a clanes", description = "Invitar estudiantes a un clan y responder invitaciones")
public interface InvitacionClanApi {

    @Operation(summary = "Invitar a un estudiante a un clan")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Invitación creada"),
            @ApiResponse(responseCode = "404", description = "Clan o estudiante no encontrado"),
            @ApiResponse(responseCode = "409", description = "El estudiante ya pertenece a un clan o ya tiene una invitación pendiente"),
            @ApiResponse(responseCode = "422", description = "Quien invita no es miembro o el clan está lleno")
    })
    ResponseEntity<InvitacionClanResponseDTO> invitar(@Valid InvitarClanRequestDTO dto);

    @Operation(summary = "Aceptar o rechazar una invitación")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Invitación respondida; si se acepta, el estudiante entra al clan"),
            @ApiResponse(responseCode = "404", description = "Invitación no encontrada"),
            @ApiResponse(responseCode = "409", description = "El estudiante ya pertenece a un clan"),
            @ApiResponse(responseCode = "422", description = "La invitación ya fue respondida o el clan está lleno")
    })
    ResponseEntity<InvitacionClanResponseDTO> responder(String id, @Valid ResponderInvitacionRequestDTO dto);

    @Operation(summary = "Consultar invitaciones pendientes de un estudiante")
    @ApiResponse(responseCode = "200", description = "Lista de invitaciones pendientes")
    ResponseEntity<List<InvitacionClanResponseDTO>> obtenerPendientes(String estudianteId);
}
