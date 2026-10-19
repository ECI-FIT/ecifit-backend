package co.edu.eci.dosw.ecifit.model.chain;

import co.edu.eci.dosw.ecifit.exception.ReglaDeNegocioException;
import co.edu.eci.dosw.ecifit.model.Actividad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias del Patrón Chain of Responsibility (Anti-Cheat)")
class ActividadChainTest {

    private ActividadHandler chain;

    @BeforeEach
    void setUp() {
        ActividadHandler integrity = new DataIntegrityHandler();
        ActividadHandler limits = new PhysiologicalLimitsHandler();
        ActividadHandler sanctions = new SanctionStatusHandler();

        integrity.enlazar(limits).enlazar(sanctions);
        this.chain = integrity;
    }

    @Test
    @DisplayName("Debe procesar exitosamente toda la cadena cuando la actividad es válida")
    void debeValidarActividadCorrectamenteEnTodaLaCadena() {
        // Arrange
        Actividad actividad = new Actividad("ACT-1", "CARDIO", 45, 7, LocalDateTime.now());

        // Act & Assert
        assertDoesNotThrow(() -> chain.validar(actividad));
    }

    @Test
    @DisplayName("DataIntegrityHandler debe rechazar actividad nula")
    void debeRechazarActividadNula() {
        // Arrange & Act & Assert
        ReglaDeNegocioException ex = assertThrows(ReglaDeNegocioException.class, () -> chain.validar(null));
        assertTrue(ex.getMessage().contains("no pueden ser nulos"));
    }

    @Test
    @DisplayName("DataIntegrityHandler debe rechazar actividad sin tipo")
    void debeRechazarActividadSinTipo() {
        // Arrange
        Actividad actividad = new Actividad("ACT-2", "", 30, 5, LocalDateTime.now());

        // Act & Assert
        ReglaDeNegocioException ex = assertThrows(ReglaDeNegocioException.class, () -> chain.validar(actividad));
        assertTrue(ex.getMessage().contains("tipo de actividad física es obligatorio"));
    }

    @Test
    @DisplayName("DataIntegrityHandler debe rechazar duración menor a 10 minutos")
    void debeRechazarDuracionMenorADiezMinutos() {
        // Arrange
        Actividad actividad = new Actividad("ACT-3", "FUERZA", 5, 5, LocalDateTime.now());

        // Act & Assert
        ReglaDeNegocioException ex = assertThrows(ReglaDeNegocioException.class, () -> chain.validar(actividad));
        assertTrue(ex.getMessage().contains("duración mínima 10 min"));
    }

    @Test
    @DisplayName("PhysiologicalLimitsHandler debe rechazar duración superior a 300 minutos (Anti-Cheat)")
    void debeRechazarDuracionSobrehumanaSuperiorA300Minutos() {
        // Arrange (actividad aparentemente válida para integridad, pero excede límites fisiológicos)
        Actividad actividad = new Actividad("ACT-4", "CARDIO", 301, 5, LocalDateTime.now());

        // Act & Assert
        ReglaDeNegocioException ex = assertThrows(ReglaDeNegocioException.class, () -> chain.validar(actividad));
        assertTrue(ex.getMessage().contains("Límites fisiológicos excedidos"));
    }

    @Test
    @DisplayName("SanctionStatusHandler debe rechazar entrenamientos con fecha futura")
    void debeRechazarFechaFuturaIncongruente() {
        // Arrange
        Actividad actividad = new Actividad("ACT-5", "CARDIO", 45, 5, LocalDateTime.now().plusDays(2));

        // Act & Assert
        ReglaDeNegocioException ex = assertThrows(ReglaDeNegocioException.class, () -> chain.validar(actividad));
        assertTrue(ex.getMessage().contains("fechas u horas futuras"));
    }

    @Test
    @DisplayName("Debe verificar el encadenamiento correcto de eslabones")
    void debeVerificarEncadenamientoCorrecto() {
        // Arrange
        DataIntegrityHandler h1 = new DataIntegrityHandler();
        PhysiologicalLimitsHandler h2 = new PhysiologicalLimitsHandler();

        // Act
        h1.enlazar(h2);

        // Assert
        assertSame(h2, h1.getSiguiente());
    }
}
