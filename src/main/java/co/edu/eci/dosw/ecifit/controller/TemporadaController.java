package co.edu.eci.dosw.ecifit.controller;

import co.edu.eci.dosw.ecifit.controller.docs.TemporadaApi;
import co.edu.eci.dosw.ecifit.dto.request.TemporadaRequestDTO;
import co.edu.eci.dosw.ecifit.dto.response.RankingTemporadaResponseDTO;
import co.edu.eci.dosw.ecifit.dto.response.TemporadaResponseDTO;
import co.edu.eci.dosw.ecifit.mapper.RankingTemporadaMapper;
import co.edu.eci.dosw.ecifit.mapper.TemporadaMapper;
import co.edu.eci.dosw.ecifit.model.RankingTemporada;
import co.edu.eci.dosw.ecifit.model.Temporada;
import co.edu.eci.dosw.ecifit.service.ITemporadaService;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/temporadas")
@RequiredArgsConstructor
public class TemporadaController implements TemporadaApi {

    private final ITemporadaService temporadaService;
    private final TemporadaMapper temporadaMapper;
    private final RankingTemporadaMapper rankingTemporadaMapper;

    @Override
    @PostMapping
    public ResponseEntity<TemporadaResponseDTO> crear(@Valid @RequestBody TemporadaRequestDTO request) {
        Temporada temporada = temporadaMapper.toDomain(request);
        Temporada creada = temporadaService.crear(temporada);
        return ResponseEntity.status(HttpStatus.CREATED).body(temporadaMapper.toResponse(creada));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<TemporadaResponseDTO> obtenerPorId(@PathVariable UUID id) {
        Temporada temporada = temporadaService.obtenerPorId(id);
        return ResponseEntity.ok(temporadaMapper.toResponse(temporada));
    }

    @Override
    @GetMapping("/activa")
    public ResponseEntity<TemporadaResponseDTO> obtenerActiva() {
        Temporada temporada = temporadaService.obtenerActiva();
        return ResponseEntity.ok(temporadaMapper.toResponse(temporada));
    }

    @Override
    @GetMapping("/{temporadaId}/ranking")
    public ResponseEntity<List<RankingTemporadaResponseDTO>> obtenerRanking(@PathVariable UUID temporadaId) {
        List<RankingTemporada> ranking = temporadaService.obtenerRankingGeneral(temporadaId);
        List<RankingTemporadaResponseDTO> response = ranking.stream()
                .map(rankingTemporadaMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }
}
