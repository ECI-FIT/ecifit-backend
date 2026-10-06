package co.edu.eci.dosw.ecifit.validator;

import co.edu.eci.dosw.ecifit.exception.ConflictoException;
import co.edu.eci.dosw.ecifit.exception.ReglaDeNegocioException;
import co.edu.eci.dosw.ecifit.repository.TemporadaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class TemporadaValidator implements ITemporadaValidator {

    private final TemporadaRepository temporadaRepository;

    @Override
    public void validarFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null || fechaFin == null || !fechaFin.isAfter(fechaInicio)) {
            throw new ReglaDeNegocioException("La fecha de fin debe ser posterior a la fecha de inicio");
        }
    }

    @Override
    public void validarTemporadaUnicaActiva(Boolean estadoActivo) {
        if (Boolean.TRUE.equals(estadoActivo) && temporadaRepository.existsByEstadoActivoTrue()) {
            throw new ConflictoException("Ya existe una temporada activa");
        }
    }

    @Override
    public void validarPuntosPositivos(int puntos) {
        if (puntos < 0) {
            throw new ReglaDeNegocioException("Los puntos a agregar no pueden ser negativos");
        }
    }
}
