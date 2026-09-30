package co.edu.eci.dosw.ecifit.validator;

import co.edu.eci.dosw.ecifit.model.Clan;

public interface IClanValidator {

    void validarNombreUnico(String nombre);

    void validarCupoDisponible(Clan clan);
}