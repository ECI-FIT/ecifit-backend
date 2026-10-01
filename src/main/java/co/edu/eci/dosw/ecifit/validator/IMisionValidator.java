package co.edu.eci.dosw.ecifit.validator;

import co.edu.eci.dosw.ecifit.model.factory.Mision;

public interface IMisionValidator {

    void validarNoCompletada(Mision mision);

    void validarLimiteMisionesActivas(String estudianteId);
}