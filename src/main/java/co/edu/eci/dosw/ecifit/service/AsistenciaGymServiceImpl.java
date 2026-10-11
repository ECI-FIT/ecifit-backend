package co.edu.eci.dosw.ecifit.service;

import co.edu.eci.dosw.ecifit.exception.ConflictoException;
import co.edu.eci.dosw.ecifit.exception.RecursoNoEncontradoException;
import co.edu.eci.dosw.ecifit.mapper.AsistenciaGymEntityMapper;
import co.edu.eci.dosw.ecifit.mapper.EstudianteEntityMapper;
import co.edu.eci.dosw.ecifit.model.AsistenciaGym;
import co.edu.eci.dosw.ecifit.model.Estudiante;
import co.edu.eci.dosw.ecifit.persistence.AsistenciaGymEntity;
import co.edu.eci.dosw.ecifit.persistence.EstudianteEntity;
import co.edu.eci.dosw.ecifit.repository.AsistenciaGymRepository;
import co.edu.eci.dosw.ecifit.repository.EstudianteRepository;
import co.edu.eci.dosw.ecifit.validator.IAsistenciaGymValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsistenciaGymServiceImpl implements IAsistenciaGymService {

    private final AsistenciaGymRepository asistenciaGymRepository;
    private final EstudianteRepository estudianteRepository;
    private final EstudianteEntityMapper estudianteEntityMapper;
    private final AsistenciaGymEntityMapper asistenciaGymEntityMapper;
    private final IAsistenciaGymValidator asistenciaGymValidator;

    @Override
    @Transactional
    public AsistenciaGym registrarAsistencia(String estudianteId, String entrenadorId) {
        asistenciaGymValidator.validar(estudianteId, entrenadorId);

        EstudianteEntity estudianteEntity = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante", estudianteId));

        LocalDate hoy = LocalDate.now();
        LocalDateTime inicioDia = hoy.atStartOfDay();
        LocalDateTime finDia = hoy.atTime(LocalTime.MAX);
        if (asistenciaGymRepository.existsByEstudianteIdAndFechaHoraBetween(
                estudianteId, inicioDia, finDia)) {
            throw new ConflictoException(
                    "El estudiante ya tiene una asistencia al gimnasio registrada para hoy.");
        }

        Estudiante estudiante = estudianteEntityMapper.toDomain(estudianteEntity);
        int puntosActuales = estudiante.getPuntosAcumulados() != null
                ? estudiante.getPuntosAcumulados()
                : 0;
        estudiante.setPuntosAcumulados(puntosActuales + AsistenciaGym.PUNTOS_ASISTENCIA);
        estudianteRepository.save(estudianteEntityMapper.toEntity(estudiante));

        AsistenciaGym asistencia = new AsistenciaGym(
                UUID.randomUUID().toString(), estudianteId, entrenadorId, LocalDateTime.now());
        AsistenciaGymEntity asistenciaEntity = asistenciaGymRepository.save(
                asistenciaGymEntityMapper.toEntity(asistencia));

        log.info("Asistencia al gimnasio confirmada. Estudiante: {}, entrenador: {}, bono: {} puntos",
                estudianteId, entrenadorId, asistencia.getPuntosOtorgados());
        return asistenciaGymEntityMapper.toDomain(asistenciaEntity);
    }
}
