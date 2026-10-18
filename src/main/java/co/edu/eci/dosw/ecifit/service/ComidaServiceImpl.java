package co.edu.eci.dosw.ecifit.service;

import co.edu.eci.dosw.ecifit.exception.RecursoNoEncontradoException;
import co.edu.eci.dosw.ecifit.mapper.ComidaEntityMapper;
import co.edu.eci.dosw.ecifit.model.Comida;
import co.edu.eci.dosw.ecifit.model.EstadoEvaluacion;
import co.edu.eci.dosw.ecifit.persistence.ComidaEntity;
import co.edu.eci.dosw.ecifit.repository.ComidaRepository;
import co.edu.eci.dosw.ecifit.repository.EstudianteRepository;
import co.edu.eci.dosw.ecifit.validator.IComidaValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComidaServiceImpl implements IComidaService {

    private final ComidaRepository comidaRepository;
    private final EstudianteRepository estudianteRepository;
    private final ComidaEntityMapper comidaEntityMapper;
    private final IComidaValidator comidaValidator;

    @Override
    @Transactional
    public Comida registrar(Comida comida) {
        if (comida != null && comida.getFechaHora() == null) {
            comida.setFechaHora(LocalDateTime.now());
        }
        comidaValidator.validar(comida);

        String estudianteId = comida.getEstudianteId();
        if (!estudianteRepository.existsById(estudianteId)) {
            throw new RecursoNoEncontradoException("Estudiante", estudianteId);
        }

        LocalDate dia = comida.getFechaHora().toLocalDate();
        boolean primeraDelTipoEnElDia = !comidaRepository.existsByEstudianteIdAndTipoAndFechaHoraBetween(
                estudianteId, comida.getTipo(), dia.atStartOfDay(), dia.atTime(LocalTime.MAX));

        comida.setId(UUID.randomUUID().toString());
        comida.setEstado(EstadoEvaluacion.PENDIENTE);
        comida.setIntentosEvaluacion(0);
        comida.setOtorgaPuntos(primeraDelTipoEnElDia);
        comida.setPuntosOtorgados(0);

        ComidaEntity guardada = comidaRepository.save(comidaEntityMapper.toEntity(comida));
        log.info("Comida registrada. Estudiante: {}, tipo: {}, otorga puntos: {}",
                estudianteId, comida.getTipo(), primeraDelTipoEnElDia);
        return comidaEntityMapper.toDomain(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public Comida obtenerPorId(String id) {
        return comidaRepository.findById(id)
                .map(comidaEntityMapper::toDomain)
                .orElseThrow(() -> new RecursoNoEncontradoException("Comida", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comida> obtenerHistorial(String estudianteId, LocalDate fecha) {
        if (!estudianteRepository.existsById(estudianteId)) {
            throw new RecursoNoEncontradoException("Estudiante", estudianteId);
        }

        List<ComidaEntity> entidades = (fecha == null)
                ? comidaRepository.findByEstudianteIdOrderByFechaHoraDesc(estudianteId)
                : comidaRepository.findByEstudianteIdAndFechaHoraBetweenOrderByFechaHoraDesc(
                        estudianteId, fecha.atStartOfDay(), fecha.atTime(LocalTime.MAX));

        log.info("Historial de comidas consultado. Estudiante: {}, fecha: {}, registros: {}",
                estudianteId, fecha, entidades.size());
        return entidades.stream().map(comidaEntityMapper::toDomain).toList();
    }
}
