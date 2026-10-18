package co.edu.eci.dosw.ecifit.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Comida registrada por un estudiante. La IA solo aporta la evaluación nutricional;
 * los puntos se calculan aquí con reglas fijas para que no puedan ser manipulados.
 */
public class Comida {

    public static final int PUNTOS_REGISTRO = 5;
    public static final int BONO_BALANCEADA = 15;
    public static final int MAX_INTENTOS_EVALUACION = 3;
    public static final int LONGITUD_MINIMA_DESCRIPCION = 3;
    public static final int LONGITUD_MAXIMA_DESCRIPCION = 500;

    private String id;
    private String estudianteId;
    private TipoComida tipo;
    private String descripcion;
    private LocalDateTime fechaHora;
    private EstadoEvaluacion estado = EstadoEvaluacion.PENDIENTE;
    private Integer intentosEvaluacion = 0;
    private Boolean otorgaPuntos = Boolean.TRUE;
    private Boolean balanceada;
    private CalificacionComida calificacion;
    private Integer puntaje;
    private String consejo;
    private List<String> recomendaciones = new ArrayList<>();
    private Integer puntosOtorgados = 0;

    public Comida() {
    }

    public Comida(String estudianteId, TipoComida tipo, String descripcion, LocalDateTime fechaHora) {
        this.estudianteId = estudianteId;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.fechaHora = fechaHora;
    }

    /**
     * Guarda el resultado de la IA, marca la comida como evaluada y calcula los puntos:
     * {@value #PUNTOS_REGISTRO} por registrarla y {@value #BONO_BALANCEADA} adicionales si es balanceada.
     * Si la comida no otorga puntos (por ejemplo, un segundo almuerzo el mismo día), se evalúa igual pero suma 0.
     *
     * @return puntos ganados con esta evaluación
     * @throws IllegalStateException si la comida ya estaba evaluada
     */
    public int aplicarEvaluacion(EvaluacionNutricional evaluacion) {
        if (evaluacion == null) {
            throw new IllegalArgumentException("La evaluación es obligatoria.");
        }
        if (estado == EstadoEvaluacion.EVALUADA) {
            throw new IllegalStateException("La comida ya fue evaluada.");
        }

        this.balanceada = evaluacion.balanceada();
        this.calificacion = evaluacion.calificacion();
        this.puntaje = evaluacion.puntaje();
        this.consejo = evaluacion.consejo();
        this.recomendaciones = new ArrayList<>(evaluacion.recomendaciones());
        this.estado = EstadoEvaluacion.EVALUADA;

        int puntos = 0;
        if (Boolean.TRUE.equals(otorgaPuntos)) {
            puntos = PUNTOS_REGISTRO + (evaluacion.balanceada() ? BONO_BALANCEADA : 0);
        }
        this.puntosOtorgados = puntos;
        return puntos;
    }

    /**
     * Registra un intento de evaluación fallido. Al llegar a {@value #MAX_INTENTOS_EVALUACION}
     * intentos la comida pasa a estado ERROR y deja de reintentarse automáticamente.
     */
    public void registrarIntentoFallido() {
        this.intentosEvaluacion = (intentosEvaluacion != null ? intentosEvaluacion : 0) + 1;
        this.estado = intentosEvaluacion >= MAX_INTENTOS_EVALUACION
                ? EstadoEvaluacion.ERROR
                : EstadoEvaluacion.PENDIENTE;
    }

    public boolean estaEvaluada() {
        return estado == EstadoEvaluacion.EVALUADA;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(String estudianteId) {
        this.estudianteId = estudianteId;
    }

    public TipoComida getTipo() {
        return tipo;
    }

    public void setTipo(TipoComida tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public EstadoEvaluacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoEvaluacion estado) {
        this.estado = estado;
    }

    public Integer getIntentosEvaluacion() {
        return intentosEvaluacion;
    }

    public void setIntentosEvaluacion(Integer intentosEvaluacion) {
        this.intentosEvaluacion = intentosEvaluacion;
    }

    public Boolean getOtorgaPuntos() {
        return otorgaPuntos;
    }

    public void setOtorgaPuntos(Boolean otorgaPuntos) {
        this.otorgaPuntos = otorgaPuntos;
    }

    public Boolean getBalanceada() {
        return balanceada;
    }

    public void setBalanceada(Boolean balanceada) {
        this.balanceada = balanceada;
    }

    public CalificacionComida getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(CalificacionComida calificacion) {
        this.calificacion = calificacion;
    }

    public Integer getPuntaje() {
        return puntaje;
    }

    public void setPuntaje(Integer puntaje) {
        this.puntaje = puntaje;
    }

    public String getConsejo() {
        return consejo;
    }

    public void setConsejo(String consejo) {
        this.consejo = consejo;
    }

    public List<String> getRecomendaciones() {
        return recomendaciones;
    }

    public void setRecomendaciones(List<String> recomendaciones) {
        this.recomendaciones = recomendaciones != null ? new ArrayList<>(recomendaciones) : new ArrayList<>();
    }

    public Integer getPuntosOtorgados() {
        return puntosOtorgados;
    }

    public void setPuntosOtorgados(Integer puntosOtorgados) {
        this.puntosOtorgados = puntosOtorgados;
    }
}
