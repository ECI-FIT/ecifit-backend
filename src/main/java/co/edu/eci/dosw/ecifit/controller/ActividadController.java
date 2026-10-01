package co.edu.eci.dosw.ecifit.controller;

import co.edu.eci.dosw.ecifit.controller.docs.ActividadApi;
import co.edu.eci.dosw.ecifit.dto.request.RegistrarActividadRequestDTO;
import co.edu.eci.dosw.ecifit.dto.response.ActividadResponseDTO;
import co.edu.eci.dosw.ecifit.mapper.ActividadMapper;
import co.edu.eci.dosw.ecifit.model.Actividad;
import co.edu.eci.dosw.ecifit.service.IActividadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/actividades")
@RequiredArgsConstructor
public class ActividadController implements ActividadApi {

    private final IActividadService actividadService;
    private final ActividadMapper actividadMapper;

    @Override
    @PostMapping
    public ResponseEntity<ActividadResponseDTO> registrarActividad(@Valid @RequestBody RegistrarActividadRequestDTO dto) {
        Actividad actividad = actividadMapper.toDomain(dto);
        final int[] puntosCalculados = new int[1];
        actividad.agregarObservador((pts, e) -> puntosCalculados[0] = pts);

        Actividad registrada = actividadService.registrarActividad(actividad, dto.estudianteId());
        ActividadResponseDTO response = actividadMapper.toResponse(registrada, dto.estudianteId(), puntosCalculados[0]);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @GetMapping("/estudiante/{estudianteId}")
    public ResponseEntity<List<ActividadResponseDTO>> obtenerHistorial(@PathVariable String estudianteId) {
        List<Actividad> actividades = actividadService.obtenerHistorialPorEstudiante(estudianteId);
        List<ActividadResponseDTO> response = actividadMapper.toResponseList(actividades, estudianteId);
        return ResponseEntity.ok(response);
    }
}
