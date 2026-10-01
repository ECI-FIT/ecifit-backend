package co.edu.eci.dosw.ecifit.service;

import co.edu.eci.dosw.ecifit.exception.RecursoNoEncontradoException;
import co.edu.eci.dosw.ecifit.mapper.ActividadEntityMapper;
import co.edu.eci.dosw.ecifit.mapper.EstudianteEntityMapper;
import co.edu.eci.dosw.ecifit.model.Actividad;
import co.edu.eci.dosw.ecifit.model.Estudiante;
import co.edu.eci.dosw.ecifit.persistence.ActividadEntity;
import co.edu.eci.dosw.ecifit.persistence.EstudianteEntity;
import co.edu.eci.dosw.ecifit.repository.ActividadRepository;
import co.edu.eci.dosw.ecifit.repository.EstudianteRepository;
import co.edu.eci.dosw.ecifit.validator.IActividadValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActividadServiceImpl implements IActividadService {

    private final ActividadRepository actividadRepository;
    private final EstudianteRepository estudianteRepository;
    private final ActividadEntityMapper actividadEntityMapper;
    private final EstudianteEntityMapper estudianteEntityMapper;
    private final IActividadValidator actividadValidator;

    @Override
    @Transactional
    public Actividad registrarActividad(Actividad actividad, String estudianteId) {
        log.info("Iniciando registro de actividad física para el estudiante con ID: {}", estudianteId);

        EstudianteEntity estudianteEntity = buscarEstudiantePorId(estudianteId);
        actividadValidator.validar(actividad);

        Estudiante estudiante = estudianteEntityMapper.toDomain(estudianteEntity);
        int puntosGenerados = calcularPuntosSegunRol(estudiante, actividad);

        ActividadEntity actividadEntity = actividadEntityMapper.toEntity(actividad, estudianteId, puntosGenerados);
        actividadRepository.save(actividadEntity);

        estudianteEntity.setPuntosAcumulados(estudiante.getPuntosAcumulados());
        estudianteRepository.save(estudianteEntity);

        log.info("Actividad física registrada exitosamente para estudiante ID: {}. Puntos concedidos: {}", estudianteId, puntosGenerados);
        return actividad;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Actividad> obtenerHistorialPorEstudiante(String estudianteId) {
        log.info("Consultando historial de actividades físicas para el estudiante ID: {}", estudianteId);

        if (!estudianteRepository.existsById(estudianteId)) {
            log.warn("Consulta de historial fallida: estudiante no existe con ID: {}", estudianteId);
            throw new RecursoNoEncontradoException("Estudiante", estudianteId);
        }

        List<ActividadEntity> entidades = actividadRepository.findByEstudianteIdOrderByFechaDesc(estudianteId);
        List<Actividad> actividades = entidades.stream()
                .map(actividadEntityMapper::toDomain)
                .toList();

        log.info("Historial de actividades físicas consultado para estudiante ID: {}. Total registros: {}", estudianteId, actividades.size());
        return actividades.isEmpty() ? List.of() : List.copyOf(actividades);
    }

    private EstudianteEntity buscarEstudiantePorId(String estudianteId) {
        return estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> {
                    log.warn("No fue posible registrar actividad: estudiante no existe con ID: {}", estudianteId);
                    return new RecursoNoEncontradoException("Estudiante", estudianteId);
                });
    }

    private int calcularPuntosSegunRol(Estudiante estudiante, Actividad actividad) {
        final int[] puntosCalculados = new int[1];
        actividad.agregarObservador((pts, e) -> puntosCalculados[0] = pts);
        estudiante.registrarActividad(actividad);
        return puntosCalculados[0];
    }
}
