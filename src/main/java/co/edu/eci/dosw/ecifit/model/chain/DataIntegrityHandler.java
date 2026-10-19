package co.edu.eci.dosw.ecifit.model.chain;

import co.edu.eci.dosw.ecifit.exception.ReglaDeNegocioException;
import co.edu.eci.dosw.ecifit.model.Actividad;

/**
 * Primer eslabón de la cadena de responsabilidad:
 * Verifica la integridad básica de la petición y la presencia obligatoria de los campos esenciales.
 */
public class DataIntegrityHandler extends ActividadHandler {

    @Override
    protected void procesarValidacion(Actividad actividad) {
        if (actividad == null) {
            throw new ReglaDeNegocioException("Los datos de la actividad no pueden ser nulos.");
        }
        if (actividad.getTipo() == null || actividad.getTipo().isBlank()) {
            throw new ReglaDeNegocioException("El tipo de actividad física es obligatorio.");
        }
        if (!Boolean.TRUE.equals(actividad.esValida())) {
            throw new ReglaDeNegocioException("Actividad inválida: duración mínima 10 min e intensidad entre 1 y 10");
        }
    }
}
