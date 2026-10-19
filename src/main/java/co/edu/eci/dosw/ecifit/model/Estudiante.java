package co.edu.eci.dosw.ecifit.model;

import co.edu.eci.dosw.ecifit.model.strategy.RolTemporada;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Entidad de dominio que representa a un estudiante de la Escuela Colombiana de Ingeniería en ECI FIT.
 * Es el actor principal que asume roles de temporada, acumula puntos mediante actividad física
 * y participa en dinámicas grupales como clanes.
 */
public class Estudiante {

    private static final int DIAS_RACHA_BONO = 7;
    private static final int BONO_RACHA_SIETE_DIAS = 200;

    private String id;
    private String nombre;
    private String correoInstitucional;
    private Integer puntosAcumulados;
    private RolTemporada rolActivo;
    private final List<Actividad> actividadesRealizadas;
    private String clanId;
    private LocalDate fechaUltimoBonoRacha;

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
        evaluarRacha(actividadesRealizadas);

        // Notifica a los observadores suscritos a la actividad
        a.notificarObservadores(puntosGenerados, this);
    }

    /**
     * Otorga la bonificación una vez por cada periodo superior a siete días
     * cuando el historial contiene actividad en siete días calendario consecutivos.
     *
     * @param historial actividades recientes del estudiante
     * @return true si se otorgó la bonificación
     */
    public boolean evaluarRacha(List<Actividad> historial) {
        if (historial == null || historial.size() < DIAS_RACHA_BONO) {
            return false;
        }

        LocalDate hoy = LocalDate.now();
        if (fechaUltimoBonoRacha != null
                && ChronoUnit.DAYS.between(fechaUltimoBonoRacha, hoy) <= DIAS_RACHA_BONO) {
            return false;
        }

        Set<LocalDate> fechasConActividad = new HashSet<>();
        for (Actividad actividad : historial) {
            if (actividad != null && actividad.getFecha() != null) {
                fechasConActividad.add(actividad.getFecha().toLocalDate());
            }
        }

        boolean tieneSieteDiasConsecutivos = fechasConActividad.stream().anyMatch(fechaReferencia -> {
            for (int i = 1; i < DIAS_RACHA_BONO; i++) {
                if (!fechasConActividad.contains(fechaReferencia.minusDays(i))) {
                    return false;
                }
            }
            return true;
        });

        if (!tieneSieteDiasConsecutivos) {
            return false;
        }

        this.puntosAcumulados = (this.puntosAcumulados != null ? this.puntosAcumulados : 0)
                + BONO_RACHA_SIETE_DIAS;
        this.fechaUltimoBonoRacha = hoy;
        return true;
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

    public LocalDate getFechaUltimoBonoRacha() {
        return fechaUltimoBonoRacha;
    }

    public void setFechaUltimoBonoRacha(LocalDate fechaUltimoBonoRacha) {
        this.fechaUltimoBonoRacha = fechaUltimoBonoRacha;
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