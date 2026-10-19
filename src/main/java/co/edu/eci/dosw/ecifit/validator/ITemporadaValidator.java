package co.edu.eci.dosw.ecifit.validator;

import java.time.LocalDate;

public interface ITemporadaValidator {

    void validarFechas(LocalDate fechaInicio, LocalDate fechaFin);

    void validarTemporadaUnicaActiva(Boolean estadoActivo);

    void validarPuntosPositivos(int puntos);
}
