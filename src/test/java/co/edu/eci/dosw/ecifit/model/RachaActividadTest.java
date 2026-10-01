package co.edu.eci.dosw.ecifit.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Pruebas de racha de actividad - RF-20 y RF-21")
class RachaActividadTest {

    @Test
    @DisplayName("Debe otorgar 200 puntos al completar 7 días consecutivos de actividad")
    void debeOtorgarBonoAlCompletarSieteDiasConsecutivos() {

        Estudiante estudiante = new Estudiante(
                "E1",
                "Julian Giral",
                "julian.giral@escuelaing.edu.co",
                null
        );

        LocalDateTime inicio = LocalDateTime.of(2026, 9, 1, 10, 0);

        for (int i = 0; i < 7; i++) {
            Actividad actividad = new Actividad(
                    "A-" + (i + 1),
                    "Cardio",
                    10,
                    1,
                    inicio.plusDays(i)
            );

            estudiante.registrarActividad(actividad);
        }

        /*
         * Cada actividad genera:
         * 10 minutos × intensidad 1 = 10 puntos.
         *
         * 7 actividades = 70 puntos.
         *
         * Al completar 7 días consecutivos se deben otorgar
         * 200 puntos adicionales por la racha.
         *
         * Total esperado: 70 + 200 = 270 puntos.
         */
        assertEquals(270, estudiante.getPuntosAcumulados());
    }

    @Test
    @DisplayName("No debe otorgar el bono si no se completan 7 días consecutivos")
    void noDebeOtorgarBonoSinSieteDiasConsecutivos() {

        Estudiante estudiante = new Estudiante(
                "E2",
                "Santiago Garcia",
                "santiago.garcia@escuelaing.edu.co",
                null
        );

        LocalDateTime inicio = LocalDateTime.of(2026, 9, 1, 10, 0);

        for (int i = 0; i < 6; i++) {
            Actividad actividad = new Actividad(
                    "A-" + (i + 1),
                    "Cardio",
                    10,
                    1,
                    inicio.plusDays(i)
            );

            estudiante.registrarActividad(actividad);
        }

        /*
         * 6 actividades × 10 puntos = 60 puntos.
         *
         * Como no se completaron 7 días consecutivos,
         * no debe existir bono de 200 puntos.
         */
        assertEquals(60, estudiante.getPuntosAcumulados());
    }

    @Test
    @DisplayName("Una interrupción debe impedir completar una racha de 7 días")
    void unaInterrupcionDebeRomperLaRacha() {

        Estudiante estudiante = new Estudiante(
                "E3",
                "Daniel Villamizar",
                "daniel.villamizar@escuelaing.edu.co",
                null
        );

        LocalDateTime inicio = LocalDateTime.of(2026, 9, 1, 10, 0);

        /*
         * Actividad durante 3 días consecutivos.
         */
        for (int i = 0; i < 3; i++) {
            Actividad actividad = new Actividad(
                    "A-" + (i + 1),
                    "Cardio",
                    10,
                    1,
                    inicio.plusDays(i)
            );

            estudiante.registrarActividad(actividad);
        }

        /*
         * Se salta el día 4.
         */
        Actividad actividadDia5 = new Actividad(
                "A-4",
                "Cardio",
                10,
                1,
                inicio.plusDays(4)
        );

        estudiante.registrarActividad(actividadDia5);

        /*
         * Se tienen 4 actividades, pero no 4 días consecutivos.
         * Por lo tanto no debe existir bono.
         */
        assertEquals(40, estudiante.getPuntosAcumulados());
    }
}