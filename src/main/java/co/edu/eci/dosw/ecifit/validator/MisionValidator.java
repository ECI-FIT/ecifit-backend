package co.edu.eci.dosw.ecifit.validator;

import co.edu.eci.dosw.ecifit.exception.EstadoInvalidoException;
import co.edu.eci.dosw.ecifit.exception.ReglaDeNegocioException;
import co.edu.eci.dosw.ecifit.model.factory.Mision;
import co.edu.eci.dosw.ecifit.repository.MisionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MisionValidator implements IMisionValidator {

    private static final int LIMITE_MISIONES_ACTIVAS = 3;

    private final MisionRepository misionRepository;

    @Override
    public void validarNoCompletada(Mision mision) {
        if (Boolean.TRUE.equals(mision.getCompletada())) {
            throw new EstadoInvalidoException("La misión '" + mision.getId() + "' ya fue completada");
        }
    }

    @Override
    public void validarLimiteMisionesActivas(String estudianteId) {
        long activas = misionRepository.findByEstudianteId(estudianteId).stream()
                .filter(m -> !m.isCompletada())
                .count();
        if (activas >= LIMITE_MISIONES_ACTIVAS) {
            throw new ReglaDeNegocioException("El estudiante ya tiene " + LIMITE_MISIONES_ACTIVAS
                    + " misiones activas simultáneamente");
        }
    }
}