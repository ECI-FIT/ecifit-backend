package co.edu.eci.dosw.ecifit.model.factory;

import co.edu.eci.dosw.ecifit.model.Actividad;

/**
 * Clase base abstracta para las misiones de gamificación en ECI FIT.
 * Define la estructura común para recompensar a los estudiantes tras cumplir metas deportivas.
 */
public abstract class Mision {

    private String id;
    private String descripcion;
    private Integer recompensa;
    private Boolean completada;
    private String estudianteId;

    protected Mision() {
        this.completada = false;
        this.recompensa = 0;
    }

    protected Mision(String id, String descripcion, Integer recompensa) {
        this.id = id;
        this.descripcion = descripcion;
        this.recompensa = (recompensa != null) ? recompensa : 0;
        this.completada = false;
    }

    /**
     * Verifica si una actividad física contribuye al cumplimiento de la misión y actualiza su estado.
     *
     * @param a Actividad física realizada por el estudiante.
     * @return true si la misión ha sido cumplida (o completada tras esta actividad), false en caso contrario.
     */
    public abstract Boolean verificarCumplimiento(Actividad a);

    /**
     * Evalúa el estado de cumplimiento intrínseco de la misión sin necesidad de una nueva actividad puntual.
     * Por defecto retorna si la misión ya cumplió sus criterios o condiciones requeridas.
     *
     * @return true si la misión cumple los criterios para ser completada, false en caso contrario.
     */
    public boolean evaluarCumplimiento() {
        // Si la misión ya fue completada, no es evaluable positivamente
        if (Boolean.TRUE.equals(this.completada)) {
            return false;
        }
        // Criterio de dominio: Es completable si tiene un estudiante asignado y está en estado activo
        return this.estudianteId != null && !this.estudianteId.isBlank();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getRecompensa() {
        return recompensa;
    }

    public void setRecompensa(Integer recompensa) {
        this.recompensa = recompensa;
    }

    public Boolean getCompletada() {
        return completada;
    }

    public void setCompletada(Boolean completada) {
        this.completada = completada;
    }

    public String getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(String estudianteId) {
        this.estudianteId = estudianteId;
    }
}
