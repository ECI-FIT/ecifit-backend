package co.edu.eci.dosw.ecifit.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Entidad de dominio que representa un reto colectivo semanal para clanes (RF-34 y RF-36).
 * Define una meta de puntos colectivos, vigencia temporal y recompensa al cumplimiento.
 */
public class RetoClan {

    private String id;
    private String clanId;
    private String descripcion;
    private Integer objetivoPuntos;
    private Integer progresoActual;
    private Integer recompensa;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Boolean completado;

    public RetoClan() {
        this.progresoActual = 0;
        this.completado = false;
    }

    public RetoClan(String id, String clanId, String descripcion, Integer objetivoPuntos,
                    Integer progresoActual, Integer recompensa, LocalDate fechaInicio,
                    LocalDate fechaFin, Boolean completado) {
        this.id = id;
        this.clanId = clanId;
        this.descripcion = descripcion;
        this.objetivoPuntos = objetivoPuntos;
        this.progresoActual = (progresoActual != null) ? progresoActual : 0;
        this.recompensa = recompensa;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.completado = (completado != null) ? completado : false;
    }

    /**
     * Evalúa si el reto está dentro de la ventana de vigencia.
     */
    public boolean estaVigente(LocalDate fecha) {
        if (fecha == null || fechaInicio == null || fechaFin == null) {
            return false;
        }
        return !fecha.isBefore(fechaInicio) && !fecha.isAfter(fechaFin);
    }

    /**
     * Evalúa si los puntos alcanzados superan el objetivo.
     */
    public boolean evaluarSuperacion(int puntos) {
        this.progresoActual = puntos;
        if (objetivoPuntos != null && puntos >= objetivoPuntos) {
            this.completado = true;
            return true;
        }
        return false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClanId() {
        return clanId;
    }

    public void setClanId(String clanId) {
        this.clanId = clanId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getObjetivoPuntos() {
        return objetivoPuntos;
    }

    public void setObjetivoPuntos(Integer objetivoPuntos) {
        this.objetivoPuntos = objetivoPuntos;
    }

    public Integer getProgresoActual() {
        return progresoActual;
    }

    public void setProgresoActual(Integer progresoActual) {
        this.progresoActual = progresoActual;
    }

    public Integer getRecompensa() {
        return recompensa;
    }

    public void setRecompensa(Integer recompensa) {
        this.recompensa = recompensa;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Boolean getCompletado() {
        return completado;
    }

    public void setCompletado(Boolean completado) {
        this.completado = completado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RetoClan retoClan)) return false;
        return Objects.equals(id, retoClan.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
