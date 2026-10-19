package co.edu.eci.dosw.ecifit.model.chain;

import co.edu.eci.dosw.ecifit.model.Actividad;

/**
 * Manejador base para el patrón de comportamiento Chain of Responsibility.
 * Modela un eslabón abstracto dentro del pipeline de validación e integridad anti-cheat
 * para el registro de actividades físicas.
 */
public abstract class ActividadHandler {

    private ActividadHandler siguiente;

    /**
     * Enlaza el siguiente eslabón en la cadena de responsabilidad.
     *
     * @param siguiente Próximo manejador en la secuencia.
     * @return El manejador enlazado para permitir configuración fluida (fluent API).
     */
    public ActividadHandler enlazar(ActividadHandler siguiente) {
        this.siguiente = siguiente;
        return siguiente;
    }

    /**
     * Ejecuta la validación del eslabón actual y propaga la petición al siguiente manejador si existe.
     *
     * @param actividad Actividad física bajo evaluación.
     */
    public void validar(Actividad actividad) {
        procesarValidacion(actividad);
        if (siguiente != null) {
            siguiente.validar(actividad);
        }
    }

    /**
     * Lógica de verificación específica encapsulada por el eslabón concreto.
     *
     * @param actividad Actividad física bajo evaluación.
     */
    protected abstract void procesarValidacion(Actividad actividad);

    public ActividadHandler getSiguiente() {
        return siguiente;
    }
}
