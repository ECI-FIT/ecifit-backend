package co.edu.eci.dosw.ecifit.controller;

import co.edu.eci.dosw.ecifit.controller.docs.AsistenciaGymApi;
import co.edu.eci.dosw.ecifit.dto.request.AsistenciaGymRequestDTO;
import co.edu.eci.dosw.ecifit.dto.response.AsistenciaGymResponseDTO;
import co.edu.eci.dosw.ecifit.mapper.AsistenciaGymMapper;
import co.edu.eci.dosw.ecifit.model.AsistenciaGym;
import co.edu.eci.dosw.ecifit.service.IAsistenciaGymService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/gimnasio/asistencias")
@RequiredArgsConstructor
public class AsistenciaGymController implements AsistenciaGymApi {

    private final IAsistenciaGymService asistenciaGymService;
    private final AsistenciaGymMapper asistenciaGymMapper;

    @Override
    @PostMapping
    public ResponseEntity<AsistenciaGymResponseDTO> registrarAsistencia(
            @Valid @RequestBody AsistenciaGymRequestDTO dto) {
        AsistenciaGym asistencia = asistenciaGymService.registrarAsistencia(
                dto.estudianteId(), dto.entrenadorId());
        AsistenciaGymResponseDTO response = asistenciaGymMapper.toResponse(asistencia);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
