package co.edu.eci.dosw.ecifit.model.chain;

import co.edu.eci.dosw.ecifit.exception.ReglaDeNegocioException;
import co.edu.eci.dosw.ecifit.model.Actividad;

/**
 * Segundo eslabón de la cadena de responsabilidad (Motor Anti-Cheat):
 * Comprueba los límites fisiológicos humanos razonables para prevenir registros fraudulentos
 * (por ejemplo: sesiones únicas sobrehumanas superiores a 5 horas continuas o intensidad desproporcionada).
 */
public class PhysiologicalLimitsHandler extends ActividadHandler {

    public static final int MAX_DURACION_MINUTOS = 300; // Máximo 5 horas continuas por registro
    public static final int MIN_DURACION_MINUTOS = 10;
    public static final int MAX_INTENSIDAD = 10;
    public static final int MIN_INTENSIDAD = 1;

    @Override
    protected void procesarValidacion(Actividad actividad) {
        if (actividad.getDuracionMinutos() > MAX_DURACION_MINUTOS) {
            throw new ReglaDeNegocioException(
                    "Límites fisiológicos excedidos: la duración máxima por sesión no puede superar los 300 minutos");
        }
        if (actividad.getDuracionMinutos() < MIN_DURACION_MINUTOS) {
            throw new ReglaDeNegocioException("Actividad inválida: duración mínima 10 min e intensidad entre 1 y 10");
        }
        if (actividad.getIntensidad() < MIN_INTENSIDAD || actividad.getIntensidad() > MAX_INTENSIDAD) {
            throw new ReglaDeNegocioException("Actividad inválida: duración mínima 10 min e intensidad entre 1 y 10");
        }
    }
}
