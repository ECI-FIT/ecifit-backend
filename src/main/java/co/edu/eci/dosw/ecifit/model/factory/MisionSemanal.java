package co.edu.eci.dosw.ecifit.model.factory;

import co.edu.eci.dosw.ecifit.model.Actividad;

/**
 * Misión de periodicidad semanal que exige acumular una cantidad agregada de minutos
 * de actividad a lo largo de los días de la semana.
 */
public class MisionSemanal extends Mision {

    private Integer duracionAcumuladaObjetivo;
    private Integer progresoActualMinutos;

    public MisionSemanal() {
        super();
        this.duracionAcumuladaObjetivo = 150; // 150 minutos semanales (recomendación OMS)
        this.progresoActualMinutos = 0;
    }

    public MisionSemanal(String id, String descripcion, Integer recompensa, Integer duracionAcumuladaObjetivo) {
        super(id, descripcion, recompensa);
        this.duracionAcumuladaObjetivo = (duracionAcumuladaObjetivo != null && duracionAcumuladaObjetivo > 0)
                ? duracionAcumuladaObjetivo
                : 150;
        this.progresoActualMinutos = 0;
    }

    /**
     * Acumula la duración de la actividad física al progreso semanal y verifica si se alcanzó la meta.
     *
     * @param a Actividad física registrada.
     * @return true si la misión ya estaba o ha sido completada con esta actividad.
     */
    @Override
    public Boolean verificarCumplimiento(Actividad a) {
        if (Boolean.TRUE.equals(getCompletada())) {
            return true;
        }

        if (a != null && Boolean.TRUE.equals(a.esValida())) {
            if (a.getDuracionMinutos() != null && a.getDuracionMinutos() > 0) {
                this.progresoActualMinutos = (this.progresoActualMinutos != null ? this.progresoActualMinutos : 0)
                        + a.getDuracionMinutos();

                if (this.progresoActualMinutos >= this.duracionAcumuladaObjetivo) {
                    setCompletada(true);
                    return true;
                }
            }
        }
        return false;
    }

    public Integer getDuracionAcumuladaObjetivo() {
        return duracionAcumuladaObjetivo;
    }

    public void setDuracionAcumuladaObjetivo(Integer duracionAcumuladaObjetivo) {
        this.duracionAcumuladaObjetivo = duracionAcumuladaObjetivo;
    }

    public Integer getProgresoActualMinutos() {
        return progresoActualMinutos;
    }

    public void setProgresoActualMinutos(Integer progresoActualMinutos) {
        this.progresoActualMinutos = progresoActualMinutos;
    }
}
