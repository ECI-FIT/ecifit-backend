package co.edu.eci.dosw.ecifit.validator;

import co.edu.eci.dosw.ecifit.exception.ReglaDeNegocioException;
import co.edu.eci.dosw.ecifit.model.Comida;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ComidaValidator implements IComidaValidator {

    private static final int HORAS_MAXIMAS_ATRAS = 24;
    private static final int MINUTOS_TOLERANCIA_FUTURO = 5;

    @Override
    public void validar(Comida comida) {
        if (comida == null) {
            throw new ReglaDeNegocioException("La comida es obligatoria.");
        }
        if (comida.getEstudianteId() == null || comida.getEstudianteId().isBlank()) {
            throw new ReglaDeNegocioException("El ID del estudiante es obligatorio.");
        }
        if (comida.getTipo() == null) {
            throw new ReglaDeNegocioException("El tipo de comida es obligatorio.");
        }
        validarDescripcion(comida.getDescripcion());
        validarFecha(comida.getFechaHora());
    }

    private void validarDescripcion(String descripcion) {
        if (descripcion == null || descripcion.isBlank()) {
            throw new ReglaDeNegocioException("La descripción de la comida es obligatoria.");
        }
        int longitud = descripcion.trim().length();
        if (longitud < Comida.LONGITUD_MINIMA_DESCRIPCION || longitud > Comida.LONGITUD_MAXIMA_DESCRIPCION) {
            throw new ReglaDeNegocioException(String.format(
                    "La descripción debe tener entre %d y %d caracteres.",
                    Comida.LONGITUD_MINIMA_DESCRIPCION, Comida.LONGITUD_MAXIMA_DESCRIPCION));
        }
    }

    private void validarFecha(LocalDateTime fechaHora) {
        if (fechaHora == null) {
            throw new ReglaDeNegocioException("La fecha y hora de la comida es obligatoria.");
        }
        LocalDateTime ahora = LocalDateTime.now();
        if (fechaHora.isAfter(ahora.plusMinutes(MINUTOS_TOLERANCIA_FUTURO))) {
            throw new ReglaDeNegocioException("No se puede registrar una comida en el futuro.");
        }
        if (fechaHora.isBefore(ahora.minusHours(HORAS_MAXIMAS_ATRAS))) {
            throw new ReglaDeNegocioException(
                    "Solo se pueden registrar comidas de las últimas " + HORAS_MAXIMAS_ATRAS + " horas.");
        }
    }
}
