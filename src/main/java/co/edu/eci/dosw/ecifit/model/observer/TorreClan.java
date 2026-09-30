package co.edu.eci.dosw.ecifit.model.observer;

import co.edu.eci.dosw.ecifit.model.Estudiante;

/**
 * Observador concreto que representa una torre defensiva de un clan en la guerra de clanes.
 * Reacciona al registro de actividades de estudiantes pertenecientes a clanes rivales,
 * sufriendo daño proporcional a los puntos generados por el entrenamiento.
 */
public class TorreClan implements ActividadObserver {

    private Integer saludHp;
    private final String clanDefensorId;

    public TorreClan(Integer saludHpInicial, String clanDefensorId) {
        this.saludHp = (saludHpInicial != null && saludHpInicial > 0) ? saludHpInicial : 1000;
        this.clanDefensorId = clanDefensorId;
    }

    /**
     * Procesa la actividad registrada. Si el estudiante atacante pertenece a un clan rival
     * (distinto a {@link #clanDefensorId}), la torre recibe daño igual a los puntos generados.
     *
     * @param puntosGenerados Puntos producidos por la actividad física.
     * @param e               Estudiante que completó la actividad.
     */
    @Override
    public void onActividadRegistrada(Integer puntosGenerados, Estudiante e) {
        if (e == null || puntosGenerados == null || puntosGenerados <= 0) {
            return;
        }

        // Si el estudiante tiene un clan asignado y no es el clan defensor de esta torre, es un ataque rival
        if (e.getClanId() != null && !e.getClanId().equalsIgnoreCase(this.clanDefensorId)) {
            recibirDano(puntosGenerados);
        }
    }

    /**
     * Aplica daño a la torre reduciendo sus puntos de salud sin permitir que caiga por debajo de cero.
     *
     * @param cantidad Cantidad de puntos de vida a restar.
     */
    public void recibirDano(Integer cantidad) {
        if (cantidad == null || cantidad <= 0) {
            return;
        }
        int saludActual = (this.saludHp != null) ? this.saludHp : 0;
        this.saludHp = Math.max(0, saludActual - cantidad);
    }

    /**
     * Indica si la torre ha sido derribada por completo.
     *
     * @return true si los puntos de vida son 0.
     */
    public boolean estaDestruida() {
        return this.saludHp != null && this.saludHp == 0;
    }

    public Integer getSaludHp() {
        return saludHp;
    }

    public void setSaludHp(Integer saludHp) {
        this.saludHp = (saludHp != null) ? Math.max(0, saludHp) : 0;
    }

    public String getClanDefensorId() {
        return clanDefensorId;
    }

    @Override
    public String toString() {
        return "TorreClan{" +
                "clanDefensorId='" + clanDefensorId + '\'' +
                ", saludHp=" + saludHp +
                ", destruida=" + estaDestruida() +
                '}';
    }
}
