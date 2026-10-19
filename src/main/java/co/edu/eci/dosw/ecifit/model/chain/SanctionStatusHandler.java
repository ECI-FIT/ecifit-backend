package co.edu.eci.dosw.ecifit.model.chain;

import co.edu.eci.dosw.ecifit.exception.ReglaDeNegocioException;
import co.edu.eci.dosw.ecifit.model.Actividad;

import java.time.LocalDateTime;

/**
 * Tercer eslabón de la cadena de responsabilidad:
 * Valida la consistencia temporal y el estado de penalización de la sesión deportiva
 * (por ejemplo: fechas futuras absurdas o marcas temporales incongruentes).
 */
public class SanctionStatusHandler extends ActividadHandler {

    @Override
    protected void procesarValidacion(Actividad actividad) {
        if (actividad.getFecha() != null && actividad.getFecha().isAfter(LocalDateTime.now().plusHours(1))) {
            throw new ReglaDeNegocioException("No es posible registrar entrenamientos con fechas u horas futuras.");
        }
    }
}
