package co.edu.eci.dosw.ecifit.service;

import co.edu.eci.dosw.ecifit.exception.RecursoNoEncontradoException;
import co.edu.eci.dosw.ecifit.mapper.RankingTemporadaEntityMapper;
import co.edu.eci.dosw.ecifit.mapper.TemporadaEntityMapper;
import co.edu.eci.dosw.ecifit.model.RankingTemporada;
import co.edu.eci.dosw.ecifit.model.Temporada;
import co.edu.eci.dosw.ecifit.persistence.TemporadaEntity;
import co.edu.eci.dosw.ecifit.repository.RankingTemporadaRepository;
import co.edu.eci.dosw.ecifit.repository.TemporadaRepository;
import co.edu.eci.dosw.ecifit.validator.ITemporadaValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TemporadaServiceImpl implements ITemporadaService {

    private final TemporadaRepository temporadaRepository;
    private final RankingTemporadaRepository rankingTemporadaRepository;
    private final TemporadaEntityMapper temporadaEntityMapper;
    private final RankingTemporadaEntityMapper rankingTemporadaEntityMapper;
    private final ITemporadaValidator temporadaValidator;

    @Override
    public Temporada crear(Temporada temporada) {
        if (temporada.getEstadoActivo() == null) {
            temporada.setEstadoActivo(true);
        }

        temporadaValidator.validarFechas(temporada.getFechaInicio(), temporada.getFechaFin());
        temporadaValidator.validarTemporadaUnicaActiva(temporada.getEstadoActivo());

        TemporadaEntity guardada = temporadaRepository.save(temporadaEntityMapper.toEntity(temporada));
        log.info("Temporada creada con ID: {}", guardada.getId());
        return temporadaEntityMapper.toDomain(guardada);
    }

    @Override
    public Temporada obtenerPorId(UUID id) {
        TemporadaEntity entity = temporadaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Intento de buscar temporada inexistente: id={}", id);
                    return new RecursoNoEncontradoException("Temporada", id);
                });

        Temporada temporada = temporadaEntityMapper.toDomain(entity);
        log.info("Temporada obtenida con ID: {}", id);
        return temporada;
    }

    @Override
    public Temporada obtenerActiva() {
        TemporadaEntity entity = temporadaRepository.findByEstadoActivoTrue()
                .orElseThrow(() -> {
                    log.warn("Intento de buscar temporada activa sin resultado");
                    return new RecursoNoEncontradoException("Temporada activa no encontrada");
                });

        Temporada temporada = temporadaEntityMapper.toDomain(entity);
        log.info("Temporada activa obtenida con ID: {}", entity.getId());
        return temporada;
    }

    @Override
    public List<RankingTemporada> obtenerRankingGeneral(UUID temporadaId) {
        if (!temporadaRepository.existsById(temporadaId)) {
            log.warn("Intento de consultar ranking de temporada inexistente: id={}", temporadaId);
            throw new RecursoNoEncontradoException("Temporada", temporadaId);
        }

        List<RankingTemporada> ranking = rankingTemporadaRepository
                .findByTemporadaIdOrderByPuntosAcumuladosDesc(temporadaId)
                .stream()
                .map(rankingTemporadaEntityMapper::toDomain)
                .toList();

        log.info("Ranking general obtenido para temporada {}: {} registros", temporadaId, ranking.size());
        return ranking;
    }
}
