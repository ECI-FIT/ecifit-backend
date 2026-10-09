package co.edu.eci.dosw.ecifit.controller;

import co.edu.eci.dosw.ecifit.controller.docs.ClanApi;
import co.edu.eci.dosw.ecifit.dto.request.CrearClanRequestDTO;
import co.edu.eci.dosw.ecifit.dto.request.UnirseClanRequestDTO;
import co.edu.eci.dosw.ecifit.dto.response.ClanResponseDTO;
import co.edu.eci.dosw.ecifit.mapper.ClanMapper;
import co.edu.eci.dosw.ecifit.model.Clan;
import co.edu.eci.dosw.ecifit.service.IClanService;
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
@RequestMapping("/api/v1/clanes")
@RequiredArgsConstructor
public class ClanController implements ClanApi {

    private final IClanService clanService;
    private final ClanMapper clanMapper;

    @Override
    @PostMapping
    @PreAuthorize("hasAnyRole('ESTUDIANTE', 'ADMINISTRADOR')")
    public ResponseEntity<ClanResponseDTO> crear(@RequestBody @Valid CrearClanRequestDTO dto) {
        Clan clan = clanMapper.toDomain(dto);
        Clan creado = clanService.crear(clan);
        return ResponseEntity.status(HttpStatus.CREATED).body(clanMapper.toResponse(creado));
    }

    @Override
    @PostMapping("/unirse")
    @PreAuthorize("hasRole('ESTUDIANTE')")
    public ResponseEntity<ClanResponseDTO> unirse(@RequestBody @Valid UnirseClanRequestDTO dto) {
        Clan actualizado = clanService.unirse(dto.clanId(), dto.estudianteId());
        return ResponseEntity.ok(clanMapper.toResponse(actualizado));
    }

    @Override
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ESTUDIANTE', 'ENTRENADOR', 'ADMINISTRADOR')")
    public ResponseEntity<ClanResponseDTO> obtenerPorId(@PathVariable String id) {
        Clan clan = clanService.obtenerPorId(id);
        return ResponseEntity.ok(clanMapper.toResponse(clan));
    }

    @Override
    @GetMapping
    @PreAuthorize("hasAnyRole('ESTUDIANTE', 'ENTRENADOR', 'ADMINISTRADOR')")
    public ResponseEntity<List<ClanResponseDTO>> obtenerTodos() {
        List<ClanResponseDTO> clanes = clanService.obtenerTodos().stream()
                .map(clanMapper::toResponse)
                .toList();
        return ResponseEntity.ok(clanes);
    }
}