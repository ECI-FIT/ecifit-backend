package co.edu.eci.dosw.ecifit.service;

import co.edu.eci.dosw.ecifit.exception.RecursoNoEncontradoException;
import co.edu.eci.dosw.ecifit.mapper.MisionEntityMapper;
import co.edu.eci.dosw.ecifit.model.Actividad;
import co.edu.eci.dosw.ecifit.model.factory.FabricaMisiones;
import co.edu.eci.dosw.ecifit.model.factory.Mision;
import co.edu.eci.dosw.ecifit.persistence.MisionEntity;
import co.edu.eci.dosw.ecifit.repository.MisionRepository;
import co.edu.eci.dosw.ecifit.validator.IMisionValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MisionServiceImpl implements IMisionService {

    private final MisionRepository misionRepository;
    private final MisionEntityMapper entityMapper;
    private final IMisionValidator validator;
    private final FabricaMisiones fabricaMisiones;

    @Override
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
    public Mision generarMisionSemanal(String estudianteId) {
        log.info("Generando misión semanal para estudiante={}", estudianteId);
        validator.validarLimiteMisionesActivas(estudianteId);

        Mision mision = fabricaMisiones.crearMisionSemanal();
        mision.setEstudianteId(estudianteId);

        MisionEntity guardada = misionRepository.save(entityMapper.toEntity(mision));
        log.info("Misión semanal generada: id={}, estudianteId={}", guardada.getId(), estudianteId);
        return entityMapper.toDomain(guardada);
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
}
