package co.edu.eci.dosw.ecifit.validator;

import co.edu.eci.dosw.ecifit.exception.ReglaDeNegocioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Pruebas Unitarias para AsistenciaGymValidator (Patrón AAA)")
class AsistenciaGymValidatorTest {

    private final AsistenciaGymValidator validator = new AsistenciaGymValidator();

    @Test
    @DisplayName("validar - ids válidos no lanzan excepción")
    void validar_idsValidos_noLanzaExcepcion() {
        // Arrange
        String estudianteId = "EST-01";
        String entrenadorId = "ENT-01";

        // Act & Assert
        assertDoesNotThrow(() -> validator.validar(estudianteId, entrenadorId));
    }

    @Test
    @DisplayName("validar - estudianteId nulo lanza ReglaDeNegocioException")
    void validar_estudianteIdNulo_lanzaReglaDeNegocio() {
        // Arrange
        String estudianteId = null;
        String entrenadorId = "ENT-01";

        // Act & Assert
        assertThrows(ReglaDeNegocioException.class, () -> validator.validar(estudianteId, entrenadorId));
    }

    @Test
    @DisplayName("validar - estudianteId vacío lanza ReglaDeNegocioException")
    void validar_estudianteIdVacio_lanzaReglaDeNegocio() {
        // Arrange
        String estudianteId = "   ";
        String entrenadorId = "ENT-01";

        // Act & Assert
        assertThrows(ReglaDeNegocioException.class, () -> validator.validar(estudianteId, entrenadorId));
    }

    @Test
    @DisplayName("validar - entrenadorId nulo lanza ReglaDeNegocioException")
    void validar_entrenadorIdNulo_lanzaReglaDeNegocio() {
        // Arrange
        String estudianteId = "EST-01";
        String entrenadorId = null;

        // Act & Assert
        assertThrows(ReglaDeNegocioException.class, () -> validator.validar(estudianteId, entrenadorId));
    }

    @Test
    @DisplayName("validar - entrenadorId vacío lanza ReglaDeNegocioException")
    void validar_entrenadorIdVacio_lanzaReglaDeNegocio() {
        // Arrange
        String estudianteId = "EST-01";
        String entrenadorId = "";

        // Act & Assert
        assertThrows(ReglaDeNegocioException.class, () -> validator.validar(estudianteId, entrenadorId));
    }
}
