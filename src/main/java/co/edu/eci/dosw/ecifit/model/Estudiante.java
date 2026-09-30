package co.edu.eci.dosw.ecifit.model;

import co.edu.eci.dosw.ecifit.model.strategy.RolTemporada;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Entidad de dominio que representa a un estudiante de la Escuela Colombiana de Ingeniería en ECI FIT.
 * Es el actor principal que asume roles de temporada, acumula puntos mediante actividad física
 * y participa en dinámicas grupales como clanes.
 */
public class Estudiante {

    private String id;
    private String nombre;
    private String correoInstitucional;
    private Integer puntosAcumulados;
    private RolTemporada rolActivo;
    private final List<Actividad> actividadesRealizadas;
    private String clanId;

    public Estudiante() {
        this.puntosAcumulados = 0;
        this.actividadesRealizadas = new ArrayList<>();
    }

    public Estudiante(String id, String nombre, String correoInstitucional, RolTemporada rolActivo) {
        this.id = id;
        this.nombre = nombre;
        this.correoInstitucional = correoInstitucional;
        this.puntosAcumulados = 0;
        this.rolActivo = rolActivo;
        this.actividadesRealizadas = new ArrayList<>();
    }

    public Estudiante(String id, String nombre, String correoInstitucional, Integer puntosAcumulados,
                      RolTemporada rolActivo, String clanId) {
        this.id = id;
        this.nombre = nombre;
        this.correoInstitucional = correoInstitucional;
        this.puntosAcumulados = (puntosAcumulados != null) ? puntosAcumulados : 0;
        this.rolActivo = rolActivo;
        this.clanId = clanId;
        this.actividadesRealizadas = new ArrayList<>();
    }

    /**
     * Registra una actividad física para el estudiante.
     * Valida que la actividad cumpla con los requisitos mínimos; si es válida, calcula los puntos generados
     * a través de la estrategia del rol activo, actualiza el puntaje acumulado, almacena la actividad
     * en el historial y dispara las notificaciones a los observadores suscritos a la actividad.
     *
     * @param a Actividad física completada.
     * @throws IllegalArgumentException si la actividad es nula o no cumple los criterios de validez.
     */
    public void registrarActividad(Actividad a) {
        if (a == null || !Boolean.TRUE.equals(a.esValida())) {
            throw new IllegalArgumentException("La actividad no es válida para ser registrada.");
        }

        int puntosGenerados = (rolActivo != null)
                ? rolActivo.calcularPuntosBase(a.getDuracionMinutos(), a.getIntensidad())
                : (a.getDuracionMinutos() * a.getIntensidad());

        this.puntosAcumulados = (this.puntosAcumulados != null ? this.puntosAcumulados : 0) + puntosGenerados;
        this.actividadesRealizadas.add(a);

        // Notifica a los observadores suscritos a la actividad
        a.notificarObservadores(puntosGenerados, this);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreoInstitucional() {
        return correoInstitucional;
    }

    public void setCorreoInstitucional(String correoInstitucional) {
        this.correoInstitucional = correoInstitucional;
    }

    public Integer getPuntosAcumulados() {
        return puntosAcumulados;
    }

    public void setPuntosAcumulados(Integer puntosAcumulados) {
        this.puntosAcumulados = puntosAcumulados;
    }

    public RolTemporada getRolActivo() {
        return rolActivo;
    }

    public void setRolActivo(RolTemporada rolActivo) {
        this.rolActivo = rolActivo;
    }

    public List<Actividad> getActividadesRealizadas() {
        return Collections.unmodifiableList(actividadesRealizadas);
    }

    public String getClanId() {
        return clanId;
    }

    public void setClanId(String clanId) {
        this.clanId = clanId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Estudiante that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Estudiante{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", puntosAcumulados=" + puntosAcumulados +
                ", rolActivo=" + (rolActivo != null ? rolActivo.getNombreRol() : "Ninguno") +
                ", clanId='" + clanId + '\'' +
                '}';
    }
}
