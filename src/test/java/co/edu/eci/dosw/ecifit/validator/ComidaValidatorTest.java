package co.edu.eci.dosw.ecifit.validator;

import co.edu.eci.dosw.ecifit.exception.ReglaDeNegocioException;
import co.edu.eci.dosw.ecifit.model.Comida;
import co.edu.eci.dosw.ecifit.model.TipoComida;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ComidaValidatorTest {

    private final ComidaValidator validator = new ComidaValidator();

    private static Comida comidaValida() {
        return new Comida("EST-1", TipoComida.ALMUERZO, "Pollo con arroz y ensalada", LocalDateTime.now());
    }

    @Test
    void debeAceptarComidaValida() {
        Comida comida = comidaValida();

        assertDoesNotThrow(() -> validator.validar(comida));
    }

    @Test
    void debeRechazarComidaNula() {
        assertThrows(ReglaDeNegocioException.class, () -> validator.validar(null));
    }

    @Test
    void debeRechazarComidaSinEstudiante() {
        Comida comida = comidaValida();
        comida.setEstudianteId(" ");

        assertThrows(ReglaDeNegocioException.class, () -> validator.validar(comida));
    }

    @Test
    void debeRechazarComidaSinTipo() {
        Comida comida = comidaValida();
        comida.setTipo(null);

        assertThrows(ReglaDeNegocioException.class, () -> validator.validar(comida));
    }

    @Test
    void debeRechazarComidaSinDescripcion() {
        Comida comida = comidaValida();
        comida.setDescripcion("   ");

        ReglaDeNegocioException ex = assertThrows(ReglaDeNegocioException.class, () -> validator.validar(comida));

        assertEquals("La descripción de la comida es obligatoria.", ex.getMessage());
    }

    @Test
    void debeRechazarDescripcionMuyCorta() {
        Comida comida = comidaValida();
        comida.setDescripcion("ab");

        assertThrows(ReglaDeNegocioException.class, () -> validator.validar(comida));
    }

    @Test
    void debeRechazarDescripcionMuyLarga() {
        Comida comida = comidaValida();
        comida.setDescripcion("a".repeat(501));

        assertThrows(ReglaDeNegocioException.class, () -> validator.validar(comida));
    }

    @Test
    void debeRechazarComidaSinFecha() {
        Comida comida = comidaValida();
        comida.setFechaHora(null);

        assertThrows(ReglaDeNegocioException.class, () -> validator.validar(comida));
    }

    @Test
    void debeRechazarComidaEnElFuturo() {
        Comida comida = comidaValida();
        comida.setFechaHora(LocalDateTime.now().plusHours(2));

        ReglaDeNegocioException ex = assertThrows(ReglaDeNegocioException.class, () -> validator.validar(comida));

        assertEquals("No se puede registrar una comida en el futuro.", ex.getMessage());
    }

    @Test
    void debeRechazarComidaDeHaceMasDeVeinticuatroHoras() {
        Comida comida = comidaValida();
        comida.setFechaHora(LocalDateTime.now().minusHours(25));

        assertThrows(ReglaDeNegocioException.class, () -> validator.validar(comida));
    }

    @Test
    void debeAceptarComidaDeHaceVariasHoras() {
        Comida comida = comidaValida();
        comida.setFechaHora(LocalDateTime.now().minusHours(20));

        assertDoesNotThrow(() -> validator.validar(comida));
    }
}
