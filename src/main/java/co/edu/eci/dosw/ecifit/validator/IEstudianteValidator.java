package co.edu.eci.dosw.ecifit.validator;

import co.edu.eci.dosw.ecifit.model.Estudiante;

public interface IEstudianteValidator {

    void validar(Estudiante estudiante, String rolString);

    void validar(Estudiante estudiante);
}
