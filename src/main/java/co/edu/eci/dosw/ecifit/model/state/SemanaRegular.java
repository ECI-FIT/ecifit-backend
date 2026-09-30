package co.edu.eci.dosw.ecifit.model.state;

/**
 * Estado concreto que representa una semana regular de clases.
 * Durante este período el juego opera en modo estándar: se permiten ataques a torres y
 * el multiplicador de puntos es base (1.0).
 */
public class SemanaRegular implements EstadoTemporada {

    @Override
    public Boolean permiteAtaqueTorres() {
        return true;
    }

    @Override
    public Double obtenerMultiplicador() {
        return 1.0;
    }

    @Override
    public String toString() {
        return "SemanaRegular";
    }
}
