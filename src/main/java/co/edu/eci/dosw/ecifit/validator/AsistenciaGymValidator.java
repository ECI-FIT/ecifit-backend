package co.edu.eci.dosw.ecifit.validator;

import co.edu.eci.dosw.ecifit.exception.ReglaDeNegocioException;
import org.springframework.stereotype.Component;

@Component
public class AsistenciaGymValidator implements IAsistenciaGymValidator {

    @Override
    public void validar(String estudianteId, String entrenadorId) {
        if (estudianteId == null || estudianteId.isBlank()) {
            throw new ReglaDeNegocioException("El ID del estudiante es obligatorio.");
        }
        if (entrenadorId == null || entrenadorId.isBlank()) {
            throw new ReglaDeNegocioException("El ID del entrenador es obligatorio.");
        }
    }
}
