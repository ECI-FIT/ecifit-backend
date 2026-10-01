package co.edu.eci.dosw.ecifit.validator;

import co.edu.eci.dosw.ecifit.exception.ReglaDeNegocioException;
import co.edu.eci.dosw.ecifit.model.Actividad;
import org.springframework.stereotype.Component;

@Component
public class ActividadValidator implements IActividadValidator {

    @Override
    public void validar(Actividad actividad) {
        aplicarFiltroAntiCheat(actividad);
    }

    public void aplicarFiltroAntiCheat(Actividad actividad) {
        if (actividad == null || !Boolean.TRUE.equals(actividad.esValida())) {
            throw new ReglaDeNegocioException("Actividad inválida: duración mínima 10 min e intensidad entre 1 y 10");
        }
    }
}
