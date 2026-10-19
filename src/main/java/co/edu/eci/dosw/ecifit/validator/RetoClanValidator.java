package co.edu.eci.dosw.ecifit.validator;

import co.edu.eci.dosw.ecifit.exception.ReglaDeNegocioException;
import co.edu.eci.dosw.ecifit.model.RetoClan;
import org.springframework.stereotype.Component;

@Component
public class RetoClanValidator implements IRetoClanValidator {

    @Override
    public void validarPublicacionReto(RetoClan reto) {
        if (reto == null) {
            throw new ReglaDeNegocioException("Los datos del reto no pueden ser nulos");
        }
        if (reto.getFechaInicio() == null || reto.getFechaFin() == null) {
            throw new ReglaDeNegocioException("Las fechas de vigencia del reto son obligatorias");
        }
        if (reto.getFechaFin().isBefore(reto.getFechaInicio())) {
            throw new ReglaDeNegocioException("La fecha de fin no puede ser anterior a la fecha de inicio del reto");
        }
        if (reto.getObjetivoPuntos() == null || reto.getObjetivoPuntos() <= 0) {
            throw new ReglaDeNegocioException("El objetivo de puntos debe ser un valor positivo");
        }
        if (reto.getRecompensa() == null || reto.getRecompensa() <= 0) {
            throw new ReglaDeNegocioException("La recompensa del reto debe ser un valor positivo");
        }
    }

    @Override
    public void validarEvaluacionReto(RetoClan reto) {
        if (reto == null) {
            throw new ReglaDeNegocioException("El reto a evaluar no existe");
        }
        if (Boolean.TRUE.equals(reto.getCompletado())) {
            throw new ReglaDeNegocioException("El reto ya ha sido completado y recompensado previamente");
        }
    }

    @Override
    public void validarDueloClanes(String clanId1, String clanId2) {
        if (clanId1 == null || clanId2 == null || clanId1.isBlank() || clanId2.isBlank()) {
            throw new ReglaDeNegocioException("Los identificadores de ambos clanes son obligatorios para el duelo");
        }
        if (clanId1.trim().equalsIgnoreCase(clanId2.trim())) {
            throw new ReglaDeNegocioException("Un clan no puede batirse en duelo contra sí mismo");
        }
    }
}
