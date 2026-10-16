package co.edu.eci.dosw.ecifit.model;

import java.time.LocalDateTime;

public class AsistenciaGym {

    public static final int PUNTOS_ASISTENCIA = 20;

    private String id;
    private String estudianteId;
    private String entrenadorId;
    private LocalDateTime fechaHora;
    private final Integer puntosOtorgados = PUNTOS_ASISTENCIA;

    public AsistenciaGym() {
    }

    public AsistenciaGym(String id, String estudianteId, String entrenadorId, LocalDateTime fechaHora) {
        this.id = id;
        this.estudianteId = estudianteId;
        this.entrenadorId = entrenadorId;
        this.fechaHora = fechaHora;
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

    public String getEntrenadorId() {
        return entrenadorId;
    }

    public void setEntrenadorId(String entrenadorId) {
        this.entrenadorId = entrenadorId;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public Integer getPuntosOtorgados() {
        return puntosOtorgados;
    }
}
