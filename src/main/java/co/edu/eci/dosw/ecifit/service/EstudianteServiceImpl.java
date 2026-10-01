package co.edu.eci.dosw.ecifit.service;

import co.edu.eci.dosw.ecifit.exception.RecursoNoEncontradoException;
import co.edu.eci.dosw.ecifit.mapper.EstudianteEntityMapper;
import co.edu.eci.dosw.ecifit.model.Estudiante;
import co.edu.eci.dosw.ecifit.persistence.EstudianteEntity;
import co.edu.eci.dosw.ecifit.repository.EstudianteRepository;
import co.edu.eci.dosw.ecifit.validator.IEstudianteValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EstudianteServiceImpl implements IEstudianteService {

    private final EstudianteRepository estudianteRepository;
    private final EstudianteEntityMapper estudianteEntityMapper;
    private final IEstudianteValidator estudianteValidator;

    @Override
    @Transactional
    public Estudiante crearEstudiante(Estudiante estudiante, String rolString) {
        log.info("Procesando solicitud de registro para estudiante con correo institucional: {}", estudiante.getCorreoInstitucional());

        estudianteValidator.validar(estudiante, rolString);

        EstudianteEntity entity = estudianteEntityMapper.toEntity(estudiante);
        if (entity.getPuntosAcumulados() == null) {
            entity.setPuntosAcumulados(0);
        }
        if (entity.getRolActivo() == null && rolString != null) {
            entity.setRolActivo(rolString.trim().toUpperCase());
        }

        EstudianteEntity guardado = estudianteRepository.save(entity);
        log.info("Perfil de estudiante registrado exitosamente con ID asignado: {} y rol: {}", guardado.getId(), guardado.getRolActivo());

        return estudianteEntityMapper.toDomain(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public Estudiante obtenerPorId(String id) {
        log.info("Consultando perfil de estudiante por ID: {}", id);

        EstudianteEntity entity = estudianteRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Consulta fallida: no se encontró estudiante registrado con ID: {}", id);
                    return new RecursoNoEncontradoException("Estudiante no encontrado con ID: " + id);
                });

        return estudianteEntityMapper.toDomain(entity);
    }
}
