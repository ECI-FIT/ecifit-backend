package co.edu.eci.dosw.ecifit.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ComidaTest {

    private static Comida comida() {
        return new Comida("EST-1", TipoComida.ALMUERZO, "Pollo con arroz", LocalDateTime.now());
    }

    private static EvaluacionNutricional evaluacion(boolean balanceada) {
        return new EvaluacionNutricional(balanceada,
                balanceada ? CalificacionComida.BUENA : CalificacionComida.REGULAR,
                balanceada ? 85 : 45, "Consejo", List.of("Agrega ensalada"));
    }

    @Test
    void debeDarVeintePuntosPorComidaBalanceada() {
        Comida comida = comida();

        int puntos = comida.aplicarEvaluacion(evaluacion(true));

        assertEquals(Comida.PUNTOS_REGISTRO + Comida.BONO_BALANCEADA, puntos);
        assertEquals(20, comida.getPuntosOtorgados());
        assertEquals(EstadoEvaluacion.EVALUADA, comida.getEstado());
        assertTrue(comida.getBalanceada());
        assertEquals(CalificacionComida.BUENA, comida.getCalificacion());
        assertEquals(85, comida.getPuntaje());
        assertEquals("Consejo", comida.getConsejo());
        assertEquals(List.of("Agrega ensalada"), comida.getRecomendaciones());
    }

    @Test
    void debeDarCincoPuntosPorComidaNoBalanceada() {
        Comida comida = comida();

        int puntos = comida.aplicarEvaluacion(evaluacion(false));

        assertEquals(Comida.PUNTOS_REGISTRO, puntos);
        assertFalse(comida.getBalanceada());
    }

    @Test
    void noDebeDarPuntosCuandoLaComidaNoOtorgaPuntos() {
        Comida comida = comida();
        comida.setOtorgaPuntos(false);

        int puntos = comida.aplicarEvaluacion(evaluacion(true));

        assertEquals(0, puntos);
        assertEquals(0, comida.getPuntosOtorgados());
        assertTrue(comida.estaEvaluada());
    }

    @Test
    void noDebePermitirEvaluarDosVeces() {
        Comida comida = comida();
        comida.aplicarEvaluacion(evaluacion(true));

        assertThrows(IllegalStateException.class, () -> comida.aplicarEvaluacion(evaluacion(true)));
        assertEquals(20, comida.getPuntosOtorgados());
    }

    @Test
    void debeRechazarEvaluacionNula() {
        Comida comida = comida();

        assertThrows(IllegalArgumentException.class, () -> comida.aplicarEvaluacion(null));
    }

    @Test
    void debeSeguirPendienteMientrasNoSeAlcanceElMaximoDeIntentos() {
        Comida comida = comida();

        comida.registrarIntentoFallido();
        comida.registrarIntentoFallido();

        assertEquals(2, comida.getIntentosEvaluacion());
        assertEquals(EstadoEvaluacion.PENDIENTE, comida.getEstado());
    }

    @Test
    void debePasarAErrorAlAlcanzarElMaximoDeIntentos() {
        Comida comida = comida();

        for (int i = 0; i < Comida.MAX_INTENTOS_EVALUACION; i++) {
            comida.registrarIntentoFallido();
        }

        assertEquals(EstadoEvaluacion.ERROR, comida.getEstado());
        assertFalse(comida.estaEvaluada());
    }

    @Test
    void debeIniciarPendienteSinPuntos() {
        Comida comida = comida();

        assertEquals(EstadoEvaluacion.PENDIENTE, comida.getEstado());
        assertEquals(0, comida.getPuntosOtorgados());
        assertEquals(0, comida.getIntentosEvaluacion());
        assertTrue(comida.getRecomendaciones().isEmpty());
    }

    @Test
    void debeCopiarRecomendacionesNulasComoListaVacia() {
        Comida comida = comida();
        comida.setRecomendaciones(null);

        assertTrue(comida.getRecomendaciones().isEmpty());
    }
}
