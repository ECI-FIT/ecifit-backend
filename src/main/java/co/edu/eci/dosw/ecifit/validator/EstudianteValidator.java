package co.edu.eci.dosw.ecifit.validator;

import co.edu.eci.dosw.ecifit.exception.ConflictoException;
import co.edu.eci.dosw.ecifit.exception.ReglaDeNegocioException;
import co.edu.eci.dosw.ecifit.model.Estudiante;
import co.edu.eci.dosw.ecifit.repository.EstudianteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class EstudianteValidator implements IEstudianteValidator {

    private static final String DOMINIO_INSTITUCIONAL = "@mail.escuelaing.edu.co";
    private static final Set<String> ROLES_DISPONIBLES = Set.of("TANQUE", "CORREDOR", "ESTRATEGA");

    private final EstudianteRepository estudianteRepository;

    @Override
    public void validar(Estudiante estudiante, String rolString) {
        if (estudiante == null) {
            throw new ReglaDeNegocioException("Los datos del estudiante no pueden ser nulos.");
        }

        validarCorreoInstitucional(estudiante.getCorreoInstitucional());
        validarRolTemporada(rolString);
        validarUnicidadCorreo(estudiante.getCorreoInstitucional());
    }

    @Override
    public void validar(Estudiante estudiante) {
        String rol = (estudiante != null && estudiante.getRolActivo() != null)
                ? estudiante.getRolActivo().getNombreRol().toUpperCase()
                : null;
        validar(estudiante, rol);
    }

    public void validarCorreoInstitucional(String correo) {
        if (correo == null || !correo.trim().toLowerCase().endsWith(DOMINIO_INSTITUCIONAL)) {
            throw new ReglaDeNegocioException("Solo se permite el dominio institucional @mail.escuelaing.edu.co");
        }
    }

    public void validarRolTemporada(String rol) {
        if (rol == null || !ROLES_DISPONIBLES.contains(rol.trim().toUpperCase())) {
            throw new ReglaDeNegocioException("Rol inválido: debe ser TANQUE, CORREDOR o ESTRATEGA");
        }
    }

    public void validarUnicidadCorreo(String correo) {
        if (estudianteRepository.existsByCorreoInstitucional(correo.trim().toLowerCase())) {
            throw new ConflictoException("El correo institucional ya se encuentra registrado");
        }
    }
}
