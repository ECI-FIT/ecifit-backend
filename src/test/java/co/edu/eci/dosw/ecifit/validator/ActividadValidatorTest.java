package co.edu.eci.dosw.ecifit.validator;

import co.edu.eci.dosw.ecifit.exception.ReglaDeNegocioException;
import co.edu.eci.dosw.ecifit.model.Actividad;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias para ActividadValidator (Patrón AAA)")
class ActividadValidatorTest {

    private final ActividadValidator validator = new ActividadValidator();

    @Test
    @DisplayName("Debe validar exitosamente cuando la actividad cumple criterios")
    void debeValidarActividadCorrecta() {
        // Arrange
        Actividad actividad = new Actividad("A1", "CARDIO", 25, 7, LocalDateTime.now());

        // Act
        org.junit.jupiter.api.function.Executable accion = () -> validator.validar(actividad);

        // Assert
        assertDoesNotThrow(accion);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando la actividad dura menos de 10 minutos")
    void debeRechazarDuracionInsuficiente() {
        // Arrange
        Actividad corta = new Actividad("A2", "CARDIO", 9, 7, LocalDateTime.now());

        // Act & Assert
        ReglaDeNegocioException ex = assertThrows(ReglaDeNegocioException.class,
                () -> validator.validar(corta));
        assertTrue(ex.getMessage().contains("duración mínima 10 min"));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando la intensidad está fuera del rango 1 a 10")
    void debeRechazarIntensidadFueraDeRango() {
        // Arrange
        Actividad intensidadInvalida = new Actividad("A3", "FUERZA", 20, 11, LocalDateTime.now());

        // Act & Assert
        ReglaDeNegocioException ex = assertThrows(ReglaDeNegocioException.class,
                () -> validator.validar(intensidadInvalida));
        assertTrue(ex.getMessage().contains("intensidad entre 1 y 10"));
    }
}
