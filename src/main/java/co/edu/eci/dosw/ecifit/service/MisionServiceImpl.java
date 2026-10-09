package co.edu.eci.dosw.ecifit.service;

import co.edu.eci.dosw.ecifit.dto.request.CrearMisionRequestDTO;
import co.edu.eci.dosw.ecifit.dto.response.MisionResponseDTO;
import co.edu.eci.dosw.ecifit.exception.EstadoInvalidoException;
import co.edu.eci.dosw.ecifit.exception.RecursoNoEncontradoException;
import co.edu.eci.dosw.ecifit.exception.ReglaDeNegocioException;
import co.edu.eci.dosw.ecifit.mapper.MisionEntityMapper;
import co.edu.eci.dosw.ecifit.mapper.MisionMapper;
import co.edu.eci.dosw.ecifit.model.Actividad;
import co.edu.eci.dosw.ecifit.model.factory.FabricaMisiones;
import co.edu.eci.dosw.ecifit.model.factory.Mision;
import co.edu.eci.dosw.ecifit.persistence.EstudianteEntity;
import co.edu.eci.dosw.ecifit.persistence.MisionEntity;
import co.edu.eci.dosw.ecifit.repository.EstudianteRepository;
import co.edu.eci.dosw.ecifit.repository.MisionRepository;
import co.edu.eci.dosw.ecifit.validator.IMisionValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MisionServiceImpl implements IMisionService {

    private final MisionRepository misionRepository;
    private final EstudianteRepository estudianteRepository;
    private final MisionEntityMapper entityMapper;
    private final MisionMapper misionMapper;
    private final IMisionValidator validator;
    private final FabricaMisiones fabricaMisiones;

    @Override
    @Transactional
    public Mision generarMisionDiaria(CrearMisionRequestDTO dto) {
        log.info("Generando misión diaria para estudiante={}", dto.estudianteId());
        validator.validarLimiteMisionesActivas(dto.estudianteId());

        Mision mision = fabricaMisiones.crearMisionDiaria();
        mision.setEstudianteId(dto.estudianteId());
        if (dto.descripcion() != null && !dto.descripcion().isBlank()) {
            mision.setDescripcion(dto.descripcion());
        }
        if (dto.recompensa() != null) {
            mision.setRecompensa(dto.recompensa());
        }

        MisionEntity guardada = misionRepository.save(entityMapper.toEntity(mision));
        log.info("Misión diaria generada: id={}, estudianteId={}", guardada.getId(), dto.estudianteId());
        return entityMapper.toDomain(guardada);
    }

    @Override
    @Transactional
    public Mision generarMisionDiaria(String estudianteId) {
        log.info("Generando misión diaria para estudiante={}", estudianteId);
        validator.validarLimiteMisionesActivas(estudianteId);

        Mision mision = fabricaMisiones.crearMisionDiaria();
        mision.setEstudianteId(estudianteId);

        MisionEntity guardada = misionRepository.save(entityMapper.toEntity(mision));
        log.info("Misión diaria generada: id={}, estudianteId={}", guardada.getId(), estudianteId);
        return entityMapper.toDomain(guardada);
    }

    @Override
    @Transactional
    public Mision generarMisionSemanal(CrearMisionRequestDTO dto) {
        log.info("Generando misión semanal para estudiante={}", dto.estudianteId());
        validarEstudianteExiste(dto.estudianteId());
        validator.validarLimiteMisionesActivas(dto.estudianteId());

        Mision mision = fabricaMisiones.crearMisionSemanal();
        mision.setEstudianteId(dto.estudianteId());
        if (dto.descripcion() != null && !dto.descripcion().isBlank()) {
            mision.setDescripcion(dto.descripcion());
        }
        if (dto.recompensa() != null) {
            mision.setRecompensa(dto.recompensa());
        }

        MisionEntity guardada = misionRepository.save(entityMapper.toEntity(mision));
        log.info("Misión semanal generada: id={}, estudianteId={}", guardada.getId(), dto.estudianteId());
        return entityMapper.toDomain(guardada);
    }

    @Override
    @Transactional
    public Mision generarMisionSemanal(String estudianteId) {
        log.info("Generando misión semanal para estudiante={}", estudianteId);
        validarEstudianteExiste(estudianteId);
        validator.validarLimiteMisionesActivas(estudianteId);

        Mision mision = fabricaMisiones.crearMisionSemanal();
        mision.setEstudianteId(estudianteId);

        MisionEntity guardada = misionRepository.save(entityMapper.toEntity(mision));
        log.info("Misión semanal generada: id={}, estudianteId={}", guardada.getId(), estudianteId);
        return entityMapper.toDomain(guardada);
    }

    private EstudianteEntity validarEstudianteExiste(String estudianteId) {
        if (estudianteId == null || estudianteId.isBlank()) {
            throw new RecursoNoEncontradoException("Estudiante no encontrado con ID: " + estudianteId);
        }
        return estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> {
                    log.warn("Estudiante no existe con ID: {}", estudianteId);
                    return new RecursoNoEncontradoException("Estudiante", estudianteId);
                });
    }

    @Override
    public Mision evaluarCumplimiento(String misionId, Actividad actividad) {
        MisionEntity entity = misionRepository.findById(misionId)
                .orElseThrow(() -> {
                    log.warn("Misión no encontrada: id={}", misionId);
                    return new RecursoNoEncontradoException("Mision con id " + misionId + " no encontrada");
                });

        Mision mision = entityMapper.toDomain(entity);
        validator.validarNoCompletada(mision);

        boolean cumplida = Boolean.TRUE.equals(mision.verificarCumplimiento(actividad));
        MisionEntity actualizada = misionRepository.save(entityMapper.toEntity(mision));

        log.info("Misión evaluada: id={}, cumplida={}", misionId, cumplida);
        return entityMapper.toDomain(actualizada);
    }

    @Override
    public List<Mision> obtenerPorEstudiante(String estudianteId) {
        return misionRepository.findByEstudianteId(estudianteId).stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public MisionResponseDTO completarMision(UUID id) {
        if (id == null) {
            throw new RecursoNoEncontradoException("Mision", null);
        }
        return completarMision(id.toString());
    }

    @Override
    @Transactional
    public MisionResponseDTO completarMision(String id) {
        log.info("Procesando solicitud para completar misión id={}", id);

        MisionEntity misionEntity = misionRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Misión no encontrada para completar: id={}", id);
                    return new RecursoNoEncontradoException("Mision", id);
                });

        if (misionEntity.isCompletada()) {
            log.warn("Intento de completar una misión ya completada: id={}", id);
            throw new EstadoInvalidoException("La misión ya se encuentra completada previamente");
        }

        Mision mision = entityMapper.toDomain(misionEntity);
        if (Boolean.TRUE.equals(mision.getCompletada())) {
            log.warn("El modelo de dominio indica que la misión ya fue completada: id={}", id);
            throw new EstadoInvalidoException("La misión ya se encuentra completada previamente");
        }

        boolean cumple = mision.evaluarCumplimiento();
        if (!cumple) {
            log.warn("Criterios de cumplimiento no alcanzados para la misión: id={}", id);
            throw new ReglaDeNegocioException("La misión no cumple los criterios requeridos para ser completada");
        }

        mision.setCompletada(true);
        misionEntity.setCompletada(true);
        misionRepository.save(misionEntity);

        String estudianteId = misionEntity.getEstudianteId();
        if (estudianteId != null && !estudianteId.isBlank()) {
            EstudianteEntity estudianteEntity = estudianteRepository.findById(estudianteId)
                    .orElseThrow(() -> {
                        log.warn("Estudiante asociado a la misión no encontrado: id={}", estudianteId);
                        return new RecursoNoEncontradoException("Estudiante", estudianteId);
                    });

            int puntosActuales = estudianteEntity.getPuntosAcumulados() != null ? estudianteEntity.getPuntosAcumulados() : 0;
            int recompensa = misionEntity.getRecompensa() != null ? misionEntity.getRecompensa() : 0;
            estudianteEntity.setPuntosAcumulados(puntosActuales + recompensa);
            estudianteRepository.save(estudianteEntity);
            log.info("Recompensa de {} puntos abonada al estudiante {}. Total acumulado: {}",
                    recompensa, estudianteId, estudianteEntity.getPuntosAcumulados());
        }

        return misionMapper.toResponse(mision);
    }
}
