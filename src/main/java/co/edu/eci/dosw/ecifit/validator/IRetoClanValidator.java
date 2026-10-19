package co.edu.eci.dosw.ecifit.validator;

import co.edu.eci.dosw.ecifit.model.RetoClan;

public interface IRetoClanValidator {

    void validarPublicacionReto(RetoClan reto);

    void validarEvaluacionReto(RetoClan reto);

    void validarDueloClanes(String clanId1, String clanId2);
}
