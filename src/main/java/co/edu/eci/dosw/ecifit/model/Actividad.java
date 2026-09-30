package co.edu.eci.dosw.ecifit.model;

import co.edu.eci.dosw.ecifit.model.observer.ActividadObserver;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Entidad de dominio que representa una sesión de actividad física registrada por un estudiante.
 * Actúa además como Sujeto (Subject / Observable) en el patrón Observer, notificando
 * a los observadores registrados cuando la actividad es validada y procesada.
 */
public class Actividad {

    private String id;
    private String tipo;
    private Integer duracionMinutos;
    private Integer intensidad;
    private LocalDateTime fecha;
    private final List<ActividadObserver> observadores;

    public Actividad() {
        this.observadores = new ArrayList<>();
        this.fecha = LocalDateTime.now();
    }

    public Actividad(String id, String tipo, Integer duracionMinutos, Integer intensidad, LocalDateTime fecha) {
        this.id = id;
        this.tipo = tipo;
        this.duracionMinutos = duracionMinutos;
        this.intensidad = intensidad;
        this.fecha = (fecha != null) ? fecha : LocalDateTime.now();
        this.observadores = new ArrayList<>();
    }

    /**
     * Valida que la actividad cumpla con los estándares mínimos de salud y consistencia.
     *
     * @return true si la duración es de al menos 10 minutos y la intensidad está en escala de 1 a 10.
     */
    public Boolean esValida() {
        return duracionMinutos != null && duracionMinutos >= 10 &&
               intensidad != null && intensidad >= 1 && intensidad <= 10;
    }

    /**
     * Registra un nuevo observador interesado en los eventos de esta actividad.
     *
     * @param obs Instancia de {@link ActividadObserver} a suscribir.
     */
    public void agregarObservador(ActividadObserver obs) {
        if (obs != null && !observadores.contains(obs)) {
            this.observadores.add(obs);
        }
    }

    /**
     * Remueve un observador previamente registrado.
     *
     * @param obs Instancia de {@link ActividadObserver} a desuscribir.
     */
    public void removerObservador(ActividadObserver obs) {
        this.observadores.remove(obs);
    }

    /**
     * Notifica a todos los observadores registrados sobre la actividad y los puntos concedidos.
     *
     * @param puntosGenerados Puntos obtenidos por la actividad.
     * @param e               Estudiante que realizó la actividad.
     */
    public void notificarObservadores(Integer puntosGenerados, Estudiante e) {
        for (ActividadObserver obs : new ArrayList<>(this.observadores)) {
            obs.onActividadRegistrada(puntosGenerados, e);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(Integer duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    public Integer getIntensidad() {
        return intensidad;
    }

    public void setIntensidad(Integer intensidad) {
        this.intensidad = intensidad;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public List<ActividadObserver> getObservadores() {
        return Collections.unmodifiableList(observadores);
    }
}
