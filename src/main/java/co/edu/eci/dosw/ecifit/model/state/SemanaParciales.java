package co.edu.eci.dosw.ecifit.model.state;

/**
 * Estado concreto que representa semanas de exámenes parciales en la universidad.
 * Durante este periodo, se restringen los ataques a torres para reducir la distracción académica,
 * y se recompensa el ejercicio con un multiplicador de 1.5 como incentivo antiestrés.
 */
public class SemanaParciales implements EstadoTemporada {

    @Override
    public Boolean permiteAtaqueTorres() {
        return false;
    }

    @Override
    public Double obtenerMultiplicador() {
        return 1.5;
    }

    @Override
    public String toString() {
        return "SemanaParciales";
    }
}
