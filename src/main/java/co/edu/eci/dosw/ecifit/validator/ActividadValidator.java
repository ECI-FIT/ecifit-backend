package co.edu.eci.dosw.ecifit.validator;

import co.edu.eci.dosw.ecifit.model.Actividad;
import co.edu.eci.dosw.ecifit.model.chain.ActividadHandler;
import co.edu.eci.dosw.ecifit.model.chain.DataIntegrityHandler;
import co.edu.eci.dosw.ecifit.model.chain.PhysiologicalLimitsHandler;
import co.edu.eci.dosw.ecifit.model.chain.SanctionStatusHandler;
import org.springframework.stereotype.Component;

/**
 * Validador de dominio para actividades físicas.
 * Orquesta la validación de integridad y prevención de fraude (Anti-Cheat)
 * implementando el patrón de comportamiento Chain of Responsibility.
 */
@Component
public class ActividadValidator implements IActividadValidator {

    private final ActividadHandler validationChain;

    public ActividadValidator() {
        ActividadHandler integrity = new DataIntegrityHandler();
        ActividadHandler limits = new PhysiologicalLimitsHandler();
        ActividadHandler sanctions = new SanctionStatusHandler();

        integrity.enlazar(limits).enlazar(sanctions);
        this.validationChain = integrity;
    }

    public ActividadValidator(ActividadHandler customChain) {
        this.validationChain = customChain;
    }

    @Override
    public void validar(Actividad actividad) {
        aplicarFiltroAntiCheat(actividad);
    }

    public void aplicarFiltroAntiCheat(Actividad actividad) {
        validationChain.validar(actividad);
    }
}
