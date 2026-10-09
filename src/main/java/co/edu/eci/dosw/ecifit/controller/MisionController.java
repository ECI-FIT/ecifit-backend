package co.edu.eci.dosw.ecifit.controller;

import co.edu.eci.dosw.ecifit.controller.docs.MisionApi;
import co.edu.eci.dosw.ecifit.dto.request.CrearMisionRequestDTO;
import co.edu.eci.dosw.ecifit.dto.response.MisionResponseDTO;
import co.edu.eci.dosw.ecifit.mapper.MisionMapper;
import co.edu.eci.dosw.ecifit.model.factory.Mision;
import co.edu.eci.dosw.ecifit.service.IMisionService;
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
@RequestMapping("/api/v1/misiones")
@RequiredArgsConstructor
public class MisionController implements MisionApi {

    private final IMisionService misionService;
    private final MisionMapper misionMapper;

    @Override
    @PostMapping("/diarias")
    @PreAuthorize("hasAnyRole('ENTRENADOR', 'ADMINISTRADOR')")
    public ResponseEntity<MisionResponseDTO> generarDiaria(@RequestBody @Valid CrearMisionRequestDTO dto) {
        Mision mision = misionService.generarMisionDiaria(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(misionMapper.toResponse(mision));
    }

    @Override
    @PostMapping("/semanales")
    @PreAuthorize("hasAnyRole('ENTRENADOR', 'ADMINISTRADOR')")
    public ResponseEntity<MisionResponseDTO> generarSemanal(@RequestBody @Valid CrearMisionRequestDTO dto) {
        Mision mision = misionService.generarMisionSemanal(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(misionMapper.toResponse(mision));
    }

    @Override
    @GetMapping("/estudiante/{estudianteId}")
    @PreAuthorize("hasAnyRole('ESTUDIANTE', 'ENTRENADOR', 'ADMINISTRADOR')")
    public ResponseEntity<List<MisionResponseDTO>> obtenerPorEstudiante(@PathVariable String estudianteId) {
        List<MisionResponseDTO> misiones = misionService.obtenerPorEstudiante(estudianteId).stream()
                .map(misionMapper::toResponse)
                .toList();
        return ResponseEntity.ok(misiones);
    }

    @Override
    @PostMapping("/{id}/completar")
    @PreAuthorize("hasAnyRole('ESTUDIANTE', 'ADMINISTRADOR')")
    public ResponseEntity<MisionResponseDTO> completar(@PathVariable String id) {
        MisionResponseDTO response = misionService.completarMision(id);
        return ResponseEntity.ok(response);
    }
}
