package co.edu.eci.dosw.ecifit.controller;

import co.edu.eci.dosw.ecifit.controller.docs.InvitacionClanApi;
import co.edu.eci.dosw.ecifit.dto.request.InvitarClanRequestDTO;
import co.edu.eci.dosw.ecifit.dto.request.ResponderInvitacionRequestDTO;
import co.edu.eci.dosw.ecifit.dto.response.InvitacionClanResponseDTO;
import co.edu.eci.dosw.ecifit.mapper.InvitacionClanMapper;
import co.edu.eci.dosw.ecifit.model.InvitacionClan;
import co.edu.eci.dosw.ecifit.service.IInvitacionClanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clanes/invitaciones")
@RequiredArgsConstructor
public class InvitacionClanController implements InvitacionClanApi {

    private final IInvitacionClanService invitacionService;
    private final InvitacionClanMapper invitacionMapper;

    @Override
    @PostMapping
    @PreAuthorize("hasRole('ESTUDIANTE')")
    public ResponseEntity<InvitacionClanResponseDTO> invitar(@RequestBody @Valid InvitarClanRequestDTO dto) {
        InvitacionClan invitacion = invitacionService.invitar(dto.clanId(), dto.estudianteId(), dto.invitadoPorId());
        return ResponseEntity.status(HttpStatus.CREATED).body(invitacionMapper.toResponse(invitacion));
    }

    @Override
    @PostMapping("/{id}/responder")
    @PreAuthorize("hasRole('ESTUDIANTE')")
    public ResponseEntity<InvitacionClanResponseDTO> responder(@PathVariable String id,
                                                               @RequestBody @Valid ResponderInvitacionRequestDTO dto) {
        InvitacionClan invitacion = invitacionService.responder(id, dto.aceptar());
        return ResponseEntity.ok(invitacionMapper.toResponse(invitacion));
    }

    @Override
    @GetMapping("/estudiante/{estudianteId}")
    @PreAuthorize("hasAnyRole('ESTUDIANTE', 'ADMINISTRADOR')")
    public ResponseEntity<List<InvitacionClanResponseDTO>> obtenerPendientes(@PathVariable String estudianteId) {
        List<InvitacionClanResponseDTO> pendientes = invitacionService.obtenerPendientes(estudianteId).stream()
                .map(invitacionMapper::toResponse)
                .toList();
        return ResponseEntity.ok(pendientes);
    }
}
