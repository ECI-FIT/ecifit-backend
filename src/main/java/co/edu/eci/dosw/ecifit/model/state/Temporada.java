package co.edu.eci.dosw.ecifit.model.state;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Contexto del patrón State que representa una temporada académica en ECI FIT.
 * Delega en {@link EstadoTemporada} el comportamiento que varía dinámicamente según el calendario.
 */
public class Temporada {

    private String semestreId;
    private EstadoTemporada estadoActual;
    private final List<RangoFechas> periodosParciales;

    public Temporada(String semestreId) {
        this(semestreId, new SemanaRegular());
    }

    public Temporada(String semestreId, EstadoTemporada estadoInicial) {
        this.semestreId = semestreId;
        this.estadoActual = Objects.requireNonNullElseGet(estadoInicial, SemanaRegular::new);
        this.periodosParciales = new ArrayList<>();
    }

    /**
     * Registra un periodo de parciales para evaluar cambios automáticos de estado.
     *
     * @param inicio Fecha inicial del periodo de parciales.
     * @param fin    Fecha final del periodo de parciales.
     */
    public void registrarPeriodoParciales(LocalDate inicio, LocalDate fin) {
        if (inicio != null && fin != null && !inicio.isAfter(fin)) {
            this.periodosParciales.add(new RangoFechas(inicio, fin));
        }
    }

    /**
     * Evalúa la fecha actual/proporcionada y alterna el estado entre SemanaRegular y SemanaParciales.
     * Si la fecha cae dentro de un período de parciales registrado, transiciona a {@link SemanaParciales};
     * en caso contrario, transiciona a {@link SemanaRegular}. Si no hay períodos explícitos, alterna el estado.
     *
     * @param fecha Fecha a evaluar.
     */
    public void evaluarCambioEstado(LocalDate fecha) {
        if (fecha == null) {
            return;
        }

        if (!periodosParciales.isEmpty()) {
            boolean esParcial = periodosParciales.stream().anyMatch(r -> r.contiene(fecha));
            if (esParcial && !(estadoActual instanceof SemanaParciales)) {
                this.estadoActual = new SemanaParciales();
            } else if (!esParcial && !(estadoActual instanceof SemanaRegular)) {
                this.estadoActual = new SemanaRegular();
            }
        } else {
            // Lógica de alternancia si no hay calendario explícito cargado
            if (this.estadoActual instanceof SemanaRegular) {
                this.estadoActual = new SemanaParciales();
            } else {
                this.estadoActual = new SemanaRegular();
            }
        }
    }

    public Boolean permiteAtaqueTorres() {
        return estadoActual != null && estadoActual.permiteAtaqueTorres();
    }

    public Double obtenerMultiplicador() {
        return estadoActual != null ? estadoActual.obtenerMultiplicador() : 1.0;
    }

    public String getSemestreId() {
        return semestreId;
    }

    public void setSemestreId(String semestreId) {
        this.semestreId = semestreId;
    }

    public EstadoTemporada getEstadoActual() {
        return estadoActual;
    }

    public void setEstadoActual(EstadoTemporada estadoActual) {
        this.estadoActual = estadoActual;
    }

    /**
     * Registro auxiliar inmutable para definir rangos de fechas de calendario en el dominio.
     */
    public record RangoFechas(LocalDate inicio, LocalDate fin) {
        public boolean contiene(LocalDate fecha) {
            return (fecha.isEqual(inicio) || fecha.isAfter(inicio)) &&
                   (fecha.isEqual(fin) || fecha.isBefore(fin));
        }
    }
}
