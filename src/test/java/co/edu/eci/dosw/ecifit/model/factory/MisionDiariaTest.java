package co.edu.eci.dosw.ecifit.model.factory;

import co.edu.eci.dosw.ecifit.model.Actividad;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Pruebas de recompensa de Misión Diaria - RF-19")
class MisionDiariaTest {

    @Test
    @DisplayName("Debe completar la misión una sola vez")
    void debeCompletarMisionUnaSolaVez() {

        MisionDiaria mision = new MisionDiaria(
                "MD-01",
                "Completar 30 minutos de actividad",
                50,
                30
        );

        Actividad actividad = new Actividad(
                "A-01",
                "Trote",
                30,
                5,
                LocalDateTime.now()
        );

        // Primera vez: la misión debe completarse.
        assertTrue(mision.verificarCumplimiento(actividad));

        // La misión ya está completada.
        assertTrue(mision.getCompletada());

        // Segunda vez: no debe generar un nuevo cumplimiento.
        assertFalse(mision.verificarCumplimiento(actividad));
    }
}