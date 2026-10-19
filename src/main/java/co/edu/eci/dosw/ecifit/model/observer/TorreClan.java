package co.edu.eci.dosw.ecifit.model.observer;

import co.edu.eci.dosw.ecifit.model.Clan;
import co.edu.eci.dosw.ecifit.model.Estudiante;

/**
 * Observador concreto que representa la torre defensiva de un clan en la guerra de clanes.
 * Reacciona al registro de actividades de estudiantes de clanes rivales, aplicando el daño
 * directamente sobre el Clan de dominio (unica fuente de verdad de la salud de la torre).
 */
public class TorreClan implements ActividadObserver {

    private final Clan clanDefensor;

    public TorreClan(Clan clanDefensor) {
        this.clanDefensor = clanDefensor;
    }

    @Override
    public void onActividadRegistrada(Integer puntosGenerados, Estudiante e) {
        if (e == null || puntosGenerados == null || puntosGenerados <= 0) {
            return;
        }

        // Si el estudiante pertenece a un clan distinto al defensor, es un ataque rival
        if (e.getClanId() != null && !e.getClanId().equalsIgnoreCase(clanDefensor.getId())) {
            clanDefensor.recibirDano(puntosGenerados);
        }
    }

    public boolean estaDestruida() {
        return clanDefensor.getSaludTorre() != null && clanDefensor.getSaludTorre() == 0;
    }

    public Clan getClanDefensor() {
        return clanDefensor;
    }
}
