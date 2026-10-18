package co.edu.eci.dosw.ecifit.controller;

import co.edu.eci.dosw.ecifit.controller.docs.ComidaApi;
import co.edu.eci.dosw.ecifit.dto.request.RegistrarComidaRequestDTO;
import co.edu.eci.dosw.ecifit.dto.response.ComidaResponseDTO;
import co.edu.eci.dosw.ecifit.mapper.ComidaMapper;
import co.edu.eci.dosw.ecifit.model.Comida;
import co.edu.eci.dosw.ecifit.service.IComidaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/nutricion/comidas")
@RequiredArgsConstructor
public class ComidaController implements ComidaApi {

    private final IComidaService comidaService;
    private final ComidaMapper comidaMapper;

    @Override
    @PostMapping
    @PreAuthorize("hasAnyRole('ESTUDIANTE', 'ADMINISTRADOR')")
    public ResponseEntity<ComidaResponseDTO> registrar(@Valid @RequestBody RegistrarComidaRequestDTO dto) {
        Comida registrada = comidaService.registrar(comidaMapper.toDomain(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(comidaMapper.toResponse(registrada));
    }

    @Override
    @PostMapping("/{id}/evaluar")
    @PreAuthorize("hasAnyRole('ESTUDIANTE', 'ADMINISTRADOR')")
    public ResponseEntity<ComidaResponseDTO> reevaluar(@PathVariable String id) {
        return ResponseEntity.ok(comidaMapper.toResponse(comidaService.reevaluar(id)));
    }

    @Override
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ESTUDIANTE', 'ENTRENADOR', 'ADMINISTRADOR')")
    public ResponseEntity<ComidaResponseDTO> obtenerPorId(@PathVariable String id) {
        return ResponseEntity.ok(comidaMapper.toResponse(comidaService.obtenerPorId(id)));
    }

    @Override
    @GetMapping("/estudiante/{estudianteId}")
    @PreAuthorize("hasAnyRole('ESTUDIANTE', 'ENTRENADOR', 'ADMINISTRADOR')")
    public ResponseEntity<List<ComidaResponseDTO>> obtenerHistorial(
            @PathVariable String estudianteId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(comidaMapper.toResponseList(comidaService.obtenerHistorial(estudianteId, fecha)));
    }
}
