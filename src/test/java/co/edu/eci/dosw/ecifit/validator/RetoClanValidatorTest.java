package co.edu.eci.dosw.ecifit.validator;

import co.edu.eci.dosw.ecifit.exception.ReglaDeNegocioException;
import co.edu.eci.dosw.ecifit.model.RetoClan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Pruebas Unitarias para RetoClanValidator (Patrón AAA)")
class RetoClanValidatorTest {

    private RetoClanValidator validator;

    @BeforeEach
    void setUp() {
        validator = new RetoClanValidator();
    }

    @Test
    @DisplayName("validarPublicacionReto - reto nulo lanza ReglaDeNegocioException")
    void validarPublicacionReto_retoNulo_lanzaExcepcion() {
        // Arrange
        RetoClan reto = null;

        // Act & Assert
        assertThrows(ReglaDeNegocioException.class, () -> validator.validarPublicacionReto(reto));
    }

    @Test
    @DisplayName("validarPublicacionReto - fechas nulas lanza ReglaDeNegocioException")
    void validarPublicacionReto_fechasNulas_lanzaExcepcion() {
        // Arrange
        RetoClan reto = new RetoClan("R1", "C1", "Reto Fuerza", 500, 0, 100, null, null, false);

        // Act & Assert
        assertThrows(ReglaDeNegocioException.class, () -> validator.validarPublicacionReto(reto));
    }

    @Test
    @DisplayName("validarPublicacionReto - fecha fin anterior a inicio lanza ReglaDeNegocioException")
    void validarPublicacionReto_fechaFinInvalida_lanzaExcepcion() {
        // Arrange
        RetoClan reto = new RetoClan("R1", "C1", "Reto Fuerza", 500, 0, 100,
                LocalDate.of(2026, 10, 20), LocalDate.of(2026, 10, 10), false);

        // Act & Assert
        assertThrows(ReglaDeNegocioException.class, () -> validator.validarPublicacionReto(reto));
    }

    @Test
    @DisplayName("validarPublicacionReto - objetivo de puntos no positivo lanza ReglaDeNegocioException")
    void validarPublicacionReto_objetivoNoPositivo_lanzaExcepcion() {
        // Arrange
        RetoClan reto = new RetoClan("R1", "C1", "Reto Fuerza", 0, 0, 100,
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 7), false);

        // Act & Assert
        assertThrows(ReglaDeNegocioException.class, () -> validator.validarPublicacionReto(reto));
    }

    @Test
    @DisplayName("validarPublicacionReto - recompensa no positiva lanza ReglaDeNegocioException")
    void validarPublicacionReto_recompensaNoPositiva_lanzaExcepcion() {
        // Arrange
        RetoClan reto = new RetoClan("R1", "C1", "Reto Fuerza", 500, 0, -10,
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 7), false);

        // Act & Assert
        assertThrows(ReglaDeNegocioException.class, () -> validator.validarPublicacionReto(reto));
    }

    @Test
    @DisplayName("validarPublicacionReto - reto con datos validos no lanza excepcion")
    void validarPublicacionReto_valido_pasaExitosamente() {
        // Arrange
        RetoClan reto = new RetoClan("R1", "C1", "Reto Fuerza", 500, 0, 150,
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 7), false);

        // Act & Assert
        assertDoesNotThrow(() -> validator.validarPublicacionReto(reto));
    }

    @Test
    @DisplayName("validarEvaluacionReto - reto nulo lanza ReglaDeNegocioException")
    void validarEvaluacionReto_retoNulo_lanzaExcepcion() {
        // Arrange
        RetoClan reto = null;

        // Act & Assert
        assertThrows(ReglaDeNegocioException.class, () -> validator.validarEvaluacionReto(reto));
    }

    @Test
    @DisplayName("validarEvaluacionReto - reto previamente completado lanza ReglaDeNegocioException")
    void validarEvaluacionReto_yaCompletado_lanzaExcepcion() {
        // Arrange
        RetoClan reto = new RetoClan("R1", "C1", "Reto", 500, 500, 100,
                LocalDate.now(), LocalDate.now().plusDays(7), true);

        // Act & Assert
        assertThrows(ReglaDeNegocioException.class, () -> validator.validarEvaluacionReto(reto));
    }

    @Test
    @DisplayName("validarEvaluacionReto - reto pendiente no lanza excepcion")
    void validarEvaluacionReto_pendiente_pasaExitosamente() {
        // Arrange
        RetoClan reto = new RetoClan("R1", "C1", "Reto", 500, 200, 100,
                LocalDate.now(), LocalDate.now().plusDays(7), false);

        // Act & Assert
        assertDoesNotThrow(() -> validator.validarEvaluacionReto(reto));
    }

    @Test
    @DisplayName("validarDueloClanes - ids vacios o nulos lanza ReglaDeNegocioException")
    void validarDueloClanes_idsInvalidos_lanzaExcepcion() {
        // Arrange & Act & Assert
        assertThrows(ReglaDeNegocioException.class, () -> validator.validarDueloClanes(null, "C2"));
        assertThrows(ReglaDeNegocioException.class, () -> validator.validarDueloClanes("C1", "   "));
    }

    @Test
    @DisplayName("validarDueloClanes - mismo clan en duelo lanza ReglaDeNegocioException")
    void validarDueloClanes_mismoClan_lanzaExcepcion() {
        // Arrange
        String clanId = "CLAN-01";

        // Act & Assert
        assertThrows(ReglaDeNegocioException.class, () -> validator.validarDueloClanes(clanId, clanId));
    }

    @Test
    @DisplayName("validarDueloClanes - clanes distintos y validos no lanza excepcion")
    void validarDueloClanes_clanesDistintos_pasaExitosamente() {
        // Arrange
        String clanId1 = "CLAN-01";
        String clanId2 = "CLAN-02";

        // Act & Assert
        assertDoesNotThrow(() -> validator.validarDueloClanes(clanId1, clanId2));
    }
}
