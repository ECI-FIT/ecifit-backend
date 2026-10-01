package co.edu.eci.dosw.ecifit.model.factory;

import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Fábrica concreta estándar para la generación de misiones del ciclo universitario regular.
 */
@Component
public class FabricaMisionesEstandar implements FabricaMisiones {

    @Override
    public Mision crearMisionDiaria() {
        return new MisionDiaria(
                UUID.randomUUID().toString(),
                "Activación ECI Diaria: Completa al menos 30 minutos de actividad física hoy",
                50,
                30
        );
    }

    @Override
    public Mision crearMisionSemanal() {
        return new MisionSemanal(
                UUID.randomUUID().toString(),
                "Desafío Semanal de Bienestar: Acumula 150 minutos de actividad en la semana",
                200,
                150
        );
    }
}
