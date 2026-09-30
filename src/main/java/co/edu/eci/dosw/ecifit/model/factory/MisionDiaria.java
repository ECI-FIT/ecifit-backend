package co.edu.eci.dosw.ecifit.model.factory;

import co.edu.eci.dosw.ecifit.model.Actividad;

/**
 * Misión de periodicidad diaria enfocada en objetivos de corto plazo,
 * como completar un entrenamiento de duración mínima en una sola sesión diaria.
 */
public class MisionDiaria extends Mision {

    private Integer duracionMinimaObjetivo;

    public MisionDiaria() {
        super();
        this.duracionMinimaObjetivo = 30; // 30 minutos por defecto
    }

    public MisionDiaria(String id, String descripcion, Integer recompensa, Integer duracionMinimaObjetivo) {
        super(id, descripcion, recompensa);
        this.duracionMinimaObjetivo = (duracionMinimaObjetivo != null && duracionMinimaObjetivo > 0)
                ? duracionMinimaObjetivo
                : 30;
    }

    /**
     * Evalúa si la actividad cumple la duración diaria objetivo requerida.
     *
     * @param a Actividad física registrada.
     * @return true si la actividad es válida y cumple con la duración mínima diaria.
     */
    @Override
    public Boolean verificarCumplimiento(Actividad a) {
        if (Boolean.TRUE.equals(getCompletada())) {
            return true;
        }

        if (a != null && Boolean.TRUE.equals(a.esValida())) {
            if (a.getDuracionMinutos() != null && a.getDuracionMinutos() >= duracionMinimaObjetivo) {
                setCompletada(true);
                return true;
            }
        }
        return false;
    }

    public Integer getDuracionMinimaObjetivo() {
        return duracionMinimaObjetivo;
    }

    public void setDuracionMinimaObjetivo(Integer duracionMinimaObjetivo) {
        this.duracionMinimaObjetivo = duracionMinimaObjetivo;
    }
}
