package co.edu.eci.dosw.ecifit.service;

import co.edu.eci.dosw.ecifit.exception.IaNoDisponibleException;
import co.edu.eci.dosw.ecifit.exception.RecursoNoEncontradoException;
import co.edu.eci.dosw.ecifit.exception.ReglaDeNegocioException;
import co.edu.eci.dosw.ecifit.mapper.ComidaEntityMapper;
import co.edu.eci.dosw.ecifit.model.Comida;
import co.edu.eci.dosw.ecifit.model.EstadoEvaluacion;
import co.edu.eci.dosw.ecifit.model.EvaluacionNutricional;
import co.edu.eci.dosw.ecifit.persistence.ComidaEntity;
import co.edu.eci.dosw.ecifit.persistence.EstudianteEntity;
import co.edu.eci.dosw.ecifit.repository.ComidaRepository;
import co.edu.eci.dosw.ecifit.repository.EstudianteRepository;
import co.edu.eci.dosw.ecifit.service.ia.IEvaluadorNutricional;
import co.edu.eci.dosw.ecifit.validator.IComidaValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComidaServiceImpl implements IComidaService {

    private final ComidaRepository comidaRepository;
    private final EstudianteRepository estudianteRepository;
    private final ComidaEntityMapper comidaEntityMapper;
    private final IComidaValidator comidaValidator;
    private final IEvaluadorNutricional evaluadorNutricional;

    @Override
    @Transactional
    public Comida registrar(Comida comida) {
        if (comida != null && comida.getFechaHora() == null) {
            comida.setFechaHora(LocalDateTime.now());
        }
        comidaValidator.validar(comida);

        String estudianteId = comida.getEstudianteId();
        EstudianteEntity estudiante = buscarEstudiante(estudianteId);

        LocalDate dia = comida.getFechaHora().toLocalDate();
        boolean primeraDelTipoEnElDia = !comidaRepository.existsByEstudianteIdAndTipoAndFechaHoraBetween(
                estudianteId, comida.getTipo(), dia.atStartOfDay(), dia.atTime(LocalTime.MAX));

        comida.setId(UUID.randomUUID().toString());
        comida.setEstado(EstadoEvaluacion.PENDIENTE);
        comida.setIntentosEvaluacion(0);
        comida.setOtorgaPuntos(primeraDelTipoEnElDia);
        comida.setPuntosOtorgados(0);

        evaluarYOtorgarPuntos(comida, estudiante);

        ComidaEntity guardada = comidaRepository.save(comidaEntityMapper.toEntity(comida));
        log.info("Comida registrada. Estudiante: {}, tipo: {}, estado: {}, puntos: {}",
                estudianteId, comida.getTipo(), comida.getEstado(), comida.getPuntosOtorgados());
        return comidaEntityMapper.toDomain(guardada);
    }

    @Override
    @Transactional
    public Comida reevaluar(String id) {
        Comida comida = comidaRepository.findById(id)
                .map(comidaEntityMapper::toDomain)
                .orElseThrow(() -> new RecursoNoEncontradoException("Comida", id));
        if (comida.estaEvaluada()) {
            throw new ReglaDeNegocioException("La comida ya fue evaluada.");
        }

        EstudianteEntity estudiante = buscarEstudiante(comida.getEstudianteId());
        evaluarYOtorgarPuntos(comida, estudiante);

        ComidaEntity guardada = comidaRepository.save(comidaEntityMapper.toEntity(comida));
        log.info("Reevaluación manual de comida {}: estado {}", id, comida.getEstado());
        return comidaEntityMapper.toDomain(guardada);
    }

    @Override
    @Transactional
    public int reintentarPendientes() {
        List<ComidaEntity> pendientes = comidaRepository
                .findTop50ByEstadoAndIntentosEvaluacionLessThanOrderByFechaHoraAsc(
                        EstadoEvaluacion.PENDIENTE, Comida.MAX_INTENTOS_EVALUACION);
        int evaluadas = 0;
        for (ComidaEntity entity : pendientes) {
            Comida comida = comidaEntityMapper.toDomain(entity);
            Optional<EstudianteEntity> estudiante = estudianteRepository.findById(comida.getEstudianteId());
            if (estudiante.isEmpty()) {
                log.warn("Comida {} sin estudiante existente ({}); se omite el reintento",
                        comida.getId(), comida.getEstudianteId());
                continue;
            }
            evaluarYOtorgarPuntos(comida, estudiante.get());
            comidaRepository.save(comidaEntityMapper.toEntity(comida));
            if (comida.estaEvaluada()) {
                evaluadas++;
            }
        }
        if (!pendientes.isEmpty()) {
            log.info("Reintento de evaluaciones nutricionales: {} pendientes, {} evaluadas",
                    pendientes.size(), evaluadas);
        }
        return evaluadas;
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

    private EstudianteEntity buscarEstudiante(String estudianteId) {
        return estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante", estudianteId));
    }

    /**
     * Pide la evaluación a la IA. Si responde, aplica el resultado y suma los puntos al estudiante;
     * si no está disponible, registra el intento fallido para reintentarlo después.
     */
    private void evaluarYOtorgarPuntos(Comida comida, EstudianteEntity estudiante) {
        EvaluacionNutricional evaluacion;
        try {
            evaluacion = evaluadorNutricional.evaluar(comida.getTipo(), comida.getDescripcion());
        } catch (IaNoDisponibleException e) {
            comida.registrarIntentoFallido();
            log.warn("Evaluación nutricional no disponible para la comida {} (intento {}): {}",
                    comida.getId(), comida.getIntentosEvaluacion(), e.getMessage());
            return;
        }

        int puntos = comida.aplicarEvaluacion(evaluacion);
        if (puntos > 0) {
            int actuales = estudiante.getPuntosAcumulados() != null ? estudiante.getPuntosAcumulados() : 0;
            estudiante.setPuntosAcumulados(actuales + puntos);
            estudianteRepository.save(estudiante);
            log.info("Puntos por nutrición otorgados al estudiante {}: {}", estudiante.getId(), puntos);
        }
    }
}
