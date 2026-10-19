package co.edu.eci.dosw.ecifit.model.observer;

import co.edu.eci.dosw.ecifit.model.Estudiante;

/**
 * Interfaz Observer que reacciona a los eventos de registro de actividad física en la plataforma.
 * Permite desencadenar efectos colaterales (como actualización de ligas o daño a torres de clanes rivales).
 */
public interface ActividadObserver {

    /**
     * Notificación emitida cuando un estudiante registra exitosamente una actividad física válida.
     *
     * @param puntosGenerados Puntos netos obtenidos por la actividad.
     * @param e               Estudiante que completó la actividad.
     */
    void onActividadRegistrada(Integer puntosGenerados, Estudiante e);
}
